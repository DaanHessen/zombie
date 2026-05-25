import os
import gzip
import csv
import subprocess
import sys
import time

continents = ["africa", "antarctica", "asia", "europe", "north_america", "oceania", "south_america"]
display    = ["Africa", "Antarctica", "Asia", "Europe", "North America", "Oceania", "South America"]
FINAL_FILE = "locations.csv.gz"

def fmt_time(seconds):
    h = int(seconds // 3600)
    m = int((seconds % 3600) // 60)
    s = int(seconds % 60)
    if h > 0:
        return f"{h}h {m}m {s}s"
    elif m > 0:
        return f"{m}m {s}s"
    return f"{s}s"

def main():
    total_start = time.time()
    
    print()
    print("  +====================================================+")
    print("  |      GLOBAL POI EXTRACTION -- RASPBERRY PI          |")
    print("  +====================================================+")
    print()
    
    completed = 0
    
    for i, cont in enumerate(continents):
        script = f"{cont}.py"
        step_start = time.time()
        
        print(f"  +-- [{i+1}/7] {display[i]}")
        print(f"  |")
        
        try:
            subprocess.run([sys.executable, script], check=True)
            elapsed = time.time() - step_start
            completed += 1
            print(f"  |")
            print(f"  +-- [OK] Done in {fmt_time(elapsed)}")
            print()
        except subprocess.CalledProcessError as e:
            print(f"  |")
            print(f"  +-- [FAIL] {e}")
            print()
            print(f"  Aborting. {completed}/7 continents completed.")
            return

    print()
    print("  +-- Packaging all continents into one file...")
    
    total_pois = 0
    with gzip.open(FINAL_FILE, 'wt', encoding='utf-8', newline='') as f_out:
        writer = csv.writer(f_out)
        writer.writerow(['lat', 'lon', 'name', 'category', 'area_sqm'])
        
        for cont in continents:
            csv_file = f"{cont}.csv"
            if os.path.exists(csv_file):
                with open(csv_file, 'r', encoding='utf-8') as f_in:
                    reader = csv.reader(f_in)
                    next(reader)
                    for row in reader:
                        writer.writerow(row)
                        total_pois += 1
                os.remove(csv_file)

    total_elapsed = time.time() - total_start
    file_mb = os.path.getsize(FINAL_FILE) / 1024 / 1024
    
    print(f"  +-- [OK] Merged all CSVs")
    print()
    print("  +====================================================+")
    print(f"  |  [OK] SUCCESS                                      |")
    print(f"  |  POIs:      {total_pois:>12,}                       |")
    print(f"  |  File size: {file_mb:>9.1f} MB                       |")
    print(f"  |  Time:      {fmt_time(total_elapsed):>12}                       |")
    print("  +====================================================+")
    print()

if __name__ == '__main__':
    main()
