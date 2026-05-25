import gzip
import csv
import os
import shutil

input_file = 'c:/Users/daanh/Documents/Zombie/src/main/resources/data/settlements.csv.gz'
output_file = 'c:/Users/daanh/Documents/Zombie/src/main/resources/data/settlements_new.csv.gz'

with gzip.open(input_file, 'rt', encoding='utf-8') as f_in, gzip.open(output_file, 'wt', encoding='utf-8', newline='') as f_out:
    reader = csv.reader(f_in)
    writer = csv.writer(f_out)
    
    header = next(reader)
    header.append('is_ground_zero')
    writer.writerow(header)
    
    rows = list(reader)
    
    ground_zero_set = False
    
    for row in rows:
        if not ground_zero_set and ('Vostok' in row[2] or 'Yakutsk' in row[2]):
            row.append('true')
            ground_zero_set = True
        else:
            row.append('false')
            
    if not ground_zero_set and rows:
        rows[0][-1] = 'true' # Set first row if none matched
        
    writer.writerows(rows)

shutil.move(output_file, input_file)
print("CSV updated successfully.")
