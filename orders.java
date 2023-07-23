import java.sql.*;
import java.util.*;

public class orders {
    public int      orderNumber;
    public String   orderDate;
    public String   requiredDate;
    public String   shippedDate;
    public String   status;
    public String   comments;
    public int      customerNumber; 
    public String   productCode; 
    public int      quantityOrdered;
    public float    priceEach; 
    public int      orderLineNumber;
    public String   productName;
    public String   productLine;
    public int      quantityInStock;
    public float    buyPrice;
    public float    MSRP;
    public List <OrderDetail> orderDetails;

    public orders() {
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

    public void createOrder(){
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

    public int getOrderInfo(){
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
                
            orderNumber                     = rs.getInt("orderNumber");     
            orderDate                       = rs.getString("orderDate");    
            requiredDate                    = rs.getString("requiredDate");                
            shippedDate                     = rs.getString("shippedDate");                
            status                          = rs.getString("status");
            comments                        = rs.getString("comments");
            customerNumber                  = rs.getInt("customerNumber");
            
            order.productCode               = rs.getString("productCode");
            order.quantityOrdered           = rs.getInt("quantityOrdered");     
            order.priceEach                 = rs.getFloat("priceEach");
            order.orderLineNumber           = rs.getInt("orderLineNumber");
            orderList.add(order);
            }

            rs.close();
            System.out.println("Order Number: "         + orderNumber);
            System.out.println("Order Date: "           + orderDate);
            System.out.println("Required Date: "        + requiredDate);
            System.out.println("Shipped Date: "         + shippedDate);
            System.out.println("Order Status: "         + status);
            System.out.println("Comment: "              + comments);
            System.out.println("Customer Number "       + customerNumber);
            System.out.println();

            for (orders order : orderList){
            System.out.println("Product Code: "         + order.productCode);
            System.out.println("Quantity: "             + order.quantityOrdered);
            System.out.println("Price: "                + order.priceEach);
            System.out.println("Order Line Number: "    + order.orderLineNumber);
            System
            .out.println();
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

    public int getProductInfo()     {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();
        
        try {
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsales?user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);     
                  
            PreparedStatement pstmt = conn.prepareStatement("SELECT productName, productLine, quantityInStock, buyPrice, MSRP FROM products WHERE productCode=? LOCK IN SHARE MODE");
            pstmt.setString(1, productCode);
            
            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   
            
            while (rs.next()) {
                productName     = rs.getString("productName");
                productLine     = rs.getString("productLine");
                quantityInStock = rs.getInt("quantityInStock");
                buyPrice        = rs.getFloat("buyPrice");
                MSRP            = rs.getFloat("MSRP");
            }
            
            rs.close();
            
            System.out.println("Product Name: " + productName);
            System.out.println("Product Line: " + productLine);
            System.out.println("Quantity:     " + quantityInStock);
            System.out.println("Buy Price:    " + buyPrice);
            System.out.println("MSRP:         " + MSRP);
            
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
            System.out.println();
            System.out.println("Do you want to cancel the order?");
            System.out.println("[1] YES [2] NO");
            choice = sc.nextInt();

            if(choice == 1){
                if(!status.equals("Shipped")){
                    status = "Cancelled";
                    pstmt = conn.prepareStatement ("UPDATE orders SET status=? WHERE orderNumber=?");
                    pstmt.setString(1,  status);
                    pstmt.setInt(2, orderNumber);
                    pstmt.executeUpdate();
                    System.out.println("Your order has been cancelled");
                    sc.nextLine();
                }
                else{
                    System.out.println("Shipped orders cannot be cancelled");
                }
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
    
    public static void main(String[] args){
        orders o = new orders();
        
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        
        //Prompts the user to choose their action
        System.out.println("Welcome! Choose your option");
        System.out.println("[1] Create and Order        [2]Inquire Products");
        System.out.println("[3] Retrieve Order Info     [4]Cancel Order");
        System.out.println();
        System.out.println("Enter your choice:");
        choice = sc.nextInt();
        if (choice == 1) o.createOrder();
        if (choice == 2) o.getOrderInfo();
        if (choice == 3) o.getProductInfo();
        if (choice == 4) o.cancelOrder();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }

}
