package smishingcutter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SCDBManager {
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final String DB_URL = "**address mosaic**";
	private final String DB_ID = "cswin";
	private final String DB_PW = "cswin123!";

	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	// Constructor
	public SCDBManager(){
		try{
			Class.forName(JDBC_DRIVER);
			this.conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PW);
			this.stmt = conn.createStatement();
		} catch(ClassNotFoundException e){
			System.out.println("[ Driver Loading FAILED ]");
		} catch(SQLException e){
			System.out.println("[ ERROR : "+e+" ]");
		}
	}

	// Method
	public void close(){
		// stmt.close();
		try{
			if(stmt!=null && !stmt.isClosed()){
				stmt.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		// conn.close();
		try{
			if(conn!=null && !conn.isClosed()){
				conn.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}

	public String select(String query){
		String selectResult = "none";
		try{
			rs = stmt.executeQuery(query);
			if(rs.next()){
				selectResult = rs.getString(1);
			}
		} catch(SQLException e){
			System.out.println("SQL : "+query);
			System.out.println("[ ERROR : "+e+" ]");
		}
		return selectResult;
	}
	
	public void insert(String query){
		try{
			int r = stmt.executeUpdate(query);
		} catch(SQLException e){
			System.out.println("SQL : "+query);
			System.out.println("[ ERROR : "+e+" ]");
		}
	}

	public void update(String query){
		try{
			int r = stmt.executeUpdate(query);
		} catch(SQLException e){
			System.out.println("SQL : "+query);
			System.out.println("[ ERROR : "+e+" ]");
		}
	}
}
