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
