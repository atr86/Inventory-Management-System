import sqlite3
import csv

# Define file paths
csv_file = "database.csv"
db_file = "inventory.db"

# Connect to SQLite database (creates if not exists)
conn = sqlite3.connect(db_file)
cursor = conn.cursor()

# Create a table (adjust column types based on CSV data)
cursor.execute('''
    CREATE TABLE IF NOT EXISTS inventory (
        name TEXT,
        itemid INTEGER PRIMARY KEY,
        quantity INTEGER,
        price REAL
    )
''')

# Read CSV and insert data into SQLite
with open(csv_file, "r", newline="") as file:
    reader = csv.reader(file)
    next(reader, None)  # safely skip header if present
    for row in reader:
        if not row:  # skip empty lines
            continue
        try:
            name = row[0]
            itemid = int(row[1])
            quantity = int(row[2])
            price = float(row[3])
            cursor.execute(
                "INSERT OR IGNORE INTO inventory (name, itemid, quantity, price) VALUES (?, ?, ?, ?)",
                (name, itemid, quantity, price)
            )
        except ValueError:
            print(f"Skipping bad row: {row}")

# Commit and close connection
conn.commit()
conn.close()
