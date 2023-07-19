import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args){
        orders o = new orders();
        orderdetails od = new orderdetails();
        products p = new products();
        
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        
        //Prompts the user to choose their action
        System.out.println("Welcome! Choose your option");
        System.out.println("[1] Create and Order        [2]Inquire Products");
        System.out.println("[3] Retrieve Order Info     [4]Cancel Order");
        System.out.println("");

        System.out.println("Enter your choice:");
        choice = sc.nextInt();
        if (choice == 1);
        if (choice == 2) p.getInfo();
        if (choice == 3) o.getInfo();
        if (choice == 4);
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
