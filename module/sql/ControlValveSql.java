package module.sql;

public class ControlValveSql {
	
	public final static String Query(String name){
		
		String result = "";
		
		switch(name){
		case "deleteTable":
			result = "DROP TABLE IF EXISTS CONTROL_VALVE;";
			break;
		case "createTable":
			result = "CREATE TABLE CONTROL_VALVE (m_seq VARCHAR(100), c_seq VARCHAR(100), kv DOUBLE, valve_dp DOUBLE, min_authority DOUBLE);";
			break;	
		case "insert":
			result = "INSERT INTO CONTROL_VALVE (m_seq, c_seq, kv, valve_dp, min_authority) VALUES(?, ?, ?, ?, ?);";
			break;
		case "getControlValveByMSeq":
			result = "SELECT * FROM CONTROL_VALVE WHERE m_seq = ?;";
			break;
		case "updateControlValve":
			result = "UPDATE CONTROL_VALVE SET c_seq = ?, kv = ?, valve_dp = ? WHERE m_seq = ?;";
			break;
		case "updateMinAuthority":
			result = "UPDATE CONTROL_VALVE SET min_authority = ? WHERE m_seq = ?;";
			break;
		default:
			result = "";
			break;
		}
		
		return result;
	}

}
