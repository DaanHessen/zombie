import os
import requests
import csv
import sys
import gc
import time
import math

try:
    import osmium
except ImportError:
    print("Please install osmium: pip install osmium requests")
    sys.exit(1)

PBF_URL = "https://download.geofabrik.de/north-america-latest.osm.pbf"
PBF_FILE = "north_america-latest.osm.pbf"
CSV_FILE = "north_america.csv"
CONTINENT = "North America"
SPINNER = ["|", "/", "-", "\\"]

def progress_bar(current, total, prefix="", bar_len=40):
    if total == 0:
        return
    pct = current / total
    filled = int(bar_len * pct)
    bar = "#" * filled + "-" * (bar_len - filled)
    cur_mb = current / 1024 / 1024
    tot_mb = total / 1024 / 1024
    if tot_mb >= 1024:
        size_str = f"{cur_mb/1024:.2f}/{tot_mb/1024:.2f} GB"
    else:
        size_str = f"{cur_mb:.1f}/{tot_mb:.1f} MB"
    sys.stdout.write(f"\r  {prefix} [{bar}] {pct*100:5.1f}% {size_str}  ")
    sys.stdout.flush()

class PoiHandler(osmium.SimpleHandler):
    def __init__(self, writer):
        osmium.SimpleHandler.__init__(self)
        self.writer = writer
        self.count = 0
        self.last_print = 0

    def _print_progress(self):
        now = time.time()
        if now - self.last_print > 0.3:
            spin = SPINNER[self.count % len(SPINNER)]
            sys.stdout.write(f"\r  Extracting {CONTINENT}... {spin} {self.count:,} POIs found  ")
            sys.stdout.flush()
            self.last_print = now

    def check_tags(self, tags):
        if 'amenity' in tags or 'shop' in tags or 'tourism' in tags or 'emergency' in tags or 'leisure' in tags:
            name = tags.get('name', '')
            category = tags.get('amenity') or tags.get('shop') or tags.get('tourism') or tags.get('emergency') or tags.get('leisure')
            return name, category
        return None, None

    def node(self, n):
        name, category = self.check_tags(n.tags)
        if category:
            self.writer.writerow([n.location.lat, n.location.lon, name, category, 0.0])
            self.count += 1
            self._print_progress()

    def way(self, w):
        name, category = self.check_tags(w.tags)
        if category:
            try:
                nodes = list(w.nodes)
                if len(nodes) < 3:
                    return
                
                lat = sum([n.lat for n in nodes]) / len(nodes)
                lon = sum([n.lon for n in nodes]) / len(nodes)
                
                area = 0.0
                for i in range(len(nodes)):
                    j = (i + 1) % len(nodes)
                    n1 = nodes[i]
                    n2 = nodes[j]
                    area += (n1.lon * n2.lat) - (n2.lon * n1.lat)
                
                lat_rad = math.radians(nodes[0].lat)
                sqm = abs(area / 2.0) * 111320.0 * (111320.0 * math.cos(lat_rad))
                
                self.writer.writerow([lat, lon, name, category, round(sqm, 2)])
                self.count += 1
                self._print_progress()
            except Exception:
                pass

def download_file(url, filename):
    max_retries = 150
    for attempt in range(max_retries):
        try:
            with requests.get(url, stream=True, timeout=60) as r:
                if r.status_code == 429:
                    print("\n  [!] Rate limited. Sleeping 15 min...")
                    time.sleep(900)
                    continue
                r.raise_for_status()
                total_size = int(r.headers.get('content-length', 0))
                downloaded = 0
                with open(filename, 'wb') as f:
                    for chunk in r.iter_content(chunk_size=8192*10):
                        if chunk:
                            f.write(chunk)
                            downloaded += len(chunk)
                            progress_bar(downloaded, total_size, prefix="Downloading")
            print(f"\n  [OK] Download complete: {filename}")
            return
        except Exception as e:
            print(f"\n  [!!] Download error: {e}")
            print(f"       Retrying in 5 min ({attempt+1}/{max_retries})...")
            time.sleep(300)
    raise Exception(f"Failed to download after {max_retries} attempts.")

def process():
    if not os.path.exists(PBF_FILE):
        download_file(PBF_URL, PBF_FILE)
    else:
        print(f"  [OK] Found {PBF_FILE} locally, skipping download.")
    
    with open(CSV_FILE, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['lat', 'lon', 'name', 'category', 'area_sqm'])
        handler = PoiHandler(writer)
        
        handler.apply_file(PBF_FILE, locations=True, idx='sparse_file_array,nodes_north_america.bin')
        
        print(f"\n  [OK] Extracted {handler.count:,} POIs -> {CSV_FILE}")
        
    if os.path.exists(PBF_FILE):
        os.remove(PBF_FILE)
    if os.path.exists('nodes_north_america.bin'):
        os.remove('nodes_north_america.bin')
    gc.collect()

if __name__ == '__main__':
    process()
