import os
import gzip
import csv
import subprocess

continents = ["africa", "antarctica", "asia", "europe", "north_america", "oceania", "south_america"]
FINAL_FILE = "locations.csv.gz"

def main():
    print("==================================================")
    print("STARTING GLOBAL POI EXTRACTION FOR RASPBERRY PI")
    print("==================================================")
    
    for cont in continents:
        script = f"{cont}.py"
        print(f"\n---> Running {script}...")
        try:
            # We use python3 explicitly since it's Raspberry Pi OS (Linux)
            subprocess.run(["python3", script], check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error running {script}: {e}")
            print("Aborting global run.")
            return

    print("\n==================================================")
    print("ALL CONTINENTS PROCESSED. PACKAGING INTO ONE FILE.")
    print("==================================================")
    
    total_pois = 0
    with gzip.open(FINAL_FILE, 'wt', encoding='utf-8', newline='') as f_out:
        writer = csv.writer(f_out)
        writer.writerow(['lat', 'lon', 'name', 'category'])
        
        for cont in continents:
            csv_file = f"{cont}.csv"
            if os.path.exists(csv_file):
                print(f"Merging {csv_file}...")
                with open(csv_file, 'r', encoding='utf-8') as f_in:
                    reader = csv.reader(f_in)
                    next(reader) # Skip header
                    for row in reader:
                        writer.writerow(row)
                        total_pois += 1
                # Clean up the individual CSV to save space
                os.remove(csv_file)
            else:
                print(f"Warning: {csv_file} not found. Skipping merge.")

    print("\n==================================================")
    print(f"SUCCESS! Packaged {total_pois} global POIs into {FINAL_FILE}")
    print(f"Final file size: {os.path.getsize(FINAL_FILE) / 1024 / 1024:.2f} MB")
    print("You can now copy locations.csv.gz to your project root!")
    print("==================================================")

if __name__ == '__main__':
    main()
