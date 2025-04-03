import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class db {
    private static final String DB_NAME = "inventory.db";
    
    public db() {
        // Initialize the database when creating a db object
        initialize();
    }
    
    /**
     * Initializes the database by creating the inventory table if it doesn't exist
     */
    public void initialize() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS inventory (" +
                               "name TEXT NOT NULL, " +
                               "itemid INTEGER PRIMARY KEY, " +
                               "quantity INTEGER, " +
                               "price REAL)";
        
        executeUpdate(createTableSQL);
        System.out.println("Database initialized successfully.");
    }
    
    /**
     * Searches for an item by name and returns its ID
     */
    public int searchName(String item) {
        String sql = "SELECT itemid FROM inventory WHERE LOWER(name) = LOWER('" + item + "')";
        String result = executeQuery(sql);
        
        if (result != null && !result.isEmpty()) {
            try {
                return Integer.parseInt(result.trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing item ID: " + e.getMessage());
            }
        }
        return -9999; // Item not found
    }
    
    /**
     * Gets an item name by its ID
     */
    public String getName(int id) {
        String sql = "SELECT name FROM inventory WHERE itemid = " + id;
        String result = executeQuery(sql);
        
        return (result != null && !result.isEmpty()) ? result.trim() : "";
    }
    
    /**
     * Gets the quantity of an item by its ID
     */
    public int getQuantity(int id) {
        String sql = "SELECT quantity FROM inventory WHERE itemid = " + id;
        String result = executeQuery(sql);
        
        if (result != null && !result.isEmpty()) {
            try {
                return Integer.parseInt(result.trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing quantity: " + e.getMessage());
            }
        }
        return -9999; // Item not found
    }
    
    /**
     * Withdraws (reduces) quantity from an item's inventory
     */
    public void withdraw(int id, int amount) {
        int currentQty = getQuantity(id);
        if (currentQty != -9999 && currentQty >= amount) {
            setQuantity(id, currentQty - amount);
        } else {
            System.err.println("Cannot withdraw: Insufficient quantity or item not found.");
        }
    }
    
    /**
     * Gets the price of an item by its ID
     */
    public double getPrice(int id) {
        String sql = "SELECT price FROM inventory WHERE itemid = " + id;
        String result = executeQuery(sql);
        
        if (result != null && !result.isEmpty()) {
            try {
                return Double.parseDouble(result.trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing price: " + e.getMessage());
            }
        }
        return -9999.00; // Item not found
    }
    
    /**
     * Displays all inventory items
     */
    public void display() {
        String sql = "SELECT name, quantity, price FROM inventory";
        String result = executeQuery(sql);
        
        System.out.println("Name\t\t\tQuantity\t\tPrice");
        System.out.println("--------------------------------------------");
        
        if (result != null && !result.isEmpty()) {
            String[] lines = result.split("\n");
            for (String line : lines) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    System.out.println(values[0] + "\t\t\t" + values[1] + "\t\t\t" + values[2]);
                }
            }
        } else {
            System.out.println("No inventory items found.");
        }
    }
    
    /**
     * Sets the quantity of an item
     */
    public void setQuantity(int id, int quantity) {
        String sql = "UPDATE inventory SET quantity = " + quantity + " WHERE itemid = " + id;
        executeUpdate(sql);
    }
    
    /**
     * Adds a new item to the inventory
     */
    public void add(int itemId, String itemName, int quantity, double price) {
        // Check if item already exists
        if (getName(itemId).isEmpty()) {
            // Item doesn't exist, insert new record
            String sql = "INSERT INTO inventory (itemid, name, quantity, price) VALUES (" + 
                          itemId + ", '" + itemName + "', " + quantity + ", " + price + ")";
            executeUpdate(sql);
            System.out.println("Item added successfully: " + itemName);
        } else {
            // Item exists, update quantity
            String sql = "UPDATE inventory SET quantity = quantity + " + quantity + 
                         ", price = " + price + " WHERE itemid = " + itemId;
            executeUpdate(sql);
            System.out.println("Item updated successfully: " + itemName);
        }
    }
    
    /**
     * Removes an item from inventory
     */
    public void removeItem(int itemId) {
        String sql = "DELETE FROM inventory WHERE itemid = " + itemId;
        executeUpdate(sql);
        System.out.println("Item with ID " + itemId + " removed from inventory.");
    }
    
    /**
     * Gets the total number of items in inventory
     */
    public int getItemCount() {
        String sql = "SELECT COUNT(*) FROM inventory";
        String result = executeQuery(sql);
        
        if (result != null && !result.isEmpty()) {
            try {
                return Integer.parseInt(result.trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing count: " + e.getMessage());
            }
        }
        return 0;
    }
    
    /**
     * Gets a list of all item IDs
     */
    public List<Integer> getAllItemIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT itemid FROM inventory";
        String result = executeQuery(sql);
        
        if (result != null && !result.isEmpty()) {
            String[] lines = result.split("\n");
            for (String line : lines) {
                try {
                    ids.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing item ID: " + e.getMessage());
                }
            }
        }
        return ids;
    }
    
    /**
     * Executes an SQL query and returns the result
     */
    private String executeQuery(String sql) {
        try {
            return DBClient.executeQuery(sql);
        } catch (Exception e) {
            System.err.println("Error executing query: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Executes an SQL update (INSERT, UPDATE, DELETE) query
     */
    private void executeUpdate(String sql) {
        try {
            DBClient.executeQuery(sql);
        } catch (Exception e) {
            System.err.println("Error executing update: " + e.getMessage());
        }
    }
}
