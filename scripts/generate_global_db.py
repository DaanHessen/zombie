import sqlite3
import gzip
import csv
import os

DB_FILE = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\global_base_new.sqlite"
DATA_DIR = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data"

if os.path.exists(DB_FILE):
    os.remove(DB_FILE)

conn = sqlite3.connect(DB_FILE)
cursor = conn.cursor()

def load_csv_to_table(filename, table_name, schema):
    filepath = os.path.join(DATA_DIR, filename)
    if not os.path.exists(filepath):
        print(f"Skipping {filename}, file not found.")
        return

    columns = ", ".join([f"{col} {dtype}" for col, dtype in schema.items()])
    cursor.execute(f"CREATE TABLE {table_name} ({columns})")
    
    with gzip.open(filepath, 'rt', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        placeholders = ", ".join(["?"] * len(header))
        insert_sql = f"INSERT INTO {table_name} ({', '.join(header)}) VALUES ({placeholders})"
        rows = [row for row in reader]
        cursor.executemany(insert_sql, rows)

load_csv_to_table("continents.csv.gz", "continents", {
    "id": "TEXT PRIMARY KEY", "name": "TEXT", "code": "TEXT"
})

load_csv_to_table("countries.csv.gz", "countries", {
    "id": "TEXT PRIMARY KEY", "name": "TEXT", "code": "TEXT", "capital": "TEXT",
    "population": "INTEGER", "square_kilometers": "REAL", "continent_id": "TEXT",
    "min_lat": "REAL", "min_lon": "REAL", "max_lat": "REAL", "max_lon": "REAL"
})

load_csv_to_table("regions.csv.gz", "regions", {
    "id": "TEXT PRIMARY KEY", "name": "TEXT", "code": "TEXT", "country_id": "TEXT"
})

load_csv_to_table("oceans_seas.csv.gz", "oceans_seas", {
    "id": "TEXT PRIMARY KEY", "name": "TEXT", "type": "TEXT"
})

load_csv_to_table("settlements.csv.gz", "settlements", {
    "id": "TEXT PRIMARY KEY", "geoname_id": "TEXT", "name": "TEXT", "latitude": "REAL",
    "longitude": "REAL", "population": "INTEGER", "timezone": "TEXT", "koppen_code": "TEXT",
    "climate_desc": "TEXT", "region_id": "TEXT", "language": "TEXT", "is_ground_zero": "TEXT"
})

cursor.execute("CREATE INDEX idx_country_continent ON countries(continent_id)")
cursor.execute("CREATE INDEX idx_region_country ON regions(country_id)")
cursor.execute("CREATE INDEX idx_settlement_region ON settlements(region_id)")
cursor.execute("CREATE INDEX idx_settlement_coords ON settlements(latitude, longitude)")

conn.commit()
conn.close()
print("Database rebuilt with climate data and bounding boxes!")
