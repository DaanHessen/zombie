import os
import requests
import csv
import sys
import gc
import time

try:
    import osmium
except ImportError:
    print("Please install osmium: pip install osmium requests")
    sys.exit(1)

PBF_URL = "https://download.geofabrik.de/south-america-latest.osm.pbf"
PBF_FILE = "south_america-latest.osm.pbf"
CSV_FILE = "south_america.csv"

class PoiHandler(osmium.SimpleHandler):
    def __init__(self, writer):
        osmium.SimpleHandler.__init__(self)
        self.writer = writer
        self.count = 0

    def check_tags(self, tags):
        if 'amenity' in tags or 'shop' in tags or 'tourism' in tags or 'emergency' in tags or 'leisure' in tags:
            name = tags.get('name', '')
            category = tags.get('amenity') or tags.get('shop') or tags.get('tourism') or tags.get('emergency') or tags.get('leisure')
            return name, category
        return None, None

    def node(self, n):
        name, category = self.check_tags(n.tags)
        if category:
            self.writer.writerow([n.location.lat, n.location.lon, name, category])
            self.count += 1

    def way(self, w):
        name, category = self.check_tags(w.tags)
        if category:
            try:
                lat = sum([n.lat for n in w.nodes]) / len(w.nodes)
                lon = sum([n.lon for n in w.nodes]) / len(w.nodes)
                self.writer.writerow([lat, lon, name, category])
                self.count += 1
            except Exception:
                pass

def download_file(url, filename):
    print(f"Downloading https://download.geofabrik.de/south-america-latest.osm.pbf...")
    max_retries = 150
    for attempt in range(max_retries):
        try:
            with requests.get(url, stream=True, timeout=60) as r:
                if r.status_code == 429:
                    print("Rate limit hit (HTTP 429). Sleeping for 15 minutes...")
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
                            if downloaded % (1024*1024*500) < (8192*10):  # Log every 500 MB
                                print(f"Downloaded {{downloaded / 1024 / 1024 / 1024:.2f}} GB / {{total_size / 1024 / 1024 / 1024:.2f}} GB")
            print(f"Download complete: {{filename}}")
            return
        except Exception as e:
            print(f"Download interrupted: {{e}}")
            print(f"Retrying in 5 minutes (Attempt {{attempt+1}}/{{max_retries}})...")
            time.sleep(300)
    raise Exception(f"Failed to download {https://download.geofabrik.de/south-america-latest.osm.pbf} after {{max_retries}} attempts.")

def process():
    if not os.path.exists(PBF_FILE):
        download_file(PBF_URL, PBF_FILE)
    else:
        print(f"Found {{PBF_FILE}} locally, skipping download.")
    
    print(f"Extracting POIs (Nodes & Building Polygons) from {{PBF_FILE}}...")
    print("Using NVMe disk index for node locations to prevent 8GB RAM crash!")
    
    with open(CSV_FILE, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['lat', 'lon', 'name', 'category'])
        handler = PoiHandler(writer)
        
        handler.apply_file(PBF_FILE, locations=True, idx='sparse_file_array,nodes_south_america.bin')
        
        print(f"Extracted {{handler.count}} POIs to {{CSV_FILE}}")
        
    print("Cleaning up massive PBF and index files to save NVMe space...")
    if os.path.exists(PBF_FILE):
        os.remove(PBF_FILE)
    if os.path.exists('nodes_south_america.bin'):
        os.remove('nodes_south_america.bin')
    gc.collect()

if __name__ == '__main__':
    process()
