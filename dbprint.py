import sqlite3

def print_db_contents(db_file):
    """Connects to the database and prints all table contents."""
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()

    # Get all table names
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()

    if not tables:
        print("No tables found in the database.")
        conn.close()
        return

    for table in tables:
        table_name = table[0]
        print(f"\nTable: {table_name}")
        print("-" * 50)

        # Get all data from the table
        cursor.execute(f"SELECT * FROM {table_name};")
        rows = cursor.fetchall()

        # Get column names
        cursor.execute(f"PRAGMA table_info({table_name});")
        columns = [col[1] for col in cursor.fetchall()]
        print(" | ".join(columns))

        if rows:
            for row in rows:
                print(" | ".join(str(value) for value in row))
        else:
            print("No data found.")

        print("-" * 50)

    conn.close()

db_file = "your_database.db"  # Change this to your database file
print_db_contents(db_file)