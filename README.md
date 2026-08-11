# Inventory Management System

An **Inventory Management System** built with **Java** and integrated with **Python utilities** for easy database usage.
This project helps to manage inventory for both sellers and customers with automatic receipt generation with database support.  

---

## Features
-  **Inventory Management**: Add, update, and track items in stock.  
-  **Receipts & Contracts**: Record purchase and sales receipts and contracts.  
-  **Database Integration**: Uses `SQLite` database (`inventory.db`) for persistent storage.  
-  **Python Database Wrapper**:
  - A subprocess to a python module that interacts with database, fetches query result in realtime and returns result to main java mosule handling it.
  - Convert CSV ↔ Database (`convert_csvtodb.py`, `convert_dbtocsv.py`).  The database state can be stored manually into csv, from which db is created,
  - and after the completion of execution of the application, is reconverted and stored into the csv for easy access.
-  **AI Inventory Investigator** *(new)*: Ask natural-language questions about your inventory directly from the application menu.
  - Powered by a local Ollama LLM via `nl_to_sql.py`, which translates questions into SQL and runs them against `inventory.db`.
  - Launched as an interactive subprocess from `db.runNlToSqlSession()` — no separate terminal needed.

---

## Project Structure
```

Inventory-Management-System/
│── Main.java               # Entry point of the Java application
│── DBClient.java           # Invokes Python wrapper for SQL queries
│── db.java                 # Database management class; hosts AI session launcher
│── buy.java                # Handles purchases (from customer)
│── sell.java               # Handles sales (from seller)
│── seller.java             # Seller management
│── customer.java           # Customer management
│── contract.java           # Contract generation for seller
│── receipt.java            # Receipt generation for customer
│── db_wrapper.py           # Python wrapper for DB operations
│── nl_to_sql.py            # NL → SQL translator powered by Ollama LLM (--repl mode)
│── convert_csvtodb.py      # CSV → DB converter
│── convert_dbtocsv.py      # DB → CSV converter
│── database.csv            # Manually input database - stores database at beginning and end for manual view
│── inventory.db            # SQLite database
│── **pycache**/            # Compiled Python cache

````

---

## Requirements

### Java
- JDK 8 or above  

### Python
- Python 3.7+  
- Required packages:
- 
  ```bash
  pip install sqlite3 pandas
  ```
- **Ollama** (for the AI Investigator feature):
  - Install from [https://ollama.com](https://ollama.com) and ensure it is running locally.
  - Pull the model used by `nl_to_sql.py` (default: `phi3`):
    ```bash
    ollama pull phi3
    ```


---

## How to Run



### This is the entry point for the entire application.

```bash
javac Main.java
java Main
```

---

### Run Python Utilities
(Optional, can be done separately)

Convert CSV → DB:

```bash
python convert_csvtodb.py
```

Convert DB → CSV:

```bash
python convert_dbtocsv.py
```

---

### AI Inventory Investigator (Natural Language Queries)

Select option **4** from the main menu after launching the application:

```
Enter 1 if you are a Customer, 2 if you are a Seller, 3 if you want to see the
Inventory state, 4 to Investigate DB with AI, 5 to exit
> 4
AI Inventory Investigator — type your question, or 'quit' to return.

Your question (or 'quit'): How many units of Rice are left?
```

- The application spawns `nl_to_sql.py --repl` as a subprocess via `db.runNlToSqlSession()`.
- Your question is piped to the Python process, translated into SQL by the LLM, executed against `inventory.db`, and the result is printed back.
- Type **`quit`** to end the AI session and return to the main menu.
- Requires Ollama to be running locally before starting the application.

---

## Database

* **Default DB**: `inventory.db`
* Includes table for the Inventory of the format - ( name	itemid quantity price )
<img width="257" height="21" alt="image" src="https://github.com/user-attachments/assets/da3e930f-883b-47cb-85cf-a0daa5363373" />


---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/my-feature`)
3. Commit changes (`git commit -m 'Add new feature'`)
4. Push branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---
