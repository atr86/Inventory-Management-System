#I, Atrij Roy, has created this program of Inventory Management System with Git (Q1) and updated the .db database of Q2 and Q3.
#Inventory Management System with Git
#a) Design a system to manage products for a store. Customers can make purchases, and
#sellers can update the list of products.
#b) Use Git for version control, and maintain a purchase history of items.



import sqlite3


db_file = "A3_03_4.db"

def initialize_db():
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS inventory (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT UNIQUE NOT NULL,
                        quantity INTEGER NOT NULL,
                        price REAL NOT NULL)''')
    conn.commit()
    conn.close()

def print_inventory():
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM inventory")
    rows = cursor.fetchall()
    conn.close()

    print("\n" + "=" * 40)
    print(f"{'ID':<5} {'Name':<15} {'Qty':<8} {'Price'}")
    print("-" * 40)
    for row in rows:
        print(f"{row[0]:<5} {row[1]:<15} {row[2]:<8} {row[3]:.2f}")
    print("=" * 40 + "\n")

def generate_contract(seller_name, gst_id, sales_data):
    """Generates a unique contract file for each seller session."""
    filename = f"contract_{seller_name}.txt"

    with open(filename, "w") as f:
        f.write(f"Seller: {seller_name}\nGST ID: {gst_id}\n")
        f.write("=" * 50 + "\n")
        f.write(f"{'Item':<15}{'Qty':<10}{'Selling Price':<15}{'Proposed Price':<15}\n")
        f.write("-" * 50 + "\n")

        total_earnings = 0
        for item, quantity, selling_price, proposed_price in sales_data:
            f.write(f"{item:<15}{quantity:<10}{selling_price:<15.2f}{proposed_price:<15.2f}\n")
            total_earnings += quantity * selling_price

        gst_deduction = total_earnings * 0.18
        final_earnings = total_earnings - gst_deduction

        f.write("=" * 50 + "\n")
        f.write(f"Total Earnings Before GST: ${total_earnings:.2f}\n")
        f.write(f"GST Deduction (18%): ${gst_deduction:.2f}\n")
        f.write(f"Total Earnings After GST: ${final_earnings:.2f}\n")
    
    print(f"Contract saved as {filename}")

def generate_receipt(customer_name, purchases):
    """Generates a unique receipt file for each customer session."""
    filename = f"receipt_{customer_name}.txt"

    with open(filename, "w") as f:
        f.write(f"Customer: {customer_name}\n")
        f.write("=" * 50 + "\n")
        f.write(f"{'Item':<15}{'Qty':<10}{'Price Per Item':<15}{'Total Price':<15}\n")
        f.write("-" * 50 + "\n")

        total_cost = 0
        for item, quantity, price in purchases:
            total_price = quantity * price
            f.write(f"{item:<15}{quantity:<10}{price:<15.2f}{total_price:<15.2f}\n")
            total_cost += total_price

        gst_amount = total_cost * 0.18
        final_cost = total_cost + gst_amount

        f.write("=" * 50 + "\n")
        f.write(f"Total Cost Before GST: ${total_cost:.2f}\n")
        f.write(f"GST (18%): ${gst_amount:.2f}\n")
        f.write(f"Total Cost After GST: ${final_cost:.2f}\n")
    
    print(f"Receipt saved as {filename}")

def seller_menu():
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    seller_name = input("Enter your name (Seller): ").strip()
    gst_id = input("Enter your GST ID: ").strip()
    sales_data = []

    while True:
        name = input("Enter the name of the item to sell: ").strip()
        quantity = int(input("Enter quantity to sell: "))

        cursor.execute("SELECT price, quantity FROM inventory WHERE name = ?", (name,))
        item = cursor.fetchone()

        if item:
            print(f"Item exists. Selling price: {item[0]}")
            for attempt in range(3):
                proposed_price = float(input("Enter proposed price: "))
                if proposed_price >= item[0] * 1.05:
                    print("Offer accepted!")
                    cursor.execute("UPDATE inventory SET quantity = quantity + ?, price = ? WHERE name = ?",
                                   (quantity, item[0], name))
                    conn.commit()
                    sales_data.append((name, quantity, item[0], proposed_price))
                    break
                else:
                    print(f"Offer too low! Attempt {attempt + 1}/3")
            else:
                print("Offer denied.")
        else:
            print("New item detected!")
            for attempt in range(3):
                selling_price = float(input("Enter selling price: "))
                proposed_price = float(input("Enter proposed price: "))
                if proposed_price >= selling_price * 1.05:
                    print("Offer accepted!")
                    cursor.execute("INSERT INTO inventory (name, quantity, price) VALUES (?, ?, ?)",
                                   (name, quantity, selling_price))
                    conn.commit()
                    sales_data.append((name, quantity, selling_price, proposed_price))
                    break
                else:
                    print(f"Offer too low! Attempt {attempt + 1}/3")
            else:
                print("Offer denied.")

        more = input("Do you want to sell more items? (yes/y/no/n): ").strip().lower()
        if more not in ["yes", "y"]:
            break

    if sales_data:
        generate_contract(seller_name, gst_id, sales_data)

    conn.close()

def customer_menu():
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    customer_name = input("Enter your name (Customer): ").strip()
    total_cost = 0
    purchases = []

    while True:
        name = input("Enter the name of the item to buy: ").strip()
        cursor.execute("SELECT quantity, price FROM inventory WHERE name = ?", (name,))
        item = cursor.fetchone()

        if not item:
            print("Item not found, try again.")
            continue

        while True:
            quantity = int(input(f"Enter quantity to buy (Available: {item[0]}): "))
            if quantity > item[0]:
                print("Not enough stock, try again.")
            else:
                break

        print("Added to cart!")
        total_cost += quantity * item[1]
        cursor.execute("UPDATE inventory SET quantity = quantity - ? WHERE name = ?", (quantity, name))
        conn.commit()
        purchases.append((name, quantity, item[1]))

        more = input("Do you want to buy more items? (yes/y/no/n): ").strip().lower()
        if more not in ["yes", "y"]:
            break

    if purchases:
        generate_receipt(customer_name, purchases)

    print(f"Total cost after GST: ${(total_cost * 1.18):.2f}")
    conn.close()

def main_menu():
    initialize_db()
    while True:
        print("\n===== Inventory Management System =====")
        print("1. Seller")
        print("2. Customer")
        print("3. View Inventory")
        print("4. Exit")
        choice = input("Enter choice: ").strip()

        if choice == "1":
            seller_menu()
        elif choice == "2":
            customer_menu()
        elif choice == "3":
            print_inventory()
        elif choice == "4":
            print("Exiting program.")
            break
        else:
            print("Invalid choice, try again.")

if __name__ == "__main__":
    main_menu()
