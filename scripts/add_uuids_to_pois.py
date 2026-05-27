import sqlite3
import uuid

conn = sqlite3.connect('src/main/resources/data/sqlite/world_data.sqlite')
conn.execute('ALTER TABLE pois ADD COLUMN id TEXT')
conn.commit()

c = conn.cursor()
c.execute('SELECT rowid FROM pois')
rows = c.fetchall()

print(f"Updating {len(rows)} POIs with UUIDs...")
for row in rows:
    c.execute('UPDATE pois SET id = ? WHERE rowid = ?', (str(uuid.uuid4()), row[0]))

conn.commit()
conn.execute('CREATE UNIQUE INDEX idx_pois_id ON pois(id)')
conn.commit()
print("Done.")
conn.close()
