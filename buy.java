import java.io.BufferedReader;
import java.io.InputStreamReader;

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

