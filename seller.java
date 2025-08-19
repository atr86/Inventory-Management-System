import java.io.*;
import java.util.*;
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