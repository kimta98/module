package module.sql;

public class ValveTempSql {
	
	public final static String Query(String name){
		String result = "";
		
		switch(name){
			case "deleteTable":
				result = "DROP TABLE IF EXISTS CONTROL_VALVE_TEMP; DROP TABLE IF EXISTS BALANCING_VALVE_TEMP; DROP TABLE IF EXISTS COMBINED_CTRL_BAL_VALVE_TEMP;";
				break;
			case "createTable":
				result = "CREATE TABLE CONTROL_VALVE_TEMP (m_seq VARCHAR(100), c_seq VARCHAR(100), product VARCHAR(200), name VARCHAR(100), size_name VARCHAR(100), size DOUBLE"
						+ ", turns_pos DOUBLE, kv DOUBLE ); "
						+ "CREATE TABLE BALANCING_VALVE_TEMP (m_seq VARCHAR(100), b_seq VARCHAR(100), product VARCHAR(200), name VARCHAR(100), size_name VARCHAR(100), size DOUBLE"
						+ ", min_flow DOUBLE, max_flow DOUBLE );"
						+ "CREATE TABLE COMBINED_CTRL_BAL_VALVE_TEMP (m_seq VARCHAR(100), cp_seq VARCHAR(100), product VARCHAR(200), name VARCHAR(100), size_name VARCHAR(100), size DOUBLE"
						+ ", setting DOUBLE, kv DOUBLE, q_max DOUBLE, dp DOUBLE );";
				break;
			case "deleteControlTable":
				result = "DROP TABLE IF EXISTS CONTROL_VALVE_TEMP;";
				break;
			case "createControlTable":
				result = "CREATE TABLE CONTROL_VALVE_TEMP (m_seq VARCHAR(100), c_seq VARCHAR(100), product VARCHAR(200), name VARCHAR(100), size_name VARCHAR(100), size DOUBLE"
						+ ", turns_pos DOUBLE, kv DOUBLE ); ";
				break;	
			case "insertControl":
				result = "INSERT INTO CONTROL_VALVE_TEMP (m_seq, c_seq, product, name, size_name, size, turns_pos, kv) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				break;
			case "insertBalancing":
				result = "INSERT INTO BALANCING_VALVE_TEMP (m_seq, b_seq, product, name, size_name, size, min_flow, max_flow) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				break;
			case "insertCombined":
				result = "INSERT INTO COMBINED_CTRL_BAL_VALVE_TEMP (m_seq, cp_seq, product, name, size_name, size, setting, kv, q_max, dp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				break;
			default:
				result = "";
				break;
		}
		
		return result;
	}

}
