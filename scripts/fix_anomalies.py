import csv
import gzip
import os

items_dir = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items"
files = [f for f in os.listdir(items_dir) if f.endswith('.csv.gz')]

for file in files:
    path = os.path.join(items_dir, file)
    rows = []
    
    with gzip.open(path, 'rt', newline='', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        rows.append(header)
        
        for row in reader:
            if len(row) < 9: continue
            
            # Base indices
            # id=0, name=1, description=2, category=3, type=4, weight=5, volume=6, maxStackSize=7, maxDurability=8
            category = row[3]
            
            # Rule 1: Consumables (Food/Medicine) should not have durability, they are one-time use.
            if category in ['FOOD', 'MEDICINE'] or file in ['food.csv.gz', 'medicine.csv.gz']:
                row[8] = "1"
                
            # Rule 2: Weapons and Clothing cannot be stacked (prevents inventory exploits)
            if category in ['WEAPON', 'CLOTHING']:
                row[7] = "1"
                
            rows.append(row)

    # Rewrite the file with fixed data
    with gzip.open(path, 'wt', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerows(rows)

print("Successfully fixed all data anomalies!")
