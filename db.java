import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

class db 
{
    String sql;
	static int n;
    DBClient dc;
	db()
	{
        n=0;
        dc=new DBClient();
	}
	
	void initialize()
	{
		//create a new database with CREATE TABLE IF NOT EXISTS inventory (
        // name TEXT,
        // itemid INTEGER PRIMARY KEY,
        // quantity INTEGER,
        // price REAL
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
		 String filename="86.csv";
		 try(PrintWriter writer = new PrintWriter(new FileOutputStream(filename)))
		 {
			for(i=0;i<n;i++)
			{
				//String line=name[i]+","+itemid[i]+","+q[i]+","+price[i];
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
    static void add(int itemid,String item,int q, double sp)// db.add(itemid,item,sp,q);
    {
        sql=""
    }

}

class DBClient {
   
    String static void main(String query) {
        try {
            // SQL query to send to Python
            String query = "SELECT * FROM your_table;";

            // Run Python script with query as argument
            ProcessBuilder pb = new ProcessBuilder("python", "db_wrapper.py", query);
            Process process = pb.start();

            // Capture output (JSON result from Python)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder jsonResult = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                jsonResult.append(line);
            }

            // Print the JSON response
            System.out.println("Received from Python: " + jsonResult.toString());

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


 
 