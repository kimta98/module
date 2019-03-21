package module.sql.service;

import java.sql.SQLException;
import java.util.List;

import module.db.SQLite;
import module.domain.ControlValve;
import module.domain.Module;
import module.sql.ControlValveSql;

public class ControlValveService {
	
	/**
	 * 컨트롤 밸브 선정 계산값 TABLE 생성
	 * @param db
	 * @return
	 */
	public boolean createTable(SQLite db) {
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(ControlValveSql.Query("deleteTable"));
			db.stmt.executeUpdate(ControlValveSql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	/**
	 * 컨트롤 밸브 선정 계산값 INSERT
	 * @param controlValves
	 * @param db
	 * @return
	 */
	public boolean createControlValve(List<ControlValve> controlValves, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ControlValveSql.Query("insert"));
			for(int i = 0; i < controlValves.size(); i++){
				db.pstmt.setString(1, controlValves.get(i).getM_seq());
				db.pstmt.setString(2, controlValves.get(i).getC_seq());
				db.pstmt.setDouble(3, controlValves.get(i).getKv());
				db.pstmt.setDouble(4, controlValves.get(i).getValve_dp());
				db.pstmt.setDouble(5, controlValves.get(i).getMin_authority());
				db.pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public ControlValve getControlValveByMSeq(String module_seq, SQLite db){
		ControlValve cv = new ControlValve();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ControlValveSql.Query("getControlValveByMSeq"));
			db.pstmt.setString(1, module_seq);
			db.rs = db.pstmt.executeQuery();
			cv.setM_seq(db.rs.getString("m_seq"));
			cv.setC_seq(db.rs.getString("c_seq"));
			cv.setKv(db.rs.getDouble("kv"));
			cv.setValve_dp(db.rs.getDouble("valve_dp"));
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return cv;
	}
	
	
	public boolean updateControlValve(ControlValve controlValve, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ControlValveSql.Query("updateControlValve"));
			
			db.pstmt.setString(1, controlValve.getC_seq());
			db.pstmt.setDouble(2, controlValve.getKv());
			db.pstmt.setDouble(3, controlValve.getValve_dp());
			db.pstmt.setString(4, controlValve.getM_seq());
			
			db.pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public boolean updateMinAuthority(List<Module> modules, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ControlValveSql.Query("updateMinAuthority"));
			
			for(Module module : modules){
				db.pstmt.setDouble(1, module.getControlValve().getMin_authority());
				db.pstmt.setString(2, module.getSeq());
				db.pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	

}
