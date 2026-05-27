import csv
import gzip
import os

weapon_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items\weapons.csv.gz"
ammo_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items\ammo.csv.gz"

weapon_ammo_reqs = set()
weapon_types = []

print("--- WEAPONS ---")
with gzip.open(weapon_file, 'rt', newline='', encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader) # skip header
    for row in reader:
        name = row[1]
        w_type = row[4]
        ammo = row[12]
        if w_type == "RANGED_WEAPON":
            weapon_types.append(f"{name} -> {ammo}")
            if ammo != "NONE":
                weapon_ammo_reqs.add(ammo)
            
for w in weapon_types[:20]:
    print(w)
print(f"... and {len(weapon_types) - 20} more ranged weapons.")

print("\n--- AVAILABLE AMMO ---")
available_ammo = set()
with gzip.open(ammo_file, 'rt', newline='', encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        a_id = row[0]
        name = row[1]
        available_ammo.add(a_id)
        if a_id in weapon_ammo_reqs or a_id.startswith('item_ammo'):
            print(f"{a_id} ({name})")

print("\n--- MISSING AMMO CHECKS ---")
for req in weapon_ammo_reqs:
    if req not in available_ammo:
        print(f"MISSING IN AMMO.CSV: {req}")
