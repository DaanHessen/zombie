import sqlite3
import gzip
import csv
import os

DB_FILE = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\netherlands_map.sqlite"
RAW_DIR = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw_regions"

if os.path.exists(DB_FILE):
    os.remove(DB_FILE)

conn = sqlite3.connect(DB_FILE)
cursor = conn.cursor()

def load_csv_to_table(filename, table_name, schema):
    filepath = os.path.join(RAW_DIR, filename)
    if not os.path.exists(filepath):
        print(f"Skipping {filename}, file not found.")
        return

    columns = ", ".join([f"{col} {dtype}" for col, dtype in schema.items()])
    cursor.execute(f"CREATE TABLE {table_name} ({columns})")
    
    print(f"Loading {filename} into {table_name}...")
    with gzip.open(filepath, 'rt', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        placeholders = ", ".join(["?"] * len(header))
        insert_sql = f"INSERT INTO {table_name} ({', '.join(header)}) VALUES ({placeholders})"
        
        # Insert in chunks to save memory
        chunk_size = 100000
        chunk = []
        for row in reader:
            chunk.append(row)
            if len(chunk) >= chunk_size:
                cursor.executemany(insert_sql, chunk)
                chunk = []
        if chunk:
            cursor.executemany(insert_sql, chunk)

# 1. POIs
load_csv_to_table("netherlands_pois.csv.gz", "pois", {
    "lat": "REAL",
    "lon": "REAL",
    "name": "TEXT",
    "category": "TEXT",
    "area_sqm": "REAL"
})

# 2. Roads
load_csv_to_table("netherlands_roads.csv.gz", "roads", {
    "center_lat": "REAL",
    "center_lon": "REAL",
    "name": "TEXT",
    "highway_type": "TEXT",
    "surface": "TEXT",
    "coordinates": "TEXT"
})

# Create Indexes for fast spatial querying
print("Creating spatial indexes (this may take a moment)...")
cursor.execute("CREATE INDEX idx_pois_coords ON pois(lat, lon)")
cursor.execute("CREATE INDEX idx_pois_category ON pois(category)")
cursor.execute("CREATE INDEX idx_roads_coords ON roads(center_lat, center_lon)")

conn.commit()
conn.close()

file_size = os.path.getsize(DB_FILE) / 1024 / 1024
print(f"\nSuccessfully generated {DB_FILE} ({file_size:.2f} MB)")
print("This is the final file you will upload to Cloudflare R2!")
