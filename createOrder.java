import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class createOrder {
    
    public int customerNumber;
    public int orderNumber;
    public LocalDateTime orderDate;
    public String requiredDate;
    public String productCode;
    public int quantityOrdered;
    public float priceEach;
    public int quantityInStock;
    public float buyPrice;
    public float MSRP;

    public createOrder() {}

    public int orderProduct(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter Customer Number:");
        customerNumber = sc.nextInt();
        sc.nextLine();
        
        System.out.println("Enter Required Date:");
        requiredDate = sc.nextLine();
        System.out.println();

        int choice = 0;
        boolean orderAnotherProduct = true;

        try{
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsales?user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, orderDate, status, customerNumber FROM orders WHERE orderNumber=? FOR UPDATE");
            pstmt.setInt(1, orderNumber);

            ResultSet rs = pstmt.executeQuery();   

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

                if(choice == 1){
                    orderAnotherProduct = true;
                    sc.nextLine();
                }
               
                else{
                    orderAnotherProduct = false;
                    break;
                }
            }

            for(orders order: orderList){
                System.out.println(customerNumber);
                System.out.println(requiredDate);
                System.out.println(order.productCode);
                System.out.println(order.quantityOrdered);
                System.out.println(order.priceEach);
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
        
        System.out.println("Enter [1] Create Order:");
        choice = sc.nextInt();
        createOrder c = new createOrder();
        if (choice==1) c.orderProduct();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
