import os
import csv
import gzip
import uuid
import math
import random
import urllib.request
import urllib.parse
import json
from datetime import datetime

# Set up directories
base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
source_dir = os.path.join(base_dir, "scripts", "source_data")
output_dir = os.path.join(base_dir, "src", "main", "resources", "data")
preview_dir = os.path.join(output_dir, "dev_preview")

os.makedirs(output_dir, exist_ok=True)
os.makedirs(preview_dir, exist_ok=True)

# Helper: Deterministic UUID generation
def get_continent_uuid(code):
    return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"continent_{code.upper()}"))

def get_country_uuid(code):
    return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"country_{code.upper()}"))

def get_region_uuid(country_code, region_code):
    return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"region_{country_code.upper()}_{region_code.upper()}"))

def get_settlement_uuid(geoname_id):
    return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"settlement_{geoname_id}"))

# Helper: Language classification
def determine_language(country_code):
    if not country_code:
        return "EN"
    cc = country_code.upper()
    if cc in ["RU", "BY", "KZ"]:
        return "RU"
    if cc == "NL":
        return "NL"
    if cc in ["DE", "AT", "CH"]:
        return "DE"
    if cc in ["FR", "BE"]:
        return "FR"
    if cc in ["ES", "MX", "AR", "CO", "CL", "PE"]:
        return "ES"
    return "EN"

# Helper: Localized naming templates
STREET_NAMES = {
    "NL": ["Hoogstraat", "Kerkstraat", "Stationsstraat", "Dorpsstraat", "Molenweg", "Schoolstraat", "Nieuwstraat", "Bosweg", "Spoorstraat", "Parklaan", "Koninginneweg", "Wilhelminastraat", "Markt", "Dijkstraat", "Bergweg", "Veldweg", "Singel", "Bredaseweg", "Utrechtsestraat", "Prinsenstraat"],
    "DE": ["Hauptstraße", "Bahnhofstraße", "Schulstraße", "Gartenstraße", "Waldweg", "Poststraße", "Lindenstraße", "Schillerstraße", "Goethestraße", "Kirchweg", "Rathausstraße", "Ringstraße", "Feldstraße", "Bergstraße", "Mühlenweg", "Jahnstraße", "Markt", "Kaiserstraße", "Ludwigstraße", "Mozartstraße"],
    "FR": ["Rue de la Gare", "Grande Rue", "Rue de la Mairie", "Rue des Écoles", "Rue du Château", "Rue Pasteur", "Rue Victor Hugo", "Rue de l'Église", "Rue Jean Jaurès", "Avenue de la République", "Rue du Moulin", "Rue de Verdun", "Avenue Charles de Gaulle", "Rue de Paris", "Rue Lafayette"],
    "ES": ["Calle Mayor", "Avenida de la Constitución", "Calle Real", "Calle de la Iglesia", "Plaza Mayor", "Calle San José", "Avenida de Madrid", "Calle de Santiago", "Calle Nueva", "Paseo de la Castellana", "Calle de la Paz", "Calle del Sol", "Avenida de Andalucía", "Calle del Carmen"],
    "RU": ["улица Ленина", "Советская улица", "Садовая улица", "улица Гагарина", "Лесной переулок", "улица Мира", "Молодёжная улица", "улица Пушкина", "Комсомольская улица", "Школьная улица", "улица Кирова", "Зелёная улица", "Набережная улица", "улица Чехова"],
    "EN": ["Main Street", "High Street", "Church Road", "Station Road", "Oak Avenue", "Pine Street", "Broadway", "Park Lane", "Elm Street", "Maple Drive", "Cedar Road", "Washington Street", "Market Street", "View Street", "Bridge Road", "King Street", "Victoria Road", "Mill Lane"]
}

