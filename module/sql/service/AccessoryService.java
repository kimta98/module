package module.sql.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import module.db.SQLite;
import module.domain.Accessory;
import module.sql.AccessorySql;

public class AccessoryService {
	
	/**
	 * 부손실 값 테이블 생성
	 * @param db
	 * @return
	 */
	public boolean createTable(SQLite db) {
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(AccessorySql.Query("deleteTable"));
			db.stmt.executeUpdate(AccessorySql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	/**
	 * 부손실 값 넣기
	 * @param accessorys
	 * @param db
	 * @return
	 */
	public boolean createAccessorys(List<Accessory> accessorys, SQLite db){
		
		try {
			db.pstmt = db.conn.prepareStatement(AccessorySql.Query("insert"));
			for(Accessory accessory : accessorys){
				db.pstmt.setString(1, accessory.getP_seq());
				db.pstmt.setString(2, accessory.getM_seq());
				db.pstmt.setString(3, accessory.getM_name());
				db.pstmt.setString(4, accessory.getType());
				db.pstmt.setString(5, accessory.getDescription());
				db.pstmt.setDouble(6, accessory.getDp());
				db.pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	
	public List<Accessory> getAccessorys(SQLite db){
		
		List<Accessory> accessorys = new ArrayList<Accessory>();
		
		try {
			db.pstmt = db.conn.prepareStatement(AccessorySql.Query("getAccessorys"));
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				Accessory accessory = new Accessory();
				accessory.setP_seq(db.rs.getString("p_seq"));
				accessory.setM_seq(db.rs.getString("m_seq"));
				accessory.setM_name(db.rs.getString("m_name"));
				accessory.setType(db.rs.getString("type"));
				accessory.setDescription(db.rs.getString("description"));
				accessory.setDp(db.rs.getDouble("dp"));
				accessorys.add(accessory);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return accessorys;
	}
	

}
