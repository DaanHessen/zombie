import sqlite3

conn = sqlite3.connect('src/main/resources/data/sqlite/netherlands_map.sqlite')
c = conn.cursor()

c.execute("SELECT name FROM sqlite_master WHERE type='table'")
print(c.fetchall())