POI_NAMES = {
    "NL": {
        "HOSPITAL": ["{city} Ziekenhuis", "St. {saint} Gasthuis"],
        "POLICE": ["Politiebureau {city}", "Politiepost {city}"],
        "FIRE_STATION": ["Brandweerkazerne {city}"],
        "SCHOOL": ["Rijksarchief School", "De {tree} Basisschool", "{city} College"],
        "SUPERMARKET": ["Albert Heijn {city}", "Jumbo {city}", "Aldi {city}"],
        "PHARMACY": ["Apotheek {city}", "Stadsapotheek"],
        "PRISON": ["Penitentiaire Inrichting {city}"],
        "PARK": ["Vondelpark", "{city} Bos", "Stadspark {city}"]
    },
    "DE": {
        "HOSPITAL": ["Krankenhaus {city}", "St. {saint} Klinikum"],
        "POLICE": ["Polizeiwache {city}", "Polizeirevier"],
        "FIRE_STATION": ["Feuerwehrhaus {city}", "Freiwillige Feuerwehr"],
        "SCHOOL": ["Goethe-Gymnasium", "Schiller-Grundschule", "{city} Realschule"],
        "SUPERMARKET": ["Edeka {city}", "Rewe {city}", "Aldi Süd"],
        "PHARMACY": ["Rathaus-Apotheke", "Linden-Apotheke"],
        "PRISON": ["Justizvollzugsanstalt {city}"],
        "PARK": ["Schlosspark", "Stadtgarten {city}", "Kurpark"]
    },
    "FR": {
        "HOSPITAL": ["Hôpital de {city}", "Clinique Saint-{saint}"],
        "POLICE": ["Commissariat de Police", "Gendarmerie Nationale"],
        "FIRE_STATION": ["Caserne de Pompiers de {city}"],
        "SCHOOL": ["École Primaire Pasteur", "Lycée Victor Hugo", "Collège Jean Jaurès"],
        "SUPERMARKET": ["Carrefour {city}", "Leclerc {city}", "Auchan"],
        "PHARMACY": ["Pharmacie de la Mairie", "Pharmacie Centrale"],
        "PRISON": ["Maison d'Arrêt de {city}"],
        "PARK": ["Jardin Public", "Parc Municipal de {city}", "Bois de {city}"]
    },
    "ES": {
        "HOSPITAL": ["Hospital General de {city}", "Hospital Universitario"],
        "POLICE": ["Comisaría de Policía Nacional", "Cuartel de la Guardia Civil"],
        "FIRE_STATION": ["Parque de Bomberos de {city}"],
        "SCHOOL": ["Colegio Público San {saint}", "Instituto de Educación Secundaria {city}"],
        "SUPERMARKET": ["Mercadona {city}", "Carrefour Express", "Dia"],
        "PHARMACY": ["Farmacia de Guardia", "Farmacia Principal"],
        "PRISON": ["Centro Penitenciario de {city}"],
        "PARK": ["Parque Municipal", "Jardines de {city}", "Plaza de la Constitución"]
    },
    "RU": {
        "HOSPITAL": ["Городская больница №1", "Центральная клиническая больница"],
        "POLICE": ["Отдел полиции №3", "УВД по городу {city}"],
        "FIRE_STATION": ["Пожарная часть №12"],
        "SCHOOL": ["Средняя школа №5", "Гимназия имени Пушкина"],
        "SUPERMARKET": ["Магнит", "Пятёрочка", "Перекрёсток"],
        "PHARMACY": ["Аптека низких цен", "Городская аптека"],
        "PRISON": ["СИЗО №1"],
        "PARK": ["Парк культуры и отдыха", "Городской сад"]
    },
    "EN": {
        "HOSPITAL": ["{city} General Hospital", "Saint {saint}'s Medical Center"],
        "POLICE": ["{city} Police Department", "Sheriff's Office"],
        "FIRE_STATION": ["{city} Fire Station"],
        "SCHOOL": ["{city} High School", "{tree} Elementary School", "Saint {saint}'s Academy"],
        "SUPERMARKET": ["Tesco {city}", "Walmart {city}", "Sainsbury's", "Kroger"],
        "PHARMACY": ["CVS Pharmacy", "Walgreens", "Boots"],
        "PRISON": ["{city} Correctional Facility", "County Jail"],
        "PARK": ["Memorial Park", "{city} Common", "{tree} Park"]
    }
}

