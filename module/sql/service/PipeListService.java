package module.sql.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import module.db.SQLite;
import module.domain.PipeList;
import module.sql.PipeListSql;

public class PipeListService {
	
	/**
	 * PIPE DB 가져오기
	 * @param db
	 * @return
	 */
	public List<PipeList> getPipeList(SQLite db, String series){
		
		List<PipeList> pipeLists = new ArrayList<PipeList>();
		try {
			db.pstmt = db.conn.prepareStatement(PipeListSql.Query("getPipeList"));
			db.pstmt.setString(1, series);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				PipeList pipeList = new PipeList();
				pipeList.setSeq(db.rs.getString("seq"));
				pipeList.setName(db.rs.getString("name"));
				pipeList.setSeries(db.rs.getString("series"));
				pipeList.setInner_diameter(db.rs.getDouble("inner_diameter"));
				pipeList.setRoughness(db.rs.getDouble("roughness"));
				pipeLists.add(pipeList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return pipeLists;
	}
	
	/**
	 * 변경 될 PIPELIST 가져오기 - 1개
	 * @param db
	 * @param seq
	 * @return
	 */
	public PipeList getPipeListBySeq(SQLite db, String seq){
		
		PipeList pipeList = new PipeList();
		
		try {
			db.pstmt = db.conn.prepareStatement(PipeListSql.Query("getPipeListBySeq"));
			db.pstmt.setString(1, seq);
			db.rs = db.pstmt.executeQuery();
			pipeList.setSeq(db.rs.getString("seq"));
			pipeList.setName(db.rs.getString("name"));
			pipeList.setSeries(db.rs.getString("series"));
			pipeList.setInner_diameter(db.rs.getDouble("inner_diameter"));
			pipeList.setRoughness(db.rs.getDouble("roughness"));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return pipeList;
	}

}
