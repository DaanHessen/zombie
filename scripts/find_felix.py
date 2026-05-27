import sqlite3

conn = sqlite3.connect('src/main/resources/data/sqlite/netherlands_map.sqlite')
c = conn.cursor()

print("Searching for Felix...")
c.execute("SELECT name, category FROM pois WHERE name LIKE '%Felix%'")
print(c.fetchall())

print("\nSearching for Café/Cafe...")
c.execute("SELECT name, category FROM pois WHERE name LIKE '%Caf%' AND latitude BETWEEN 52.173 AND 52.273 AND longitude BETWEEN 5.126 AND 5.226 LIMIT 10")
print(c.fetchall())
