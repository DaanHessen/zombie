import os
import subprocess
import sys
import time

continents = ["africa", "antarctica", "asia", "europe", "north_america", "oceania", "south_america"]
display    = ["Africa", "Antarctica", "Asia", "Europe", "North America", "Oceania", "South America"]

def fmt_time(seconds):
    h = int(seconds // 3600)
    m = int((seconds % 3600) // 60)
    s = int(seconds % 60)
    if h > 0:
        return f"{h}h {m}m {s}s"
    elif m > 0:
        return f"{m}m {s}s"
    return f"{s}s"

def main():
    total_start = time.time()
    
    print()
    print("  +====================================================+")
    print("  |        GLOBAL ROAD EXTRACTION -- RASPBERRY PI       |")
    print("  +====================================================+")
    print()
    
    completed = 0
    
    for i, cont in enumerate(continents):
        script = f"{cont}_roads.py"
        step_start = time.time()
        
        print(f"  +-- [{i+1}/7] {display[i]} Roads")
        print(f"  |")
        
        try:
            subprocess.run([sys.executable, script], check=True)
            elapsed = time.time() - step_start
            completed += 1
            print(f"  |")
            print(f"  +-- [OK] Done in {fmt_time(elapsed)}")
            print()
        except subprocess.CalledProcessError as e:
            print(f"  |")
            print(f"  +-- [FAIL] {e}")
            print()
            print(f"  Aborting. {completed}/7 continents completed.")
            return

    total_elapsed = time.time() - total_start
    
    print("  +====================================================+")
    print(f"  |  [OK] ALL ROADS EXTRACTED                          |")
    print(f"  |  Time:      {fmt_time(total_elapsed):>12}                       |")
    print("  +====================================================+")
    print()

if __name__ == '__main__':
    main()
