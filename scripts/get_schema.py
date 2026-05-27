import sqlite3
import sys

db_path = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\sqlite\netherlands_map.sqlite"

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT name, sql FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    print(f"--- SCHEMA FOR {db_path} ---")
    for table_name, schema in tables:
        print(f"\nTable: {table_name}")
        print(schema)
    conn.close()
except Exception as e:
    print(f"Error reading database: {e}")
