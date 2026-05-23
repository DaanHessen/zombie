import os
import json
import csv

# Set up paths
base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
data_dir = os.path.join(base_dir, "src", "main", "resources", "data")
source_data_dir = os.path.join(base_dir, "scripts", "source_data")

# Create source_data directory if it doesn't exist
os.makedirs(source_data_dir, exist_ok=True)

def json_to_csv(json_filename, csv_filename, fields):
    json_path = os.path.join(data_dir, json_filename)
    csv_path = os.path.join(source_data_dir, csv_filename)
    
    if not os.path.exists(json_path):
        print(f"Skipping {json_filename} (does not exist)")
        return
        
    print(f"Converting {json_filename} to {csv_filename}...")
    
    with open(json_path, "r", encoding="utf-8") as jf:
        data = json.load(jf)
        
    with open(csv_path, "w", encoding="utf-8", newline="") as cf:
        writer = csv.DictWriter(cf, fieldnames=fields, quoting=csv.QUOTE_MINIMAL)
        writer.writeheader()
        
        for item in data:
            # Filter item to contain only specified fields, filling in None if missing
            row = {field: item.get(field) for field in fields}
            writer.writerow(row)
            
    print(f"Saved {csv_filename}")

# Define fields for each dataset
datasets = [
    {
        "json": "continents.json",
        "csv": "continents.csv",
        "fields": ["code", "name"]
    },
    {
        "json": "countries.json",
        "csv": "countries.csv",
        "fields": ["iso", "iso3", "name", "capital", "continent", "population", "areaSqKm"]
    },
    {
        "json": "regions.json",
        "csv": "regions.csv",
        "fields": ["countryCode", "regionCode", "name", "geonameId"]
    },
    {
        "json": "settlements.json",
        "csv": "settlements.csv",
        "fields": ["geonameId", "name", "latitude", "longitude", "countryCode", "regionCode", "population", "timezone", "biome"]
    },
    {
        "json": "oceans_seas.json",
        "csv": "oceans_seas.csv",
        "fields": ["name", "type"]
    }
]

# Run conversions
for ds in datasets:
    json_to_csv(ds["json"], ds["csv"], ds["fields"])

# Delete original JSON files
for ds in datasets:
    json_path = os.path.join(data_dir, ds["json"])
    if os.path.exists(json_path):
        os.remove(json_path)
        print(f"Deleted obsolete JSON file: {ds['json']}")

print("All conversions complete and raw JSON files removed!")
