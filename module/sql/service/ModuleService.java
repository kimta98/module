package module.sql.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import module.db.SQLite;
import module.domain.Module;
import module.sql.ModuleSql;

public class ModuleService {

		/**
		 * PIPE TABLE 생성 - 존재하면 삭제 후 생성
		 * @param db
		 * @return
		 */
		public boolean createTable(SQLite db) {
			
			try {
				
				db.stmt = db.conn.createStatement();
				db.stmt.executeUpdate(ModuleSql.Query("deleteTable"));
				db.stmt.executeUpdate(ModuleSql.Query("createTable"));
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return true;
		}
		
		
		public boolean updatePreDp(SQLite db) {
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("updatePreDp"));
				db.pstmt.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return true;
			
		}
		
		
		/**
		 * PIPE DATA INSERT
		 * @param pipes
		 * @param db
		 * @return
		 */
		public boolean createModules(List<Module> modules, SQLite db) {
			
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("insert"));
				for(int i = 0; i < modules.size(); i++){
					db.pstmt.setString(1, modules.get(i).getName().substring(0, 1)); // project_id 임시로 넣기
					db.pstmt.setString(2, modules.get(i).getName());
					db.pstmt.setString(3, modules.get(i).getDescription());
					db.pstmt.setString(4, modules.get(i).getBalancing_type());
					db.pstmt.setString(5, modules.get(i).getTerminal_unit_exist());
					db.pstmt.setString(6, modules.get(i).getCircuit_type());
					db.pstmt.setInt(7, modules.get(i).getLevel());
					db.pstmt.setDouble(8, modules.get(i).getP_head());
					db.pstmt.setDouble(9, modules.get(i).getP_flow());
					db.pstmt.setDouble(10, modules.get(i).getC_length());
					db.pstmt.setDouble(11, modules.get(i).getD_length());
					db.pstmt.setDouble(12, modules.get(i).getC_flow());
					db.pstmt.setDouble(13, modules.get(i).getD_flow());
					db.pstmt.setString(14, modules.get(i).getT_description());
					db.pstmt.setDouble(15, modules.get(i).getDp());
					db.pstmt.executeUpdate();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return true;
		}
		
		
		public int isExistModule(SQLite db){
			
			int count = 0;
			
			try {
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("isExistModule"));
				count = db.pstmt.executeQuery().getInt("count");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return count;
		}
		
		
		
