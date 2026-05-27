import csv
import gzip
import os
import re

weapons_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items\weapons.csv.gz"
ammo_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items\ammo.csv.gz"

def determine_caliber_from_string(text):
    text = text.lower()
    if '9mm' in text: return "9mm"
    if '5.56' in text or 'm193' in text or 'm855' in text: return "5.56x45mm"
    if '7.62x39' in text: return "7.62x39mm"
    if '7.62x54' in text: return "7.62x54mmR"
    if '5.45' in text: return "5.45x39mm"
    if '308' in text: return ".308 Win"
    if '30-06' in text or '3006' in text: return ".30-06 Springfield"
    if '300blk' in text or '300 blackout' in text: return ".300 Blackout"
    if '45acp' in text or '45 acp' in text: return ".45 ACP"
    if '380acp' in text or '380 acp' in text: return ".380 ACP"
    if '10mm' in text: return "10mm Auto"
    if '40sw' in text or '40 s&w' in text: return ".40 S&W"
    if '357mag' in text or '357 mag' in text: return ".357 Magnum"
    if '44mag' in text or '44 mag' in text: return ".44 Magnum"
    if '38sp' in text or '38 special' in text: return ".38 Special"
    if '12g' in text or '12 gauge' in text: return "12 Gauge"
    if '20g' in text or '20 gauge' in text: return "20 Gauge"
    if '410' in text: return ".410 Bore"
    if '22lr' in text or '22 lr' in text: return ".22 LR"
    if '57_' in text or '5.7x28' in text: return "5.7x28mm"
    if '46_' in text or '4.6x30' in text: return "4.6x30mm"
    if '338' in text: return ".338 Lapua"
    if '50bmg' in text or '50 bmg' in text: return ".50 BMG"
    if 'arrow' in text or 'bow' in text: return "Arrow"
    if 'bolt' in text or 'crossbow' in text: return "Crossbow Bolt"
    if '40mm' in text: return "40mm Grenade"
    if 'rpg' in text: return "RPG-7 Rocket"
    
    return "NONE"

# Process Ammo
ammo_rows = []
ammo_header = []
with gzip.open(ammo_file, 'rt', newline='', encoding='utf-8') as f:
    reader = csv.reader(f)
    ammo_header = next(reader)
    if 'caliber' not in ammo_header:
        ammo_header.append('caliber')
    for row in reader:
        # id is row[0], name is row[1]
        caliber = determine_caliber_from_string(row[0] + " " + row[1])
        if len(row) < len(ammo_header):
            row.append(caliber)
        else:
            row[len(ammo_header)-1] = caliber
        ammo_rows.append(row)

with gzip.open(ammo_file, 'wt', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(ammo_header)
    writer.writerows(ammo_rows)

print(f"Updated {ammo_file} with caliber field.")

# Process Weapons
weapon_rows = []
weapon_header = []
with gzip.open(weapons_file, 'rt', newline='', encoding='utf-8') as f:
    reader = csv.reader(f)
    weapon_header = next(reader)
    # Find ammoType column and rename to caliber
    col_idx = -1
    if 'ammoType' in weapon_header:
        col_idx = weapon_header.index('ammoType')
        weapon_header[col_idx] = 'caliber'
    elif 'caliber' in weapon_header:
        col_idx = weapon_header.index('caliber')
        
    for row in reader:
        name = row[1]
        type_ = row[4]
        if type_ == "RANGED_WEAPON":
            caliber = determine_caliber_from_string(name)
            if caliber == "NONE":
                # Fallback to generic assignments based on name
                if 'pistol' in name.lower() or 'glock' in name.lower(): caliber = '9mm'
                elif 'rifle' in name.lower() or 'ak' in name.lower(): caliber = '5.56x45mm'
                elif 'shotgun' in name.lower(): caliber = '12 Gauge'
                elif 'revolver' in name.lower(): caliber = '.357 Magnum'
            
            row[col_idx] = caliber
        else:
            row[col_idx] = "NONE"
        weapon_rows.append(row)

with gzip.open(weapons_file, 'wt', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(weapon_header)
    writer.writerows(weapon_rows)

print(f"Updated {weapons_file} to map calibers to weapons.")
