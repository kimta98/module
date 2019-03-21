package module.sql;

public class PipeTempSql {
	
	public final static String Query(String name){
		String result = "";
		
		switch(name){
			case "deleteTable":
				result = "DROP TABLE IF EXISTS PIPE_TEMP;";
				break;
			case "createTable":
				result = "CREATE TABLE PIPE_TEMP (m_seq VARCHAR(100), type VARCHAR(100), p_seq VARCHAR(100), series VARCHAR(200), name VARCHAR(100), inner_diameter DOUBLE, roughness DOUBLE"
						+ ", max_pressure DOUBLE, max_temp DOUBLE ); ";
				break;
			case "insert":
				result = "INSERT INTO PIPE_TEMP (m_seq, type, p_seq, series, name, inner_diameter, roughness) VALUES (?, ?, ?, ?, ?, ?, ?)";
				break;
			default:
				result = "";
				break;
		}
		
		return result;
	}

}
