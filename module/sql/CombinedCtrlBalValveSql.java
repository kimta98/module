package module.sql;

public class CombinedCtrlBalValveSql {
	
	public final static String Query(String name){
		
		String result = "";
		
		switch(name){
		case "deleteTable":
			result = "DROP TABLE IF EXISTS COMBINED_CTRL_BAL_VALVE;";
			break;
		case "createTable":
			result = "CREATE TABLE COMBINED_CTRL_BAL_VALVE (m_seq VARCHAR(100), cp_seq VARCHAR(100),  valve_dp DOUBLE);";
			break;	
		case "insert":
			result = "INSERT INTO COMBINED_CTRL_BAL_VALVE (m_seq, cp_seq, valve_dp) VALUES(?, ?, ?);";
			break;	
		case "getCombinedValveByMSeq":
			result = "SELECT * FROM COMBINED_CTRL_BAL_VALVE WHERE m_seq = ?;";
			break;
		case "updateCombinedValve":
			result = "UPDATE COMBINED_CTRL_BAL_VALVE SET cp_seq = ?, valve_dp = ? WHERE m_seq = ?;";
			break;
		default:
			result = "";
			break;
		}
		
		return result;
	}

}
