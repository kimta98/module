package module.sql.service;

import java.sql.SQLException;
import java.util.List;

import module.db.SQLite;
import module.domain.BalancingValve;
import module.domain.Module;
import module.sql.BalancingValveSql;
import module.sql.ControlValveSql;

public class BalancingValveService {
	
	
	/**
	 * 
	 * @param db
	 * @return
	 */
	public boolean createTable(SQLite db) {
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(BalancingValveSql.Query("deleteTable"));
			db.stmt.executeUpdate(BalancingValveSql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	
	public boolean createBalancingValve(List<BalancingValve> BalancingValves, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(BalancingValveSql.Query("insert"));
			for(int i = 0; i < BalancingValves.size(); i++){
				db.pstmt.setString(1, BalancingValves.get(i).getM_seq());
				db.pstmt.setString(2, BalancingValves.get(i).getB_seq());
				db.pstmt.setDouble(3, BalancingValves.get(i).getPre_valve_dp());
				db.pstmt.setDouble(4, BalancingValves.get(i).getValve_dp());
				db.pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public BalancingValve getBalancingValveByMSeq(String module_seq, SQLite db){
		
		BalancingValve bv = new BalancingValve();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(BalancingValveSql.Query("getBalancingValveByMSeq"));
			db.pstmt.setString(1, module_seq);
			db.rs = db.pstmt.executeQuery();
			bv.setM_seq(db.rs.getString("m_seq"));
			bv.setB_seq(db.rs.getString("b_seq"));
			bv.setValve_dp(db.rs.getDouble("valve_dp"));
			bv.setPre_valve_dp(db.rs.getDouble("pre_valve_dp"));
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return bv;
	}
	
	
	public boolean updateBalancingValve(BalancingValve bv, SQLite db){
		
		try {
			db.pstmt = db.conn.prepareStatement(BalancingValveSql.Query("updateBalancingValve"));
			db.pstmt.setString(1, bv.getB_seq());
			db.pstmt.setDouble(2, bv.getPre_valve_dp());
			db.pstmt.setString(3, bv.getM_seq());
			db.pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public boolean updateValveDp(List<Module> modules, SQLite db){
		
		try {
			for(Module module : modules){
				db.pstmt = db.conn.prepareStatement(BalancingValveSql.Query("updateValveDp"));
				db.pstmt.setDouble(1, module.getBalancingValve().getValve_dp());
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
