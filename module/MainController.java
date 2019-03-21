package module;

import java.util.Collections;
import java.util.List;

import module.core.AccessoryCore;
import module.core.PipeCore;
import module.core.ValveCore;
import module.db.SQLite;
import module.domain.Module;
import module.domain.Pipe;
import module.domain.TechParam;
import module.sql.service.ModuleService;
import module.sql.service.PipeService;
import module.sql.service.TechParamService;
import module.util.FileUtil;
//import module.util.FileUtil;

public class MainController {
	
	
	/**
	 * CSV 파일 업로드 이후 첫 알고리즘
	 * @param path
	 * @return
	 */
	public boolean calc(String path){
		
		SQLite db = new SQLite(path);
		//SQLite db = new SQLite("module.db");
		try {
			
			//---------- Service
			ModuleService ms = new ModuleService();
			TechParamService ts = new TechParamService();
			PipeService ps = new PipeService();
			
			//---------- SQLite db 파일 지정 -- 이름 임시로 정함
			//SQLite db = new SQLite("module.db");
			db.dbConnect(); //DB 연결
			
			//---------- module
			FileUtil file = new FileUtil();
			ms.createTable(db);
			ms.createModules(file.File(), db);
			List<Module> modules = ms.getModules(db); // 추가 된 모듈 리스트 가져오기
			
			//---------- tech param 
			TechParam techParam = ts.getTechParam(db);
			
			//---------- pipe 선정 알고리즘
			PipeCore pc = new PipeCore();
			pc.calcFlow(modules, db); // module level 에 따른 circuit, distribution flow 값 계산
			pc.calcPipe(techParam, db); // 테크니컬 파라메터 값에 따른 배관 선정
			List<Pipe> pipes = ps.getPipes(db); // 선정 된 배관 리스트 가져오기
			
			//---------- Accessory - 부손실 계산
			AccessoryCore ac = new AccessoryCore();
			ac.calcLoss(modules, pipes, techParam, db); // 부손실 계산
			
			//---------- Control, Balancing, Composite Valve 선정 및 H_available 계산
			ValveCore vc = new ValveCore();
			vc.calcValve(techParam, db);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			//---------- db 연결 종료
			db.dbClose();
		}
		
		return true;
	}
	
	
	/**
	 * 파이프 변경 시 알고리즘
	 * @param path
	 * @param module_seq
	 * @param pipe_seq
	 * @param type
	 * @return
	 */
	public boolean updateCalcPipe(String path, String module_seq, String pipe_seq, String type){
		
		SQLite db = new SQLite(path);
		
		try {
			
			//---------- Service
			ModuleService ms = new ModuleService();
			TechParamService ts = new TechParamService();
			PipeService ps = new PipeService();
			
			//---------- SQLite
			db.dbConnect(); //DB 연결
			
			ms.updatePreDp(db);
			ps.updatePreHA(db);
			//---------- module
			List<Module> modules = ms.getModules(db); // 추가 된 모듈 리스트 가져오기
			
			//---------- tech param 
			TechParam techParam = ts.getTechParam(db);
			
			//---------- pipe 변경 된 계산 알고리즘
			PipeCore pc = new PipeCore();
			Collections.reverse(modules);
			pc.updateCalcPipe(techParam, module_seq, pipe_seq, type, db);
			List<Pipe> pipes = ps.getPipes(db);
			
			//---------- Accessory - 부손실 계산
			AccessoryCore ac = new AccessoryCore();
			ac.calcLoss(modules, pipes, techParam, db); // 부손실 계산
			
			//---------- Control, Balancing, Composite Valve 선정 및 H_available 계산
			ValveCore vc = new ValveCore();
			vc.calcValve(techParam, db);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			//---------- db 연결 종료
			db.dbClose();
		}
		
		
		
		return true;
	}
	
	
	/**
	 * 밸브 변경 시 알고리즘
	 * @param path
	 * @param module_seq
	 * @param valve_seq
	 * @param valve_type
	 * @return
	 */
	public boolean updateCalcValve(String path, String module_seq, String valve_seq, String valve_type){
		
		
		SQLite db = new SQLite(path);
		
		try {
			
			//---------- Service
			TechParamService ts = new TechParamService();
			ModuleService ms = new ModuleService();
			PipeService ps = new PipeService();
			
			//---------- SQLite
			db.dbConnect(); //DB 연결
			
			ms.updatePreDp(db);
			ps.updatePreHA(db);
			
			//---------- tech param 
			TechParam techParam = ts.getTechParam(db);
			/*
			 * type : control, balancing, combined 타입 별로 계산 로직 다름
			 */
			ValveCore vc = new ValveCore();
			vc.updateCalcValve(techParam, module_seq, valve_seq, valve_type, db);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			//---------- db 연결 종료
			db.dbClose();
		}
		
		
		return true;
		
	}
	
	

}
