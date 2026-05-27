import gzip
import csv
import os

data_dir = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data"
files_to_inspect = [
    "continents.csv.gz",
    "countries.csv.gz",
    "oceans_seas.csv.gz",
    "regions.csv.gz",
    "road_nodes.csv.gz",
    "road_segments.csv.gz",
    "settlements.csv.gz",
    "special_locations.csv.gz"
]

for filename in files_to_inspect:
    filepath = os.path.join(data_dir, filename)
    print(f"\n--- Inspecting {filename} ---")
    if not os.path.exists(filepath):
        print("File not found.")
        continue
    
    try:
        with gzip.open(filepath, 'rt', encoding='utf-8') as f:
            reader = csv.reader(f)
            header = next(reader, None)
            print(f"Header: {header}")
            print("First 2 rows:")
            for i, row in enumerate(reader):
                if i < 2:
                    print(f"  {row}")
                else:
                    break
            
            # Count rows (simplified, not full count for speed)
            f.seek(0)
            row_count = sum(1 for _ in f) - 1 # subtract header
            print(f"Total Rows: ~{row_count}")
    except Exception as e:
        print(f"Error reading file: {e}")
