import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

class db {
    private static final String DB_NAME = "inventory.db";
    private static final String syscall = "py";
    public db() {
        // Initialize the database when creating a db object
        initialize();
    }

    /**
     * Initializes the database by creating the inventory table if it doesn't exist
     */
    public static void initialize() {
        try {
            String line;
            StringBuilder result = new StringBuilder();
            ProcessBuilder pb = new ProcessBuilder(syscall, "convert_csvtodb.py");
            Process process = pb.start();
            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Read error stream if process failed
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorMessage = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                System.err.println("Python process error: " + errorMessage);
            }
            System.out.println(result.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        System.out.println("Database retrieved from database.csv");

        String createTableSQL = "CREATE TABLE IF NOT EXISTS inventory (" +
                "name TEXT NOT NULL, " +
                "itemid INTEGER PRIMARY KEY, " +
                "quantity INTEGER, " +
                "price REAL)";

        executeUpdate(createTableSQL);
        System.out.println("Database initialized successfully.");
        }
    }

    /**
     * Searches for an item by name and returns its ID
     */
    public static int searchName(String item) {
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
    public static String getName(int id) {
        String sql = "SELECT name FROM inventory WHERE itemid = " + id;
        String result = executeQuery(sql);

        return (result != null && !result.isEmpty()) ? result.trim() : "";
    }

    /**
     * Gets the quantity of an item by its ID
     */
    public static int getQuantity(int id) {
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
    public static void withdraw(int id, int amount) {
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
    public static double getPrice(int id) {
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
    public static void display() {
        String sql = "SELECT name, quantity, price FROM inventory";
        String result = executeQuery(sql);

        System.out.println("Name\t\t\tQuantity\t\tPrice");
        System.out.println("-------------------------------------------------");

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
    public static void setQuantity(int id, int quantity) {
        String sql = "UPDATE inventory SET quantity = " + quantity + " WHERE itemid = " + id;
        executeUpdate(sql);
    }

    /**
     * Adds a new item to the inventory
     */
    public static void add(int itemId, String itemName, int quantity, double price) {
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
    public static void removeItem(int itemId) {
        String sql = "DELETE FROM inventory WHERE itemid = " + itemId;
        executeUpdate(sql);
        System.out.println("Item with ID " + itemId + " removed from inventory.");
    }

    /**
     * Gets the total number of items in inventory
     */
    public static int getItemCount() {
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
    public static List<Integer> getAllItemIds() {
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
    private static String executeQuery(String sql) {
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
    private static void executeUpdate(String sql) {
        try {
            DBClient.executeQuery(sql);
        } catch (Exception e) {
            System.err.println("Error executing update: " + e.getMessage());
        }
    }
    public static void saveInCsv()
    {
         try {
            String line;
            StringBuilder result = new StringBuilder();
            ProcessBuilder pb = new ProcessBuilder(syscall, "convert_dbtocsv.py");
            Process process = pb.start();
            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Read error stream if process failed
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorMessage = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                System.err.println("Python process error: " + errorMessage);
            }
            System.out.println(result.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("Database saved to database.csv");
        }

    }

    /**
     * Starts nl_to_sql.py as a subprocess and keeps a conversation loop alive.
     * Each question is written to the process stdin; stdout/stderr are printed live.
     * The loop ends when the user types "quit" (case-insensitive).
     *
     * @param br The BufferedReader connected to System.in
     */
    public static void runNlToSqlSession(BufferedReader br) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(syscall, "nl_to_sql.py", "--repl");
        pb.directory(new java.io.File("."));
        pb.redirectErrorStream(true); // merge stderr into stdout
        Process process = pb.start();

        // Writer to send questions into the subprocess stdin
        BufferedWriter processIn = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream()));

        // Reader to receive the subprocess output
        BufferedReader processOut = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        // Drain subprocess output in a background thread so it never blocks
        Thread outputDrainer = new Thread(() -> {
            try {
                String line;
                while ((line = processOut.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ignored) {
            }
        });
        outputDrainer.setDaemon(true);
        outputDrainer.start();

        // Main question-answer loop
        while (true) {
            System.out.print("\nYour question (or 'quit'): ");
            String question = br.readLine();
            if (question == null || question.trim().equalsIgnoreCase("quit")) {
                processIn.write("quit\n");
                processIn.flush();
                break;
            }
            processIn.write(question + "\n");
            processIn.flush();
            // Small pause so the drainer thread can print the response
            // before the next prompt appears
            Thread.sleep(4000);
        }

        processIn.close();
        process.waitFor();
        System.out.println("\nAI Investigator session ended.");
    }
}
