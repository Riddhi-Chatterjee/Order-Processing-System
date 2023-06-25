/*
   *This is the implementation of an Order Processing System.
   *This system can be used by some Store Manager 
    to keep track of the customers, the orders placed, and the products of the store.
*/

//Import required packages
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IMT2020094_menu_app {
    //Set JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    //static final String DB_URL = "jdbc:mysql://localhost/companydb";
    static final String DB_URL = "jdbc:mysql://localhost/IMT2020094_DB?allowPublicKeyRetrieval=true&useSSL=false";
    //  Database credentials
    static final String USER = "root";// add your user 
    static final String PASS = "riddhi1509";// add password
      
   
    public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Connection conn = null;
    Statement stmt = null;
    //Connecting to the Database
    try{
        //Register JDBC driver
        Class.forName(JDBC_DRIVER);
        //Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        //Execute a query
        System.out.println("Creating statement...");
        stmt = conn.createStatement();


        System.out.println("Welcome to the Order Processing System!\n");

        System.out.println("Use the system as a:");
        System.out.println("1. Customer");
        System.out.println("2. Store Manager");
        System.out.println("3. Exit\n");

        System.out.print("ENTER YOUR CHOICE : ");
        
        int ch1 = sc.nextInt();

        switch(ch1)
        {
            case 1: //Customer
            {
                int my_Cust_ID = -1;
                boolean loginSuccess = false;
                boolean signUpSuccess = false;
                System.out.println("");
                System.out.println("1.Sign up");
                System.out.println("2.Login\n");

                System.out.print("ENTER YOUR CHOICE : ");
                int ch2 = sc.nextInt();
                sc.nextLine();
                if(ch2 == 1)
                {
                    String name;
                    String address;
                    String Phone_number;
                    System.out.println("\nEnter your name:");
                    name = quote(sc.nextLine());
                    System.out.println("Enter your address:");
                    address = quote(sc.nextLine());
                    System.out.println("Enter your phone number:");
                    Phone_number = quote(sc.nextLine());

                    add_Customer(stmt, name, address, Phone_number);
                    System.out.println("\nSign up was successfull!");
                    String sql;
                    ResultSet rs;
                    sql = "SELECT max(`Customer_number`) from `Customer`;";
                    rs = stmt.executeQuery(sql);
                    rs.next();
                    int Customer_number = rs.getInt("max(`Customer_number`)");
                    sql = "SELECT `Customer_ID` from `Customer` WHERE `Customer_number` = "+Customer_number+";";
                    rs = stmt.executeQuery(sql);
                    rs.next();
                    int Customer_ID = rs.getInt("Customer_ID");
                    System.out.println("Your Customer_ID is : "+Customer_ID);
                    my_Cust_ID = Customer_ID;

                    signUpSuccess = true;
                }
                else
                {
                    int attempts = 0;
                    int Customer_ID;
                    while(true)
                    {
                        attempts++;
                        System.out.print("\nEnter your Customer_ID : ");
                        Customer_ID = sc.nextInt();
                        if(validate_Customer_ID(stmt, Customer_ID))
                        {
                            System.out.println("Login was successfull!");
                            my_Cust_ID = Customer_ID;
                            loginSuccess = true;
                            break;
                        }
                        else if(attempts < 3)
                        {
                            System.out.println("Invalid Customer_ID! Try again!");
                        }
                        else
                        {
                            System.out.println("Too many unsuccessfull attempts!");
                            break;
                        }
                    }
                }

                if(loginSuccess || signUpSuccess)
                {
                    boolean flag = true;
                    while(flag)
                    {
                        System.out.println("\nChoose one of the following options:");
                        System.out.println("1.View available products");
                        System.out.println("2.Place order");
                        System.out.println("3.View orders placed (which are not yet delivered)");
                        System.out.println("4.View products ordered (which are not yet delivered)");
                        System.out.println("5.View order details");
                        System.out.println("6.Cancel order");
                        System.out.println("7.Edit profile");
                        System.out.println("8.View profile");
                        System.out.println("9.Delete account");
                        System.out.println("10.View product details");
                        System.out.println("11.Exit\n");
                        System.out.print("ENTER YOUR CHOICE : ");
                        int ch3 = sc.nextInt();
                        sc.nextLine();
                        System.out.println("");
                        switch(ch3)
                        {
                            case 1: //View available products
                            {
                                display_Available_Products(stmt);
                                break;
                            }
                            case 2: //Place order
                            {
                                ArrayList<Integer> productID = new ArrayList<Integer>();
                                while(true)
                                {
                                    System.out.println("Enter the ID of the product you want to buy:");
                                    productID.add(sc.nextInt());
                                    System.out.println("Do you want to buy more products? Enter Y/N:");
                                    String response = sc.next();
                                    if(response.equalsIgnoreCase("N"))
                                    {
                                        break;
                                    }
                                }
                                sc.nextLine();
                                System.out.println("Enter order description:");
                                String Description = quote(sc.nextLine());
                                System.out.println("Enter the date when order is placed (in YYYY-MM-DD format):");
                                String Order_date = quote(sc.next());
                                System.out.println("Your order will be delivered in 7 days!");
                                System.out.println("Please enter the appropriate delivery date (in YYYY-MM-DD format):");
                                String Delivery_date = quote(sc.next());
                                if(place_Order(stmt, productID, Description, Order_date, Delivery_date, my_Cust_ID).equals("SUCCESS"))
                                {
                                    System.out.println("Order was placed successfully!");
                                }
                                else
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                break;
                            }
                            case 3: //View orders placed (which are not yet delivered)
                            {
                                display_Orders_Placed(stmt, my_Cust_ID);
                                break;
                            }
                            case 4: //View products ordered (which are not yet delivered)
                            {
                                display_Ordered_Products(stmt, my_Cust_ID);
                                break;
                            }
                            case 5: //View order details
                            {
                                int Order_ID;
                                System.out.println("Enter the ID of the order:");
                                Order_ID = sc.nextInt();
                                if(get_Order_Details(stmt, Order_ID).equals("ERROR"))
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                break;
                            }
                            case 6: //Cancel order
                            {
                                int Order_ID;
                                System.out.println("Enter the ID of the order:");
                                Order_ID = sc.nextInt();
                                if(cancel_Order(stmt, Order_ID).equals("ERROR"))
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                else
                                {
                                    System.out.println("The order was cancelled successfully!");
                                }
                                break;
                            }
                            case 7: //Edit profile
                            { //A customer can't edit profiles of other customers
                                int Customer_ID = my_Cust_ID;
                                String name;
                                String address;
                                String Phone_number;
                                System.out.println("Please enter your new credentials");
                                System.out.println("Enter your name:");
                                name = quote(sc.nextLine());
                                System.out.println("Enter your address:");
                                address = quote(sc.nextLine());
                                System.out.println("Enter your phone number:");
                                Phone_number = quote(sc.nextLine());
                                if(edit_Customer_Details(stmt, Customer_ID, name, address, Phone_number).equals("SUCCESS"))
                                {
                                    System.out.println("Your profile was edited successfully!");
                                }
                                else
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                break;
                            }
                            case 8: //View profile
                            { //A customer can't view profiles of other customers
                                if(get_Customer_Details(stmt, my_Cust_ID).equals("ERROR"))
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                break;
                            }
                            case 9: //Delete account
                            { //A customer can't delete the accounts of other customers
                                if(delete_Customer(stmt, my_Cust_ID).equals("ERROR"))
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                else
                                {
                                    System.out.println("Your account was deleted successfully!");
                                    flag = false;
                                }
                                break;
                            }
                            case 10: //View product details
                            {
                                int Product_ID;
                                System.out.println("Enter the ID of the product whose details are required:");
                                Product_ID = sc.nextInt();
                                if(get_Product_Details(stmt, Product_ID).equals("ERROR"))
                                {
                                    System.out.println("Smomething went wrong. Please try again...");
                                }
                                break;
                            }
                            default : //Exit
                            {
                                flag = false;
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 2: //Store Manager
            {
                System.out.println("NOTE: This section of code could have asked for the Store manager's password.");
                System.out.println("      But this has not been done for ease of usage");
                boolean flag = true;
                while(flag)
                {
                    System.out.println("\nChoose one of the following options:");
                    System.out.println("1.Add product");
                    System.out.println("2.Change product price");
                    System.out.println("3.View product details");
                    System.out.println("4.Delete product");
                    System.out.println("5.View all products");
                    System.out.println("6.View available products");
                    System.out.println("7.View list of customers");
                    System.out.println("8.View list of orders");
                    System.out.println("9.Exit\n");
                    System.out.print("ENTER YOUR CHOICE : ");
                    int ch4 = sc.nextInt();
                    sc.nextLine();
                    System.out.println("");
                    switch(ch4)
                    {
                        case 1: //Add product
                        {
                            String Name;
                            String Type;
                            Float Price;
                            System.out.println("Enter the name of the product:");
                            Name = quote(sc.nextLine());
                            System.out.println("Enter the type of the product:");
                            Type = quote(sc.nextLine());
                            System.out.println("Enter the price of the product:");
                            Price = sc.nextFloat();
                            add_Product(stmt, Name, Type, Price);
                            System.out.println("Product was added successfully!");
                            break;
                        }
                        case 2: //Change product price
                        {
                            int Product_ID;
                            Float newPrice;
                            System.out.println("Enter the ID of the product:");
                            Product_ID = sc.nextInt();
                            System.out.println("Enter the new price of the product:");
                            newPrice = sc.nextFloat();
                            if(update_Product_Price(stmt, Product_ID, newPrice).equals("SUCCESS"))
                            {
                                System.out.println("The product's price was updated successfully!");
                            }
                            else
                            {
                                System.out.println("Smomething went wrong. Please try again...");
                            }
                            break;
                        }
                        case 3: //View product details
                        {
                            int Product_ID;
                            System.out.println("Enter the ID of the product:");
                            Product_ID = sc.nextInt();
                            if(get_Product_Details(stmt, Product_ID).equals("ERROR"))
                            {
                                System.out.println("Smomething went wrong. Please try again...");
                            }
                            break;
                        }
                        case 4: //Delete product
                        {
                            int Product_ID;
                            System.out.println("Enter the ID of the product:");
                            Product_ID = sc.nextInt();
                            if(delete_Product(stmt, Product_ID).equals("ERROR"))
                            {
                                System.out.println("Smomething went wrong. Please try again...");
                            }
                            else
                            {
                                System.out.println("The product was deleted successfully!");
                            }
                            break;
                        }
                        case 5: //View all products
                        {
                            display_Product_List(stmt);
                            break;
                        }
                        case 6: //View available products
                        {
                            display_Available_Products(stmt);
                            break;
                        }
                        case 7: //View list of customers
                        {
                            display_Customer_List(stmt);
                            break;
                        }
                        case 8: //View list of orders
                        {
                            display_Order_List(stmt);
                            break;
                        }
                        default : //Exit
                        {
                            flag = false;
                            break;
                        }
                    }
                }
                break;
            }
            default : //Exit
            {
                break;
            }
        }

    //Clean-up environment
        stmt.close();
        conn.close();
        sc.close();
      }catch(SQLException se){    	 //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){        	//Handle errors for Class.forName
        e.printStackTrace();
     }finally{				//finally block used to close resources
        try{
           if(stmt!=null)
              stmt.close();
        }catch(SQLException se2){
        }
        try{
           if(conn!=null)
              conn.close();
        }catch(SQLException se){
           se.printStackTrace();
        }					//end finally try
     }					//end try
     System.out.println("End of Code");
    }					//end main

    //Operations of Customer table:
    static void add_Customer(Statement stmt, String name, String address, String Phone_number) throws java.sql.SQLException
    {
        String sql = "";
        ResultSet rs;
        int Customer_number;

        sql = "SELECT max(`Customer_number`) from `Customer`;";
        rs = stmt.executeQuery(sql);
        rs.next();
        Customer_number = rs.getInt("max(`Customer_number`)") + 1;

        sql = "INSERT into `Customer` (`Name`, `Address`, `Phone_number`, `Customer_number`) values ("+name+", "+address+", "+Phone_number+", "+Customer_number+");";
        stmt.executeUpdate(sql);

        rs.close();
    }

    static String edit_Customer_Details(Statement stmt, int Customer_ID, String name, String address, String Phone_number) throws java.sql.SQLException
    {
        if(!validate_Customer_ID(stmt, Customer_ID))
        {
            return "ERROR";
        }
        String sql = "";

        sql = "UPDATE `Customer` SET `Name` = "+name+", `Address` = "+address+", `Phone_number` = "+Phone_number+" WHERE `Customer_ID` = "+Customer_ID+";";
        stmt.executeUpdate(sql);
        return "SUCCESS";
    }

    static String get_Customer_Details(Statement stmt, int Customer_ID) throws java.sql.SQLException
    {
        if(!validate_Customer_ID(stmt, Customer_ID))
        {
            return "ERROR";
        }
        String sql = "";
        ResultSet rs;

        sql = "SELECT * from `Customer` WHERE `Customer_ID` = "+Customer_ID+";";
        rs = stmt.executeQuery(sql);
        rs.next();
        //Retrieve by column name
        String Name  = rs.getString("Name");
        String Address = rs.getString("Address");
        String Phone_number = rs.getString("Phone_number");
        int Customer_number = rs.getInt("Customer_number");
        int CustomerID = rs.getInt("Customer_ID");
        //Displaying the values
        System.out.println("The requested customer details are:");
        System.out.println("Customer_ID : "+CustomerID);
        System.out.println("Customer_number : "+Customer_number);
        System.out.println("Name : "+Name);
        System.out.println("Address : "+Address);
        System.out.println("Phone_number : "+Phone_number);

        rs.close();
        return "SUCCESS";
    }

    static String delete_Customer(Statement stmt, int Customer_ID) throws java.sql.SQLException
    {
        if(!validate_Customer_ID(stmt, Customer_ID))
        {
            return "ERROR";
        }
        String sql;
        ResultSet rs;
        ArrayList<Integer> orderID = new ArrayList<Integer>();

        sql = "SELECT `Order_ID` from `Order` WHERE `Cust_ID` = "+Customer_ID+";";
        rs = stmt.executeQuery(sql);
        while(rs.next())
        {
            orderID.add(rs.getInt("Order_ID"));
        }

        int i;
        for(i=0;i<orderID.size();i++)
        {
            cancel_Order(stmt, orderID.get(i));
        }

        sql = "DELETE from `Customer` WHERE `Customer_ID` = "+Customer_ID+";";
        stmt.executeUpdate(sql);

        rs.close();
        return "SUCCESS";
    }

    static void display_Customer_List(Statement stmt) throws java.sql.SQLException
    {
        String sql = "";
        ResultSet rs;

        sql = "SELECT * from `Customer`;";
        rs = stmt.executeQuery(sql);
        System.out.println("List of Customers:");
        int count = 0;
        while(rs.next()){
            count++;
            //Retrieve by column name
            String Name  = rs.getString("Name");
            String Address = rs.getString("Address");
            String Phone_number = rs.getString("Phone_number");
            int Customer_number = rs.getInt("Customer_number");
            int Customer_ID = rs.getInt("Customer_ID");

            //Displaying the values
            System.out.println("");
            System.out.println("Customer_ID : "+Customer_ID);
            System.out.println("Customer_number : "+Customer_number);
            System.out.println("Name : "+Name);
            System.out.println("Address : "+Address);
            System.out.println("Phone_number : "+Phone_number);
        }
        if(count == 0)
        {
            System.out.println("<Empty list>");
        }

        rs.close();
    }

    //Operations of Order table:
    static String place_Order(Statement stmt, ArrayList<Integer> productID, String Description, String Order_date, String Delivery_date, int Cust_ID) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        String sql = "";
        ResultSet rs;
        int Order_number;
        float Amount;

        sql = "SELECT max(`Order_number`) from `Order`;";
        rs = stmt.executeQuery(sql);
        rs.next();
        Order_number = rs.getInt("max(`Order_number`)") + 1;

        sql = "select sum(`Price`) from Product WHERE";
        int i;
        String str = "";
        for(i=0;i<productID.size();i++)
        {
            String sql1;
            ResultSet rs1;
            sql1 = "SELECT `Ordr_ID` from `Product` WHERE `Product_ID` = "+productID.get(i)+";";
            rs1 = stmt.executeQuery(sql1);
            if(!rs1.next())
            {
                rs1.close();
                rs.close();
                return "ERROR";
            }
            String Order_ID_str = rs1.getString("Ordr_ID");
            rs1.close();
            if(Order_ID_str != null)
            {
                rs.close();
                return "ERROR";
            }

            str = str + " `Product_ID` = "+productID.get(i);
            if(i != productID.size()-1)
            {
                str = str + " OR";
            }
        }
        str = str +";";
        sql = sql + str;
        rs = stmt.executeQuery(sql);
        rs.next();
        Amount = rs.getFloat("sum(`Price`)");        

        sql = "INSERT into `Order` (`Description`, `Amount`, `Order_date`, `Delivery_date`, `Order_number`, `Cust_ID`) values ("+Description+", "+Amount+", "+Order_date+", "+Delivery_date+", "+Order_number+", "+Cust_ID+");";
        stmt.executeUpdate(sql);

        sql = "SELECT `Order_ID` from `Order` WHERE `Order_number` = "+Order_number+";";
        rs = stmt.executeQuery(sql);
        rs.next();
        int Order_ID = rs.getInt("Order_ID");

        sql = "UPDATE `Product` SET `Ordr_ID` = "+Order_ID+" WHERE";
        sql = sql + str;
        stmt.executeUpdate(sql);

        rs.close();
        return "SUCCESS";
    }

    static String get_Order_Details(Statement stmt, int Order_ID) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        if(!validate_Order_ID(stmt, Order_ID))
        {
            return "ERROR";
        }
        String sql = "";
        ResultSet rs;

        sql = "SELECT * from `Order` WHERE `Order_ID` = "+Order_ID+";";
        rs = stmt.executeQuery(sql);
        rs.next();
        //Retrieve by column name
        String Description = rs.getString("Description");
        Float Amount = rs.getFloat("Amount");
        String Order_date = rs.getString("Order_date");
        String Delivery_date = rs.getString("Delivery_date");
        int Order_number = rs.getInt("Order_number");
        int OrderID = rs.getInt("Order_ID");
        int Cust_ID = rs.getInt("Cust_ID");


        //Displaying the values
        System.out.println("The requested order details are:");
        System.out.println("Order_ID : "+OrderID);
        System.out.println("Order_number : "+Order_number);
        System.out.println("Cust_ID : "+Cust_ID);
        System.out.println("Description : "+Description);
        System.out.println("Amount : "+Amount);
        System.out.println("Order_date : "+Order_date);
        System.out.println("Delivery_date : "+Delivery_date);

        rs.close();
        return "SUCCESS";
    }

    static String cancel_Order(Statement stmt, int Order_ID) throws java.sql.SQLException
    {
        if(!validate_Order_ID(stmt, Order_ID))
        {
            return "ERROR";
        }
        String sql;
        if(isDelivered(stmt, Order_ID))
        {
            ResultSet rs;
            ArrayList<Integer> productID = new ArrayList<Integer>();
            sql = "SELECT `Product_ID` from `Product` WHERE `Ordr_ID` = "+Order_ID+";";
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                productID.add(rs.getInt("Product_ID"));
            }
            rs.close();

            int i;
            for(i=0;i<productID.size();i++)
            {
                String sql1;
                sql1 = "DELETE from `Product` WHERE Product_ID = "+productID.get(i)+";";
                stmt.executeUpdate(sql1);
            }
        }
        else
        {
            sql = "UPDATE `Product` SET `Ordr_ID` = NULL WHERE `Ordr_ID` = "+Order_ID+";";
            stmt.executeUpdate(sql);
        }

        sql = "DELETE from `Order` WHERE `Order_ID` = "+Order_ID+";";
        stmt.executeUpdate(sql);
        return "SUCCESS";
    }

    static void display_Order_List(Statement stmt) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        String sql = "";
        ResultSet rs;

        sql = "SELECT * from `Order`;";
        rs = stmt.executeQuery(sql);
        System.out.println("List of orders:");
        int count = 0;
        while(rs.next())
        {
            count++;
            //Retrieve by column name
            String Description = rs.getString("Description");
            Float Amount = rs.getFloat("Amount");
            String Order_date = rs.getString("Order_date");
            String Delivery_date = rs.getString("Delivery_date");
            int Order_number = rs.getInt("Order_number");
            int OrderID = rs.getInt("Order_ID");
            int Cust_ID = rs.getInt("Cust_ID");


            //Displaying the values
            System.out.println("");
            System.out.println("Order_ID : "+OrderID);
            System.out.println("Order_number : "+Order_number);
            System.out.println("Cust_ID : "+Cust_ID);
            System.out.println("Description : "+Description);
            System.out.println("Amount : "+Amount);
            System.out.println("Order_date : "+Order_date);
            System.out.println("Delivery_date : "+Delivery_date);
        }
        if(count == 0)
        {
            System.out.println("<Empty list>");
        }

        rs.close();
    }

    static void remove_Delivered_Orders(Statement stmt) throws java.sql.SQLException
    {
        String sql;
        ResultSet rs;
        ArrayList<Integer> orderID = new ArrayList<Integer>();

        sql = "SELECT `Order_ID` from `Order`;";
        rs = stmt.executeQuery(sql);
        while(rs.next())
        {
            orderID.add(rs.getInt("Order_ID"));
        }

        int i;
        for(i=0;i<orderID.size();i++)
        {
            if(isDelivered(stmt, orderID.get(i)))
            {
                cancel_Order(stmt, orderID.get(i));
            }
        }

        rs.close();
    }

    static void display_Orders_Placed(Statement stmt, int CustID) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        String sql;
        ResultSet rs;

        sql = "SELECT * from `Order` WHERE `Cust_ID` = "+CustID+";";
        rs = stmt.executeQuery(sql);
        System.out.println("List of orders which are not delivered yet:");
        int count = 0;
        while(rs.next())
        {
            count++;
            //Retrieve by column name
            String Description = rs.getString("Description");
            Float Amount = rs.getFloat("Amount");
            String Order_date = rs.getString("Order_date");
            String Delivery_date = rs.getString("Delivery_date");
            int Order_number = rs.getInt("Order_number");
            int OrderID = rs.getInt("Order_ID");
            int Cust_ID = rs.getInt("Cust_ID");


            //Displaying the values
            System.out.println("");
            System.out.println("Order_ID : "+OrderID);
            System.out.println("Order_number : "+Order_number);
            System.out.println("Cust_ID : "+Cust_ID);
            System.out.println("Description : "+Description);
            System.out.println("Amount : "+Amount);
            System.out.println("Order_date : "+Order_date);
            System.out.println("Delivery_date : "+Delivery_date);
        }
        if(count == 0)
        {
            System.out.println("<Empty list>");
        }


        rs.close();
    }

    //Operations of Product table:
    static void add_Product(Statement stmt, String Name, String Type, Float Price) throws java.sql.SQLException
    {
        String sql;
        ResultSet rs;

        int Product_number;

        sql = "SELECT max(`Product_number`) from `Product`;";
        rs = stmt.executeQuery(sql);
        rs.next();
        Product_number = rs.getInt("max(`Product_number`)") + 1;

        sql = "INSERT into `Product` (`Name`, `Type`, `Price`, `Product_number`, `Ordr_ID`) values ("+Name+", "+Type+", "+Price+", "+Product_number+", "+"NULL"+");";
        stmt.executeUpdate(sql);

        rs.close();
    }

    static String update_Product_Price(Statement stmt, int Product_ID, Float newPrice) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        if(!validate_Product_ID(stmt, Product_ID))
        {
            return "ERROR";
        }

        if(isOrdered(stmt, Product_ID))
        {
            return "ERROR";
        }
        String sql;

        sql = "UPDATE `Product` SET `Price` = "+newPrice+" WHERE `Product_ID` = "+Product_ID+";";
        stmt.executeUpdate(sql); 
        return "SUCCESS";
    }

    static String get_Product_Details(Statement stmt, int Product_ID) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        if(!validate_Product_ID(stmt, Product_ID))
        {
            return "ERROR";
        }
        String sql = "";
        ResultSet rs;

        sql = "SELECT * from `Product` WHERE `Product_ID` = "+Product_ID+";";
        rs = stmt.executeQuery(sql);
        rs.next();
        //Retrieve by column name
        String Name = rs.getString("Name");
        String Type = rs.getString("Type");
        Float Price = rs.getFloat("Price");
        int Product_number = rs.getInt("Product_number");
        int ProductID = rs.getInt("Product_ID");
        String Ordr_ID = rs.getString("Ordr_ID");


        //Displaying the values
        System.out.println("The requested product details are:");
        System.out.println("Product_ID : "+ProductID);
        System.out.println("Product_number : "+Product_number);
        System.out.println("Name : "+Name);
        System.out.println("Type : "+Type);
        System.out.println("Price : "+Price);
        System.out.println("Ordr_ID : "+Ordr_ID);

        rs.close();
        return "SUCCESS";
    }

    static String delete_Product(Statement stmt, int Product_ID) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        if(!validate_Product_ID(stmt, Product_ID))
        {
            return "ERROR";
        }
        String sql;

        if(!isOrdered(stmt, Product_ID))
        {
            sql = "DELETE from `Product` WHERE Product_ID = "+Product_ID+";";
            stmt.executeUpdate(sql);
            return "SUCCESS";
        }
        else
        {
            ResultSet rs;
            sql = "SELECT `Ordr_ID` from `Product` WHERE Product_ID = "+Product_ID+";";
            rs = stmt.executeQuery(sql);
            rs.next();
            int Order_ID = rs.getInt("Ordr_ID");
            rs.close();
            if(!isDelivered(stmt, Order_ID))
            {
                return "ERROR";
            }
        }
        return "SUCCESS";
    }

    static void display_Product_List(Statement stmt) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        String sql = "";
        ResultSet rs;

        sql = "SELECT * from `Product`;";
        rs = stmt.executeQuery(sql);
        System.out.println("List of products:");
        int count = 0;
        while(rs.next())
        {
            count++;
            //Retrieve by column name
            String Name = rs.getString("Name");
            String Type = rs.getString("Type");
            Float Price = rs.getFloat("Price");
            int Product_number = rs.getInt("Product_number");
            int ProductID = rs.getInt("Product_ID");
            String Ordr_ID = rs.getString("Ordr_ID");


            //Displaying the values
            System.out.println("");
            System.out.println("Product_ID : "+ProductID);
            System.out.println("Product_number : "+Product_number);
            System.out.println("Name : "+Name);
            System.out.println("Type : "+Type);
            System.out.println("Price : "+Price);
            System.out.println("Ordr_ID : "+Ordr_ID);
        }
        if(count == 0)
        {
            System.out.println("<Empty list>");
        }

        rs.close();
    }

    static void display_Available_Products(Statement stmt) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        String sql;
        ResultSet rs;

        sql = "SELECT * from `Product` WHERE `Ordr_ID` IS NULL;";
        rs = stmt.executeQuery(sql);
        System.out.println("List of available products:");
        int count = 0;
        while(rs.next())
        {
            count++;
            //Retrieve by column name
            String Name = rs.getString("Name");
            String Type = rs.getString("Type");
            Float Price = rs.getFloat("Price");
            int Product_number = rs.getInt("Product_number");
            int ProductID = rs.getInt("Product_ID");
            String Ordr_ID = rs.getString("Ordr_ID");

            //Displaying the values
            System.out.println("");
            System.out.println("Product_ID : "+ProductID);
            System.out.println("Product_number : "+Product_number);
            System.out.println("Name : "+Name);
            System.out.println("Type : "+Type);
            System.out.println("Price : "+Price);
            System.out.println("Ordr_ID : "+Ordr_ID);
        }
        rs.close();

        if(count == 0)
        {
            System.out.println("<Empty list>");
        }
    }

    static void display_Ordered_Products(Statement stmt, int Customer_ID) throws java.sql.SQLException
    {
        remove_Delivered_Orders(stmt);

        String sql;
        ResultSet rs;

        sql = "SELECT * from `Product` WHERE `Ordr_ID` IN (SELECT `Order_ID` from `Order` WHERE `Cust_ID` = "+Customer_ID+");";
        rs = stmt.executeQuery(sql);
        System.out.println("List of ordered products which are not yet delivered:");
        int count = 0;
        while(rs.next())
        {
            count++;
            //Retrieve by column name
            String Name = rs.getString("Name");
            String Type = rs.getString("Type");
            Float Price = rs.getFloat("Price");
            int Product_number = rs.getInt("Product_number");
            int ProductID = rs.getInt("Product_ID");
            String Ordr_ID = rs.getString("Ordr_ID");


            //Displaying the values
            System.out.println("");
            System.out.println("Product_ID : "+ProductID);
            System.out.println("Product_number : "+Product_number);
            System.out.println("Name : "+Name);
            System.out.println("Type : "+Type);
            System.out.println("Price : "+Price);
            System.out.println("Ordr_ID : "+Ordr_ID);
        }
        if(count == 0)
        {
            System.out.println("<Empty list>");
        }

        rs.close();
    }

    //Helper functions:
    static boolean isDelivered(Statement stmt, int Order_ID) throws java.sql.SQLException
    {
        if(!validate_Order_ID(stmt, Order_ID))
        {
            return false;
        }
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String sql = "";
        ResultSet rs;

        sql = "SELECT `Delivery_date` from `Order` WHERE `Order_ID` = "+Order_ID+";";
        rs = stmt.executeQuery(sql);
        rs.next();
        String Delivery_date = rs.getString("Delivery_date");
        if(date.compareToIgnoreCase(Delivery_date) >= 0)
        {
            //System.out.println(date+" Vs "+Delivery_date);
            //System.out.println("DELIVERED ORDER FOUND!");
            return true;
        }
        return false;
    }

    static boolean isOrdered(Statement stmt, int Product_ID) throws java.sql.SQLException
    {
        if(!validate_Product_ID(stmt, Product_ID))
        {
            return false;
        }
        String sql;
        ResultSet rs;
        sql = "SELECT `Ordr_ID` from `Product` WHERE Product_ID = "+Product_ID+";";
        rs = stmt.executeQuery(sql);
        rs.next();
        String Order_ID_str = rs.getString("Ordr_ID");
        int Order_ID = rs.getInt("Ordr_ID");

        if(Order_ID_str == null)
        {
            return false;
        }
        if(isDelivered(stmt, Order_ID))
        {
            cancel_Order(stmt, Order_ID); //Removing this order as it's already delivered
        }
        return true;
    }

    static boolean validate_Customer_ID(Statement stmt, int Customer_ID) throws java.sql.SQLException
    {
        String sql;
        ResultSet rs;

        sql = "SELECT count(`Customer_ID`) from `Customer` WHERE `Customer_ID` = "+Customer_ID+";";
        rs = stmt.executeQuery(sql);

        rs.next();
        int count = rs.getInt("count(`Customer_ID`)");
        rs.close();

        if(count == 0)
        {
            return false;
        }
        return true;
    }

    static boolean validate_Order_ID(Statement stmt, int Order_ID) throws java.sql.SQLException
    {
        String sql;
        ResultSet rs;

        sql = "SELECT count(`Order_ID`) from `Order` WHERE `Order_ID` = "+Order_ID+";";
        rs = stmt.executeQuery(sql);

        rs.next();
        int count = rs.getInt("count(`Order_ID`)");
        rs.close();

        if(count == 0)
        {
            return false;
        }
        return true;
    }

    static boolean validate_Product_ID(Statement stmt, int Product_ID) throws java.sql.SQLException
    {
        String sql;
        ResultSet rs;

        sql = "SELECT count(`Product_ID`) from `Product` WHERE `Product_ID` = "+Product_ID+";";
        rs = stmt.executeQuery(sql);

        rs.next();
        int count = rs.getInt("count(`Product_Id`)");
        rs.close();

        if(count == 0)
        {
            return false;
        }
        return true;
    }

    static String quote(String s)
    {
        return "'"+s+"'";
    }

}					//end class


//Note : By default autocommit is on. you can set to false using con.setAutoCommit(false)