TREES = ["Oak", "Pine", "Maple", "Cedar", "Birch", "Elm", "Linden", "Willow", "Ash"]
SAINTS = ["Jude", "Mary", "George", "Luke", "Paul", "John", "Andrew", "Francis", "Joseph"]

def generate_local_name(poi_type, lang, city):
    lang_dict = POI_NAMES.get(lang, POI_NAMES["EN"])
    templates = lang_dict.get(poi_type, POI_NAMES["EN"][poi_type])
    template = random.choice(templates)
    
    saint = random.choice(SAINTS)
    tree = random.choice(TREES)
    
    return template.replace("{city}", city).replace("{saint}", saint).replace("{tree}", tree)

# CSV writing wrapper (outputs both .csv.gz and uncompressed preview)
def write_csv_datasets(filename, headers, rows):
    gz_path = os.path.join(output_dir, f"{filename}.gz")
    preview_path = os.path.join(preview_dir, filename)
    
    print(f"Writing {filename}.gz & dev_preview/{filename} ({len(rows)} rows)...")
    
    # 1. Write compressed .csv.gz
    with gzip.open(gz_path, "wt", encoding="utf-8", newline="") as f:
        writer = csv.writer(f, quoting=csv.QUOTE_MINIMAL)
        writer.writerow(headers)
        writer.writerows(rows)
        
    # 2. Write uncompressed preview file
    with open(preview_path, "w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f, quoting=csv.QUOTE_MINIMAL)
        writer.writerow(headers)
        writer.writerows(rows)

# ----------------------------------------------------
# Task 1: Package Geography Data from raw CSVs
# ----------------------------------------------------

print("Migrating raw geography CSV files to seed files...")

# Continents
continents_rows = []
with open(os.path.join(source_dir, "continents.csv"), "r", encoding="utf-8") as f:
    reader = csv.DictReader(f)
    for row in reader:
        c_id = get_continent_uuid(row["code"])
        continents_rows.append([c_id, row["name"], row["code"]])
write_csv_datasets("continents.csv", ["id", "name", "code"], continents_rows)

# Oceans & Seas
oceans_rows = []
with open(os.path.join(source_dir, "oceans_seas.csv"), "r", encoding="utf-8") as f:
    reader = csv.DictReader(f)
    for row in reader:
        o_id = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"ocean_{row['name']}"))
        oceans_rows.append([o_id, row["name"], row["type"].upper()])
write_csv_datasets("oceans_seas.csv", ["id", "name", "type"], oceans_rows)

# Countries
countries_rows = []
country_code_to_uuid = {}
with open(os.path.join(source_dir, "countries.csv"), "r", encoding="utf-8") as f:
    reader = csv.DictReader(f)
    for row in reader:
        co_id = get_country_uuid(row["iso"])
        country_code_to_uuid[row["iso"]] = co_id
        continent_id = get_continent_uuid(row["continent"])
        countries_rows.append([
            co_id, 
            row["name"], 
            row["iso"], 
            row["capital"], 
            int(row["population"]), 
            float(row["areaSqKm"]), 
            continent_id
        ])
write_csv_datasets("countries.csv", ["id", "name", "code", "capital", "population", "square_kilometers", "continent_id"], countries_rows)

# Regions
regions_rows = []
region_key_to_uuid = {}
with open(os.path.join(source_dir, "regions.csv"), "r", encoding="utf-8") as f:
    reader = csv.DictReader(f)
    for row in reader:
        cc = row["countryCode"]
        rc = row["regionCode"]
        r_id = get_region_uuid(cc, rc)
        region_key_to_uuid[f"{cc}_{rc}"] = r_id
        
        country_id = country_code_to_uuid.get(cc, "")
        regions_rows.append([
            r_id,
            row["name"],
            rc,
            country_id
        ])
write_csv_datasets("regions.csv", ["id", "name", "code", "country_id"], regions_rows)

