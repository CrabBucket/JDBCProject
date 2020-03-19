package jdbcproject;

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;

// TODO: Auto-generated Javadoc
/**
 * The Class ProjectMain.
 */
public class ProjectMain {
	private static String resultSetToString(ResultSet set) {
		try {
			ResultSetMetaData meta = set.getMetaData();
			
			int columnCount = meta.getColumnCount();
			ArrayList<Triple<String,String,Integer>> columnmetadata = new ArrayList<Triple<String,String,Integer>>();
			for(int i = 0; i < columnCount;i++) {
				columnmetadata.add(new Triple<String,String,Integer>(meta.getColumnLabel(i+1),meta.getColumnTypeName(i+1),meta.getPrecision(i+1)));
			}

			String preFormat = "";
			Object printfargs[] = new Object[columnCount];
			int count = 0;
			set.next();
			for(Triple<String,String,Integer> t : columnmetadata) {
				if(t.y.equals(new String("VARCHAR"))) {
					preFormat+= "%" + -t.z + "s";
					
				}else if (t.y.equals(new String("INTEGER"))){
					preFormat+="%-5d";
					
					
				}
				
				count++;
			}
			preFormat += "%n";
			String toRet = "";
			
			do{
				count = 0;
				for(Triple<String,String,Integer> t : columnmetadata) {
					if(t.y.equals(new String("VARCHAR"))) {
						
						printfargs[count] = set.getString(t.x);
						
					}else if (t.y.equals(new String("INTEGER"))){
						printfargs[count] = set.getInt(t.x);
						
						
					}
					count++;
				}
//				System.out.println(preFormat);
//				for ( Object x : printfargs) {
//					System.out.print(x.toString() + " ");
//				}
				toRet+= String.format(preFormat, printfargs);
//				System.out.println(toRet);
				
				
				count = 0;
			}while(set.next());
			return toRet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	private static String viewWritingGroups(Connection con){
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT * FROM WritingGroup");
			prep.execute();
			ResultSet temp = prep.getResultSet();
			
			toRet = resultSetToString(temp); 
		}catch(SQLException e) {
			e.printStackTrace();
			toRet = "Error when trying to viewWritingGroups";
		}
		
		
		
		
		
		return toRet;
	}
	
	/**
	 * Accept string input for insertion into a prepared statement to insert into a table.
	 *
	 * @param maxLen the max length the database will accept
	 * @param index the index of the data in the prepared statement
	 * @param category the name to display when asking for data to the user
	 * @param prep the prep the prepared statement you want to insert data into
	 * @return the prepared statement with data inserted into the correct index
	 * @throws SQLException the SQL exception shouldn't ever be thrown if I handled the case correctly.
	 */
	private static PreparedStatement acceptStringInput(int maxLen, int index,String category,PreparedStatement prep) throws SQLException {
		while(true) {
			System.out.println("Please enter the "+ category +" name:");
			
			String datain = in.nextLine();
			if(datain.length()>100) {
				System.out.println("Please enter a " + category + " name less than" + index + "characters.");
				continue;
			}
			prep.setString(index,datain);
			break;
		}
		return prep;
		
	}
	
	/**
	 * Insert writing group.
	 *
	 * @param in the in
	 * @param con the con
	 * @throws SQLException the SQL exception should only be thrown if uniquesness constraint is violated.
	 */
	// Inserting into the table using a prepared statement
	private static void insertWritingGroup(Scanner in,Connection con) throws SQLException{
		
		//This is the skeleton of our statement the ?'s are what we are using as a placeholder to put variables into it so sql can optimize.
		String statement = "INSERT INTO WritingGroup (groupName,headWriter,yearFormed,subject) VALUES (?,?,?,?)";
		
		//Turns the string into an sql statement this can throw errors if the string is malformed
		PreparedStatement prepstate = con.prepareStatement(statement);
		//Standard Input/Ouput stuff to get user input for the next couple of lines
		//The setString / setInt / setDataType has the format setData(index, data of that type)
		//It will set the question marks in our statement to their actual values whatever they may be.
		//We repeat this 4 times for the 4 fields
		acceptStringInput(100, 1, "Group", prepstate);
		
		
		acceptStringInput(20,2,"head writer",prepstate);
		int yearsFormed;
		//Keep repeating obviously
		while(true) {
			try {
				System.out.println("Please enter the year formed.");
				//If the next line throws an error we don't get to the break and we repeat the loop through the catch
				//If the parse goes through fine we break the loop.
				yearsFormed = Integer.parseInt(in.nextLine());
				if(yearsFormed>2100 || yearsFormed < 1800) {
					System.out.println("Please enter a year from 1800 to 2100");
					continue;
				}
				break;
				
			}catch (NumberFormatException nfe) {
				System.out.println("You entered a non valid integer.");
			}
		}
		prepstate.setInt(3, yearsFormed);
		acceptStringInput(20,4,"subject",prepstate);
		//We make sure to execute the statment at the end.
		//This can error if we have problems with our data but it likely would have happened earlier,
		//Will error if uniqueness constraints are broken.
		prepstate.executeUpdate();
		
	}
	
	
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
	            System.out.println(viewWritingGroups(con));
	            
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
