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
	    
    	/**
    	 * The main method.
    	 *
    	 * @param args the arguments
    	 */
    	public static void main(String args[]){
	        //System.out.println("kdjflksdjflskdjfsdlkfj");
	        try {
	            Class.forName(driver);
	            con = DriverManager.getConnection("jdbc:derby://localhost:1527/CECS323Projekt;username =\"\";password=\"\";");
	            //System.out.println("test");
	            //insertWritingGroup(in,con);
//	            System.out.println(DBLibrary.viewWritingGroups(con));
	            System.out.println(DBLibrary.viewWritingGroupData("Rito gaems", con));
	            
	            con.close();
	        }catch(SQLException sqle) {
	        	sqle.printStackTrace();
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }finally {
	        	
	        }
	    }

	}
