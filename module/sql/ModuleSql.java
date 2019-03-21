package module.sql;

public class ModuleSql {

	/**
	 * DB Query name 받아서 쿼리문 리턴!
	 * @param name
	 * @return
	 */
	public final static String Query(String name){
		String result = "";
		
		switch(name){
			case "deleteTable":
				result = "DROP TABLE IF EXISTS MODULE;";
				break;
			case "createTable":
				result = "CREATE TABLE MODULE (seq INTEGER PRIMARY KEY, p_seq VARCHAR(100), name VARCHAR(100), p_bv_exist CHAR(1), description VARCHAR(100), balancing_type VARCHAR(50), terminal_unit_exist CHAR(1), circuit_type VARCHAR(50)"
						+ ", level INTEGER, p_head DOUBLE, p_flow DOUBLE, c_length DOUBLE, d_length DOUBLE, c_flow DOUBLE, d_flow DOUBLE, t_description VARCHAR(100), dp DOUBLE, created DATETIME, enabled CHAR(1));";
				break;
			case "insert":
				result = "INSERT INTO MODULE (p_seq, name, description, balancing_type, terminal_unit_exist, circuit_type, level, p_head, p_flow, c_length, d_length, c_flow, d_flow, t_description, dp, created, enabled) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, DATETIME('NOW', 'LOCALTIME'), 'Y');";
				break;
			case "isExistModule":
				result = "SELECT count(*) as count FROM SQLITE_MASTER WHERE name = 'MODULE';";
				break;
			case "getModules":
				result = "SELECT * FROM MODULE ORDER BY LEVEL, p_seq, seq, name";
				break;
			case "getPumpModules":
				result = "SELECT *, (SELECT COUNT(DISTINCT level) FROM MODULE d WHERE d.p_seq = m.p_seq) AS count FROM MODULE m WHERE circuit_type = 'Pump'";
				break;
			case "getModulesByPSeq":
				result = "SELECT * FROM MODULE WHERE p_seq = ? ORDER BY LEVEL, p_seq, seq, name;";
				break;
			case "getModuleBySeq":
				result = "SELECT * FROM MODULE WHERE seq = ?;";
				break;
			case "updateFlow":
				result = "UPDATE MODULE SET "
						+ "p_flow=?, c_flow=?, d_flow=?"
						+ "WHERE seq=?;";
				break;
			case "updateDp":
				result = "UPDATE MODULE SET dp = ? WHERE seq = ?";
				break;	
			case "updatePreDp":
				result = "UPDATE MODULE SET dp = 0 WHERE terminal_unit_exist != 'O' ";
				break;
			default:
				result = "";
				break;
		}
		
		return result;
	}
	
}
