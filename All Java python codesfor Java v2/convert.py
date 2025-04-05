import sqlite3
import csv

# Define file paths
csv_file = "86.csv"
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
with open(csv_file, "r") as file:
    reader = csv.reader(file)
    for row in reader:
        cursor.execute("INSERT OR IGNORE INTO inventory (name, itemid, quantity, price) VALUES (?, ?, ?, ?)", row)

# Commit and close connection
conn.commit()
conn.close()

print("CSV successfully converted to SQLite database!")
