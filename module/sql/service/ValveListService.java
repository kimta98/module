package module.sql.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import module.db.SQLite;
import module.domain.BalancingValveList;
import module.domain.CombinedCtrlBalValveList;
import module.domain.ControlValveList;
import module.sql.ControlValveSql;
import module.sql.ValveListSql;

public class ValveListService {
	
	/**
	 * 컨트롤밸브 리스트 가져오기 - KV 값
	 * @param db
	 * @return
	 */
	public List<ControlValveList> getControlValveList(SQLite db, double below, double above, String product){
		
		List<ControlValveList> controlValveLists = new ArrayList<ControlValveList>();
		try {
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getControlValveList"));
			db.pstmt.setDouble(1, below);
			db.pstmt.setDouble(2, above);
			db.pstmt.setString(3, product);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				ControlValveList controlValveList = new ControlValveList();
				controlValveList.setSeq(db.rs.getString("seq"));
				controlValveList.setProduct(db.rs.getString("product"));
				controlValveList.setName(db.rs.getString("name"));
				controlValveList.setSize_name(db.rs.getString("size_name"));
				controlValveList.setSize(db.rs.getDouble("size"));
				controlValveList.setTurns_pos(db.rs.getDouble("turns_pos"));
				controlValveList.setKv(db.rs.getDouble("kv"));
				controlValveLists.add(controlValveList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return controlValveLists;
	}
	
	
	public List<BalancingValveList> getBalancingValveList(SQLite db, double flow, double below, double above, String product){
		
		List<BalancingValveList> balancingValveLists = new ArrayList<BalancingValveList>();
		
		try {
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getBalancingValveList"));
			db.pstmt.setDouble(1, flow);
			db.pstmt.setDouble(2, flow);
			db.pstmt.setDouble(3, below);
			db.pstmt.setDouble(4, above);
			db.pstmt.setString(5, product);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				BalancingValveList bvl = new BalancingValveList();
				bvl.setSeq(db.rs.getString("seq"));
				bvl.setProduct(db.rs.getString("product"));
				bvl.setName(db.rs.getString("name"));
				bvl.setSize_name(db.rs.getString("size_name"));
				bvl.setSize(db.rs.getDouble("size"));
				bvl.setMin_flow(db.rs.getDouble("min_flow"));
				bvl.setMax_flow(db.rs.getDouble("max_flow"));
				balancingValveLists.add(bvl);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return balancingValveLists;
	}
	
	
	
	/**
	 *  복합 밸브 리스트 가져오기 - Dp 값
	 * @param db
	 * @return
	 */
	public List<CombinedCtrlBalValveList> getCombinedValveList(SQLite db, double flow, double below, double above, String product){
		
		List<CombinedCtrlBalValveList> combinedCtrlBalValveLists = new ArrayList<CombinedCtrlBalValveList>();
		
		try {
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getCombinedValveList"));
			db.pstmt.setDouble(1, below);
			db.pstmt.setDouble(2, above);
			db.pstmt.setDouble(3, flow);
			db.pstmt.setString(4, product);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				CombinedCtrlBalValveList combinedCtrlBalValveList = new CombinedCtrlBalValveList();
				combinedCtrlBalValveList.setSeq(db.rs.getString("seq"));
				combinedCtrlBalValveList.setProduct(db.rs.getString("product"));
				combinedCtrlBalValveList.setName(db.rs.getString("name"));
				combinedCtrlBalValveList.setSize_name(db.rs.getString("size_name"));
				combinedCtrlBalValveList.setSize(db.rs.getDouble("size"));
				combinedCtrlBalValveList.setSetting(db.rs.getDouble("setting"));
				combinedCtrlBalValveList.setKv(db.rs.getDouble("kv"));
				combinedCtrlBalValveList.setQ_max(db.rs.getDouble("q_max"));
				combinedCtrlBalValveList.setDp(db.rs.getDouble("dp"));
				combinedCtrlBalValveLists.add(combinedCtrlBalValveList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return combinedCtrlBalValveLists;
	}
	
	
	public ControlValveList getControlValveBySeq(SQLite db, String valve_seq){
		
		ControlValveList cvl = new ControlValveList();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getControlValveBySeq"));
			db.pstmt.setString(1, valve_seq);
			db.rs = db.pstmt.executeQuery();
			cvl.setSeq(db.rs.getString("seq"));
			cvl.setProduct(db.rs.getString("product"));
			cvl.setName(db.rs.getString("name"));
			cvl.setSize_name(db.rs.getString("size_name"));
			cvl.setSize(db.rs.getDouble("size"));
			cvl.setTurns_pos(db.rs.getDouble("turns_pos"));
			cvl.setKv(db.rs.getDouble("kv"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return cvl;
	}
	
	
	public BalancingValveList getBalancingValveBySeq(SQLite db, String valve_seq){
		BalancingValveList bvl = new BalancingValveList();
		
		try {
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getBalancingValveBySeq"));
			db.pstmt.setString(1, valve_seq);
			db.rs = db.pstmt.executeQuery();
			bvl.setSeq(db.rs.getString("seq"));
			bvl.setProduct(db.rs.getString("product"));
			bvl.setName(db.rs.getString("name"));
			bvl.setSize_name(db.rs.getString("size_name"));
			bvl.setSize(db.rs.getDouble("size"));
			bvl.setMin_flow(db.rs.getDouble("min_flow"));
			bvl.setMax_flow(db.rs.getDouble("max_flow"));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return bvl;
	}
	
	
	public CombinedCtrlBalValveList getCombinedValveBySeq(SQLite db, String valve_seq){
		
		CombinedCtrlBalValveList cbvl = new CombinedCtrlBalValveList();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getCombinedValveBySeq"));
			db.pstmt.setString(1, valve_seq);
			db.rs = db.pstmt.executeQuery();
			cbvl.setSeq(db.rs.getString("seq"));
			cbvl.setProduct(db.rs.getString("product"));
			cbvl.setName(db.rs.getString("name"));
			cbvl.setSize_name(db.rs.getString("size_name"));
			cbvl.setSize(db.rs.getDouble("size"));
			cbvl.setSetting(db.rs.getDouble("setting"));
			cbvl.setKv(db.rs.getDouble("kv"));
			cbvl.setQ_max(db.rs.getDouble("q_max"));
			cbvl.setDp(db.rs.getDouble("dp"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		
		return cbvl;
	}
	
	
	public List<ControlValveList> getControlInnerDiameters(SQLite db, String product){
		
		List<ControlValveList> cvls = new ArrayList<ControlValveList>();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getControlInnerDiameters"));
			db.pstmt.setString(1, product);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				ControlValveList cvl = new ControlValveList();
				cvl.setSize(db.rs.getDouble("size"));
				cvls.add(cvl);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return cvls;
	}
	
	
	public List<BalancingValveList> getBalancingInnerDiameters(SQLite db, String product){
		
		List<BalancingValveList> bvls = new ArrayList<BalancingValveList>();
		
		try {

			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getBalancingInnerDiameters"));
			db.pstmt.setString(1, product);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				BalancingValveList bvl = new BalancingValveList();
				bvl.setSize(db.rs.getDouble("size"));
				bvls.add(bvl);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return bvls;
	}
	
	
	public List<CombinedCtrlBalValveList> getCombinedInnerDiameters(SQLite db, String product){
		
		List<CombinedCtrlBalValveList> cbvls = new ArrayList<CombinedCtrlBalValveList>();
		
		try {

			db.pstmt = db.conn.prepareStatement(ValveListSql.Query("getCombinedInnerDiameters"));
			db.pstmt.setString(1, product);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				CombinedCtrlBalValveList cbvl = new CombinedCtrlBalValveList();
				cbvl.setSize(db.rs.getDouble("size"));
				cbvls.add(cbvl);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return cbvls;
	}
	
	
	

}
