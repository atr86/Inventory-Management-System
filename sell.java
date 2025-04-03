import java.io.*;
import java.util.*;
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
                 db.add(itemid,item,q,sp);
                //  db.itemid[db.n - 1] = itemid;
                //  db.name[db.n - 1] = item;
                //  db.price[db.n - 1] = sp;
                //  db.q[db.n - 1] = q;
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
             itemid=db.getItemid(db.n-1)+1;
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
