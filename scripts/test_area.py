import math
import osmium

class AreaTestHandler(osmium.SimpleHandler):
    def __init__(self):
        osmium.SimpleHandler.__init__(self)
        self.count = 0

    def way(self, w):
        if self.count >= 20: 
            return
            
        tags = w.tags
        category = tags.get('amenity') or tags.get('shop') or tags.get('tourism') or tags.get('emergency') or tags.get('leisure')
        
        if category:
            try:
                nodes = list(w.nodes)
                if len(nodes) < 3: return
                
                area = 0.0
                for i in range(len(nodes)):
                    j = (i + 1) % len(nodes)
                    n1 = nodes[i]
                    n2 = nodes[j]
                    area += (n1.lon * n2.lat) - (n2.lon * n1.lat)
                
                lat_rad = math.radians(nodes[0].lat)
                sqm = abs(area / 2.0) * 111320.0 * (111320.0 * math.cos(lat_rad))
                
                name = tags.get('name', 'Unknown')
                print(f"[{category}] {name}: {sqm:.2f} sqm")
                self.count += 1
                
                # if count reached 20, abort parsing to save time
                if self.count >= 20:
                    raise Exception("Done testing")
            except Exception as e:
                if str(e) == "Done testing":
                    raise e
                pass

if __name__ == '__main__':
    print("Testing area calculation on first 20 building polygons...")
    h = AreaTestHandler()
    try:
        h.apply_file('netherlands-latest.osm.pbf', locations=True)
    except Exception as e:
        if str(e) != "Done testing":
            print(f"Error: {e}")
