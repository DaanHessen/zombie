import csv
import gzip
import os

try:
    from kgcpy import lookupCZ
except ImportError:
    print("Please install kgcPy: pip install kgcPy")
    exit(1)

koppen_names = {
    'Af': 'Tropical Rainforest', 'Am': 'Tropical Monsoon', 'Aw': 'Tropical Savanna',
    'BWh': 'Hot Desert', 'BWk': 'Cold Desert', 'BSh': 'Hot Semi-Arid', 'BSk': 'Cold Semi-Arid',
    'Csa': 'Hot-summer Mediterranean', 'Csb': 'Warm-summer Mediterranean', 'Csc': 'Cold-summer Mediterranean',
    'Cwa': 'Humid Subtropical', 'Cwb': 'Subtropical Highland', 'Cwc': 'Cold Subtropical Highland',
    'Cfa': 'Humid Subtropical', 'Cfb': 'Temperate Oceanic', 'Cfc': 'Subpolar Oceanic',
    'Dsa': 'Hot-summer Humid Continental', 'Dsb': 'Warm-summer Humid Continental', 'Dsc': 'Subarctic', 'Dsd': 'Extremely Cold Subarctic',
    'Dwa': 'Monsoon Hot-summer Continental', 'Dwb': 'Monsoon Warm-summer Continental', 'Dwc': 'Monsoon Subarctic', 'Dwd': 'Monsoon Extreme Subarctic',
    'Dfa': 'Hot-summer Humid Continental', 'Dfb': 'Warm-summer Humid Continental', 'Dfc': 'Subarctic', 'Dfd': 'Extremely Cold Subarctic',
    'ET': 'Tundra', 'EF': 'Ice Cap'
}

DATA_DIR = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data"
SETTLEMENTS_FILE = os.path.join(DATA_DIR, "settlements.csv.gz")

def main():
    print("Updating settlements with accurate Köppen-Geiger climates...")
    
    rows = []
    with gzip.open(SETTLEMENTS_FILE, 'rt', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        
        # We need to replace 'biome' with 'koppen_code' and 'climate_desc'
        if 'biome' in header:
            biome_idx = header.index('biome')
            header.pop(biome_idx)
            header.insert(biome_idx, 'climate_desc')
            header.insert(biome_idx, 'koppen_code')
        else:
            print("Warning: 'biome' column not found, assuming already updated.")
            return

        lat_idx = header.index('latitude')
        lon_idx = header.index('longitude')

        count = 0
        for row in reader:
            lat = float(row[lat_idx])
            lon = float(row[lon_idx])
            
            # lookupCZ returns a single string like 'Cfb'
            try:
                code = lookupCZ(lat, lon)
            except Exception:
                code = 'Unknown'
                
            desc = koppen_names.get(code, 'Unknown Climate')
            
            # Replace biome with code and desc
            row.pop(biome_idx)
            row.insert(biome_idx, desc)
            row.insert(biome_idx, code)
            
            rows.append(row)
            
            count += 1
            if count % 5000 == 0:
                print(f"Processed {count} cities...")

    print("Writing updated settlements.csv.gz...")
    with gzip.open(SETTLEMENTS_FILE, 'wt', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(header)
        writer.writerows(rows)
        
    print(f"Success! {count} cities updated to true Köppen-Geiger climates.")

if __name__ == "__main__":
    main()
