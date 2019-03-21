package module.sql.service;

import java.sql.SQLException;
import java.util.List;

import module.db.SQLite;
import module.domain.PipeList;
import module.sql.PipeTempSql;

public class PipeTempService {
	
	
	
	public boolean createTable(SQLite db){
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(PipeTempSql.Query("deleteTable"));
			db.stmt.executeUpdate(PipeTempSql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
		
	}
	
	
	public boolean createPipeTemp(List<PipeList> pipeLists, String type, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeTempSql.Query("insert"));
			//m_seq, p_seq, series, name, inner_diameter, roughness, max_pressure, max_temp
			for(PipeList pipeList : pipeLists){
				
				db.pstmt.setString(1, pipeList.getM_seq());
				db.pstmt.setString(2, type);
				db.pstmt.setString(3, pipeList.getSeq());
				db.pstmt.setString(4, pipeList.getSeries());
				db.pstmt.setString(5, pipeList.getName());
				db.pstmt.setDouble(6, pipeList.getInner_diameter());
				db.pstmt.setDouble(7, pipeList.getRoughness());
				db.pstmt.executeUpdate();
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	/*public boolean createControlValveTemp(ControlValveList controlValveTemp, SQLite db){
		
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
	}*/
	
	

}
