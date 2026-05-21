import os
import urllib.request
import zipfile
import io
import json

# Setup directories
base_dir = os.path.dirname(os.path.abspath(__file__))
output_dir = base_dir

print(f"Target directory: {output_dir}")

# 1. Continents
continents = [
    {"code": "AF", "name": "Africa"},
    {"code": "AN", "name": "Antarctica"},
    {"code": "AS", "name": "Asia"},
    {"code": "EU", "name": "Europe"},
    {"code": "NA", "name": "North America"},
    {"code": "OC", "name": "Oceania"},
    {"code": "SA", "name": "South America"}
]

continents_file = os.path.join(output_dir, "continents.json")
with open(continents_file, "w", encoding="utf-8") as f:
    json.dump(continents, f, indent=2, ensure_ascii=False)
print("Saved continents.json")

# 2. Oceans and Seas
oceans_seas = [
    {"name": "Pacific Ocean", "type": "Ocean"},
    {"name": "Atlantic Ocean", "type": "Ocean"},
    {"name": "Indian Ocean", "type": "Ocean"},
    {"name": "Southern Ocean", "type": "Ocean"},
    {"name": "Arctic Ocean", "type": "Ocean"},
    {"name": "Mediterranean Sea", "type": "Sea"},
    {"name": "Caribbean Sea", "type": "Sea"},
    {"name": "South China Sea", "type": "Sea"},
    {"name": "Bering Sea", "type": "Sea"},
    {"name": "Gulf of Mexico", "type": "Sea"},
    {"name": "Sea of Okhotsk", "type": "Sea"},
    {"name": "East China Sea", "type": "Sea"},
    {"name": "Hudson Bay", "type": "Sea"},
    {"name": "Sea of Japan", "type": "Sea"},
    {"name": "Andaman Sea", "type": "Sea"},
    {"name": "North Sea", "type": "Sea"},
    {"name": "Red Sea", "type": "Sea"},
    {"name": "Baltic Sea", "type": "Sea"},
    {"name": "Tasman Sea", "type": "Sea"},
    {"name": "Celtic Sea", "type": "Sea"},
    {"name": "Coral Sea", "type": "Sea"},
    {"name": "Arabian Sea", "type": "Sea"},
    {"name": "Black Sea", "type": "Sea"},
    {"name": "Caspian Sea", "type": "Sea"}
]

oceans_seas_file = os.path.join(output_dir, "oceans_seas.json")
with open(oceans_seas_file, "w", encoding="utf-8") as f:
    json.dump(oceans_seas, f, indent=2, ensure_ascii=False)
print("Saved oceans_seas.json")

# Download Helper
def download_file(url):
    print(f"Downloading {url}...")
    with urllib.request.urlopen(url) as response:
        return response.read()

# 3. Country Info
country_info_url = "https://download.geonames.org/export/dump/countryInfo.txt"
try:
    country_data = download_file(country_info_url).decode("utf-8")
    countries = []
    
    for line in country_data.splitlines():
        if line.startswith("#") or not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) >= 9:
            try:
                population = int(parts[7])
            except ValueError:
                population = 0
            
            try:
                area = float(parts[6])
            except ValueError:
                area = 0.0
                
            countries.append({
                "iso": parts[0],
                "iso3": parts[1],
                "name": parts[4],
                "capital": parts[5],
                "continent": parts[8],
                "population": population,
                "areaSqKm": area
            })
            
    countries_file = os.path.join(output_dir, "countries.json")
    with open(countries_file, "w", encoding="utf-8") as f:
        json.dump(countries, f, indent=2, ensure_ascii=False)
    print("Downloaded and parsed countries.json")
except Exception as e:
    print(f"Failed to fetch country info: {e}")

# 4. Admin1 Codes (Regions)
admin_url = "https://download.geonames.org/export/dump/admin1CodesASCII.txt"
try:
    admin_data = download_file(admin_url).decode("utf-8")
    regions = []
    
    for line in admin_data.splitlines():
        if not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) >= 4:
            full_code = parts[0]  # e.g., "US.CA" or "NL.07"
            name = parts[1]
            try:
                geoname_id = int(parts[3])
            except ValueError:
                geoname_id = 0
            
            code_parts = full_code.split(".")
            if len(code_parts) == 2:
                regions.append({
                    "countryCode": code_parts[0],
                    "regionCode": code_parts[1],
                    "name": name,
                    "geonameId": geoname_id
                })
                
    regions_file = os.path.join(output_dir, "regions.json")
    with open(regions_file, "w", encoding="utf-8") as f:
        json.dump(regions, f, indent=2, ensure_ascii=False)
    print("Downloaded and parsed regions.json")
except Exception as e:
    print(f"Failed to fetch admin1 codes: {e}")

# 5. Cities (Settlements) > 15,000 population
cities_url = "https://download.geonames.org/export/dump/cities15000.zip"
try:
    zip_data = download_file(cities_url)
    print("Extracting cities15000.zip...")
    with zipfile.ZipFile(io.BytesIO(zip_data)) as z:
        cities_txt = z.read("cities15000.txt").decode("utf-8")
        
    settlements = []
    for line in cities_txt.splitlines():
        if not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) >= 18:
            try:
                geoname_id = int(parts[0])
            except ValueError:
                geoname_id = 0
            
            try:
                lat = float(parts[4])
                lng = float(parts[5])
            except ValueError:
                lat = 0.0
                lng = 0.0
                
            try:
                population = int(parts[14])
            except ValueError:
                population = 0
                
            try:
                elevation = int(parts[15]) if parts[15].strip() else 0
            except ValueError:
                elevation = 0
                
            settlements.append({
                "geonameId": geoname_id,
                "name": parts[1],
                "latitude": lat,
                "longitude": lng,
                "countryCode": parts[8],
                "regionCode": parts[10],
                "population": population,
                "elevation": elevation,
                "timezone": parts[17]
            })
            
    settlements_file = os.path.join(output_dir, "settlements.json")
    with open(settlements_file, "w", encoding="utf-8") as f:
        json.dump(settlements, f, indent=2, ensure_ascii=False)
    print("Downloaded and parsed settlements.json")
except Exception as e:
    print(f"Failed to fetch and parse cities: {e}")

# Cleanup old text files
txt_files = ["continents.txt", "oceans_seas.txt", "countries.txt", "regions.txt", "settlements.txt"]
for tf in txt_files:
    path = os.path.join(output_dir, tf)
    if os.path.exists(path):
        os.remove(path)
        print(f"Deleted obsolete file: {tf}")

print("GeoNames JSON extraction complete!")
