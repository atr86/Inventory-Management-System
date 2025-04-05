import java.util.Scanner;

public class customer {
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

