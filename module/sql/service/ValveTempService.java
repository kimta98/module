package module.sql.service;

import java.sql.SQLException;

import module.db.SQLite;
import module.domain.BalancingValveList;
import module.domain.CombinedCtrlBalValveList;
import module.domain.ControlValveList;
import module.sql.ValveTempSql;

public class ValveTempService {
	
	
	public boolean createTable(SQLite db){
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(ValveTempSql.Query("deleteTable"));
			db.stmt.executeUpdate(ValveTempSql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
		
	}
	
	
	public boolean createControlTable(SQLite db){
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(ValveTempSql.Query("deleteControlTable"));
			db.stmt.executeUpdate(ValveTempSql.Query("createControlTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
		
		
	}
	
	
	public boolean createControlValveTemp(ControlValveList controlValveTemp, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ValveTempSql.Query("insertControl"));
			
			db.pstmt.setString(1, controlValveTemp.getM_seq());
			db.pstmt.setString(2, controlValveTemp.getSeq());
			db.pstmt.setString(3, controlValveTemp.getProduct());
			db.pstmt.setString(4, controlValveTemp.getName());
			db.pstmt.setString(5, controlValveTemp.getSize_name());
			db.pstmt.setDouble(6, controlValveTemp.getSize());
			db.pstmt.setDouble(7, controlValveTemp.getTurns_pos());
			db.pstmt.setDouble(8, controlValveTemp.getKv());
			db.pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public boolean createBalancingValveTemp(BalancingValveList balancingValveTemp, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ValveTempSql.Query("insertBalancing"));
			
			db.pstmt.setString(1, balancingValveTemp.getM_seq());
			db.pstmt.setString(2, balancingValveTemp.getSeq());
			db.pstmt.setString(3, balancingValveTemp.getProduct());
			db.pstmt.setString(4, balancingValveTemp.getName());
			db.pstmt.setString(5, balancingValveTemp.getSize_name());
			db.pstmt.setDouble(6, balancingValveTemp.getSize());
			db.pstmt.setDouble(7, balancingValveTemp.getMin_flow());
			db.pstmt.setDouble(8, balancingValveTemp.getMax_flow());
			db.pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public boolean createCombinedTemp(CombinedCtrlBalValveList combinedValveTemp, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ValveTempSql.Query("insertCombined"));
			
			db.pstmt.setString(1, combinedValveTemp.getM_seq());
			db.pstmt.setString(2, combinedValveTemp.getSeq());
			db.pstmt.setString(3, combinedValveTemp.getProduct());
			db.pstmt.setString(4, combinedValveTemp.getName());
			db.pstmt.setString(5, combinedValveTemp.getSize_name());
			db.pstmt.setDouble(6, combinedValveTemp.getSize());
			db.pstmt.setDouble(7, combinedValveTemp.getSetting());
			db.pstmt.setDouble(8, combinedValveTemp.getKv());
			db.pstmt.setDouble(9, combinedValveTemp.getQ_max());
			db.pstmt.setDouble(10, combinedValveTemp.getDp());
			db.pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	

}
