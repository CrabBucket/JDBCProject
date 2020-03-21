package jdbcproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class DBLibrary {
	
	public static int rowsInSet(ResultSet set) {
		
		
		 int rows = 0;
		    try {
		        set.last();
		        rows = set.getRow();
		        set.beforeFirst();
		        
		        
		    } 
		    catch(Exception ex)  {
		    	ex.printStackTrace();
		        return 0;
		    }
		    return rows ;
	}
	
	/**
	 * Exists in column.
	 *
	 * @param con the con
	 * @param table the table
	 * @param columnName the column name
	 * @param data the data
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 */
	public static boolean existsInColumn(Connection con, String table, String columnName, String data) throws SQLException {
		PreparedStatement prep;
		try {
			String sql = "SELECT "+columnName+" FROM "+table+" WHERE "+columnName+"='"+data+"'";
			//Note, you cannot use ? for the select or for the table name or for both sides of the equals. 
			//I guess that means you should only use for the last check.
			String sql_ = "SELECT "+columnName+" FROM " + table +" WHERE "+columnName+"=?";
			prep = con.prepareStatement(sql_,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			prep.setString(1,data);
		}catch(SQLException e) {
			System.out.println("Used invalid sql syntax in either the columnName table or data.");
			e.printStackTrace();
			throw new SQLException(e);
		}
		ResultSet results;
		try {
			results = prep.executeQuery();
		}catch(SQLException e) {
			System.out.println("Couldn't find table or column name.");
			throw new SQLException(e);
		}
		if(rowsInSet(results)>0) {
			return true;
		}else {
			return false;
		}
	}
	public static String insertNewPublisher(Connection con,Scanner in) {
		
		String SQL = "INSERT INTO Publishers (PublisherName,PublisherAddress, PublisherPhone, PublisherEmail) VALUES (?,?,?,?)";
		try {
			PreparedStatement prep = con.prepareStatement(SQL,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			PreparedStatement tempstate = con.prepareStatement("SELECT PublisherName,PublisherAddress, PublisherPhone, PublisherEmail from Publishers");
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
			//inputHelper is basically a wrapper for acceptStringInput that handles uniqueness and keeps the function calls simple.
			
			class inputHelper {
				public String pk;
				public inputHelper(int index, String subject) {
					while(true) {
						try {
							this.pk =acceptStringInput(metadata.get(index-1).z,index, subject, prep,in);
							
							break;
						} catch(SQLException e) {
							System.out.println("Used wrong index");
							continue;
						}
					}
				}
				public inputHelper(int index, String subject,boolean unique) {
					
					while(true) {
						try {
							String data = acceptStringInput(metadata.get(index-1).z,index, subject, prep,in);
							if(unique) {
								if(existsInColumn(con, "Publishers", "PublisherName",data)) {
									System.out.println("Publisher Already in the System.");
									continue;
								}
								
							}
							this.pk = data;
							break;
						} catch(SQLException e) {
							System.out.println("Used wrong index");
							continue;
						}
					}
				}
			}
			
			String temp = (new inputHelper(1,"Publisher Name",true)).pk;
			
			new inputHelper(2,"Publisher Address");
			
			new inputHelper(3,"Publisher Phone Number");
			
			new inputHelper(4, "Publisher Email");
			
			prep.execute();
			
			return temp;
			
		}catch(SQLException e) {
			System.out.println("Table name or columns have likely changed");
			e.printStackTrace();
		}
		return "ERROR";
		
	}
	public static void usurpOldPublisher(Connection con, Scanner in) throws SQLException {
		String updateSQL = "UPDATE Books SET PublisherName = ? WHERE PublisherName = ?";
		PreparedStatement prep = con.prepareStatement(updateSQL);
		PreparedStatement tempstate = con.prepareStatement("SELECT PublisherName from Books");
		ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
		String newPub = insertNewPublisher(con, in);
		String oldPub = null;
		while(true) {
			System.out.println("What is the name of the publisher you are trying to replace?");
			oldPub = in.nextLine().trim();
			if(existsInColumn(con, "Publishers", "PublisherName", oldPub)) {
				break;
			}else {
				System.out.println("No such publisher exists please enter one that does");
				continue;
			}
		}
		prep.setString(1, newPub);
		prep.setString(2, oldPub);
		System.out.println(prep.executeUpdate()+" Records were changed");
		
	}
	
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
	public static String acceptStringInput(int maxLen, int index,String category,PreparedStatement prep, Scanner in) throws SQLException {
		String datain;
		while(true) {
			System.out.println("Please enter the "+ category +" name:");
			
			datain = in.nextLine();
			if(datain.length()>100) {
				System.out.println("Please enter a " + category + " name less than" + index + "characters.");
				continue;
			}
			prep.setString(index,datain);
			break;
		}
		
		return datain;
		
	}
	
	
	/**
	 * Insert writing group.
	 *
	 * @param in the in
	 * @param con the con
	 * @throws SQLException the SQL exception should only be thrown if uniquesness constraint is violated.
	 */
	// Inserting into the table using a prepared statement
	public static void insertWritingGroup(Scanner in,Connection con) throws SQLException{
		
		//This is the skeleton of our statement the ?'s are what we are using as a placeholder to put variables into it so sql can optimize.
		String statement = "INSERT INTO WritingGroup (groupName,headWriter,yearFormed,subject) VALUES (?,?,?,?)";
		try {
		//Turns the string into an sql statement this can throw errors if the string is malformed
			PreparedStatement prepstate = con.prepareStatement(statement);
			
			
			PreparedStatement tempstate = con.prepareStatement("SELECT groupName,headWriter,yearFormed,subject from WritingGroup");
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
			class inputHelper {
				public inputHelper(int index, String subject) {
					while(true) {
						try {
							acceptStringInput(metadata.get(index-1).z,index, subject, prepstate,in);
							break;
						} catch(SQLException e) {
							System.out.println("Used wrong index");
							continue;
						}
					}
				}
				public inputHelper(int index, String subject,boolean unique) {
					
					while(true) {
						try {
							String data = acceptStringInput(metadata.get(index-1).z,index, subject, prepstate,in);
							if(unique) {
								if(existsInColumn(con, "WritingGroup", "GroupName",data)) {
									System.out.println("Group Already in the System.");
									continue;
								}
								
							}
							break;
						} catch(SQLException e) {
							System.out.println("Used wrong index");
							continue;
						}
					}
				}
			}
			//To insert the data into our prepstate I made function that takes the max length of the string input
			//The index to enter, the name to call it for the user, and the PreparedStatement
			//We get the max allowed size from the metadata of the table.
			new inputHelper(1,"group",true);
			
			
			new inputHelper(2, "Head Writer");
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
			new inputHelper(4,"subject");
			//We make sure to execute the statment at the end.
			//This can error if we have problems with our data but it likely would have happened earlier,
			//Will error if uniqueness constraints are broken.
			prepstate.executeUpdate();
		}catch(SQLException e) {
			System.out.println("The database column or table names have probably changed");
			e.printStackTrace();
			
		}
	}
}