# Settlements
settlements = []
settlement_rows = []
with open(os.path.join(source_dir, "settlements.csv"), "r", encoding="utf-8") as f:
    reader = csv.DictReader(f)
    for row in reader:
        geoname_id = int(row["geonameId"])
        cc = row["countryCode"]
        rc = row["regionCode"]
        
        s_id = get_settlement_uuid(geoname_id)
        region_id = region_key_to_uuid.get(f"{cc}_{rc}", "")
        lang = determine_language(cc)
        
        settlements.append({
            "id": s_id,
            "geonameId": geoname_id,
            "name": row["name"],
            "latitude": float(row["latitude"]),
            "longitude": float(row["longitude"]),
            "population": int(row["population"]),
            "timezone": row["timezone"],
            "biome": row["biome"],
            "countryCode": cc,
            "language": lang
        })
        
        settlement_rows.append([
            s_id,
            geoname_id,
            row["name"],
            float(row["latitude"]),
            float(row["longitude"]),
            int(row["population"]),
            row["timezone"],
            row["biome"],
            region_id,
            lang
        ])
write_csv_datasets("settlements.csv", ["id", "geoname_id", "name", "latitude", "longitude", "population", "timezone", "biome", "region_id", "language"], settlement_rows)

# ----------------------------------------------------
# Task 2: OpenStreetMap Data Scraper (Andorra AD Overlay)
# ----------------------------------------------------

osm_roads_by_settlement = {}
osm_pois_by_settlement = {}
osm_success = False

ad_settlements = [s for s in settlements if s["countryCode"] == "AD"]