		/**
		 * PIPE SELECT
		 * @param db
		 * @return
		 */
		public List<Module> getModules(SQLite db) {
			
			
			List<Module> modules = new ArrayList<Module>();
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("getModules"));
				db.rs = db.pstmt.executeQuery();
				while(db.rs.next()){
					Module module = new Module();
					module.setSeq(db.rs.getString("seq"));
					module.setP_seq(db.rs.getString("p_seq"));
					module.setName(db.rs.getString("name"));
					module.setP_bv_exist(db.rs.getString("p_bv_exist"));
					module.setDescription(db.rs.getString("description"));
					module.setBalancing_type(db.rs.getString("balancing_type"));
					module.setTerminal_unit_exist(db.rs.getString("terminal_unit_exist"));
					module.setCircuit_type(db.rs.getString("circuit_type"));
					module.setLevel(db.rs.getInt("level"));
					module.setP_head(db.rs.getDouble("p_head"));
					module.setP_flow(db.rs.getDouble("p_flow"));
					module.setC_length(db.rs.getDouble("c_length"));
					module.setD_length(db.rs.getDouble("d_length"));
					module.setC_flow(db.rs.getDouble("c_flow"));
					module.setD_flow(db.rs.getDouble("d_flow"));
					module.setT_description(db.rs.getString("t_description"));
					module.setDp(db.rs.getDouble("dp"));
					module.setCreated(db.rs.getString("created"));
					module.setEnabled(db.rs.getString("enabled"));
					modules.add(module);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return modules;
		}
		
		
		public List<Module> getPumpModules(SQLite db){
			
			List<Module> modules = new ArrayList<Module>();
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("getPumpModules"));
				db.rs = db.pstmt.executeQuery();
				while(db.rs.next()){
					Module module = new Module();
					module.setSeq(db.rs.getString("seq"));
					module.setP_seq(db.rs.getString("p_seq"));
					module.setName(db.rs.getString("name"));
					module.setP_bv_exist(db.rs.getString("p_bv_exist"));
					module.setDescription(db.rs.getString("description"));
					module.setBalancing_type(db.rs.getString("balancing_type"));
					module.setTerminal_unit_exist(db.rs.getString("terminal_unit_exist"));
					module.setCircuit_type(db.rs.getString("circuit_type"));
					module.setLevel(db.rs.getInt("level"));
					module.setP_head(db.rs.getDouble("p_head"));
					module.setP_flow(db.rs.getDouble("p_flow"));
					module.setC_length(db.rs.getDouble("c_length"));
					module.setD_length(db.rs.getDouble("d_length"));
					module.setC_flow(db.rs.getDouble("c_flow"));
					module.setD_flow(db.rs.getDouble("d_flow"));
					module.setT_description(db.rs.getString("t_description"));
					module.setCount(db.rs.getInt("count"));
					module.setDp(db.rs.getDouble("dp"));
					module.setCreated(db.rs.getString("created"));
					module.setEnabled(db.rs.getString("enabled"));
					modules.add(module);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return modules;
		}
		
		
		public List<Module> getModulesByPSeq(SQLite db, String p_seq){
			
			List<Module> modules = new ArrayList<Module>();
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("getModulesByPSeq"));
				db.pstmt.setString(1, p_seq);
				db.rs = db.pstmt.executeQuery();
				while(db.rs.next()){
					Module module = new Module();
					module.setSeq(db.rs.getString("seq"));
					module.setP_seq(db.rs.getString("p_seq"));
					module.setName(db.rs.getString("name"));
					module.setP_bv_exist(db.rs.getString("p_bv_exist"));
					module.setDescription(db.rs.getString("description"));
					module.setBalancing_type(db.rs.getString("balancing_type"));
					module.setTerminal_unit_exist(db.rs.getString("terminal_unit_exist"));
					module.setCircuit_type(db.rs.getString("circuit_type"));
					module.setLevel(db.rs.getInt("level"));
					module.setP_head(db.rs.getDouble("p_head"));
					module.setP_flow(db.rs.getDouble("p_flow"));
					module.setC_length(db.rs.getDouble("c_length"));
					module.setD_length(db.rs.getDouble("d_length"));
					module.setC_flow(db.rs.getDouble("c_flow"));
					module.setD_flow(db.rs.getDouble("d_flow"));
					module.setT_description(db.rs.getString("t_description"));
					module.setDp(db.rs.getDouble("dp"));
					module.setCreated(db.rs.getString("created"));
					module.setEnabled(db.rs.getString("enabled"));
					modules.add(module);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return modules;
			
		}
		
		
		public Module getModuleBySeq(SQLite db, String seq){
			
			Module module = new Module();
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("getModuleBySeq"));
				db.pstmt.setString(1, seq);
				db.rs = db.pstmt.executeQuery();
				module.setSeq(db.rs.getString("seq"));
				module.setP_seq(db.rs.getString("p_seq"));
				module.setName(db.rs.getString("name"));
				module.setP_bv_exist(db.rs.getString("p_bv_exist"));
				module.setDescription(db.rs.getString("description"));
				module.setBalancing_type(db.rs.getString("balancing_type"));
				module.setTerminal_unit_exist(db.rs.getString("terminal_unit_exist"));
				module.setCircuit_type(db.rs.getString("circuit_type"));
				module.setLevel(db.rs.getInt("level"));
				module.setP_head(db.rs.getDouble("p_head"));
				module.setP_flow(db.rs.getDouble("p_flow"));
				module.setC_length(db.rs.getDouble("c_length"));
				module.setD_length(db.rs.getDouble("d_length"));
				module.setC_flow(db.rs.getDouble("c_flow"));
				module.setD_flow(db.rs.getDouble("d_flow"));
				module.setT_description(db.rs.getString("t_description"));
				module.setDp(db.rs.getDouble("dp"));
				module.setCreated(db.rs.getString("created"));
				module.setEnabled(db.rs.getString("enabled"));
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
			return module;
		}
		
		
		public void updateFlow(List<Module> modules, SQLite db){
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("updateFlow"));
				for(Module row : modules){
					db.pstmt.setDouble(1, row.getP_flow());
					db.pstmt.setDouble(2, row.getC_flow());
					db.pstmt.setDouble(3, row.getD_flow());
					db.pstmt.setString(4, row.getSeq());
					db.pstmt.executeUpdate();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
		}
		
		
		public void updateDp(Module module, SQLite db){
			
			try {
				
				db.pstmt = db.conn.prepareStatement(ModuleSql.Query("updateDp"));
				
				db.pstmt.setDouble(1, module.getDp());
				db.pstmt.setString(2, module.getSeq());
				db.pstmt.executeUpdate();
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.stmtClose();
			}
			
		}
		

}
