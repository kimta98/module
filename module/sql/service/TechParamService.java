package module.sql.service;

import java.sql.SQLException;

import module.db.SQLite;
import module.domain.TechParam;
import module.sql.TechParamSql;

public class TechParamService {
	
	/**
	 * 테크니컬 파라메터 가져오기 - 설정값 사용자
	 * @param db
	 * @return
	 */
	public TechParam getTechParam(SQLite db){
		
		TechParam techParam = new TechParam();
		
		try {
			db.pstmt = db.conn.prepareStatement(TechParamSql.Query("getTechParam"));
			db.rs = db.pstmt.executeQuery();
			//techParam.setP_seq(db.rs.getString("p_seq"));
			//techParam.setPump_capacity(db.rs.getDouble("pump_capacity"));
			techParam.setPipe_series(db.rs.getString("pipe_series"));
			techParam.setManufacturer(db.rs.getString("manufacturer"));
			techParam.setPressure_drop_min(db.rs.getDouble("pressure_drop_min"));
			techParam.setPressure_drop_max(db.rs.getDouble("pressure_drop_max"));
			techParam.setVelocity_min(db.rs.getDouble("velocity_min"));
			techParam.setVelocity_max(db.rs.getDouble("velocity_max"));
			techParam.setPipe_safe_rate(db.rs.getDouble("pipe_safe_rate"));
			techParam.setControl_valve_min(db.rs.getDouble("control_valve_min"));
			techParam.setControl_valve_max(db.rs.getDouble("control_valve_max"));
			techParam.setBalancing_valve_min(db.rs.getDouble("balancing_valve_min"));
			techParam.setBalancing_valve_max(db.rs.getDouble("balancing_valve_max"));
			techParam.setTemp(db.rs.getDouble("temp"));
			techParam.setMin_authority(db.rs.getDouble("min_authority"));
			techParam.setT_value_branch(db.rs.getDouble("t_value_branch"));
			techParam.setT_value_inline(db.rs.getDouble("t_value_inline"));
			techParam.setDevice_above(db.rs.getInt("device_above"));
			techParam.setDevice_below(db.rs.getInt("device_below"));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.stmtClose();
		}
		
		return techParam;
	}

}