if ad_settlements:
    print("Attempting to scrape OpenStreetMap data for Andorra (AD) via Overpass API...")
    overpass_url = "https://overpass-api.de/api/interpreter"
    
    # Query for all roads and POIs in Andorra
    query = """[out:json][timeout:30];
    area["ISO3166-1"="AD"]->.a;
    (
      way["highway"~"motorway|trunk|primary|secondary|tertiary|residential"](area.a);
      node["amenity"~"hospital|police|school|supermarket|pharmacy|prison|fire_station"](area.a);
      way["amenity"~"hospital|police|school|supermarket|pharmacy|prison|fire_station"](area.a);
    );
    out center;
    """
    
    try:
        data = urllib.parse.urlencode({"data": query}).encode("utf-8")
        req = urllib.request.Request(overpass_url, data=data, headers={"User-Agent": "ZombieGameDataExtractor/1.0"})
        with urllib.request.urlopen(req, timeout=30) as response:
            osm_data = json.loads(response.read().decode("utf-8"))
            
        elements = osm_data.get("elements", [])
        
        # Maps to store nodes and ways
        nodes_coords = {}
        ways = []
        pois = []
        
        for el in elements:
            el_type = el["type"]
            el_id = el["id"]
            
            if el_type == "node":
                lat, lon = el["lat"], el["lon"]
                nodes_coords[el_id] = (lat, lon)
                
                # Check if it is a POI
                if "tags" in el and "amenity" in el["tags"]:
                    pois.append({
                        "name": el["tags"].get("name", el["tags"]["amenity"].replace("_", " ").title()),
                        "type": el["tags"]["amenity"].upper(),
                        "lat": lat,
                        "lon": lon
                    })
            elif el_type == "way":
                if "tags" in el and "highway" in el["tags"]:
                    ways.append(el)
                    
                # If way is also tagged as POI (e.g. outline of building)
                if "tags" in el and "amenity" in el["tags"]:
                    lat = el.get("center", {}).get("lat")
                    lon = el.get("center", {}).get("lon")
                    if lat and lon:
                        pois.append({
                            "name": el["tags"].get("name", el["tags"]["amenity"].replace("_", " ").title()),
                            "type": el["tags"]["amenity"].upper(),
                            "lat": lat,
                            "lon": lon
                        })
                        
        print(f"Scraped {len(nodes_coords)} node geometries, {len(ways)} road ways, and {len(pois)} POIs from OSM.")
        
        # Resolve any extra node geometries queried implicitly
        # (Though 'out center' might not return full way geometries, we can use the way's center as a fallback node if geometries are missing, or simple segment connections)
        # Let's count junction nodes across ways
        node_ref_count = {}
        for way in ways:
            for nd in way.get("nodes", []):
                node_ref_count[nd] = node_ref_count.get(nd, 0) + 1
                
        # Define junctions
        junctions = set()
        for way in ways:
            nodes = way.get("nodes", [])
            if nodes:
                junctions.add(nodes[0])
                junctions.add(nodes[-1])
                for nd in nodes:
                    if node_ref_count[nd] > 1:
                        junctions.add(nd)
                        
        # Map nodes to settlements in Andorra
        def get_nearest_ad_settlement(lat, lon):
            best_s = ad_settlements[0]
            best_d = float("inf")
            for s in ad_settlements:
                d = math.hypot(lat - s["latitude"], lon - s["longitude"])
                if d < best_d:
                    best_d = d
                    best_s = s
            return best_s
            
        # Parse road networks per settlement
        osm_nodes_map = {} # settlement_uuid -> list of (id, lat, lon)
        osm_segments_map = {} # settlement_uuid -> list of (name, start_id, end_id, type)
        
        for s in ad_settlements:
            osm_nodes_map[s["id"]] = []
            osm_segments_map[s["id"]] = []
            osm_roads_by_settlement[s["id"]] = True
            
        # Extract road segments
        for way in ways:
            name = way.get("tags", {}).get("name", "Unnamed Road")
            hw_type = way["tags"]["highway"]
            nodes = way.get("nodes", [])
            
            # Find coordinates for all nodes in the way, skipping elements without geometries
            coords = []
            node_ids = []
            for nd in nodes:
                if nd in nodes_coords:
                    coords.append(nodes_coords[nd])
                    node_ids.append(nd)
                    
            if len(coords) < 2:
                continue
                
            # Split way into segments at junctions
            last_j_idx = 0
            for idx in range(1, len(node_ids)):
                nd = node_ids[idx]
                if nd in junctions or idx == len(node_ids) - 1:
                    start_nd = node_ids[last_j_idx]
                    end_nd = nd
                    
                    start_coords = coords[last_j_idx]
                    end_coords = coords[idx]
                    
                    # Associate with nearest settlement based on start node
                    s = get_nearest_ad_settlement(start_coords[0], start_coords[1])
                    
                    # Add start and end node
                    osm_nodes_map[s["id"]].append((start_nd, start_coords[0], start_coords[1]))
                    osm_nodes_map[s["id"]].append((end_nd, end_coords[0], end_coords[1]))
                    
                    # Add segment
                    osm_segments_map[s["id"]].append((name, start_nd, end_nd, hw_type))
                    
                    last_j_idx = idx
                    
        # Map POIs to nearest settlement
        for s in ad_settlements:
            osm_pois_by_settlement[s["id"]] = []
            
        for poi in pois:
            s = get_nearest_ad_settlement(poi["lat"], poi["lon"])
            # Validate types to match enum values
            p_type = poi["type"]
            if p_type not in ["HOSPITAL", "POLICE", "FIRE_STATION", "MILITARY", "SCHOOL", "SUPERMARKET", "PHARMACY", "PRISON", "PARK"]:
                # Map standard OSM amenities
                if p_type == "KINDERGARTEN" or p_type == "UNIVERSITY": p_type = "SCHOOL"
                elif p_type == "CLINIC" or p_type == "DOCTORS": p_type = "HOSPITAL"
                elif p_type == "TOWN_HALL": p_type = "POLICE"
                else: p_type = "SUPERMARKET" # Default fallback
            
            osm_pois_by_settlement[s["id"]].append(poi)
            
        # Re-save processed OSM lists to overall lookup
        for s in ad_settlements:
            # Deduplicate nodes
            unique_nodes = {}
            for n_id, n_lat, n_lon in osm_nodes_map[s["id"]]:
                unique_nodes[n_id] = (n_lat, n_lon)
                
            osm_nodes_by_settlement_list = []
            # We map local node IDs to deterministic UUIDs
            node_osm_id_to_uuid = {}
            for idx, (n_id, (n_lat, n_lon)) in enumerate(unique_nodes.items()):
                n_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"node_osm_{n_id}"))
                node_osm_id_to_uuid[n_id] = n_uuid
                osm_nodes_by_settlement_list.append((n_uuid, n_lat, n_lon))
                
            osm_nodes_map[s["id"]] = osm_nodes_by_settlement_list
            
            # Map segments using the new UUIDs
            mapped_segments = []
            for name, start_nd, end_nd, hw_type in osm_segments_map[s["id"]]:
                start_uuid = node_osm_id_to_uuid.get(start_nd)
                end_uuid = node_osm_id_to_uuid.get(end_nd)
                if start_uuid and end_uuid:
                    seg_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"segment_osm_{s['id']}_{start_nd}_{end_nd}"))
                    mapped_segments.append((seg_uuid, name, start_uuid, end_uuid, hw_type))
            osm_segments_map[s["id"]] = mapped_segments
            
            # Save to global override dictionaries
            osm_roads_by_settlement[s["id"]] = {
                "nodes": osm_nodes_map[s["id"]],
                "segments": osm_segments_map[s["id"]]
            }
            
        osm_success = True
        print("Successfully loaded real OSM infrastructure data for Andorra!")
    except Exception as e:
        print(f"OSM scraper failed or offline ({e}). Falling back to procedural generation for Andorra.")

