import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class receipt
 {
 buy b;
 customer c;
 static int no;
 String filename;
 double sum;
 int tno;
 static
 {
	no=1;
 }
	receipt(customer c1)
	{
		c=c1;
		filename = "receipt"+no+".txt";
		sum=0.0;
		tno=0;
	}

	
	
    public void header() 
    {

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) 
		{
            writer.printf("%-10s","Money Receipt\n");
			
		    LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
			writer.print("Generated On ");
			writer.print(formattedDateTime);
			writer.println();

			writer.printf("%-20s %-30s\n", "GSTIN:", new String("290969785764231"));
            writer.println();

			writer.printf("%-20s %-20s\n", "Customer Name:", c.name);
            writer.printf("%-20s %-30s\n", "Customer GSTIN:", c.gst);
            writer.println(); //Blank line.
            writer.printf("%-10s %-15s %-20s %-15s\n", "Sl No","ItemName", "Quantity","Price"); 
            writer.println(); //Blank line.
            //System.out.println("Printed till now");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	void purchase(buy b)
	{
		tno++;
		double price;
		try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename, true))) { // Open in append mode
	        price=db.getPrice(b.itemid)*b.q;
            writer.printf("%-10d %-15s %-20d %-8.2f\n", tno, db.getName(b.itemid), b.q, price); // Corrected to use b.itemid
            writer.println(); // Blank line
			sum += price;
		}
		catch (Exception e) {
            e.printStackTrace();
        }
	}
	void trailer()
	{
		try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename, true))) 
		{ // Open in append mode
		    writer.printf("%-47s %-8.2f\n","Total Price:",sum);
            writer.println(); // Blank line.
			double gst = sum * 0.18;
            writer.printf("%-47s %-8.2f\n","GST 18%:",gst);
            writer.printf("%-47s %-8.2f\n","Grand Total:",(sum + gst));
			writer.println();
            writer.println("Thank you for your purchase!\n");
            writer.println(); // Blank line.
			writer.println("Electronically Generated Document. No signature is required. Copyright - NewIndia Inventory @2025.");           
            System.out.println("Data written to " + filename);
			no++;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
 }		