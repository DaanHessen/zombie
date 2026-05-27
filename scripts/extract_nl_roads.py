import os
import csv
import sys
import time
import gzip
import requests
import math

try:
    import osmium
except ImportError:
    print("Please install osmium: pip install osmium requests")
    sys.exit(1)

PBF_URL = "https://download.geofabrik.de/europe/netherlands-latest.osm.pbf"
PBF_FILE = "netherlands-latest.osm.pbf"
CSV_FILE = "netherlands_roads.csv.gz"
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

def download_file(url, filename):
    max_retries = 150
    for attempt in range(max_retries):
        try:
            with requests.get(url, stream=True, timeout=60) as r:
                r.raise_for_status()
                total_size = int(r.headers.get('content-length', 0))
                downloaded = 0
                with open(filename, 'wb') as f:
                    for chunk in r.iter_content(chunk_size=8192*10):
                        if chunk:
                            f.write(chunk)
                            downloaded += len(chunk)
                            progress_bar(downloaded, total_size, prefix="Downloading NL PBF")
            print(f"\n  [OK] Download complete: {filename}")
            return
        except Exception as e:
            print(f"\n  [!!] Download error: {e}")
            time.sleep(10)

class RoadHandler(osmium.SimpleHandler):
    def __init__(self, writer):
        osmium.SimpleHandler.__init__(self)
        self.writer = writer
        self.count = 0
        self.last_print = 0

    def _print_progress(self):
        now = time.time()
        if now - self.last_print > 0.3:
            spin = SPINNER[self.count % len(SPINNER)]
            sys.stdout.write(f"\r  Extracting NL Roads... {spin} {self.count:,} roads found  ")
            sys.stdout.flush()
            self.last_print = now

    def way(self, w):
        highway = w.tags.get('highway')
        if highway:
            name = w.tags.get('name', '')
            surface = w.tags.get('surface', 'unknown')
            
            try:
                nodes = list(w.nodes)
                if len(nodes) < 2:
                    return
                
                lat = sum([n.lat for n in nodes]) / len(nodes)
                lon = sum([n.lon for n in nodes]) / len(nodes)
                
                coords = "|".join([f"{n.lat:.5f},{n.lon:.5f}" for n in nodes])
                
                self.writer.writerow([round(lat, 5), round(lon, 5), name, highway, surface, coords])
                self.count += 1
                self._print_progress()
            except Exception:
                pass

def process():
    start = time.time()
    print("\n  +====================================================+")
    print("  |      NETHERLANDS ROAD EXTRACTION (Local Dev)        |")
    print("  +====================================================+\n")

    if not os.path.exists(PBF_FILE):
        download_file(PBF_URL, PBF_FILE)
    else:
        print(f"  [OK] Found {PBF_FILE} locally.")

    with gzip.open(CSV_FILE, 'wt', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['center_lat', 'center_lon', 'name', 'highway_type', 'surface', 'coordinates'])
        handler = RoadHandler(writer)
        
        handler.apply_file(PBF_FILE, locations=True)
        
        print(f"\n  [OK] Extracted {handler.count:,} Roads -> {CSV_FILE}")

    if os.path.exists(PBF_FILE):
        os.remove(PBF_FILE)

    elapsed = time.time() - start
    m, s = divmod(elapsed, 60)
    file_mb = os.path.getsize(CSV_FILE) / 1024 / 1024

    print(f"\n  +====================================================+")
    print(f"  |  [OK] SUCCESS                                      |")
    print(f"  |  Roads:      {handler.count:>12,}                       |")
    print(f"  |  File size:  {file_mb:>9.1f} MB                       |")
    print(f"  |  Time:       {int(m)}m {int(s)}s                                |")
    print("  +====================================================+\n")

if __name__ == '__main__':
    process()
