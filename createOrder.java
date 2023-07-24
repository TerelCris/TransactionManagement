import java.sql.*;
import java.util.*;

public class createOrder {
    
    public int customerNumber;
    public int orderNumber;
    public String requiredDate;
    public List <OrderDetail> orderDetails;

    public createOrder() {
        orderDetails = new ArrayList<>();
    }

    private static class OrderDetail {
        public String productCode;
        public int quantityOrdered;
        public float priceEach;
        public int orderLineNumber;

        public OrderDetail(String productCode, int quantityOrdered, float priceEach, int orderLineNumber){
            this.productCode = productCode;
            this.quantityOrdered = quantityOrdered;
            this.priceEach = priceEach;
            this.orderLineNumber = orderLineNumber;
        }
    }

    public void orderProduct(){
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        boolean orderAnotherProduct = true;
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtOrderinfo = null;
        PreparedStatement pstmtProduct = null;
        
        System.out.println("Enter Customer Number:");
        customerNumber = sc.nextInt();
        sc.nextLine();
        
        System.out.println("Enter Required Date:");
        requiredDate = sc.nextLine();
        System.out.println();

        try{
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsales?user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            Timestamp orderDate = new Timestamp(System.currentTimeMillis());
            String status = "In Process";

            while(orderAnotherProduct){
                System.out.println("Enter Product Code:");
                String productCode = sc.nextLine();

                System.out.println("Enter Quantity:");
                int quantityOrdered = sc.nextInt(); 

                System.out.println("Enter the Price Each:");
                float priceEach = sc.nextFloat();
                System.out.println();

                orderDetails.add(new OrderDetail(productCode, quantityOrdered, priceEach, orderDetails.size() + 1));
                
                System.out.println("Do you want to add another order?");
                System.out.println("[1] YES [2] NO");
                choice = sc.nextInt();
                System.out.println();

                if(choice == 2){
                    orderAnotherProduct = false;
                    break;
                }
            }

            System.out.println("Customer Number: " + customerNumber);
            System.out.println("Required Date of Delivery: " + requiredDate);
            System.out.println("Order Details:");
            System.out.println();

            for(OrderDetail order: orderDetails){
                System.out.println("Product Code: " + order.productCode);
                System.out.println("Quantity: " + order.quantityOrdered);
                System.out.println("Price Each: " + order.priceEach);
                System.out.println("Order Line Number: " + order.orderLineNumber);
                System.out.println();
            }

            System.out.println("Confirm Order?");
            System.out.println("[1] YES [2] NO");
            choice = sc.nextInt();

            if (choice == 1) {
                pstmtOrder = conn.prepareStatement ("INSERT INTO orders (orderDate, requiredDate, status, customerNumber) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pstmtOrder.setTimestamp(1, orderDate);
                pstmtOrder.setString(2, requiredDate);
                pstmtOrder.setString(3, status);
                pstmtOrder.setInt(4, customerNumber);
                pstmtOrder.executeUpdate();

                ResultSet generatedKeys = pstmtOrder.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderNumber = generatedKeys.getInt(1);
                }

                pstmtOrderinfo = conn.prepareStatement ("INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) " + "VALUES (?, ?, ?, ?, ?)");
                for(OrderDetail order: orderDetails){
                    pstmtOrderinfo.setInt(1, orderNumber);
                    pstmtOrderinfo.setString(2, order.productCode);
                    pstmtOrderinfo.setInt(3, order.quantityOrdered);
                    pstmtOrderinfo.setFloat(4, order.priceEach);
                    pstmtOrderinfo.setInt(5, order.orderLineNumber);
                    System.out.println();
                }
                
                
                pstmtProduct = conn.prepareStatement("UPDATE products SET quantityInStock = quantityInStock - ? WHERE productCode = ?");
    
                for (OrderDetail order : orderDetails) {
                    pstmtProduct.setInt(1, order.quantityOrdered);
                    pstmtProduct.setString(2, order.productCode);
                    pstmtProduct.executeUpdate();
                }
                
                System.out.println("Order confirmed!");
            } 
            
            if (choice == 2) {
                System.out.println("Order cancelled!");
            }

            conn.commit();
            conn.close();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
