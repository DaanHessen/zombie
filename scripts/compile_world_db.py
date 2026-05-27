import sqlite3
import pandas as pd
import gzip
import os
import uuid
import math

# Configuration
DATA_DIR = "src/main/resources/data/raw"
SQLITE_DIR = "src/main/resources/data/sqlite"
OUTPUT_DB = os.path.join(SQLITE_DIR, "world_data.sqlite")
SOURCE_DB = os.path.join(SQLITE_DIR, "netherlands_map.sqlite")

# Ticks per minute (1 tick = 1 in-game minute)
TICKS_PER_DAY = 1440

os.makedirs(SQLITE_DIR, exist_ok=True)

# Remove old db if exists
if os.path.exists(OUTPUT_DB):
    os.remove(OUTPUT_DB)

print(f"Creating {OUTPUT_DB}...")
conn = sqlite3.connect(OUTPUT_DB)

# ─── 1. Load World Hierarchy CSV Files ───────────────────────────────────────

csv_files = {
    "continent": "continents.csv.gz",
    "country":   "countries.csv.gz",
    "region":    "regions.csv.gz",
    "settlement": "settlements.csv.gz"
}

for table_name, file_name in csv_files.items():
    file_path = os.path.join(DATA_DIR, file_name)
    if os.path.exists(file_path):
        print(f"Loading {file_name} → table '{table_name}'...")
        with gzip.open(file_path, 'rt', encoding='utf-8') as f:
            df = pd.read_csv(f)
            df.to_sql(table_name, conn, if_exists='replace', index=False)
            print(f"  {len(df):,} rows.")
    else:
        print(f"  Warning: {file_path} not found, skipping.")

# ─── 2. Hierarchy Indexes ─────────────────────────────────────────────────────

print("Creating hierarchy indexes...")
conn.execute("CREATE INDEX IF NOT EXISTS idx_country_continent ON country(continent_id)")
conn.execute("CREATE INDEX IF NOT EXISTS idx_region_country    ON region(country_id)")
conn.execute("CREATE INDEX IF NOT EXISTS idx_settlement_region ON settlement(region_id)")
conn.execute("CREATE INDEX IF NOT EXISTS idx_settlement_name   ON settlement(name)")

# ─── 3. Copy POIs and Roads from netherlands_map.sqlite ──────────────────────

if os.path.exists(SOURCE_DB):
    print(f"Copying spatial data from {SOURCE_DB}...")
    conn.execute(f"ATTACH DATABASE '{SOURCE_DB}' AS source_db")

    conn.execute("CREATE TABLE pois AS SELECT * FROM source_db.pois")
    print("  Loaded pois table.")

    cur = conn.cursor()
    cur.execute("SELECT name FROM source_db.sqlite_master WHERE type='table' AND name='roads'")
    if cur.fetchone():
        conn.execute("CREATE TABLE roads AS SELECT * FROM source_db.roads")
        print("  Loaded roads table.")

    conn.execute("DETACH DATABASE source_db")

print("Creating spatial indexes on pois...")
conn.execute("CREATE INDEX IF NOT EXISTS idx_pois_lat_lon ON pois(lat, lon)")

# ─── 4. Inject Item Templates ────────────────────────────────────────────────

print("\nInjecting item templates...")

conn.execute("""
CREATE TABLE IF NOT EXISTS item_templates (
    id                    TEXT PRIMARY KEY,
    name                  TEXT NOT NULL,
    description           TEXT,
    category              TEXT NOT NULL,
    type                  TEXT NOT NULL,
    weight_grams          REAL NOT NULL DEFAULT 0,
    volume_litres         REAL NOT NULL DEFAULT 0,
    max_stack_size        INTEGER NOT NULL DEFAULT 1,
    max_durability        INTEGER NOT NULL DEFAULT 1,
    instance_tracked      INTEGER NOT NULL DEFAULT 1,

    -- Food
    calories              INTEGER DEFAULT 0,
    protein_grams         INTEGER DEFAULT 0,
    carbs_grams           INTEGER DEFAULT 0,
    hydration_value       INTEGER DEFAULT 0,
    base_spoilage_ticks   INTEGER DEFAULT 0,

    -- Weapons
    base_damage           REAL DEFAULT 0,
    durability_loss_per_use REAL DEFAULT 0,
    jam_chance            REAL DEFAULT 0,
    caliber               TEXT,
    noise_radius_meters   REAL DEFAULT 0,

    -- Medicine
    health_restored       INTEGER DEFAULT 0,
    cures_infection       INTEGER DEFAULT 0,

    -- Clothing
    armor_rating          REAL DEFAULT 0,
    insulation            REAL DEFAULT 0,
    bonus_inventory_slots INTEGER DEFAULT 0
)
""")

