package module.sql.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import module.db.SQLite;
import module.domain.Pipe;
import module.sql.PipeSql;

public class PipeService {
	
	
	
	/**
	 * 배관 선정 데이터 테이블 생성
	 * @param db
	 * @return
	 */
	public boolean createTable(SQLite db) {
		
		try {
			
			db.stmt = db.conn.createStatement();
			db.stmt.executeUpdate(PipeSql.Query("deleteTable"));
			db.stmt.executeUpdate(PipeSql.Query("createTable"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
	}
	
	
	public boolean updatePreHA(SQLite db) {
		
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("updatePreHA"));
			
			db.pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return true;
		
	}
	
	
	/**
	 * 배관 선정 및 계산 값 등록
	 * @param db
	 * @return
	 */
	public boolean createPipe(Pipe pipe, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("insert"));
			db.pstmt.setString(1, pipe.getP_seq());
			db.pstmt.setString(2, pipe.getM_seq());
			db.pstmt.setString(3, pipe.getM_name());
			db.pstmt.setString(4, pipe.getType());
			db.pstmt.setString(5, pipe.getPipe_seq());
			db.pstmt.setString(6, pipe.getPipe_name());
			db.pstmt.setDouble(7, pipe.getInner_diameter());
			db.pstmt.setDouble(8, pipe.getRoughness());
			db.pstmt.setDouble(9, pipe.getFlow());
			db.pstmt.setDouble(10, pipe.getStream_velocity());
			db.pstmt.setDouble(11, pipe.getViscosity());
			db.pstmt.setDouble(12, pipe.getReynolds());
			db.pstmt.setDouble(13, pipe.getDarcy());
			db.pstmt.setDouble(14, pipe.getDensity());
			db.pstmt.setDouble(15, pipe.getDp());
			db.pstmt.setDouble(16, pipe.getTotal_dp());
			db.pstmt.setDouble(17, pipe.getH_available());
			db.pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		
		return true;
	}
	
	
	public List<Pipe> getPipes(SQLite db){
		
		List<Pipe> pipes = new ArrayList<Pipe>();
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("getPipes"));
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				Pipe pipe = new Pipe();
				pipe.setP_seq(db.rs.getString("p_seq"));
				pipe.setM_seq(db.rs.getString("m_seq"));
				pipe.setM_name(db.rs.getString("m_name"));
				pipe.setType(db.rs.getString("type"));
				pipe.setPipe_seq(db.rs.getString("pipe_seq"));
				pipe.setPipe_name(db.rs.getString("pipe_name"));
				pipe.setInner_diameter(db.rs.getDouble("inner_diameter"));
				pipe.setRoughness(db.rs.getDouble("roughness"));
				pipe.setFlow(db.rs.getDouble("flow"));
				pipe.setStream_velocity(db.rs.getDouble("stream_velocity"));
				pipe.setViscosity(db.rs.getDouble("viscosity"));
				pipe.setReynolds(db.rs.getDouble("reynolds"));
				pipe.setDarcy(db.rs.getDouble("darcy"));
				pipe.setDensity(db.rs.getDouble("density"));
				pipe.setDp(db.rs.getDouble("dp"));
				pipe.setTotal_dp(db.rs.getDouble("total_dp"));
				pipe.setH_available(db.rs.getDouble("h_available"));
				pipes.add(pipe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return pipes;
	}
	
	
	public List<Pipe> getPipesByMSeq(SQLite db, String m_seq){
		
		List<Pipe> pipes = new ArrayList<Pipe>();
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("getPipesByMSeq"));
			db.pstmt.setString(1, m_seq);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				Pipe pipe = new Pipe();
				pipe.setP_seq(db.rs.getString("p_seq"));
				pipe.setM_seq(db.rs.getString("m_seq"));
				pipe.setM_name(db.rs.getString("m_name"));
				pipe.setType(db.rs.getString("type"));
				pipe.setPipe_seq(db.rs.getString("pipe_seq"));
				pipe.setPipe_name(db.rs.getString("pipe_name"));
				pipe.setInner_diameter(db.rs.getDouble("inner_diameter"));
				pipe.setRoughness(db.rs.getDouble("roughness"));
				pipe.setFlow(db.rs.getDouble("flow"));
				pipe.setStream_velocity(db.rs.getDouble("stream_velocity"));
				pipe.setViscosity(db.rs.getDouble("viscosity"));
				pipe.setReynolds(db.rs.getDouble("reynolds"));
				pipe.setDarcy(db.rs.getDouble("darcy"));
				pipe.setDensity(db.rs.getDouble("density"));
				pipe.setDp(db.rs.getDouble("dp"));
				pipe.setTotal_dp(db.rs.getDouble("total_dp"));
				pipe.setH_available(db.rs.getDouble("h_available"));
				pipe.setDescription(db.rs.getString("description"));
				pipe.setAccessory_dp(db.rs.getDouble("accessory_dp"));
				pipes.add(pipe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return pipes;
	}
	
	
	public Pipe getPipeByName(String m_name, String type, SQLite db){
		
		Pipe pipe = new Pipe();
		
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("getPipeByName"));
			db.pstmt.setString(1, m_name);
			db.pstmt.setString(2, type);
			db.rs = db.pstmt.executeQuery();
			while(db.rs.next()){
				pipe.setP_seq(db.rs.getString("p_seq"));
				pipe.setM_seq(db.rs.getString("m_seq"));
				pipe.setM_name(db.rs.getString("m_name"));
				pipe.setType(db.rs.getString("type"));
				pipe.setPipe_seq(db.rs.getString("pipe_seq"));
				pipe.setPipe_name(db.rs.getString("pipe_name"));
				pipe.setInner_diameter(db.rs.getDouble("inner_diameter"));
				pipe.setRoughness(db.rs.getDouble("roughness"));
				pipe.setFlow(db.rs.getDouble("flow"));
				pipe.setStream_velocity(db.rs.getDouble("stream_velocity"));
				pipe.setViscosity(db.rs.getDouble("viscosity"));
				pipe.setReynolds(db.rs.getDouble("reynolds"));
				pipe.setDarcy(db.rs.getDouble("darcy"));
				pipe.setDensity(db.rs.getDouble("density"));
				pipe.setDp(db.rs.getDouble("dp"));
				pipe.setTotal_dp(db.rs.getDouble("total_dp"));
				pipe.setH_available(db.rs.getDouble("h_available"));
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return pipe;
	}
	
	
	public void updatePipe(Pipe pipe, SQLite db){
		
		try {
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("updatePipe"));
			
			db.pstmt.setString(1, pipe.getPipe_seq());
			db.pstmt.setString(2, pipe.getPipe_name());
			db.pstmt.setDouble(3, pipe.getInner_diameter());
			db.pstmt.setDouble(4, pipe.getRoughness());
			db.pstmt.setDouble(5, pipe.getFlow());
			db.pstmt.setDouble(6, pipe.getStream_velocity());
			db.pstmt.setDouble(7, pipe.getViscosity());
			db.pstmt.setDouble(8, pipe.getReynolds());
			db.pstmt.setDouble(9, pipe.getDarcy());
			db.pstmt.setDouble(10, pipe.getDensity());
			db.pstmt.setDouble(11, pipe.getDp());
			db.pstmt.setDouble(12, pipe.getTotal_dp());
			db.pstmt.setString(13, pipe.getM_seq());
			db.pstmt.setString(14, pipe.getType());
			
			db.pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
	}
	
	
	public void updateHA(Pipe pipe, SQLite db){
		
		try {
			
			db.pstmt = db.conn.prepareStatement(PipeSql.Query("updateHA"));
			
			db.pstmt.setDouble(1, pipe.getH_available());
			db.pstmt.setString(2, pipe.getM_seq());
			db.pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
	}
	

}
