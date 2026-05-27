import sqlite3

db_path = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\sqlite\netherlands_map.sqlite"

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Query exact bounding box from the log
    query = """
    SELECT category, count(*) as c 
    FROM pois 
    WHERE lat BETWEEN 52.1748 AND 52.2748 
      AND lon BETWEEN 5.1264 AND 5.2264
    GROUP BY category
    ORDER BY c DESC
    """
    
    cursor.execute(query)
    results = cursor.fetchall()
    print("RAW CATEGORIES IN HILVERSUM BOUNDING BOX:")
    for cat, count in results:
        print(f" - {cat}: {count}")
        
    conn.close()
except Exception as e:
    print(e)
