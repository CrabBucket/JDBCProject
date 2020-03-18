package jdbcproject;

import java.util.Scanner;
import java.sql.*;

public class ProjectMain {
	
	private static void insertWritingGroup(Scanner in,Connection con) throws SQLException{
		String statement = "INSERT INTO WritingGroup (groupName,headWriter,yearFormed,subject) VALUES (?,?,?,?)";
		
		
		PreparedStatement prepstate = con.prepareStatement(statement);
		
		System.out.println("Please enter the group name:");
		String groupName = in.nextLine();
		prepstate.setString(1,groupName);
		System.out.println("Enter head writer:");
		String headWriter = in.nextLine();
		prepstate.setString(2, headWriter);
		int yearsFormed;
		while(true) {
			try {
				System.out.println("Please enter a valid integer");
				yearsFormed = Integer.parseInt(in.nextLine());
				break;
				
			}catch (NumberFormatException nfe) {
				System.out.println("You entered a non valid integer.");
			}
		}
		prepstate.setInt(3, yearsFormed);
		System.out.println("Please enter their subject");
		String subject = in.nextLine();
		prepstate.setString(4, subject);
		
		prepstate.executeUpdate();
		
	}
	
	
	 private String user;
	 private String pass;
	 private String dbName;
	 private static Scanner in = new Scanner(System.in);   
	    static final String driver = "org.apache.derby.jdbc.ClientDriver";
	    static Connection con;
	    public static void main(String args[]){
	        System.out.println("kdjflksdjflskdjfsdlkfj");
	        try {
	            Class.forName(driver);
	            con = DriverManager.getConnection("jdbc:derby://localhost:1527/CECS323Projekt;username =\"\";password=\"\";");
	            System.out.println("test");
	            insertWritingGroup(in,con);
	            
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
