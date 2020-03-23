package jdbcproject;

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;

// TODO: Auto-generated Javadoc
/**
 * The Class ProjectMain.
 */
public class ProjectMain {
	
	
	
	
	 /** The user. */
 	private String user;
	 
 	/** The pass. */
 	private String pass;
	 
 	/** The db name. */
 	private String dbName;
	 
 	/** The input. */
 	private static Scanner in = new Scanner(System.in);   
	    
    	/** The Constant driver. */
    static final String driver = "org.apache.derby.jdbc.ClientDriver";
	    
    	/** The con. */
    static Connection con;
    private static void dataMenu() {
    	System.out.println("Welcome to the data view menu");
    	System.out.println("Please choose one of the following:");
    	System.out.println("1: View Writing Groups");
        System.out.println("2: View data of specific Writing Group");
        System.out.println("3: View Publishers");
        System.out.println("4: View data of specific Publisher");
        System.out.println("5: List all book titles");
        System.out.println("6: View data on specific bookTitle");
        System.out.println("7: Go back");
        int choice = acceptMenuInput(7);
        switch(choice) {
    	case 1:
    		System.out.println(DBLibrary.viewWritingGroups(con));
    		break;
    	case 2:
    		System.out.println(DBLibrary.viewWritingGroupData(in, con));
    		break;
    	case 3:
    		System.out.println(DBLibrary.viewPublishers(con));
    		break;
    	case 4:
    		System.out.println(DBLibrary.viewPublishersData(in, con));
    		break;
    	case 5:
    		System.out.println(DBLibrary.viewBooks(con));
    		break;
    	case 6:
    		System.out.println(DBLibrary.viewBooksData(in, con));
    		break;
    	case 7:
    		break;
        }
    }
    
    private static void insertMenu() {
    	System.out.println("Welcome to the insert menu");
    	System.out.println("Please choose one of the following:");
    	System.out.println("1: Insert Writing Group");
        System.out.println("2: Insert Book");
        System.out.println("3: Insert Independent Publisher");
        System.out.println("4: Insert Publisher With TakeOver");
        System.out.println("5: Go back");
        int choice = acceptMenuInput(5);
        switch(choice) {
    	case 1:
    		DBLibrary.insertWritingGroup(in, con);
    		break;
    	case 2:
    		DBLibrary.insertBook(con, in);
    		break;
    	case 3:
    		DBLibrary.insertPublishers(in, con);
    		break;
    	case 4:
    		DBLibrary.usurpPublisher(con, in);
    		break;
    	case 5:
    		break;
        }
        
    }
    
    private static void removeMenu() {
    	System.out.println("Welcome to the remove menu!");
    	System.out.println("Please enter one of the following");
    	System.out.println("1: Remove Book");
    	System.out.println("2: Go back");
    	int choice = acceptMenuInput(3);
        switch(choice) {
        	case 1:
        		DBLibrary.removeBook(con, in);;
        		break;
        	case 2:
        		break;
        	
        	
        
        
        }
    }
    
    private static int acceptMenuInput(int max) {
    	if(in.hasNextInt()) {
        	int choice = in.nextInt();
        	if (choice < 1 || choice > max) {
        		System.out.println("Please enter a number from 1 to "+max);
        		
        	}
        	in.nextLine();
        	return choice;
        }else {
        	System.out.println("Please enter an integer.");
        	in.nextLine();
        	return acceptMenuInput(max);
        }
    }
	    
    	/**
    	 * The main method.
    	 *
    	 * @param args the arguments
    	 */
    	public static void main(String args[]){
	        //System.out.println("kdjflksdjflskdjfsdlkfj");
	        try {
	            Class.forName(driver);
	            con = DriverManager.getConnection("jdbc:derby://localhost:1527/CECSProject;username =\"\";password=\"\";");
	            while(true) {
		            System.out.println("Welcome to the Book Database Management Program!");
		            System.out.println("Please enter one of the following menu option:");
		            System.out.println("1: View Data menu");
		            System.out.println("2: Insert Data Menu");
		            System.out.println("3: Remove Data Menu");
		            System.out.println("4: Quit");
		            int choice = acceptMenuInput(4);
		            switch(choice) {
		            	case 1:
		            		dataMenu();
		            		break;
		            	case 2:
		            		insertMenu();
		            		break;
		            	case 3:
		            		removeMenu();
		            		break;
		            	case 4:
		            		con.close();
		            		System.exit(0);
		            		break;
		            	
		            
		            
		            }
		            
	            }
	            
	            
	            
	            
	            
	            
	        }catch(SQLException sqle) {
	        	sqle.printStackTrace();
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }finally {
	        	
	        }
	    }

	}
