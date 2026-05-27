import csv
import os

files = [
    r"C:\Users\daanh\.gemini\antigravity\brain\7093dd04-0c41-4cc2-b01e-a9acb4cf68d9\scratch\food_items.csv",
    r"C:\Users\daanh\.gemini\antigravity\brain\7761f491-c03c-4692-893a-d64f19e8418c\scratch\medicine_items.csv",
    r"C:\Users\daanh\.gemini\antigravity\brain\fec0cbbc-b635-4335-ad5a-2658f979038b\scratch\weapon_items.csv",
    r"C:\Users\daanh\.gemini\antigravity\brain\a121c248-efd9-4fe6-a66c-4c626c21d4d4\scratch\ammo_items.csv",
    r"C:\Users\daanh\.gemini\antigravity\brain\7bb85813-c6a7-47da-833d-cad12a8c7523\scratch\resource_items.csv",
    r"C:\Users\daanh\.gemini\antigravity\brain\213947f3-050f-4bf4-bae3-f4bcfe980d41\scratch\clothing_items.csv"
]

output_file = r"C:\Users\daanh\Documents\Zombie\src\main\resources\data\raw\items.csv"
header_written = False

with open(output_file, 'w', newline='', encoding='utf-8') as out_f:
    writer = csv.writer(out_f)
    for file in files:
        if not os.path.exists(file):
            print(f"File not found: {file}")
            continue
            
        with open(file, 'r', newline='', encoding='utf-8') as in_f:
            reader = csv.reader(in_f)
            header = next(reader, None)
            
            if not header_written and header:
                writer.writerow(header)
                header_written = True
                
            for row in reader:
                writer.writerow(row)

print(f"Successfully merged all items into {output_file}")
