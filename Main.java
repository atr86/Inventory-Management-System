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
