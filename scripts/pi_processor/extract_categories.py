import csv
import gzip
import glob

def main():
    print("Extracting unique categories from finished POI files...")
    unique_categories = set()
    
    files = glob.glob("*_pois.csv.gz")
    
    if not files:
        print("No POI files found!")
        return

    for file in files:
        print(f"Reading {file}...")
        try:
            with gzip.open(file, 'rt', encoding='utf-8') as f:
                reader = csv.reader(f)
                header = next(reader)
                
                try:
                    cat_idx = header.index("category")
                except ValueError:
                    print(f"  Warning: 'category' column not found in {file}")
                    continue
                    
                for row in reader:
                    if len(row) > cat_idx:
                        unique_categories.add(row[cat_idx])
        except Exception as e:
            print(f"  Error reading {file}: {e}")

    out_file = "unique_categories.txt"
    with open(out_file, "w", encoding="utf-8") as f:
        for cat in sorted(unique_categories):
            f.write(cat + "\n")
            
    print(f"\nFound {len(unique_categories)} unique categories!")
    print(f"Saved to {out_file}. You can open this file to see them all!")

if __name__ == "__main__":
    main()
