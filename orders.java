import java.sql.*;
import java.util.*;

public class orders {

    public int              orderNumber;
    public String           orderDate;
    public String           requiredDate;
    public String           shippedDate;
    public String           status;
    public String           comments;
    public int              customerNumber; //Customer Number is a Foreign Key
    
    public orders() {}
    
    public int getInfo(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextInt();
        
        try {
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsales?user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber FROM orders WHERE orderNumber=? LOCK IN SHARE MODE");
            pstmt.setInt(1, orderNumber);

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   

            while (rs.next()) {
            orderNumber         = rs.getInt("orderNumber");     
            orderDate           = rs.getString("orderDate");    
            requiredDate        = rs.getString("requiredDate");                
            shippedDate         = rs.getString("shippedDate");                
            status              = rs.getString("status");
            comments            = rs.getString("comments");
            customerNumber      = rs.getInt("customerNumber");
            }

            rs.close();

            System.out.println("Order Number: "         + orderNumber);
            System.out.println("Order Date: "           + orderDate);
            System.out.println("Required Date: "        + requiredDate);
            System.out.println("Shipped Date: "         + shippedDate);
            System.out.println("Order Status: "         + status);
            System.out.println("Comment: "              + comments);
            System.out.println("Customer Number "       + customerNumber);
            
            System.out.println("Press enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
    
    public static void main(String args[]) {
        Scanner sc = new Scanner (System.in);
        int choice = 0;
        
        System.out.println("Enter [1] Get Product Info:");
        choice = sc.nextInt();
        orders o = new orders();
        if (choice==1) o.getInfo();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
