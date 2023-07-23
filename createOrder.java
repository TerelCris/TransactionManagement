import java.sql.*;
import java.util.*;

public class createOrder {
    
    public int      customerNumber;
    public int      orderNumber;
    public String   requiredDate;
    public String   productCode;
    public int      quantityOrdered;
    public float    priceEach;
    public String   status;
    public int      orderLineNumber = 1;

    public createOrder() {}

    public int orderProduct(){
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        boolean orderAnotherProduct = true;
        orders o = new orders();
        products p = new products();

        
        System.out.println("Enter Customer Number:");
        customerNumber = sc.nextInt();
        sc.nextLine();
        
        System.out.println("Enter Required Date:");
        requiredDate = sc.nextLine();
        System.out.println();

        try{
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsales?user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            Timestamp orderDate = new Timestamp(System.currentTimeMillis());
            status = "In Process";

            List <orders> orderList = new ArrayList<>();

            while(orderAnotherProduct){
                orders order = new orders();

                System.out.println("Enter Product Code:");
                order.productCode = sc.nextLine();

                System.out.println("Enter the Quantity of the Product:");
                order.quantityOrdered = sc.nextInt(); 

                System.out.println("Enter the Price Each:");
                order.priceEach = sc.nextFloat();
                System.out.println();

                System.out.println("Do you want to add another order?");
                System.out.println("[1] YES [2] NO");
                choice = sc.nextInt();
                System.out.println();

                order.orderLineNumber = orderLineNumber++;

                orderList.add(order);

                if(choice == 1){
                    orderAnotherProduct = true;
                    sc.nextLine();
                }
               
                else{
                    orderAnotherProduct = false;
                    break;
                }
            }

            System.out.println("Customer Number" + customerNumber);
            System.out.println("Required Date of Delivery" + requiredDate);
            System.out.println("Order Details:");
            System.out.println();

            for(orders order: orderList){
                System.out.println(order.productCode);
                System.out.println(order.quantityOrdered);
                System.out.println(order.priceEach);
                System.out.println(order.orderLineNumber);
                System.out.println();
            }

            System.out.println("Confirm Order?");
            System.out.println("[1] YES [2] NO");
            choice = sc.nextInt();

            if (choice == 1) {
                PreparedStatement pstmt = conn.prepareStatement ("INSERT INTO orders (orderDate, requiredDate, status, customerNumber) " + "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pstmt.setTimestamp(1, orderDate);
                pstmt.setString(2, requiredDate);
                pstmt.setString(3, status);
                pstmt.setInt(4, customerNumber);
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderNumber = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve auto-generated orderNumber.");
                }
    
                for(orders order: orderList){
                PreparedStatement pstmtorderinfo = conn.prepareStatement ("INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) " + "VALUES (?, ?, ?, ?, ?)");
                pstmtorderinfo.setInt(1, orderNumber);
                pstmtorderinfo.setString(2, order.productCode);
                pstmtorderinfo.setInt(3, order.quantityOrdered);
                pstmtorderinfo.setFloat(4, order.priceEach);
                pstmtorderinfo.setInt(5, order.orderLineNumber);
                pstmtorderinfo.executeUpdate();

                pstmtorderinfo.close();
                }

                //p.updateInfo(); // Update the Product Quantity
                System.out.println("Order confirmed!");

                pstmt.close();
            } 
            
            if (choice == 2) {
                System.out.println("Order canceled!");
            }

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
        
        System.out.println("Enter [1] Create Order:");
        choice = sc.nextInt();
        createOrder c = new createOrder();
        if (choice==1) c.orderProduct();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
