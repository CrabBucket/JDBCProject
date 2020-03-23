package jdbcproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

// TODO: Auto-generated Javadoc
/**
 * The Class DBLibrary.
 */
public class DBLibrary {
	
	/** The Constant scrollable. */
	private final static int scrollable[] = {ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY}; 
	
	/**
	 * Rows in ResultSet
	 *
	 * @param set the set
	 * @return the number of rows in result set.
	 */
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
	 * Checks if there is a record that exists that matches the data in the column.
	 *
	 * @param con the DB connection
	 * @param table the table
	 * @param columnName the column name
	 * @param data the data
	 * @return true, if there exists one or more records that much the data in the specified column
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
	
	/**
	 * Checks if there is a record that exists that matches both the columns to both the string accordingly
	 *
	 * @param con the DB connection
	 * @param table the table
	 * @param column1 First column to be matched
	 * @param column2 Second Column to be mached.
	 * @param data1 the String to match to column1
	 * @param data2 the String to match to column2
	 * @return true, if at least single record exists false otherwise.
	 * @throws SQLException, since we use strings to take in column and table names data we can do some pretty powerful things however it does throw errors.
	 */
	public static boolean existsInColumnPair(Connection con, String table, String column1,String column2, String data1, String data2) throws SQLException {
		PreparedStatement prep;
		try {
			String sql = "SELECT "+column1+","+ column2 + " FROM "+table+" WHERE "+column1+"='"+data1+"' && "+column2+ "= '"+data2+"'";
			//Note, you cannot use ? for the select or for the table name or for both sides of the equals. 
			//I guess that means you should only use for the last check.
			String sql_ = "SELECT "+column1+","+ column2 + " FROM "+table+" WHERE "+column1+"= ? AND "+column2+ "= ?";
			prep = con.prepareStatement(sql_,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			prep.setString(1,data1);
			prep.setString(2, data2);
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

	
	
	/**
	 * Gets the meta triples, each triple contains (Column Name, Column Type, Column Size) with type String,String,Int.
	 *
	 * @param set the ResultSet we want to get meta data on.
	 * @return the meta triples
	 */
	public static ArrayList<Triple<String,String,Integer>>getMetaTriples(ResultSet set){
		ResultSetMetaData meta;
		ArrayList<Triple<String,String,Integer>> columnmetadata = new ArrayList<Triple<String,String,Integer>>();
		try {
			meta = set.getMetaData();
			int columnCount = meta.getColumnCount();
			for(int i = 0; i < columnCount;i++) {
				columnmetadata.add(new Triple<String,String,Integer>(meta.getColumnLabel(i+1),meta.getColumnTypeName(i+1),meta.getColumnDisplaySize(i+1)));
			}
			
		} catch (SQLException e) {
			columnmetadata.add(new Triple<String,String,Integer>("Error making triple","Error Making Triple",0));
			e.printStackTrace();
			
		}
		return columnmetadata;

	}
	
	/**
	 * Multiplies a string O(num)
	 *
	 * @param str the string
	 * @param num the number multiplied
	 * @return the multiplied string
	 */
	private static String strMult(String str, int num) {
		String ret = str;
		for(int i = 0; i<num;i++) {
			ret+= str;
		}
		return ret;
	}
	/**
	 * Result set to string.
	 *
	 * @param set the ResultSet
	 * @return the string represention of the ResultSet
	 */
	public static String resultSetToString(ResultSet set) {
		try {
			
			ArrayList<Triple<String,String,Integer>> columnmetadata = getMetaTriples(set);
			int columnCount = columnmetadata.size();
			String preFormat = "";
			Object printfargs[] = new Object[columnCount];
			String lines = "";
			
			int count = 0;
			set.next();
			for(Triple<String,String,Integer> t : columnmetadata) {
				if(t.y.equals(new String("VARCHAR"))) {
					preFormat+= "%" + -t.z + "s";
					lines += lines + strMult("-",t.z);
					
				}else if (t.y.equals(new String("INTEGER"))){
					preFormat+="%"+ -t.x.length()+1 + "s";
					lines += strMult("-",t.x.length()+1);
					
					
				}
				
				count++;
			}
			preFormat += "%n";
			String toRet = "";
			count = 0;
			for(Triple<String,String,Integer> t : columnmetadata) {
				printfargs[count] = t.x;
				count++;
			}
			toRet+= String.format(preFormat,printfargs);
			toRet+= String.format("%s%n", lines);
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
		return "Error in result set to string";
	}
	
	/**
	 * View writing groups.
	 *
	 * @param in the Input stream
	 * @param con the DB Connection
	 */
	public static String viewWritingGroups(Connection con){
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT GROUPNAME,HEADWRITER,YEARFORMED,SUBJECT FROM WritingGroup ORDER BY GroupName");
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
	 * View writing group data.
	 *
	 * @param in the Input stream
	 * @param con the DB Connection
	 * @return the string represention of writing group data.
	 */
	public static String viewWritingGroupData (Scanner in,Connection con) {
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT GROUPNAME,HEADWRITER,YEARFORMED,SUBJECT FROM WritingGroup "+
										"WHERE GROUPNAME=? ORDER BY GroupName");
			PreparedStatement tempstate = con.prepareStatement("SELECT groupName from WritingGroup",scrollable[0],scrollable[1]);
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
			String toCheck = acceptStringInput(metadata.get(0).z,1,"Group Name",prep,in);
			if(!existsInColumn(con, "WritingGroup", "GroupName", toCheck)) {
				System.out.println("No such writing group exists please try again");
				return viewWritingGroupData(in,con);
			}
			
			toRet= resultSetToString(prep.executeQuery());
		}catch(SQLException e) {
			e.printStackTrace();
			toRet = "Error in viewWritingGroupData";
		}
		return toRet;
		
	}
	
	/**
	 * Insert book into DB.
	 *
	 * @param in the Input stream
	 * @param con the DB Connection
	 */
	public static void insertBook (Connection con,Scanner in) {
		PreparedStatement prep;
		try{
			prep = con.prepareStatement("INSERT INTO Books (groupName,bookTitle,publisherName,yearPublished,numberOfPages) VALUES (?,?,?,?,?)",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			PreparedStatement tempstate = con.prepareStatement("SELECT groupName,bookTitle,publisherName,yearPublished,numberOfPages from Books");
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
			while(true) {
				try {
					String data2 = acceptStringInput(metadata.get(1).z,2, "Book Title", prep,in);
					String data1;
					while(true) {
						data1 = acceptStringInput(metadata.get(0).z,1, "Group Name", prep,in);
						if(existsInColumn(con,"Books","GroupName",data1)) {
							break;
						}
					}
					 
					
					if(existsInColumnPair(con, "Books", "groupName","bookTitle",data1,data2)) {
						System.out.println("Book is already in the system.");
						continue;								
					}
					break;
				} catch(SQLException e) {
					System.out.println("Used wrong index");
					continue;
				}
			}
			while(true) {
				try {
					String data = acceptStringInput(metadata.get(2).z,3,"Publisher Name",prep,in);
					if(!existsInColumn(con,"Publishers","publisherName",data)) {
						System.out.println("Publisher doesn't exist please try again");
						continue;
					}
					break;
					
				}catch(SQLException e) {
					
				}
			}
			int yearPublished;
			while(true) {
				try {
					System.out.println("Please enter the year published.");
					//If the next line throws an error we don't get to the break and we repeat the loop through the catch
					//If the parse goes through fine we break the loop.
					yearPublished = Integer.parseInt(in.nextLine());
					if(yearPublished>2200 || yearPublished< 500) {
						System.out.println("Please enter a year from 500 to 2100");
						continue;
					}
					break;
					
				}catch (NumberFormatException nfe) {
					System.out.println("You entered a non valid integer.");
				}
			}
			prep.setInt(4, yearPublished);
			int numberOfPages;
			while(true) {
				try {
					System.out.println("Please enter the number of pages");
					//If the next line throws an error we don't get to the break and we repeat the loop through the catch
					//If the parse goes through fine we break the loop.
					numberOfPages = Integer.parseInt(in.nextLine());
					if(numberOfPages<1) {
						System.out.println("Please enter a postivie integer");
						continue;
					}
					break;
					
				}catch (NumberFormatException nfe) {
					System.out.println("You entered a non valid integer.");
				}
			}
			prep.setInt(5, numberOfPages);
			prep.executeUpdate();
			
			
			
			
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Removes specified book.
	 *
	 * @param in the Input stream
	 * @param con the DB Connection
	 */
	public static void removeBook(Connection con, Scanner in) {
		PreparedStatement checkRows;
		PreparedStatement prepNoGrp;
		PreparedStatement prepWGrp;
		try {
			boolean needsGroup;
			PreparedStatement meta = con.prepareStatement("SELECT bookTitle,groupName FROM Books");
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(meta.executeQuery());
			prepNoGrp = con.prepareStatement("DELETE FROM Books WHERE bookTitle =?");
			prepWGrp = con.prepareStatement("DELETE FROM Books Where bookTitle = ? and groupName = ?");
			checkRows = con.prepareStatement("SELECT bookTitle,groupName FROM Books Where bookTitle = ?",scrollable[0],scrollable[1]);
			
			String bookTitle = acceptStringInput(metadata.get(0).z, 1, "Book Title", prepNoGrp, in);
			checkRows.setString(1, bookTitle);
			String group = "";
			int rows = rowsInSet(checkRows.executeQuery());
			if (rows>1) {
				needsGroup=true;
				System.out.println("The book title you entered has more than one book associated with it");
				while(true) {
					group = acceptStringInput(metadata.get(1).z,2,"Group Name", prepWGrp,in);
					if(existsInColumnPair(con,"Books", "BookTitle" , "GroupName", bookTitle, group)) {
						prepWGrp.setString(1, bookTitle);
						System.out.println("Book successfully removed");
						prepWGrp.executeUpdate();
						break;
					}else {
						System.out.println("Group name does not exist please try again");
						continue;
					}
				}
			}else if(rows == 1){
				prepNoGrp.executeUpdate();
				System.out.println("Only one book matched you book title so that book was removed");
				
			}else {
				//I think techincally this can give you a stack overflow with when the recursion call stack runneth over.
				System.out.println("Book not found please try again");
				removeBook(con,in);
				return;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * View publishers.
	 *
	 * @param in the Input stream
	 * @param con the DB Connection
	 */
	public static String viewPublishers(Connection con){
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERPHONE,PUBLISHEREMAIL FROM Publishers ORDER BY PublisherName");
			prep.execute();
			ResultSet temp = prep.getResultSet();
			
			toRet = resultSetToString(temp); 
		}catch(SQLException e) {
			e.printStackTrace();
			toRet = "Error when trying to viewPublishers";
		}
		
		
		
		
		
		return toRet;
	}
	
	/**
	 * View publishers data.
	 *
	 * @param in the Input stream
	 * @param con the DB Connection
	 * @return the Publisher Data as a string
	 */
	public static String viewPublishersData (Scanner in,Connection con) {
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERPHONE,PUBLISHEREMAIL FROM Publishers "+
										"WHERE PUBLISHERNAME=? ORDER BY PublisherName");
			PreparedStatement tempstate = con.prepareStatement("SELECT publisherName from publishers",scrollable[0],scrollable[1]);
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
			String toCheck = acceptStringInput(metadata.get(0).z,1,"Publisher Name",prep,in);
			if(!existsInColumn(con, "Publishers", "PublisherName", toCheck)) {
				System.out.println("No such publisher exists please try again");
				return viewWritingGroupData(in,con);
			}
			
			toRet= resultSetToString(prep.executeQuery());
		}catch(SQLException e) {
			e.printStackTrace();
			toRet = "Error in viewPublishersData";
		}
		return toRet;
		
	}
	
	
	/**
	 * View all book titles.
	 *
	 * @param con the DB connection
	 * @return the string representation of the all the book titles in the database
	 */
	public static String viewBooks(Connection con){
		PreparedStatement prep;
		String toRet;
		try {
			prep = con.prepareStatement("SELECT BOOKTITLE,GROUPNAME,PUBLISHERNAME FROM Books ORDER BY BookTitle");
			prep.execute();
			ResultSet temp = prep.getResultSet();
			
			toRet = resultSetToString(temp); 
		}catch(SQLException e) {
			e.printStackTrace();
			toRet = "Error when trying to viewPublishers";
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
	 * @param in the in
	 * @return the prepared statement with data inserted into the correct index
	 * @throws SQLException the SQL exception shouldn't ever be thrown if I handled the case correctly.
	 */
	public static String acceptStringInput(int maxLen, int index,String category,PreparedStatement prep, Scanner in) {
		String datain;
		while(true) {
			System.out.println("Please enter the "+ category +":");
			
			datain = in.nextLine();
			if(datain.length()>maxLen) {
				System.out.println("Please enter a " + category + " that is less than " + maxLen + " characters.");
				continue;
			}
			try {
				prep.setString(index,datain);
			} catch (SQLException e) {
				System.out.println("Error setting "+category);
				e.printStackTrace();
			}
			break;
		}
		
		return datain;
		
	}

	/**
	 * View specific book data
	 *
	 * @param in the input stream we are reading from
	 * @param con the DB connection
	 * @return The Book Data as a string
	 */
	public static String viewBooksData (Scanner in,Connection con) {
		PreparedStatement checkRows;
		PreparedStatement prepNoGrp;
		PreparedStatement prepWGrp;
		try {
			
			PreparedStatement meta = con.prepareStatement("SELECT bookTitle,groupName FROM Books");
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(meta.executeQuery());
			prepNoGrp = con.prepareStatement("SELECT BOOKTITLE,GROUPNAME,PUBLISHERNAME,YEARPUBLISHED, NUMBEROFPAGES FROM Books WHERE bookTitle =? ORDER BY BookTitle");
			prepWGrp = con.prepareStatement("SELECT BOOKTITLE,GROUPNAME,PUBLISHERNAME,YEARPUBLISHED, NUMBEROFPAGES FROM Books WHERE bookTitle = ? and groupName = ? ORDER BY BookTitle");
			checkRows = con.prepareStatement("SELECT bookTitle,groupName FROM Books Where bookTitle = ?",scrollable[0],scrollable[1]);
			
			String bookTitle = acceptStringInput(metadata.get(0).z, 1, "Book Title", prepNoGrp, in);
			checkRows.setString(1, bookTitle);
			String group = "";
			int rows = rowsInSet(checkRows.executeQuery());
			if (rows>1) {
				
				System.out.println("The book title you entered has more than one book associated with it");
				while(true) {
					group = acceptStringInput(metadata.get(1).z,2,"Group Name", prepWGrp,in);
					if(existsInColumnPair(con,"Books", "BookTitle" , "GroupName", bookTitle, group)) {
						prepWGrp.setString(1, bookTitle);
						return resultSetToString(prepWGrp.executeQuery());
						
						
					}else {
						System.out.println("Book title is not associated with that group name please try again");
						continue;
					}
					
				}
			}else if(rows == 1){
				System.out.println("Only one book with that title exists there is no need to further specify:");
				return resultSetToString(prepNoGrp.executeQuery());
				
			}else {
				//I think techincally this can give you a stack overflow with when the recursion call stack runneth over.
				System.out.println("Book not found please try again");
				return viewBooksData(in,con);
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return "Error in viewBooksData()";
	
		
	}
	
	
	/**
	 * Insert writing group into database.
	 *
	 * @param in the input stream we are taking user input from
	 * @param con the DB connection
	 */
	// Inserting into the table using a prepared statement
	public static void insertWritingGroup(Scanner in,Connection con) {
		
		//This is the skeleton of our statement the ?'s are what we are using as a placeholder to put variables into it so sql can optimize.
		String statement = "INSERT INTO WritingGroup (groupName,headWriter,yearFormed,subject) VALUES (?,?,?,?)";
		
		//Turns the string into an sql statement this can throw errors if the string is malformed
		
		
		PreparedStatement tempstate;
		PreparedStatement prepstate;
		ArrayList<Triple<String,String,Integer>> metadata;
		try {
			tempstate = con.prepareStatement("SELECT groupName,headWriter,yearFormed,subject from WritingGroup");
			prepstate = con.prepareStatement(statement);
			metadata = getMetaTriples(tempstate.executeQuery());
			class inputHelper {
				public inputHelper(int index, String subject) {
					while(true) {
						acceptStringInput(metadata.get(index-1).z,index, subject, prepstate,in);
						break;
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
			//We use our input helpers to insert data.
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
					prepstate.setInt(3, yearsFormed);
					break;
					
				}catch (NumberFormatException | SQLException nfe) {
					System.out.println("You entered a non valid integer.");
					nfe.printStackTrace();
					return;
				}
			}
			new inputHelper(4, "subject");
			//We make sure to execute the statment at the end.
			//This can error if we have problems with our data but it likely would have happened earlier,
			//Will error if uniqueness constraints are broken.
			try {
				prepstate.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Error when inserting WritingGroup at execute");
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Insert publishers.
	 *
	 * @param in the input stream we are taking user input from
	 * @param con the DB connection we are inserting into
	 * @return the Returns the name of the publisher added.
	 */
	public static String insertPublishers(Scanner in,Connection con) {
		
		//This is the skeleton of our statement the ?'s are what we are using as a placeholder to put variables into it so sql can optimize.
		String statement = "INSERT INTO Publishers (publisherName,publisherAddress,publisherPhone,publisherEmail) VALUES (?,?,?,?)";
		
		
		PreparedStatement prepstate;
		PreparedStatement tempstate;
		ArrayList<Triple<String,String,Integer>> metadata;
		try {
			//Turns the string into an sql statement this can throw errors if the string is malformed
			prepstate = con.prepareStatement(statement);
			tempstate = con.prepareStatement("SELECT publisherName,publisherAddress,publisherPhone,publisherEmail from Publishers");
			metadata = getMetaTriples(tempstate.executeQuery());
		} catch (SQLException e1) {
			System.out.println("Publisher table probably changed");
			e1.printStackTrace();
			return "Error";
			
		}
		
		
		
		
		class inputHelper {
			public String publisher;
			public inputHelper(int index, String subject) {
				while(true) {
					this.publisher = acceptStringInput(metadata.get(index-1).z,index, subject, prepstate,in);
					break;
				}
			}
			public inputHelper(int index, String subject,boolean unique) {
				
				while(true) {
					try {
						String data = acceptStringInput(metadata.get(index-1).z,index, subject, prepstate,in);
						if(unique) {
							if(existsInColumn(con, "Publishers", "PublisherName",data)) {
								System.out.println("PublisherName Already in the System.");
								continue;
							}
							
						}
						this.publisher = data;
						break;
					} catch(SQLException e) {
						System.out.println("Used wrong index");
						continue;
					}
				}
			}
		}
		//To insert the data into our database we use the input helper
		String publisher = new inputHelper(1,"Publisher Name",true).publisher;
		
		new inputHelper(2,"Publisher Address");
		
		String publisherPhone;
		while(true) {
			try {
				System.out.println("Please enter the publisher phone number.");
				//If the next line throws an error we don't get to the break and we repeat the loop through the catch
				//If the parse goes through fine we break the loop.
				publisherPhone = in.nextLine();
				if(publisherPhone.matches("\\d{10}") || publisherPhone.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}") || publisherPhone.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")
						|| publisherPhone.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) {
				}
				else
				{
					System.out.println("Please enter a valid phone number, Ex: 323-100-7777");
					continue;
				}
				break;
			
			}catch (NumberFormatException e) {
				System.out.println("You entered a non valid phone number\n");
			}
		}
		
		try {
			prepstate.setString(3, publisherPhone);
		} catch (SQLException e1) {
			System.out.println("Parsing error when inserting string into publisherPhone");
			e1.printStackTrace();
			return "Error";
			
		}
		
		while(true) {
			String email = acceptStringInput(metadata.get(3).z,4,"publisher email",prepstate,in);
			try {
				
				//If the next line throws an error we don't get to the break and we repeat the loop through the catch
				//If the parse goes through fine we break the loop.
				
				if(email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
				}
				else
				{
					System.out.println("Please enter a valid email");
					continue;
				}
				
				break;
			
			}catch (NumberFormatException e) {
				System.out.println("You entered a non valid email\n");
				continue;
			}
		}
		
		//We make sure to execute the statment at the end.
		//This can error if we have problems with our data but it likely would have happened earlier,
		//Will error if uniqueness constraints are broken.
		try {
			prepstate.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Insert publisher failed when trying to update DB");
			e.printStackTrace();
		}
		return publisher;
		
	}
	
	/**
	 * Creates a new publisher and take an old publisher, the new publisher than takes over all the old publishers current books
	 * We leave the old publisher alone
	 *
	 * @param in the input stream we are taking user input from
	 * @param con the DB connection we are inserting into
	 */
	public static void usurpPublisher(Connection con, Scanner in) {
		String pub = insertPublishers(in,con);
		
		final String sql = "UPDATE Books SET publisherName = ? WHERE publisherName = ?";
		try {
			PreparedStatement prep = con.prepareStatement(sql);
			prep.setString(1, pub);
			PreparedStatement tempstate = con.prepareStatement("SELECT publisherName from Publishers");
			ArrayList<Triple<String,String,Integer>> metadata = getMetaTriples(tempstate.executeQuery());
			while(true) {
				System.out.println("Please enter the old publisher that "+pub+" will take over.");
				
				String oldPub = acceptStringInput(metadata.get(0).z,2,"Publisher Name",prep,in);
				if(!existsInColumn(con, "Publishers", "publisherName", oldPub)) {
					continue;
				}
				prep.executeUpdate();
				break;
			}
				
		}catch(SQLException e) {
			System.out.println("Publisher or Books tables most likely changed structure");
			
			e.printStackTrace();
		}
		
	
	}
	
	
}
