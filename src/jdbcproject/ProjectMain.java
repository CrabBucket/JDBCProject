package jdbcproject;

import java.util.Scanner;
import java.sql.*;

public class ProjectMain {
	// Inserting into the table using a prepared statement
	private static void insertWritingGroup(Scanner in,Connection con) throws SQLException{
		
		//This is the skeleton of our statement the ?'s are what we are using as a placeholder to put variables into it so sql can optimize.
		String statement = "INSERT INTO WritingGroup (groupName,headWriter,yearFormed,subject) VALUES (?,?,?,?)";
		
		//Turns the string into an sql statement this can throw errors if the string is malformed
		PreparedStatement prepstate = con.prepareStatement(statement);
		//Stand Input/Ouput stuff to get user input for the next couple of lines
		System.out.println("Please enter the group name:");
		String groupName = in.nextLine();
		//The setString / setInt / setDataType has the format setData(index, data of that type)
		//It will set the question marks in our statement to their actual values whatever they may be.
		//We repeat this 4 times for the 4 fields.
		prepstate.setString(1,groupName);
		System.out.println("Enter head writer:");
		String headWriter = in.nextLine();
		prepstate.setString(2, headWriter);
		int yearsFormed;
		//Keep repeating obviously
		while(true) {
			try {
				System.out.println("Please enter a valid integer");
				//If the next line throws an error we don't get to the break and we repeat the loop through the catch
				//If the parse goes through fine we break the loop.
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
		//We make sure to execute the statment at the end.
		//This can error if we have problems with our data but it likely would have happened earlier,
		//Will error if uniqueness constraints are broken.
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
