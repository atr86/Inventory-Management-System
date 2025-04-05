import sqlite3
import sys
import os

#Database file path
DB_FILE = "inventory.db"

def execute_query(query):
    """Execute the given SQL query and return results in CSV format"""
    # Connect to the SQLite database
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()

    try:
        cursor.execute(query)

#Check if this is a SELECT query (has results to fetch)
        if query.strip().upper().startswith(("SELECT", "PRAGMA")):
            rows = cursor.fetchall()

#ormat results as CSV
            result_lines = []
            for row in rows:
                result_lines.append(",".join(str(item) for item in row))

#Return as string
            return "\n".join(result_lines)
        else:
            # For non-SELECT queries, commit changes and return affected rows
            conn.commit()
            return str(cursor.rowcount)

    except sqlite3.Error as e:
        return f"SQL Error: {str(e)}"
    finally:
        conn.close()



query = sys.argv[1]  # Get the SQL command from arguments
result = execute_query(query)
print(result)