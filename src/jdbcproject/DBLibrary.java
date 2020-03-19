package jdbcproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class DBLibrary {
	public static ArrayList<Triple<String,String,Integer>>getMetaTriples(ResultSet set){
		ResultSetMetaData meta;
		ArrayList<Triple<String,String,Integer>> columnmetadata = new ArrayList<Triple<String,String,Integer>>();
		try {
			meta = set.getMetaData();
			int columnCount = meta.getColumnCount();
			for(int i = 0; i < columnCount;i++) {
				columnmetadata.add(new Triple<String,String,Integer>(meta.getColumnLabel(i+1),meta.getColumnTypeName(i+1),meta.getPrecision(i+1)));
			}
			
		} catch (SQLException e) {
			columnmetadata.add(new Triple<String,String,Integer>("Error making triple","Error Making Triple",0));
			e.printStackTrace();
			
		}
		return columnmetadata;

	}
	/**
	 * Result set to string.
	 *
	 * @param set the set
	 * @return the string **/
	public static String resultSetToString(ResultSet set) {
		try {
			
			ArrayList<Triple<String,String,Integer>> columnmetadata = getMetaTriples(set);
			int columnCount = columnmetadata.size();
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
	public static String viewWritingGroups(Connection con){
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
	
	public static String viewWritingGroupData (String group,Connection con) {
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT GROUPNAME,HEADWRITER,YEARFORMED,SUBJECT FROM WritingGroup "+
										"WHERE GROUPNAME=?");
			prep.setString(1,group);
			
			toRet= resultSetToString(prep.executeQuery());
		}catch(SQLException e) {
			e.printStackTrace();
			toRet = "Error in viewWritingGroupData";
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
	public static PreparedStatement acceptStringInput(int maxLen, int index,String category,PreparedStatement prep) throws SQLException {
		Scanner in = new Scanner(System.in);
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
		in.close();
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
		
		
		PreparedStatement tempstate = con.prepareStatement("SELECT groupName,headWriter,yearFormed,subect from WritingGroup");
		ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
		//To insert the data into our prepstate I made function that takes the max length of the string input
		//The index to enter, the name to call it for the user, and the PreparedStatement
		//We get the max allowed size from the metadata of the table.
		acceptStringInput(metadata.get(0).z, 1, "Group", prepstate);
		
		
		acceptStringInput(metadata.get(1).z,2,"head writer",prepstate);
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
		acceptStringInput(metadata.get(3).z,4,"subject",prepstate);
		//We make sure to execute the statment at the end.
		//This can error if we have problems with our data but it likely would have happened earlier,
		//Will error if uniqueness constraints are broken.
		prepstate.executeUpdate();
		
	}
}
