package module.sql.service;

import java.sql.SQLException;
import java.util.List;

import module.db.SQLite;
import module.domain.CombinedCtrlBalValve;
import module.sql.CombinedCtrlBalValveSql;
import module.sql.ControlValveSql;

public class CombinedCtrlBalValveService {
	
	/**
	 * 복합 밸브 db 생성
	 * @param db
	 * @return
	 */
	public boolean createTable(SQLite db) {
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(CombinedCtrlBalValveSql.Query("deleteTable"));
			db.stmt.executeUpdate(CombinedCtrlBalValveSql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	/**
	 * 복합밸브 DATA 추가
	 * @param compositeValves
	 * @param db
	 * @return
	 */
	public boolean createCombinedValve(List<CombinedCtrlBalValve> combinedValves, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(CombinedCtrlBalValveSql.Query("insert"));
			for(int i = 0; i < combinedValves.size(); i++){
				db.pstmt.setString(1, combinedValves.get(i).getM_seq());
				db.pstmt.setString(2, combinedValves.get(i).getCp_seq());
				db.pstmt.setDouble(3, combinedValves.get(i).getValve_dp());
				db.pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public CombinedCtrlBalValve getCombinedValveByMSeq(String module_seq, SQLite db){
		CombinedCtrlBalValve cbv = new CombinedCtrlBalValve();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(CombinedCtrlBalValveSql.Query("getCombinedValveByMSeq"));
			db.pstmt.setString(1, module_seq);
			db.rs = db.pstmt.executeQuery();
			cbv.setM_seq(db.rs.getString("m_seq"));
			cbv.setCp_seq(db.rs.getString("cp_seq"));
			cbv.setValve_dp(db.rs.getDouble("valve_dp"));
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return cbv;
	}
	
	
	public boolean updateCombinedValve(CombinedCtrlBalValve combinedCtrlBalValve, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(CombinedCtrlBalValveSql.Query("updateCombinedValve"));
			
			db.pstmt.setString(1, combinedCtrlBalValve.getCp_seq());
			db.pstmt.setDouble(2, combinedCtrlBalValve.getValve_dp());
			db.pstmt.setString(3, combinedCtrlBalValve.getM_seq());
			
			db.pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	

}
