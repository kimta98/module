package module.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import module.db.SQLite;
import module.domain.Module;
import module.domain.Pipe;
import module.domain.PipeList;
import module.domain.TechParam;
import module.sql.service.ModuleService;
import module.sql.service.PipeListService;
import module.sql.service.PipeService;
import module.sql.service.PipeTempService;

public class PipeCore {
	
	
	
	/**
	 * 유량 구하기
	 * @param modules
	 * @param db
	 */
	public void calcFlow(List<Module> modules, SQLite db){
		List<Module> calc_modules = new ArrayList<Module>();
		Collections.reverse(modules); // 모듈명 역순 -- 하위 레벨(terminal unit) 유량부터 계산
		
		for(Module module : modules){
			//모듈 데이터 반복
			
			Module calc_module = new Module(); // 계산 값을 계산 컬렉션에 담을 모듈 변수
			
			String top_module_name = "";
			
			double p_flow = 0.0d;
			double d_flow = 0.0d;
			double c_flow = 0.0d;
			
			if(module.getLevel() != 1){
				System.out.println(module.getName());
				int index = module.getName().lastIndexOf(".");
				top_module_name = module.getName().substring(0, index);
			}
			
			for(Module temp : modules){
				//임시 모듈 반복 -- 모듈명 포함 관계를 알기 위해 두번 돌림 ex) A.1,  A.1.1 <<
			
				if(module.getLevel() == 1){
					// Pump 는 무조건 레벨 1 가장 최상위
					// Pump 인 경우 - calc_modules 에 펌프 유량 계산 된 리스트 넣기
					
						if(temp.getTerminal_unit_exist().equals("O")){
							if(temp.getName().indexOf(module.getName()) > -1){
								// 터미널 유닛이 존재하고 펌프 이름이 존재하는 모듈이면
								// terminal unit flow 를 모두 더해서 pump flow 로 넣는다.
								p_flow = p_flow + temp.getC_flow(); // 터미널 유닛 경우엔 circuit flow == terminal unit flow 이므로 c_flow 로 DATA 가 들어가 있다.
							}
						}
					
				} else {
					// Pump 가 아닌 경우 - calc_modules 컬렉션에 담겨있는 값을 이용해야 한다.
					
					if(temp.getTerminal_unit_exist().equals("O")){
						if(module.getTerminal_unit_exist().equals("O")){
							if(temp.getName().equals(module.getName())){
								c_flow = temp.getC_flow();
							}
						}else{
							if(temp.getName().indexOf(module.getName()) > -1 && temp.getName().substring(module.getName().length(), module.getName().length()+1).equals(".")){
								// 순서
								// A.1 이 들어간 경우 A.1.1, A.1.2 두개의 C_flow 합산
								// A.2 이 들어간 경우 A.2.1, A.2.1 두개의 C_flow 합산.. 
								// module 이 터미널 유닛일 경우엔 사실 안돌려도 상관 없으나 돌렸음 A.1.1 이 포함 된 임시 모듈은 A.1.1 뿐
								// 모듈 이름 포함한  터미널 유닛 값 합산 C_flow
								c_flow = c_flow + temp.getC_flow();
							}
						}
					}
					
				}
				
			}// for 문 temp_modules
			
			//c_flow, p_flow 값 저장
			calc_module.setSeq(module.getSeq());
			calc_module.setName(module.getName());
			calc_module.setLevel(module.getLevel());
			calc_module.setP_flow(p_flow);
			calc_module.setC_flow(c_flow);
			calc_module.setD_flow(d_flow);
			calc_modules.add(calc_module);
			
		}// for 문 modules
		
		//d_flow 값 계산
		for(Module calc : calc_modules){
			double d_flow = 0.0d;
			
			String top_module_name = "";
			int index = 0;
			if(calc.getLevel() != 1){
				index = calc.getName().lastIndexOf(".");
				top_module_name = calc.getName().substring(0, index);
			}
			
			for(Module temp_calc : calc_modules){
				//위와 같이 모듈 두개 반복
				if(calc.getLevel() != 1 
				&& temp_calc.getLevel() != 1
				&& top_module_name.equals(temp_calc.getName().substring(0, temp_calc.getName().lastIndexOf(".")))){
					// 레벨 1이 아니고(pump 가 아니고) 
					// 모듈명의 마지막 댑스 기준 번호를 지운 상태의 모듈명이 같을 경우 - 상위 모듈이 같을 경우를 찾기 위해서
					
					if(Integer.parseInt(calc.getName().substring(index+1)) <= Integer.parseInt(temp_calc.getName().substring(temp_calc.getName().lastIndexOf(".")+1))){
						// 상위 모듈이 같은 배관의 댑스 순서에 따라 더 숫자가 작을 수록 높은 배관 순번을 가진 c_flow 값을 더한다.  ex) 3 <= 3 한번더함 2 <= 2, 2 <= 3 두번더함  1 <= 3, 1 <= 2, 1 <= 1 세번더함
						d_flow = d_flow + temp_calc.getC_flow();
					}
					
				}
			}
			calc.setD_flow(d_flow);
		}
		
		//d_flow 업데이트
		ModuleService ms = new ModuleService();
		ms.updateFlow(calc_modules, db);
		
	}
	
	
	
	
	/**
	 * 배관 선정 및 유속 / Dp 계산
	 * @param modules
	 * @return
	 */
	public void calcPipe(TechParam techParam, SQLite db){
		
		//List<Pipe> pipes = new ArrayList<Pipe>();
		List<PipeList> pipeLists = new ArrayList<PipeList>(); 
		List<Module> modules = new ArrayList<Module>(); 
		
		PipeListService pls = new PipeListService();
		pipeLists = pls.getPipeList(db, techParam.getPipe_series()); // 배관 DB 가져오기
		ModuleService ms = new ModuleService();
		modules = ms.getModules(db);
		PipeService ps = new PipeService();
		ps.createTable(db);
		PipeTempService pts = new PipeTempService();
		pts.createTable(db);
		
		if(modules != null){
			for(Module module : modules){
				System.out.println(module.getName());
				
				boolean c_flag = false; // 최소 Circuit 내경 저장 값
				boolean d_flag = false; // 최소 Distribution 내경 저장 값
				List<PipeList> temp_c_pipeLists = new ArrayList<PipeList>();
				List<PipeList> temp_d_pipeLists = new ArrayList<PipeList>();
				
				Pipe min_c_pipe = new Pipe(); // 최적의 Circuit 파이프 객체
				Pipe min_d_pipe = new Pipe(); // 최적의 Distribution 파이프 객체
				double min_c_per_target = 0.0d; // target 근사치 퍼센트
				double min_d_per_target = 0.0d; // target 근사치 퍼센트
				
				if(module.getCircuit_type().equals("Pump")){
					Pipe pipe = new Pipe();
					pipe.setP_seq(module.getP_seq());
					pipe.setM_seq(module.getSeq());
					pipe.setM_name(module.getName());
					pipe.setType("circuit");
					pipe.setPipe_seq("");
					pipe.setPipe_name("");
					pipe.setInner_diameter(0);
					pipe.setRoughness(0);
					pipe.setViscosity(0);
					pipe.setReynolds(0);
					pipe.setDarcy(0);
					pipe.setDensity(( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000);
					pipe.setDp(0);
					pipe.setTotal_dp(0);
					pipe.setStream_velocity(0);
					pipe.setFlow(module.getP_flow());
					ps.createPipe(pipe, db);
					
					pipe = new Pipe();
					pipe.setP_seq(module.getP_seq());
					pipe.setM_seq(module.getSeq());
					pipe.setM_name(module.getName());
					pipe.setType("distribution");
					pipe.setPipe_seq("");
					pipe.setPipe_name("");
					pipe.setInner_diameter(0);
					pipe.setRoughness(0);
					pipe.setViscosity(0);
					pipe.setReynolds(0);
					pipe.setDarcy(0);
					pipe.setDensity(( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000);
					pipe.setDp(0);
					pipe.setTotal_dp(0);
					pipe.setStream_velocity(0);
					pipe.setFlow(module.getP_flow());
					//pipes.add(pipe);
					ps.createPipe(pipe, db);
				}else{
					
					for(PipeList pipeList : pipeLists){
						
						
						pipeList.setM_seq(module.getSeq());
						
						double stream_velocity = 0; //유속
						double viscosity = 0; // 점성
						double reynolds = 0; // 레이놀즈수
						double darcy = 0; // darcy 계수
						double density = 0; // 밀도
						double dp = 0; // 길이당 dp
						double total_dp = 0; // 총 dp
						
						if(module.getC_length() != 0){
							stream_velocity = (module.getC_flow() / 3600) / ((Math.PI / 4) * Math.pow(pipeList.getInner_diameter() / 1000, 2));
							viscosity = ( 309 * ( 1 - Math.exp( -0.111 * (37 + techParam.getTemp()) ) ) / Math.pow( (37 + techParam.getTemp()) , 1.42)) * Math.pow(10, -6); 
							reynolds = stream_velocity * ((pipeList.getInner_diameter() / 1000) / viscosity);
							darcy = (1 / Math.pow(2 * Math.log10(3.715/(pipeList.getRoughness()/pipeList.getInner_diameter()*0.001)), 2)) 
									+ Math.pow((0.938 / Math.log10(reynolds)), 2.393) *Math.exp( -0.44 * Math.pow(( reynolds * ( pipeList.getRoughness() / pipeList.getInner_diameter() * 0.001 )), 0.33) ) ;
							density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000;
							dp = (darcy * 5 * Math.pow(stream_velocity, 2)) / pipeList.getInner_diameter() * Math.pow(10, 5) * (density / 1000);
							total_dp = (dp * module.getC_length() * (1 + 5 / 100d)) / 1000;
							if(stream_velocity < techParam.getVelocity_max() && dp < techParam.getPressure_drop_max()){ 
								
								temp_c_pipeLists.add(pipeList);
								
								if(!c_flag){
									c_flag = true;
									min_c_pipe.setP_seq(module.getP_seq());
									min_c_pipe.setM_seq(module.getSeq());
									min_c_pipe.setM_name(module.getName());
									min_c_pipe.setType("circuit");
									min_c_pipe.setPipe_seq(pipeList.getSeq());
									min_c_pipe.setPipe_name(pipeList.getName());
									min_c_pipe.setInner_diameter(pipeList.getInner_diameter());
									min_c_pipe.setRoughness(pipeList.getRoughness());
									min_c_pipe.setFlow(module.getC_flow());
									min_c_pipe.setStream_velocity(stream_velocity);
									min_c_pipe.setViscosity(viscosity);
									min_c_pipe.setReynolds(reynolds);
									min_c_pipe.setDarcy(darcy);
									min_c_pipe.setDensity(density);
									min_c_pipe.setDp(dp);
									min_c_pipe.setTotal_dp(total_dp);
								}
								
							}
						}else {
							
							stream_velocity = (module.getC_flow() / 3600) / ((Math.PI / 4) * Math.pow(pipeList.getInner_diameter() / 1000, 2));
							viscosity = ( 309 * ( 1 - Math.exp( -0.111 * (37 + techParam.getTemp()) ) ) / Math.pow( (37 + techParam.getTemp()) , 1.42)) * Math.pow(10, -6); 
							reynolds = stream_velocity * ((pipeList.getInner_diameter() / 1000) / viscosity);
							darcy = (1 / Math.pow(2 * Math.log10(3.715/(pipeList.getRoughness()/pipeList.getInner_diameter()*0.001)), 2)) 
									+ Math.pow((0.938 / Math.log10(reynolds)), 2.393) *Math.exp( -0.44 * Math.pow(( reynolds * ( pipeList.getRoughness() / pipeList.getInner_diameter() * 0.001 )), 0.33) ) ;
							density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000;
							dp = (darcy * 5 * Math.pow(stream_velocity, 2)) / pipeList.getInner_diameter() * Math.pow(10, 5) * (density / 1000);
							total_dp = (dp * module.getC_length() * (1 + 5 / 100d)) / 1000;
							if(stream_velocity < techParam.getVelocity_max() && dp < techParam.getPressure_drop_max()){ 
								
								temp_c_pipeLists.add(pipeList);
								
								if(!c_flag){
									c_flag = true;
									min_c_pipe.setP_seq(module.getP_seq());
									min_c_pipe.setM_seq(module.getSeq());
									min_c_pipe.setM_name(module.getName());
									min_c_pipe.setType("circuit");
									min_c_pipe.setPipe_seq(pipeList.getSeq());
									min_c_pipe.setPipe_name(pipeList.getName());
									min_c_pipe.setInner_diameter(pipeList.getInner_diameter());
									min_c_pipe.setRoughness(pipeList.getRoughness());
									min_c_pipe.setFlow(module.getC_flow());
									min_c_pipe.setStream_velocity(stream_velocity);
									min_c_pipe.setViscosity(viscosity);
									min_c_pipe.setReynolds(reynolds);
									min_c_pipe.setDarcy(darcy);
									min_c_pipe.setDensity(density);
									min_c_pipe.setDp(dp);
									min_c_pipe.setTotal_dp(total_dp);
								}
								
							}
							
						}
						
						if(module.getD_length() != 0){
							stream_velocity = (module.getD_flow() / 3600) / ((Math.PI / 4) * Math.pow(pipeList.getInner_diameter() / 1000, 2));
							viscosity = ( 309 * ( 1 - Math.exp( -0.111 * (37 + techParam.getTemp()) ) ) / Math.pow( (37 + techParam.getTemp()) , 1.42)) * Math.pow(10, -6); 
							reynolds = stream_velocity * ((pipeList.getInner_diameter() / 1000) / viscosity);
							darcy = (1 / Math.pow(2 * Math.log10(3.715/(pipeList.getRoughness()/pipeList.getInner_diameter()*0.001)), 2)) 
									+ Math.pow((0.938 / Math.log10(reynolds)), 2.393) *Math.exp( -0.44 * Math.pow(( reynolds * ( pipeList.getRoughness() / pipeList.getInner_diameter() * 0.001 )), 0.33) ) ;
							density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000;
							dp = (darcy * 5 * Math.pow(stream_velocity, 2)) / pipeList.getInner_diameter() * Math.pow(10, 5) * (density / 1000);
							total_dp = (dp * module.getD_length() * (1 + 5 / 100d)) / 1000;
							if(stream_velocity < techParam.getVelocity_max() && dp < techParam.getPressure_drop_max()){
								
								temp_d_pipeLists.add(pipeList);
								
								if(!d_flag){
									d_flag = true; // D_length 가 있는 경우
									min_d_pipe.setP_seq(module.getP_seq());
									min_d_pipe.setM_seq(module.getSeq());
									min_d_pipe.setM_name(module.getName());
									min_d_pipe.setType("distribution");
									min_d_pipe.setPipe_seq(pipeList.getSeq());
									min_d_pipe.setPipe_name(pipeList.getName());
									min_d_pipe.setInner_diameter(pipeList.getInner_diameter());
									min_d_pipe.setRoughness(pipeList.getRoughness());
									min_d_pipe.setFlow(module.getD_flow());
									min_d_pipe.setStream_velocity(stream_velocity);
									min_d_pipe.setViscosity(viscosity);
									min_d_pipe.setReynolds(reynolds);
									min_d_pipe.setDarcy(darcy);
									min_d_pipe.setDensity(density);
									min_d_pipe.setDp(dp);
									min_d_pipe.setTotal_dp(total_dp);
								}
								
							}
						}else {
							
							stream_velocity = (module.getD_flow() / 3600) / ((Math.PI / 4) * Math.pow(pipeList.getInner_diameter() / 1000, 2));
							viscosity = ( 309 * ( 1 - Math.exp( -0.111 * (37 + techParam.getTemp()) ) ) / Math.pow( (37 + techParam.getTemp()) , 1.42)) * Math.pow(10, -6); 
							reynolds = stream_velocity * ((pipeList.getInner_diameter() / 1000) / viscosity);
							darcy = (1 / Math.pow(2 * Math.log10(3.715/(pipeList.getRoughness()/pipeList.getInner_diameter()*0.001)), 2)) 
									+ Math.pow((0.938 / Math.log10(reynolds)), 2.393) *Math.exp( -0.44 * Math.pow(( reynolds * ( pipeList.getRoughness() / pipeList.getInner_diameter() * 0.001 )), 0.33) ) ;
							density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000;
							dp = (darcy * 5 * Math.pow(stream_velocity, 2)) / pipeList.getInner_diameter() * Math.pow(10, 5) * (density / 1000);
							total_dp = (dp * module.getD_length() * (1 + 5 / 100d)) / 1000;
							if(stream_velocity < techParam.getVelocity_max() && dp < techParam.getPressure_drop_max()){
								
								temp_d_pipeLists.add(pipeList);
								
								if(!d_flag){
									d_flag = true; // D_length 가 있는 경우
									min_d_pipe.setP_seq(module.getP_seq());
									min_d_pipe.setM_seq(module.getSeq());
									min_d_pipe.setM_name(module.getName());
									min_d_pipe.setType("distribution");
									min_d_pipe.setPipe_seq(pipeList.getSeq());
									min_d_pipe.setPipe_name(pipeList.getName());
									min_d_pipe.setInner_diameter(pipeList.getInner_diameter());
									min_d_pipe.setRoughness(pipeList.getRoughness());
									min_d_pipe.setFlow(module.getD_flow());
									min_d_pipe.setStream_velocity(stream_velocity);
									min_d_pipe.setViscosity(viscosity);
									min_d_pipe.setReynolds(reynolds);
									min_d_pipe.setDarcy(darcy);
									min_d_pipe.setDensity(density);
									min_d_pipe.setDp(dp);
									min_d_pipe.setTotal_dp(total_dp);
								}
								
							}
							
						}
						
					}
					
					if(c_flag){
						ps.createPipe(min_c_pipe, db); // target 에 가장 근접한 pipe
						pts.createPipeTemp(temp_c_pipeLists, "circuit", db);
					}
					
					if(d_flag){
						ps.createPipe(min_d_pipe, db); // target 에 가장 근접한 pipe
						pts.createPipeTemp(temp_d_pipeLists, "distribution", db);
					}
				}
				
				
				
				
			}
		}
		
	}
	
	
	/**
	 * 파이프 변경시
	 * @param techParam - 테크니컬파라메터
	 * @param module_seq - module seq
	 * @param pipe_seq - 파이프 seq
	 * @param type - distribution / circuit
	 * @param db - sqlite
	 */
	public void updateCalcPipe(TechParam techParam, String module_seq, String pipe_seq, String type, SQLite db) {
		
		PipeListService pls = new PipeListService();
		ModuleService ms = new ModuleService();
		PipeService ps = new PipeService();
		Module module = ms.getModuleBySeq(db, module_seq); // 해당 모듈
		PipeList pipeList = pls.getPipeListBySeq(db, pipe_seq); //  파이프 리스트 중 변경하고자 하는 파이프 - 1개
		
		double stream_velocity = 0; //유속
		double viscosity = 0; // 점성
		double reynolds = 0; // 레이놀즈수
		double darcy = 0; // darcy 계수
		double density = 0; // 밀도
		double dp = 0; // 길이당 dp
		double total_dp = 0; // 총 dp
		
		Pipe pipe = new Pipe();
		
		if(type.equals("distribution")){
			//distribution
			stream_velocity = (module.getD_flow() / 3600) / ((Math.PI / 4) * Math.pow(pipeList.getInner_diameter() / 1000, 2));
			viscosity = ( 309 * ( 1 - Math.exp( -0.111 * (37 + techParam.getTemp()) ) ) / Math.pow( (37 + techParam.getTemp()) , 1.42)) * Math.pow(10, -6); 
			reynolds = stream_velocity * ((pipeList.getInner_diameter() / 1000) / viscosity);
			darcy = (1 / Math.pow(2 * Math.log10(3.715/(pipeList.getRoughness()/pipeList.getInner_diameter()*0.001)), 2)) 
					+ Math.pow((0.938 / Math.log10(reynolds)), 2.393) *Math.exp( -0.44 * Math.pow(( reynolds * ( pipeList.getRoughness() / pipeList.getInner_diameter() * 0.001 )), 0.33) ) ;
			density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000;
			dp = (darcy * 5 * Math.pow(stream_velocity, 2)) / pipeList.getInner_diameter() * Math.pow(10, 5) * (density / 1000);
			total_dp = (dp * module.getD_length() * (1 + 5 / 100d)) / 1000;
			pipe.setP_seq(module.getP_seq());
			pipe.setM_seq(module.getSeq());
			pipe.setM_name(module.getName());
			pipe.setType("distribution");
			pipe.setPipe_seq(pipeList.getSeq());
			pipe.setPipe_name(pipeList.getName());
			pipe.setInner_diameter(pipeList.getInner_diameter());
			pipe.setRoughness(pipeList.getRoughness());
			pipe.setFlow(module.getD_flow());
			pipe.setStream_velocity(stream_velocity);
			pipe.setViscosity(viscosity);
			pipe.setReynolds(reynolds);
			pipe.setDarcy(darcy);
			pipe.setDensity(density);
			pipe.setDp(dp);
			pipe.setTotal_dp(total_dp);
		}else{
			//circuit
			stream_velocity = (module.getC_flow() / 3600) / ((Math.PI / 4) * Math.pow(pipeList.getInner_diameter() / 1000, 2));
			viscosity = ( 309 * ( 1 - Math.exp( -0.111 * (37 + techParam.getTemp()) ) ) / Math.pow( (37 + techParam.getTemp()) , 1.42)) * Math.pow(10, -6); 
			reynolds = stream_velocity * ((pipeList.getInner_diameter() / 1000) / viscosity);
			darcy = (1 / Math.pow(2 * Math.log10(3.715/(pipeList.getRoughness()/pipeList.getInner_diameter()*0.001)), 2)) 
					+ Math.pow((0.938 / Math.log10(reynolds)), 2.393) *Math.exp( -0.44 * Math.pow(( reynolds * ( pipeList.getRoughness() / pipeList.getInner_diameter() * 0.001 )), 0.33) ) ;
			density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000;
			dp = (darcy * 5 * Math.pow(stream_velocity, 2)) / pipeList.getInner_diameter() * Math.pow(10, 5) * (density / 1000);
			total_dp = (dp * module.getC_length() * (1 + 5 / 100d)) / 1000;
			pipe.setP_seq(module.getP_seq());
			pipe.setM_seq(module.getSeq());
			pipe.setM_name(module.getName());
			pipe.setType("circuit");
			pipe.setPipe_seq(pipeList.getSeq());
			pipe.setPipe_name(pipeList.getName());
			pipe.setInner_diameter(pipeList.getInner_diameter());
			pipe.setRoughness(pipeList.getRoughness());
			pipe.setFlow(module.getC_flow());
			pipe.setStream_velocity(stream_velocity);
			pipe.setViscosity(viscosity);
			pipe.setReynolds(reynolds);
			pipe.setDarcy(darcy);
			pipe.setDensity(density);
			pipe.setDp(dp);
			pipe.setTotal_dp(total_dp);
		}
		
		ps.updatePipe(pipe, db);
		
	}
	
	
	
}
