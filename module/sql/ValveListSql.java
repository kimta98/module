package module.sql;

public class ValveListSql {
	
	public final static String Query(String name){
		String result = "";
		
		switch(name){
			case "getControlValveList":
				result = "SELECT * FROM CONTROL_VALVE_LIST WHERE kv != 0 AND size >= ? AND size <= ? AND product = ? ORDER BY kv DESC;";
				break;
			case "getBalancingValveList":
				result = "SELECT * FROM BALANCING_VALVE_LIST  WHERE (min_flow <= ? AND max_flow >= ?) AND (size >= ? AND size <= ?) AND product = ? ORDER BY size ASC;";
				break;
			case "getCombinedValveList":
				result = "SELECT * FROM COMBINED_CTRL_BAL_VALVE_LIST WHERE (size >= ? AND size <= ?) AND q_max >= ? AND product = ? ORDER BY size, q_max ASC;";
				break;
			case "getControlValveBySeq":
				result = "SELECT * FROM CONTROL_VALVE_LIST WHERE seq = ?;";
				break;
			case "getBalancingValveBySeq":
				result = "SELECT * FROM BALANCING_VALVE_LIST WHERE seq = ?;";
				break;
			case "getCombinedValveBySeq":
				result = "SELECT * FROM COMBINED_CTRL_BAL_VALVE_LIST WHERE seq = ?;";
				break;
			case "getControlInnerDiameters":
				result = "SELECT DISTINCT(size) as size FROM CONTROL_VALVE_LIST WHERE product = ? ORDER BY size ASC;";
				break;	
			case "getBalancingInnerDiameters":
				result = "SELECT DISTINCT(size) as size FROM BALANCING_VALVE_LIST WHERE product = ? ORDER BY size ASC";
				break;
			case "getCombinedInnerDiameters":
				result = "SELECT DISTINCT(size) as size FROM COMBINED_CTRL_BAL_VALVE_LIST WHERE product = ? ORDER BY size ASC;";
				break;
			default:
				result = "";
				break;
		}
		
		return result;
	}

}
