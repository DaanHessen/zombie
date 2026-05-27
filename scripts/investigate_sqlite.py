import sqlite3

db_path = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\sqlite\netherlands_map.sqlite"

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    query = """
    SELECT lat, lon, name, category, area_sqm FROM pois 
    WHERE lat BETWEEN ? AND ? AND lon BETWEEN ? AND ?
    """
    
    cursor.execute(query, (52.1748, 52.2748, 5.1264, 5.2264))
    results = cursor.fetchall()
    
    print(f"Total Rows Returned: {len(results)}")
    
    # Check how many are "House"
    house_count = 0
    for row in results:
        name = row[2]
        if name and "house" in name.lower():
            house_count += 1
            
    print(f"Total containing 'House' in name: {house_count}")

    conn.close()
except Exception as e:
    print(e)
