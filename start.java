import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



class Main {
    public static void main(String[] args) 
    {
        try
        {
        int n;
        int k = 0;
        receipt r;
        contract con;
        db d = new db(1000);
        d.initialize();
        d.extractFull();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to Invertory Management System");
        System.out.println("Name of the inventory - NewIndia Inventory");
        System.out.println();
        while (true) {
            System.out.println("Enter 1 if you are a Customer, 2 if you are a Seller, 3 if you want to see the Inventory state, 4 to exit");
            n = Integer.parseInt(br.readLine());
            switch (n) {
                case 1:
                    System.out.println("Welcome Customer to Inventory");
                    System.out.println("Please enter the following details to get started");
                    customer c = new customer();
                    c.input();
                    r = new receipt(c);
                    r.header();
                    buy b;
                    k = 0;
                    while (true) {
                        b = new buy();
                        b.run();
                        if (b.itemid != -9999&& b.status==true)
                            r.purchase(b);
                        System.out.println("Enter YES if you want More purchases else NO");
                        String ch = br.readLine();
                        if ((ch).compareToIgnoreCase("NO") == 0)
                            break;
                        k++;
                    }
                    r.trailer();
                    break;
                case 2:
                    System.out.println("Welcome Seller to Inventory");
                    System.out.println("Please enter the following details to get started");
                    seller selr = new seller();
                    selr.input();
                    con = new contract(selr);
                    con.header();
                    sell s;
                    k = 0;
                    while (true) {
                        s = new sell();
                        s.run();
                        if (s.status)
                            {
                                con.purchase(s);
                            }
                        System.out.println("Enter YES if you want to sell more items else NO");
                        String ch = br.readLine();
                        if ((ch).compareToIgnoreCase("NO") == 0)
                            break;
                        k++;
                    }
                    con.trailer();
                    break;
                case 3:
                    d.update();
                    d.display();
                    break;
                default:
                    break;
            }
            if (n >= 4)
                break;
        }
        d.update();
    }
    
    catch(Exception e)
    {
        e.printStackTrace();
    }
}
}

class customer
{
	String name;
	String gst;
	customer()
	{
		name="";
		gst="";
	}
	
	void input()
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Customer name");
		name = sc.nextLine(); // Use nextLine() to read the full name
		System.out.println("Enter GST id");
		gst = sc.nextLine(); // Use nextLine() to read the GST ID
	}

	void display()
	{
		System.out.println("Customer Name: " + name);
		System.out.println("GST ID: " + gst);
	}

	public static void main(String args[])
	{
		customer c = new customer();
		c.input();
		c.display();
		
	}
}

class buy
{
	int q;
	int itemid;
	private String item;
	boolean status;
	
	public buy()
	{
		q=0;
		itemid=0;
		item="";
	}
	
	public void run()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.println("Enter the name of the item");
			item = br.readLine();
			if(find() == false)
			{	
				System.out.println("Not found!");
				return;
			}
			else
			{
				System.out.println("Enter the no of items");
				q = Integer.parseInt(br.readLine());
				int n = db.getQuantity(itemid);
				if(q > n)
				{
					System.out.println("Not enough no. of products in inventory is remaining. Only " + n + " left");
					status =false;
					return;
				}
				else
				{
					status=true;
					db.withdraw(itemid, q);
					System.out.println("The product is available... Transaction processing");
					System.out.println("You have brought " + q + " units of " + db.getName(itemid));
	            }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	boolean find()
	{	
		
		item.trim();
		item.toLowerCase();
		item=removeExcessSpaces(item);
		int search=db.searchName(item);
		itemid=search;
		return(search!=-9999);
	}
	
	String removeExcessSpaces(String str)
	{
		int i;
		char ch=str.charAt(0);
		char ch1;
		String str2="";
		str2+=ch;
		for(i=1;i<str.length();i++)
		{
			ch1=str.charAt(i);
			if(ch1==' '&& ch==' ')
				;
			else
				str2+=ch1;
		}
		str=str2;
		return str;
			
	}
}

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
 class seller
 {
     String name;
     String gst;
     seller()
     {
     }
     
     void input()
     {
         Scanner sc = new Scanner(System.in);
         System.out.println("Enter Seller name");
         name= sc.next()+sc.nextLine();
         System.out.println("Enter GSTIN");
         gst= sc.nextLine();
     }
     public static void main(String args[])
     {
         
         seller s = new seller();
         s.input();
         s.display();
     }
     void display()
     {
         System.out.println("Seller Name: " + name);
         System.out.println("GST ID: " + gst);
     }
 
 }
 
 class sell
 {
     int q;
     int itemid;
     String item;
     double sp;
     double pp;
     boolean status;
     sell()
     {
 
     }
     