# ----------------------------------------------------
# Task 3: Procedural Road & POI Data Generation
# ----------------------------------------------------

road_nodes = []
road_segments = []
special_locations = []

print("Generating road networks and POIs globally (33,615 settlements)...")
start_time = datetime.now()

# We loop through all settlements
for idx, s in enumerate(settlements):
    s_uuid = s["id"]
    geoname_id = s["geonameId"]
    lat = s["latitude"]
    lon = s["longitude"]
    pop = s["population"]
    lang = s["language"]
    city_name = s["name"]
    
    # Progress indicator
    if (idx + 1) % 5000 == 0:
        elapsed = (datetime.now() - start_time).total_seconds()
        print(f"Processed {idx + 1}/33615 settlements... ({elapsed:.1f}s)")
        
    # Check if we should use the real OSM data for this settlement
    if s_uuid in osm_roads_by_settlement and osm_success:
        # Load real OSM data
        osm_data = osm_roads_by_settlement[s_uuid]
        for n_uuid, n_lat, n_lon in osm_data["nodes"]:
            road_nodes.append([n_uuid, n_lat, n_lon, s_uuid])
            
        for seg_uuid, name, start_uuid, end_uuid, hw_type in osm_data["segments"]:
            road_segments.append([seg_uuid, name, start_uuid, end_uuid, hw_type, "false", s_uuid])
            
        osm_pois = osm_pois_by_settlement.get(s_uuid, [])
        for p_idx, poi in enumerate(osm_pois):
            poi_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"poi_osm_{s_uuid}_{p_idx}"))
            special_locations.append([poi_uuid, poi["name"], poi["type"], poi["lat"], poi["lon"], "false", s_uuid])
        continue
        
    # Otherwise, generate procedurally
    # 1. Deterministic seed based on geonameId
    random.seed(geoname_id)
    
    # Determine scale based on population
    if pop < 30000:
        num_nodes = 6
        max_r = 400
    elif pop < 100000:
        num_nodes = 12
        max_r = 750
    elif pop < 500000:
        num_nodes = 24
        max_r = 1500
    else:
        num_nodes = 50
        max_r = 3000
        
    # Generate nodes
    nodes = []
    # Center node
    center_node_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"node_{geoname_id}_0"))
    nodes.append((center_node_uuid, lat, lon))
    
    # Outer nodes
    for i in range(1, num_nodes):
        r = random.uniform(100, max_r)
        theta = random.uniform(0, 2 * math.pi)
        dlat = (r * math.cos(theta)) / 111320.0
        dlon = (r * math.sin(theta)) / (111320.0 * math.cos(math.radians(lat)))
        n_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"node_{geoname_id}_{i}"))
        nodes.append((n_uuid, lat + dlat, lon + dlon))
        
    for n in nodes:
        road_nodes.append([n[0], n[1], n[2], s_uuid])
        
    # Generate connected graph (MST via Prim's algorithm)
    # List of nodes in tree, starts with center
    in_tree = {0}
    edges = []
    
    while len(in_tree) < num_nodes:
        best_dist = float("inf")
        best_edge = None
        for u in in_tree:
            for v in range(num_nodes):
                if v not in in_tree:
                    # Euclidean distance
                    dist = math.hypot(nodes[u][1] - nodes[v][1], nodes[u][2] - nodes[v][2])
                    if dist < best_dist:
                        best_dist = dist
                        best_edge = (u, v)
        if best_edge:
            edges.append(best_edge)
            in_tree.add(best_edge[1])
            
    # Add some extra edges to create cycles (loops)
    all_pairs = []
    for u in range(num_nodes):
        for v in range(u + 1, num_nodes):
            if (u, v) not in edges and (v, u) not in edges:
                dist = math.hypot(nodes[u][1] - nodes[v][1], nodes[u][2] - nodes[v][2])
                all_pairs.append((dist, u, v))
                
    all_pairs.sort()
    # Add a percentage of shortest edges to complete grid layout
    num_extra = int(num_nodes * 0.35)
    for i in range(min(num_extra, len(all_pairs))):
        edges.append((all_pairs[i][1], all_pairs[i][2]))
        
    # Write segments
    streets = STREET_NAMES.get(lang, STREET_NAMES["EN"])
    
    for seg_idx, (u, v) in enumerate(edges):
        seg_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"segment_{geoname_id}_{u}_{v}"))
        
        # Localized road name
        street_name = random.choice(streets)
        # 10% chance to be "Highway" or "Main Road"
        if random.random() < 0.1:
            if lang == "NL": street_name = "Rijksweg"
            elif lang == "DE": street_name = "Bundesstraße"
            elif lang == "FR": street_name = "Route Nationale"
            elif lang == "ES": street_name = "Autovía"
            elif lang == "RU": street_name = "Шоссе"
            else: street_name = "Highway"
            hw_type = "primary"
        else:
            hw_type = "secondary" if random.random() < 0.3 else "residential"
            
        road_segments.append([
            seg_uuid,
            street_name,
            nodes[u][0],
            nodes[v][0],
            hw_type,
            "false",
            s_uuid
        ])
        
    # Generate POIs
    num_pois = max(1, min(10, int(math.log10(max(1, pop)))))
    poi_types = ["SCHOOL", "SUPERMARKET", "PHARMACY", "POLICE", "FIRE_STATION", "HOSPITAL", "PARK", "PRISON"]
    
    for i in range(num_pois):
        poi_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, f"poi_{geoname_id}_{i}"))
        
        # Place POI near a random node
        anchor_node = random.choice(nodes)
        
        # Offsets (within ~15-30 meters)
        r = random.uniform(10, 30)
        theta = random.uniform(0, 2 * math.pi)
        dlat = (r * math.cos(theta)) / 111320.0
        dlon = (r * math.sin(theta)) / (111320.0 * math.cos(math.radians(anchor_node[1])))
        
        poi_lat = anchor_node[1] + dlat
        poi_lon = anchor_node[2] + dlon
        
        # Select type
        if i == 0:
            poi_type = "SUPERMARKET" # Every settlement gets a supermarket
        elif i == 1:
            poi_type = "SCHOOL"      # Every settlement gets a school
        elif i == 2:
            poi_type = "PHARMACY"    # Every settlement gets a pharmacy
        else:
            poi_type = random.choice(poi_types)
            
        poi_name = generate_local_name(poi_type, lang, city_name)
        special_locations.append([
            poi_uuid,
            poi_name,
            poi_type,
            poi_lat,
            poi_lon,
            "false",
            s_uuid
        ])

print(f"Generated all infrastructure in {(datetime.now() - start_time).total_seconds():.1f}s.")

# Write nodes, segments, and POIs to CSV/gzip
write_csv_datasets("road_nodes.csv", ["id", "latitude", "longitude", "settlement_id"], road_nodes)
write_csv_datasets("road_segments.csv", ["id", "name", "start_node_id", "end_node_id", "type", "blocked", "settlement_id"], road_segments)
write_csv_datasets("special_locations.csv", ["id", "name", "type", "latitude", "longitude", "looted", "settlement_id"], special_locations)

print("\nPipeline complete! All datasets written to src/main/resources/data/ (and uncompressed copies in dev_preview/).")
