lass contract {

    seller s; // Add a seller reference
    static int no;
    String filename;
    double sum;
    int tno;

    static {
        no = 1;
    }

    contract(seller s1) 
    {
        s=s1;
        filename = "contract" + no + ".txt";
        sum = 0.0;
        tno = 0;
    }


    public void header() {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            writer.printf("%-10s", "Contract Receipt\n");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            writer.print("Generated On ");
            writer.print(formattedDateTime);
            writer.println();

            writer.printf("%-20s %-30s\n", "GSTIN:", new String("290969785764231"));
            writer.println();

            
                writer.printf("%-20s %-20s\n", "Seller Name:", s.name);
                writer.printf("%-20s %-30s\n", "Seller GSTIN:", s.gst);
                writer.println(); //Blank line.
            writer.printf("%-10s %-15s %-20s %-15s\n", "Sl No", "ItemName", "Quantity", "Selling Price At Inventory");
            writer.println(); // Blank line.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void purchase(sell se) { 
        // if (se.q <= 0 || se.pp <= 0) { // Validate quantity and price
        //     System.err.println("Error: Invalid sell object. Quantity and price must be greater than 0.");
        //     return;
        // }
        tno++;
        double price;
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename, true))) { // Open in append mode
            price = se.q * se.pp; // Calculate price based on quantity and proposed price
            writer.printf("%-10d %-15s %-20d %-8.2f\n", tno, se.item, se.q, price);
            writer.println(); // Blank line
            sum += price;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void trailer() {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename, true))) { // Open in append mode
            writer.printf("%-47s %-8.2f\n", "Total Amount:", sum);
            writer.println(); // Blank line.
            double gst = sum * 0.18;
            writer.printf("%-47s %-8.2f\n", "GST 18%:", gst);
            writer.printf("%-47s %-8.2f\n", "Grand Total:", (sum + gst));
            writer.println();
            
            writer.println("Thank you for your sale!\n");
            writer.println("You are liable to obtain Rs "+(sum+gst)+" from Inventory. Please collect the amount from office.");
            writer.println(); // Blank line.
            writer.println("Electronically Generated Document. No signature is required. Copyright - NewIndia Inventory @2025.");
            System.out.println("Data written to " + filename);
            no++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}