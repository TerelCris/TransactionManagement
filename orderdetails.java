import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class orderdetails {
    
    public int orderNumber; //Private Key
    public String productCode; // Private Key
    public int quantityOrdered;
    public float priceEach; 
    public int orderLineNumber;

    public orderdetails() {}

    public int getInfo(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextInt();

        try{
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false); 

            PreparedStatement pstmt = conn.prepareStatement("SELECT orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber FROM orderdetails WHERE orderNumber=? LOCK IN SHARE MODE");
            pstmt.setInt(1, orderNumber);

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();   

            while (rs.next()) {
            orderNumber             = rs.getInt("orderNumber");
            productCode             = rs.getString("productCode");
            quantityOrdered         = rs.getInt("quantityOrdered");     
            priceEach               = rs.getFloat("priceEach");
            orderLineNumber         = rs.getInt("orderLineNumber");
            }

            rs.close();

            System.out.println("Order Number: "         + orderNumber);
            System.out.println("Product Code: "         + productCode);
            System.out.println("Quantity: "             + quantityOrdered);
            System.out.println("Price: "                + priceEach);
            System.out.println("Order Line Number: "    + orderLineNumber);
                
            System.out.println("Press enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;

        }catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

     public static void main(String args[]) {
        // TODO code application logic here
    }
}
