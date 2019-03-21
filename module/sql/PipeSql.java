package module.sql;

public class PipeSql {
	
	public final static String Query(String name){
		
		String result = "";
		
		switch(name){
		case "deleteTable":
			result = "DROP TABLE IF EXISTS PIPE;";
			break;
		case "createTable":
			result = "CREATE TABLE PIPE (p_seq VARCHAR(100), m_seq VARCHAR(100), m_name VARCHAR(100), type VARCHAR(100), pipe_seq VARCHAR(100), pipe_name VARCHAR(100), inner_diameter DOUBLE, roughness DOUBLE, flow DOUBLE"
					+ ", stream_velocity DOUBLE, viscosity DOUBLE, reynolds DOUBLE, darcy DOUBLE, density DOUBLE, dp DOUBLE, total_dp DOUBLE, h_available DOUBLE)";
			break;	
		case "insert":
			result = "INSERT INTO PIPE (p_seq, m_seq, m_name, type, pipe_seq, pipe_name, inner_diameter, roughness, flow, stream_velocity, viscosity, reynolds, darcy, density, dp, total_dp, h_available )"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?)";
			break;	
		case "getPipes":
			result = "SELECT * FROM PIPE ORDER BY p_seq, LENGTH(m_name), type DESC";
			break;
		case "getPipesByMSeq":
			result = "SELECT p.*, a.description, a.dp as accessory_dp"
					+ " FROM PIPE p, ACCESSORY a WHERE p.m_seq = ? AND p.m_seq = a.m_seq AND p.type = a.type;";
			break;
		case "getPipeByName":
			result = "SELECT * FROM PIPE WHERE m_name = ? AND type = ?;";
			break;
		case "updatePipe":
			result = "UPDATE PIPE SET pipe_seq = ?, pipe_name = ?, inner_diameter = ?, roughness = ?, flow = ?, stream_velocity = ?, viscosity = ?, reynolds = ?, darcy = ?, density = ?, dp = ?, total_dp = ? WHERE m_seq = ? AND type = ?;";
			break;
		case "updateHA":
			result = "UPDATE PIPE SET h_available = ? WHERE m_seq = ?";
			break;
		case "updatePreHA":
			result = "UPDATE PIPE SET h_available = 0;";
			break;
		default:
			result = "";
			break;
	}
		
		return result;
	}

}
