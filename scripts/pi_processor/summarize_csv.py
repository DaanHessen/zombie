import csv
import gzip
import sys
import os
from collections import Counter

def summarize_file(filepath):
    if not os.path.exists(filepath):
        print(f"Error: File '{filepath}' not found.")
        return

    print(f"Analyzing {filepath}...")
    category_counts = Counter()
    total_rows = 0

    # Handle both .csv and .csv.gz
    is_gzipped = filepath.endswith('.gz')
    open_func = gzip.open if is_gzipped else open
    mode = 'rt' if is_gzipped else 'r'

    try:
        with open_func(filepath, mode, encoding='utf-8') as f:
            reader = csv.reader(f)
            header = next(reader)
            
            try:
                cat_idx = header.index("category")
            except ValueError:
                print("Error: Could not find 'category' column in the CSV.")
                return

            for row in reader:
                if len(row) > cat_idx:
                    category = row[cat_idx]
                    category_counts[category] += 1
                    total_rows += 1
                    
                if total_rows % 500000 == 0:
                    print(f"  Processed {total_rows:,} rows...")

    except Exception as e:
        print(f"Error reading file: {e}")
        return

    print(f"\n--- SUMMARY FOR {os.path.basename(filepath)} ---")
    print(f"Total POIs: {total_rows:,}")
    print(f"Unique Categories: {len(category_counts):,}\n")
    
    # Save the summary to a file based on the input filename
    out_filename = os.path.basename(filepath).split('.')[0] + "_summary.md"
    
    with open(out_filename, "w", encoding="utf-8") as out:
        out.write(f"# Summary for {os.path.basename(filepath)}\n\n")
        out.write(f"- **Total POIs:** {total_rows:,}\n")
        out.write(f"- **Unique Categories:** {len(category_counts):,}\n\n")
        out.write("| Category | Count |\n")
        out.write("| :--- | :--- |\n")
        
        # Print top 50 to console, but write ALL to the file
        print(f"{'Category':<40} | {'Count':>10}")
        print("-" * 53)
        
        for i, (cat, count) in enumerate(category_counts.most_common()):
            if i < 50:
                print(f"{cat:<40} | {count:>10,}")
            out.write(f"| {cat} | {count:,} |\n")

    print("-" * 53)
    print(f"\nFull detailed summary saved to: {out_filename}")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python summarize_csv.py <path_to_csv_file>")
        print("Example: python summarize_csv.py africa_pois.csv.gz")
    else:
        summarize_file(sys.argv[1])
