package module.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class SQLite {
	
	public Connection conn;
	public Statement stmt;
	public PreparedStatement pstmt;
	public ResultSet rs;
	private String dbFile;
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * DB 담을 파일명
	 * @param fileName
	 */
	public SQLite(String fileName) {
		this.dbFile = fileName;
	}
	
	/**
	 * DB 연결
	 * @return
	 */
	public boolean dbConnect() {
		
		try {
			
			//SQLiteConfig config = new SQLiteConfig();
			Properties properties = new Properties();
			properties.setProperty("characterEncoding", "UTF-8");
			properties.setProperty("encoding", "\"UTF-8\"");
			conn = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile, properties);
			
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * DB 연결 종료
	 * @return
	 */
	public boolean dbClose() {
		
		try {
			
			if(conn != null){
				conn.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Statemant 관련 종료
	 * @return
	 */
	public boolean stmtClose() {
		
		try {
			
			if(stmt != null){
				stmt.close();
			}
			
			if(pstmt != null){
				pstmt.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