     public void run()
     {
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         System.out.println("Enter the name of the item");
         try {
             item = br.readLine();
             int k = 0;
 
             if (find() == false)
             {
                 System.out.println("Not found!");
                 System.out.println("This is a new item you are selling into the Inventory");
                 
                 System.out.println("Enter no. of items you are selling, selling price, and proposed price to pay to you");
                 q = Integer.parseInt(br.readLine());
                 sp = Double.parseDouble(br.readLine());
                 pp = Double.parseDouble(br.readLine());
                 while ((sp - pp) < 0.05 * sp) // less than 5 percent profit
                 {
                     System.out.println("Not agreed to the offer");
                     System.out.println("Lower the proposed price:");
                     pp = Double.parseDouble(br.readLine());
                     k++;
                     if (k > 2) {
                         System.out.println("Offer Declined");
                         status = false;
                         return;
                     }
                 }
                 System.out.println("Offer Accepted");
                 status = true;
                 db.n++;
                 db.itemid[db.n - 1] = itemid;
                 db.name[db.n - 1] = item;
                 db.price[db.n - 1] = sp;
                 db.q[db.n - 1] = q;
             }
             else
             {
                 System.out.println("Enter the no of items and proposed price");
                 q = Integer.parseInt(br.readLine());
                 pp = Double.parseDouble(br.readLine());
                 sp = db.getPrice(itemid);
                 System.out.println("Selling price is - " + sp);
                 while ((sp - pp) < 0.05 * sp) // less than 5 percent profit
                 {
                     System.out.println("Not agreed to the offer");
                     System.out.println("Lower the proposed price:");
                     pp = Double.parseDouble(br.readLine());
                     k++;
                     if (k > 3) {
                         System.out.println("Offer Declined");
                         status = false;
                         return;
                     }
                 }
                 System.out.println("Offer Accepted");
                 status = true;
                 db.setQuantity(itemid,db.getQuantity(itemid)+q);
             }
         } catch (IOException e) {
             System.out.println("Error reading input: " + e.getMessage());
         }
     }
     
     
     boolean find()
     {	
         
         item.trim();
         item.toLowerCase();
         removeExcessSpaces(item);
         int search=db.searchName(item);
         if(search==-9999)
         {
             itemid=db.getItemid(db.n-1)+1;;
             return false;
         }
         else
         {
             itemid=search;
             return true;
         }
     }
     
     void removeExcessSpaces(String str)
     {
         int i;
         char ch=str.charAt(0);
         char ch1;
         String str2="";
         str2+=ch;
         for(i=1;i<str.length();i++)
         {
             ch1=str.charAt(i);
             if(ch1==' '&& ch==' ')
                 ;
             else
                 str2+=ch1;
         }
         str=str2;
             
     }
}

class contract {

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

class db 
{
	static int itemid[];
	static String name[];
	static int q[];
	static double price[];
	static int n;
	db(int n1)
	{
		name= new String[n1];
		itemid= new int[n1];
		q= new int[n1];
		price=new double[n1];
	}
	
	void initialize()
	{
		String line, filePath="database.csv";
		try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
         	{
				n=0;
         		while ((line = br.readLine()) != null) 
         		{
         			n++;
         		}
				// line=line+"";
         		// name= new String[n];
	    		// itemid= new int[n];
                // q= new int[n];
	    		// price=new double[n];
				//System.out.println(n);
         	}
         	catch(IOException e) 
         	{
                   e.printStackTrace();
        	}
           
	}
	
	
	    
	public void extractFull() 
     {
        String filePath = "database.csv";  // Path to your CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
         {
            String line;
            int k=0;//index
            while ((line = br.readLine()) != null) 
            {
                String[] values = line.split(",");  // Split by comma
                name[k]=values[0];
                itemid[k]=Integer.parseInt(values[1]);
                q[k]=Integer.parseInt(values[2]);
                price[k]=Double.parseDouble(values[3]);
                k++;
            } 
		 }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    static int searchName(String item)
    {
    	for(int i=0;i<n;i++)
    	{
    		if(item.equalsIgnoreCase(name[i]))
    		return itemid[i];
    	}
    	return -9999;
    }
	static String getName(int x)
	{
		for(int i=0;i<n;i++)
		{
			if(x==itemid[i])
			return name[i];
		}
		return ""; // Return a null value if no match is found
	}
    static int getQuantity(int x)
    {
    	for(int i=0;i<n;i++)
    	{
    		if(x==itemid[i])
    		return q[i];
    	}
    	return -9999;
    }
    
    static void withdraw(int id, int n)
    {
    	for(int i=0;i<n;i++)
    	{
    		if(id==itemid[i])
    		{
    			q[i]-=n;
    			break;
    		}
    	}
    }
    
    static double getPrice(int id)
    {
    	for(int i=0;i<n;i++)
    	{
    		if(id==itemid[i])
    		{
    			return price[i];
    		}
    	}
    	return -9999.00;
    }

	public void display()
	{
		 int i;
		 System.out.println("Name \t\t\tQuantity \t\tPrice");
			 for(i=0;i<n;i++)
			 {
					String line=name[i]+"\t\t\t"+q[i]+"\t\t\t"+price[i];
					System.out.println(line);
		 	 }
	
	}

	public void update() 
	{
		 int i;
		 String filename="database.csv";
		 try(PrintWriter writer = new PrintWriter(new FileOutputStream(filename)))
		 {
			for(i=0;i<n;i++)
			{
				String line=name[i]+","+itemid[i]+","+q[i]+","+price[i];
				writer.println(line);	
		 	}
	    }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    static void setQuantity(int id, int q1)
        {
            for(int i=0;i<n;i++)
    	     {
    		    if(id==itemid[i])
    		    {
    			    q[i]=q1;
                    return;
    		    }
    	    }
        return;
        }
	
    static int getItemid(int index)
        {
            return itemid[index];
        }

	// public static void main(String args[])
	// {
	// 	db d= new db(1000);
	// 	d.initialize();
	// 	d.extractFull();
	// 	d.display();
	// 	System.out.println("Guava result"+db.searchName("guava"));
	// 	d.update();
	// 	d.display(); 

	//  }

}



 
 
 
 
