import gzip
import csv
from collections import Counter

CSV_FILE = "netherlands_pois.csv.gz"

def analyze():
    print(f"Analyzing {CSV_FILE}...")
    category_counter = Counter()
    total_rows = 0
    
    with gzip.open(CSV_FILE, 'rt', encoding='utf-8') as f:
        reader = csv.reader(f)
        next(reader) # skip header
        for row in reader:
            if len(row) == 4:
                category = row[3]
                category_counter[category] += 1
                total_rows += 1
                
    print(f"Total POIs analyzed: {total_rows}")
    print("\nTop 50 most common categories:")
    for cat, count in category_counter.most_common(50):
        print(f"{cat}: {count}")

if __name__ == '__main__':
    analyze()
