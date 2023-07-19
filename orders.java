import java.io.ObjectInputFilter.Status;
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
    public String           productCode; // Private Key
    public int              quantityOrdered;
    public float            priceEach; 
    public int              orderLineNumber;
    
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

            PreparedStatement pstmt = conn.prepareStatement("SELECT o.orderNumber, o.orderDate, o.requiredDate, o.shippedDate, o.status, o.comments, o.customerNumber, od.productCode, od.quantityOrdered, od.priceEach, od.orderLineNumber FROM orders o JOIN orderdetails od ON o.orderNumber=od.orderNumber WHERE o.orderNumber=? LOCK IN SHARE MODE");
            pstmt.setInt(1, orderNumber);
            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   

            List <orders> orderList = new ArrayList<>();

            while (rs.next()) {

            orders order = new orders();
                
            order.orderNumber             = rs.getInt("orderNumber");     
            order.orderDate               = rs.getString("orderDate");    
            order.requiredDate            = rs.getString("requiredDate");                
            order.shippedDate             = rs.getString("shippedDate");                
            order.status                  = rs.getString("status");
            order.comments                = rs.getString("comments");
            order.customerNumber          = rs.getInt("customerNumber");
            order.productCode             = rs.getString("productCode");
            order.quantityOrdered         = rs.getInt("quantityOrdered");     
            order.priceEach               = rs.getFloat("priceEach");
            order.orderLineNumber         = rs.getInt("orderLineNumber");

            orderList.add(order);
            }

            rs.close();

            for (orders order : orderList){
            System.out.println("Order Number: "         + order.orderNumber);
            System.out.println("Order Date: "           + order.orderDate);
            System.out.println("Required Date: "        + order.requiredDate);
            System.out.println("Shipped Date: "         + order.shippedDate);
            System.out.println("Order Status: "         + order.status);
            System.out.println("Comment: "              + order.comments);
            System.out.println("Customer Number "       + order.customerNumber);
            System.out.println("Product Code: "         + order.productCode);
            System.out.println("Quantity: "             + order.quantityOrdered);
            System.out.println("Price: "                + order.priceEach);
            System.out.println("Order Line Number: "    + order.orderLineNumber);
            System.out.println("");
            }

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

    public int cancelOrder(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextInt();

        int choice = 0;

        try {
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsales?user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, status, customerNumber FROM orders WHERE orderNumber=? FOR UPDATE");
            pstmt.setInt(1, orderNumber);
            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   

            while (rs.next()) {
                
            orderNumber             = rs.getInt("orderNumber");     
            orderDate               = rs.getString("orderDate");                 
            status                  = rs.getString("status");
            customerNumber          = rs.getInt("customerNumber");

            }

            rs.close();

            System.out.println("Order Number: "         + orderNumber);
            System.out.println("Order Date: "           + orderDate);
            System.out.println("Order Status: "         + status);
            System.out.println("Customer Number "       + customerNumber);
            System.out.println("");

            System.out.println("Do you want to cancel the order?");
            System.out.println("[1] YES [2] NO");
            choice = sc.nextInt();

            if(choice == 1){
                status = "Cancelled";
                pstmt = conn.prepareStatement ("UPDATE orders SET status=? WHERE orderNumber=?");
                pstmt.setString(1,  status);
                pstmt.setInt(2, orderNumber);

                pstmt.executeUpdate();
                System.out.println("Your order has been cancelled");
                sc.nextLine();
            }

           if(choice == 2){
                System.out.println("Order will not be Cancelled");
                sc.nextLine();
           }

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
        
        System.out.println("Enter [1] Get Product Info: [2] Cancel Order");
        choice = sc.nextInt();
        orders o = new orders();
        if (choice==1) o.getInfo();
        if (choice==2) o.cancelOrder();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
