package module.sql;

public class BalancingValveSql {
	
	public final static String Query(String name){
		
		String result = "";
		
		switch(name){
		case "deleteTable":
			result = "DROP TABLE IF EXISTS BALANCING_VALVE;";
			break;
		case "createTable":
			result = "CREATE TABLE BALANCING_VALVE (m_seq VARCHAR(100), b_seq VARCHAR(100), pre_valve_dp DOUBLE, valve_dp DOUBLE);";
			break;	
		case "insert":
			result = "INSERT INTO BALANCING_VALVE (m_seq, b_seq, pre_valve_dp, valve_dp) VALUES(?, ?, ?, ?);";
			break;	
		case "getBalancingValveByMSeq":
			result = "SELECT * FROM BALANCING_VALVE WHERE m_seq = ?;";
			break;
		case "updateBalancingValve":	
			result = "UPDATE BALANCING_VALVE SET b_seq = ?, pre_valve_dp = ? WHERE m_seq = ?;";
			break;
		case "updateValveDp":
			result = "UPDATE BALANCING_VALVE SET valve_dp = ? WHERE m_seq = ?;";
			break;
		default:
			result = "";
			break;
		}
		
		return result;
	}

}
