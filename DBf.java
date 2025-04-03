import java.util.Scanner;

class InventoryManagementApp {
    private static Scanner scanner = new Scanner(System.in);
    private static db inventory;
    
    public static void main(String[] args) {
        // Initialize inventory database
        inventory = new db();
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    withdrawItem();
                    break;
                case 3:
                    displayInventory();
                    break;
                case 4:
                    searchByName();
                    break;
                case 5:
                    viewItemDetails();
                    break;
                case 6:
                    updateQuantity();
                    break;
                case 7:
                    removeItem();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
    
    private static void displayMenu() {
        System.out.println("\n===== INVENTORY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add New Item");
        System.out.println("2. Withdraw Items");
        System.out.println("3. Display All Inventory");
        System.out.println("4. Search Item by Name");
        System.out.println("5. View Item Details");
        System.out.println("6. Update Item Quantity");
        System.out.println("7. Remove Item");
        System.out.println("0. Exit");
        System.out.println("======================================");
    }
    
    private static void addItem() {
        System.out.println("\n----- ADD NEW ITEM -----");
        int itemId = getIntInput("Enter Item ID: ");
        String name = getStringInput("Enter Item Name: ");
        int quantity = getIntInput("Enter Quantity: ");
        double price = getDoubleInput("Enter Price: $");
        
        inventory.add(itemId, name, quantity, price);
    }
    
    private static void withdrawItem() {
        System.out.println("\n----- WITHDRAW ITEMS -----");
        int itemId = getIntInput("Enter Item ID: ");
        String name = inventory.getName(itemId);
        
        if (name.isEmpty()) {
            System.out.println("Item not found!");
            return;
        }
        
        int currentQty = inventory.getQuantity(itemId);
        System.out.println("Item: " + name + " (Current Quantity: " + currentQty + ")");
        
        int withdrawQty = getIntInput("Enter quantity to withdraw: ");
        if (withdrawQty <= 0) {
            System.out.println("Invalid quantity. Must be greater than 0.");
            return;
        }
        
        if (withdrawQty > currentQty) {
            System.out.println("Error: Cannot withdraw more than available quantity.");
            return;
        }
        
        inventory.withdraw(itemId, withdrawQty);
        System.out.println("Withdrawal successful. New quantity: " + inventory.getQuantity(itemId));
    }
    
    private static void displayInventory() {
        System.out.println("\n----- INVENTORY LISTING -----");
        inventory.display();
    }
    
    private static void searchByName() {
        System.out.println("\n----- SEARCH BY NAME -----");
        String name = getStringInput("Enter item name to search: ");
        int itemId = inventory.searchName(name);
        
        if (itemId != -9999) {
            System.out.println("Item found! ID: " + itemId);
            displayItemInfo(itemId);
        } else {
            System.out.println("Item not found!");
        }
    }
    
    private static void viewItemDetails() {
        System.out.println("\n----- VIEW ITEM DETAILS -----");
        int itemId = getIntInput("Enter Item ID: ");
        displayItemInfo(itemId);
    }
    
    private static void displayItemInfo(int itemId) {
        String name = inventory.getName(itemId);
        if (!name.isEmpty()) {
            int quantity = inventory.getQuantity(itemId);
            double price = inventory.getPrice(itemId);
            
            System.out.println("Item Details:");
            System.out.println("ID: " + itemId);
            System.out.println("Name: " + name);
            System.out.println("Quantity: " + quantity);
            System.out.println("Price: $" + price);
        } else {
            System.out.println("Item not found!");
        }
    }
    
    private static void updateQuantity() {
        System.out.println("\n----- UPDATE QUANTITY -----");
        int itemId = getIntInput("Enter Item ID: ");
        String name = inventory.getName(itemId);
        
        if (name.isEmpty()) {
            System.out.println("Item not found!");
            return;
        }
        
        int currentQty = inventory.getQuantity(itemId);
        System.out.println("Item: " + name + " (Current Quantity: " + currentQty + ")");
        
        int newQty = getIntInput("Enter new quantity: ");
        if (newQty < 0) {
            System.out.println("Invalid quantity. Must be 0 or greater.");
            return;
        }
        
        inventory.setQuantity(itemId, newQty);
        System.out.println("Quantity updated successfully.");
    }
    
    private static void removeItem() {
        System.out.println("\n----- REMOVE ITEM -----");
        int itemId = getIntInput("Enter Item ID to remove: ");
        String name = inventory.getName(itemId);
        
        if (name.isEmpty()) {
            System.out.println("Item not found!");
            return;
        }
        
        System.out.println("Item to remove: " + name);
        String confirm = getStringInput("Are you sure you want to remove this item? (y/n): ");
        
        if (confirm.equalsIgnoreCase("y")) {
            inventory.removeItem(itemId);
            System.out.println("Item removed successfully.");
        } else {
            System.out.println("Operation cancelled.");
        }
    }
    
    // Input helper methods
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}