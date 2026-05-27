import sqlite3
conn = sqlite3.connect('src/main/resources/data/sqlite/world_data.sqlite')
cur = conn.cursor()
print('=== Item counts by category ===')
for row in cur.execute('SELECT category, COUNT(*) FROM item_templates GROUP BY category ORDER BY category'):
    print(f'  {row[0]}: {row[1]}')

print('\n=== Sample perishable foods ===')
for row in cur.execute('SELECT name, base_spoilage_ticks, calories FROM item_templates WHERE category="FOOD" AND base_spoilage_ticks > 0 LIMIT 5'):
    days = row[1] / 1440
    print(f'  {row[0]} | {days:.0f} days | {row[2]} cal')

print('\n=== Sample non-perishable foods ===')
for row in cur.execute('SELECT name FROM item_templates WHERE category="FOOD" AND base_spoilage_ticks = 0 LIMIT 5'):
    print(f'  {row[0]} | NEVER SPOILS')

conn.close()
