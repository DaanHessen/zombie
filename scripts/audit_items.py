import csv
import gzip
import os

items_dir = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items"
files = [f for f in os.listdir(items_dir) if f.endswith('.csv.gz')]

all_ids = set()
duplicate_ids = set()
anomalies = []

for file in files:
    path = os.path.join(items_dir, file)
    with gzip.open(path, 'rt', newline='', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        
        for idx, row in enumerate(reader):
            if len(row) < 9:
                anomalies.append(f"{file} Row {idx+2}: Missing base fields.")
                continue
                
            item_id = row[0]
            name = row[1]
            category = row[3]
            item_type = row[4]
            
            try:
                weight = float(row[5])
                volume = float(row[6])
                max_stack = int(row[7])
                max_durability = int(row[8])
            except ValueError:
                anomalies.append(f"{item_id}: Invalid number format in base stats.")
                continue

            # Check Duplicates
            if item_id in all_ids:
                duplicate_ids.add(item_id)
            all_ids.add(item_id)
            
            # Check Physics / Balance
            if weight <= 0: anomalies.append(f"{item_id}: Weight is 0 or negative ({weight})")
            if volume <= 0: anomalies.append(f"{item_id}: Volume is 0 or negative ({volume})")
            
            if category in ['WEAPON', 'CLOTHING'] and max_stack > 1:
                anomalies.append(f"{item_id}: {category} should not be stackable! (maxStackSize={max_stack})")
            
            # Category specifics
            if file == "food.csv.gz":
                calories = int(row[9])
                if calories > 5000: anomalies.append(f"{item_id}: Crazy high calories ({calories})")
                if max_durability > 1: anomalies.append(f"{item_id}: Food shouldn't have durability ({max_durability})")
                
            if file == "medicine.csv.gz":
                if max_durability > 1: anomalies.append(f"{item_id}: Medicine shouldn't have durability ({max_durability})")

            if file == "weapons.csv.gz":
                damage = float(row[9])
                if damage <= 0: anomalies.append(f"{item_id}: Weapon does no damage ({damage})")
                if max_durability <= 1 and item_type == "MELEE_WEAPON":
                    anomalies.append(f"{item_id}: Melee weapon breaks instantly (durability={max_durability})")
                    
            if file == "clothing.csv.gz":
                armor = float(row[9])
                insulation = float(row[10])
                if armor > 50 or insulation > 50: anomalies.append(f"{item_id}: Crazy high armor/insulation ({armor}/{insulation})")
                if max_durability <= 1: anomalies.append(f"{item_id}: Clothing breaks instantly (durability={max_durability})")

print("=== ITEM AUDIT REPORT ===")
print(f"Total Unique Items Checked: {len(all_ids)}")

if duplicate_ids:
    print(f"\nCRITICAL: Found {len(duplicate_ids)} Duplicate IDs!")
    for d in duplicate_ids: print(f" - {d}")
else:
    print("\nNo duplicate IDs found.")

if anomalies:
    print(f"\nFound {len(anomalies)} Potential Balance Anomalies:")
    # Print max 30 to not flood
    for a in anomalies[:30]:
        print(f" - {a}")
    if len(anomalies) > 30:
        print(f" ... and {len(anomalies)-30} more.")
else:
    print("\nNo balance anomalies found! Data looks extremely clean.")
