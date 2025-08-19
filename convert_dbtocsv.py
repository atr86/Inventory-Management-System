import sqlite3
import csv

# Define file paths
db_file = "inventory.db"
csv_file = "database.csv"

# Connect to SQLite database
conn = sqlite3.connect(db_file)
cursor = conn.cursor()

# Fetch all data from the inventory table
cursor.execute("SELECT * FROM inventory")
rows = cursor.fetchall()

# Get column names from the cursor description
column_names = [description[0] for description in cursor.description]

# Write to CSV file
with open(csv_file, "w", newline="") as file:
    writer = csv.writer(file)
    # Write header
    writer.writerow(column_names)
    # Write rows
    writer.writerows(rows)

# Close connection
conn.close()

#print("SQLite database successfully converted to CSV!")
