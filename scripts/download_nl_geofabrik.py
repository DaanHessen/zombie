import os
import requests
import gzip
import csv
import sys
try:
    import osmium
except ImportError:
    print("Please install osmium: pip install osmium")
    sys.exit(1)

PBF_URL = "https://download.geofabrik.de/europe/netherlands-latest.osm.pbf"
PBF_FILE = "netherlands-latest.osm.pbf"
CSV_FILE = "netherlands_pois.csv"
GZ_FILE = "netherlands_pois.csv.gz"

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
                # Calculate the centroid of the building polygon
                lat = sum([n.lat for n in w.nodes]) / len(w.nodes)
                lon = sum([n.lon for n in w.nodes]) / len(w.nodes)
                self.writer.writerow([lat, lon, name, category])
                self.count += 1
            except Exception:
                pass

def download_file(url, filename):
    print(f"Downloading {url}...")
    with requests.get(url, stream=True) as r:
        r.raise_for_status()
        total_size = int(r.headers.get('content-length', 0))
        downloaded = 0
        with open(filename, 'wb') as f:
            for chunk in r.iter_content(chunk_size=8192*10):
                if chunk:
                    f.write(chunk)
                    downloaded += len(chunk)
                    if downloaded % (50*1024*1024) < (8192*10):
                        print(f"Downloaded {downloaded / 1024 / 1024:.0f} MB / {total_size / 1024 / 1024:.0f} MB")
    print(f"Download complete: {filename}")

if __name__ == '__main__':
    if not os.path.exists(PBF_FILE):
        download_file(PBF_URL, PBF_FILE)
    else:
        print(f"Found {PBF_FILE} locally, skipping download.")
    
    print(f"Extracting POIs (Nodes & Building Polygons) from {PBF_FILE}...")
    print(f"This requires caching node locations (uses ~1.5GB RAM) and takes about 60 seconds...")
    with open(CSV_FILE, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['lat', 'lon', 'name', 'category'])
        handler = PoiHandler(writer)
        
        # locations=True caches the coordinates so we can get the center of building polygons!
        handler.apply_file(PBF_FILE, locations=True)
        
        print(f"Extracted {handler.count} POIs to {CSV_FILE}")
        
    print(f"Compressing to {GZ_FILE}...")
    with open(CSV_FILE, 'rb') as f_in:
        with gzip.open(GZ_FILE, 'wb') as f_out:
            f_out.writelines(f_in)
            
    print(f"\nSUCCESS!")
    print(f"Raw Geofabrik PBF size: {os.path.getsize(PBF_FILE) / 1024 / 1024:.2f} MB")
    print(f"Extracted CSV Size: {os.path.getsize(CSV_FILE) / 1024 / 1024:.2f} MB")
    print(f"Final Compressed GZ Size: {os.path.getsize(GZ_FILE) / 1024 / 1024:.2f} MB")
