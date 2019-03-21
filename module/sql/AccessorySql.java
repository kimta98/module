package module.sql;

public class AccessorySql {
	
	public final static String Query(String name){
		String result = "";
		
		switch(name){
			case "deleteTable":
				result = "DROP TABLE IF EXISTS ACCESSORY;";
				break;
			case "createTable":
				result = "CREATE TABLE ACCESSORY (p_seq VARCHAR(100), m_seq VARCHAR(100), m_name VARCHAR(100), type VARCHAR(100), description VARCHAR(100), dp DOUBLE);";
				break;
			case "insert":
				result = "INSERT INTO ACCESSORY (p_seq, m_seq, m_name, type, description, dp) "
						+ "VALUES (?, ?, ?, ?, ?, ?);";
				break;
			case "getAccessorys":
				result = "SELECT * FROM ACCESSORY;";
				break;
			default:
				result = "";
				break;
		}
		
		return result;
	}

}
