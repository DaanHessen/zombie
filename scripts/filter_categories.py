import csv
import gzip
import os
import sys
from collections import Counter

# Categories that are utterly useless for a zombie survival game loot table
DISCARD_CATEGORIES = {
    'bench', 'waste_basket', 'waste_basket3', 'post_box', 'letter_box', 'information', 'map', 
    'guidepost', 'board', 'sign', 'signs', 'parking', 'parking_space', 'bicycle_parking', 
    'motorcycle_parking', 'scooter_parking', 'parking_entrance', 'parking_exit', 'bus_stop', 
    'tram_stop', 'taxi', 'tree', 'grass', 'bushes', 'street_lamp', 'traffic_signals', 
    'fire_hydrant', 'fountain', 'pitch', 'playground', 'artwork', 'monument', 'memorial',
    'picnic_table', 'picnic_site', 'viewpoint', 'shelter', 'toilet', 'toilets', 'drinking_water',
    'water_point', 'hunting_stand', 'recycling', 'waste_disposal', 'telephone', 'atm', 
    'vending_machine', 'parcel_locker', 'clock', 'camera_surveillance', 'charging_station',
    'car_sharing', 'bicycle_rental', 'boat_rental', 'speed_camera', 'flagpole', 'crossing',
    'level_crossing', 'elevator', 'escalator', 'stairs', 'ferry_terminal', 'power_pole',
    'power_tower', 'power_box', 'transformer', 'switchgear', 'manhole', 'drain', 'water_well'
}

def main(filepath):
    if not os.path.exists(filepath):
        print(f"File not found: {filepath}")
        return

    is_gzipped = filepath.endswith('.gz')
    open_func = gzip.open if is_gzipped else open
    mode = 'rt' if is_gzipped else 'r'

    useful_counts = Counter()
    discarded_count = 0
    total_count = 0

    print(f"Reading {filepath}...")
    with open_func(filepath, mode, encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        try:
            cat_idx = header.index("category")
        except ValueError:
            print("No 'category' column found!")
            return

        for row in reader:
            if len(row) > cat_idx:
                cat = row[cat_idx]
                total_count += 1
                if cat in DISCARD_CATEGORIES:
                    discarded_count += 1
                else:
                    useful_counts[cat] += 1

    print(f"\nTotal POIs processed: {total_count:,}")
    print(f"Total Discarded POIs: {discarded_count:,} ({(discarded_count/total_count)*100:.1f}%)")
    print(f"Unique Useful Categories: {len(useful_counts):,}\n")
    
    print("--- TOP 100 USEFUL CATEGORIES ---")
    print(f"{'Category':<40} | {'Count':>10}")
    print("-" * 53)
    for cat, count in useful_counts.most_common(100):
        print(f"{cat:<40} | {count:>10,}")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        main(sys.argv[1])
    else:
        # Default to local Netherlands data if no arg provided
        main(r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw_regions\netherlands_pois.csv.gz")
