import csv
import gzip
import os
import random

input_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items.csv.gz"
output_dir = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items"

os.makedirs(output_dir, exist_ok=True)

# Base fields: id,name,description,category,type,weight,volume,maxStackSize,maxDurability,expiration_days (from our previous script)
def process_food(row):
    # base fields + calories, protein, carbs, hydration, expirationDays
    base = row[:9]
    expiration = row[9] # From previous script
    name = row[1].lower()
    
    calories = random.randint(100, 800)
    protein = random.randint(0, 50)
    carbs = random.randint(10, 100)
    hydration = 0
    if 'water' in name or 'soda' in name or 'alcohol' in name or 'beer' in name:
        hydration = random.randint(20, 100)
        
    return base + [calories, protein, carbs, hydration, expiration]

def process_weapon(row):
    # base fields + damage, durabilityLossPerUse, jamChance, ammoType, noiseRadius
    base = row[:9]
    name = row[1].lower()
    type_ = row[4] # MELEE_WEAPON or RANGED_WEAPON
    
    damage = round(random.uniform(5.0, 50.0), 1)
    durability_loss = random.randint(1, 5)
    jam_chance = 0.0
    ammo_type = "NONE"
    noise = random.randint(2, 10)
    
    if type_ == "RANGED_WEAPON":
        jam_chance = round(random.uniform(0.01, 0.05), 3)
        noise = random.randint(50, 200)
        if '9mm' in name or 'pistol' in name: ammo_type = 'item_ammo_9mm'
        elif 'shotgun' in name: ammo_type = 'item_ammo_12g'
        elif 'rifle' in name: ammo_type = 'item_ammo_556'
        else: ammo_type = 'item_ammo_generic'
        
    return base + [damage, durability_loss, jam_chance, ammo_type, noise]

def process_clothing(row):
    # base fields + armorRating, insulation, bonusInventorySlots
    base = row[:9]
    name = row[1].lower()
    
    armor = round(random.uniform(0.0, 5.0), 1)
    insulation = round(random.uniform(0.0, 3.0), 1)
    bonus_slots = 0
    
    if 'backpack' in name or 'bag' in name:
        bonus_slots = random.randint(10, 30)
    if 'kevlar' in name or 'helmet' in name or 'vest' in name:
        armor = round(random.uniform(5.0, 15.0), 1)
        
    return base + [armor, insulation, bonus_slots]

def process_medicine(row):
    # base fields + healthRestored, curesInfection
    base = row[:9]
    name = row[1].lower()
    
    health = round(random.uniform(5.0, 30.0), 1)
    cures = "false"
    
    if 'antibiotic' in name:
        cures = "true"
        health = 0.0
        
    return base + [health, cures]

# File handles
files = {
    'FOOD': {'path': os.path.join(output_dir, 'food.csv.gz'), 'header': ['id','name','description','category','type','weight','volume','maxStackSize','maxDurability','calories','protein','carbs','hydration','expirationDays'], 'rows': []},
    'WEAPON': {'path': os.path.join(output_dir, 'weapons.csv.gz'), 'header': ['id','name','description','category','type','weight','volume','maxStackSize','maxDurability','damage','durabilityLossPerUse','jamChance','ammoType','noiseRadius'], 'rows': []},
    'CLOTHING': {'path': os.path.join(output_dir, 'clothing.csv.gz'), 'header': ['id','name','description','category','type','weight','volume','maxStackSize','maxDurability','armorRating','insulation','bonusInventorySlots'], 'rows': []},
    'MEDICINE': {'path': os.path.join(output_dir, 'medicine.csv.gz'), 'header': ['id','name','description','category','type','weight','volume','maxStackSize','maxDurability','healthRestored','curesInfection'], 'rows': []},
    'AMMO': {'path': os.path.join(output_dir, 'ammo.csv.gz'), 'header': ['id','name','description','category','type','weight','volume','maxStackSize','maxDurability'], 'rows': []},
    'RESOURCE': {'path': os.path.join(output_dir, 'resources.csv.gz'), 'header': ['id','name','description','category','type','weight','volume','maxStackSize','maxDurability'], 'rows': []}
}

if not os.path.exists(input_file):
    print("Could not find input file.")
else:
    with gzip.open(input_file, 'rt', newline='', encoding='utf-8') as in_f:
        reader = csv.reader(in_f)
        header = next(reader, None)
        
        for row in reader:
            if len(row) < 9: continue
            
            category = row[3].upper()
            
            if category == 'FOOD': files['FOOD']['rows'].append(process_food(row))
            elif category == 'WEAPON': files['WEAPON']['rows'].append(process_weapon(row))
            elif category == 'CLOTHING': files['CLOTHING']['rows'].append(process_clothing(row))
            elif category == 'MEDICINE': files['MEDICINE']['rows'].append(process_medicine(row))
            elif category == 'AMMO': files['AMMO']['rows'].append(row[:9])
            elif category == 'RESOURCE': files['RESOURCE']['rows'].append(row[:9])
            else:
                pass # ignore unknown

    # Write split files
    for cat, data in files.items():
        with gzip.open(data['path'], 'wt', newline='', encoding='utf-8') as out_f:
            writer = csv.writer(out_f)
            writer.writerow(data['header'])
            writer.writerows(data['rows'])

    # Safely remove the monolithic file
    os.remove(input_file)
    print(f"Successfully split database into {output_dir}")