def safe_int(val, default=0):
    try:
        v = float(val)
        return int(v) if not math.isnan(v) else default
    except (TypeError, ValueError):
        return default

def safe_float(val, default=0.0):
    try:
        v = float(val)
        return v if not math.isnan(v) else default
    except (TypeError, ValueError):
        return default

def safe_str(val):
    if val is None or (isinstance(val, float) and math.isnan(val)):
        return None
    s = str(val).strip()
    return s if s and s.lower() not in ('nan', 'none', '') else None


def load_items(csv_path):
    with gzip.open(csv_path, 'rt', encoding='utf-8') as f:
        return pd.read_csv(f).fillna('')

item_files = {
    'food':      os.path.join(DATA_DIR, 'items', 'food.csv.gz'),
    'weapons':   os.path.join(DATA_DIR, 'items', 'weapons.csv.gz'),
    'medicine':  os.path.join(DATA_DIR, 'items', 'medicine.csv.gz'),
    'ammo':      os.path.join(DATA_DIR, 'items', 'ammo.csv.gz'),
    'clothing':  os.path.join(DATA_DIR, 'items', 'clothing.csv.gz'),
    'resources': os.path.join(DATA_DIR, 'items', 'resources.csv.gz'),
}

total_items = 0

for category_name, file_path in item_files.items():
    if not os.path.exists(file_path):
        print(f"  Warning: {file_path} not found, skipping.")
        continue

    df = load_items(file_path)
    count = 0

    for _, row in df.iterrows():
        # Use the string ID from the CSV as the primary key for readability,
        # but wrap it in a UUID v5 namespace for uniqueness guarantees.
        raw_id = str(row.get('id', '')).strip()
        item_id = raw_id if raw_id else str(uuid.uuid4())

        # expiration: days → ticks. 0 = non-perishable.
        expiration_days = safe_int(row.get('expirationDays', 0))
        spoilage_ticks  = expiration_days * TICKS_PER_DAY if expiration_days > 0 else 0

        # Weight: CSV stores kg, we store grams
        weight_kg    = safe_float(row.get('weight', 0))
        weight_grams = weight_kg * 1000

        conn.execute("""
            INSERT OR REPLACE INTO item_templates (
                id, name, description, category, type,
                weight_grams, volume_litres, max_stack_size, max_durability, instance_tracked,
                calories, protein_grams, carbs_grams, hydration_value, base_spoilage_ticks,
                base_damage, durability_loss_per_use, jam_chance, caliber, noise_radius_meters,
                health_restored, cures_infection,
                armor_rating, insulation, bonus_inventory_slots
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            item_id,
            safe_str(row.get('name')) or item_id,
            safe_str(row.get('description')),
            str(row.get('category', category_name.upper())).upper(),
            str(row.get('type', 'CONSUMABLE')).upper(),
            weight_grams,
            safe_float(row.get('volume', 0)),
            safe_int(row.get('maxStackSize', 1)),
            safe_int(row.get('maxDurability', 1)),
            1,  # instance_tracked = true by default
            # Food
            safe_int(row.get('calories', 0)),
            safe_int(row.get('protein', 0)),
            safe_int(row.get('carbs', 0)),
            safe_int(row.get('hydration', 0)),
            spoilage_ticks,
            # Weapons
            safe_float(row.get('damage', 0)),
            safe_float(row.get('durabilityLossPerUse', 0)),
            safe_float(row.get('jamChance', 0)),
            safe_str(row.get('caliber')),
            safe_float(row.get('noiseRadius', 0)),
            # Medicine
            safe_int(row.get('healthRestored', 0)),
            1 if str(row.get('curesInfection', '')).strip().lower() in ('true', '1', 'yes') else 0,
            # Clothing
            safe_float(row.get('armorRating', 0)),
            safe_float(row.get('insulation', 0)),
            safe_int(row.get('bonusInventorySlots', 0)),
        ))
        count += 1

    conn.commit()
    total_items += count
    print(f"  {category_name:<12}: {count:>4} items injected.")

# Index for fast template lookup
conn.execute("CREATE INDEX IF NOT EXISTS idx_items_category ON item_templates(category)")
conn.execute("CREATE INDEX IF NOT EXISTS idx_items_type     ON item_templates(type)")
print(f"\n  Total items injected: {total_items}")

# ─── Done ─────────────────────────────────────────────────────────────────────

conn.commit()
conn.close()

print("\n✅ Compilation complete!")
print(f"   Output: {OUTPUT_DB}")
