import csv
import gzip
import os

input_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items.csv"
output_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items.csv.gz"

fast_spoil = ['fresh', 'meat', 'milk', 'cheese', 'fruit', 'vegetable', 'apple', 'banana', 'beef', 'chicken', 'pork']
medium_spoil = ['bread', 'baguette', 'muffin', 'cake', 'pie']
long_spoil = ['canned', 'can', 'water', 'bottle', 'soda', 'alcohol', 'wine', 'beer', 'whiskey', 'chips', 'snack', 'candy', 'chocolate', 'jerky']

with open(input_file, 'r', newline='', encoding='utf-8') as in_f:
    reader = csv.reader(in_f)
    header = next(reader, None)
    
    if header:
        header.append("expiration_days")
    
    rows = [header] if header else []
    
    for row in reader:
        # id,name,description,category,type,weight,volume,maxStackSize,maxDurability
        name = row[1].lower()
        desc = row[2].lower()
        category = row[3]
        
        expiration_days = "-1" # Infinite by default (crowbars, bandages, etc)
        
        if category == "FOOD":
            if any(word in name or word in desc for word in long_spoil):
                expiration_days = "3650" # 10 years
            elif any(word in name or word in desc for word in fast_spoil):
                expiration_days = "5" # 5 days
            elif any(word in name or word in desc for word in medium_spoil):
                expiration_days = "14" # 2 weeks
            else:
                # Default for unknown food
                expiration_days = "30" 
                
        row.append(expiration_days)
        rows.append(row)

# Write to gzipped CSV
with gzip.open(output_file, 'wt', newline='', encoding='utf-8') as out_f:
    writer = csv.writer(out_f)
    writer.writerows(rows)

# Delete the uncompressed file
os.remove(input_file)

print(f"Successfully added expiration_days and compressed to {output_file}")
