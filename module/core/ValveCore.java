package module.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.synth.SynthSpinnerUI;

import module.db.SQLite;
import module.domain.BalancingValve;
import module.domain.BalancingValveList;
import module.domain.CombinedCtrlBalValve;
import module.domain.CombinedCtrlBalValveList;
import module.domain.ControlValve;
import module.domain.ControlValveList;
import module.domain.Module;
import module.domain.Pipe;
import module.domain.TechParam;
import module.sql.service.BalancingValveService;
import module.sql.service.CombinedCtrlBalValveService;
import module.sql.service.ControlValveService;
import module.sql.service.ModuleService;
import module.sql.service.PipeService;
import module.sql.service.ValveListService;
import module.sql.service.ValveTempService;

public class ValveCore {

	/**
	 * 컨트롤 밸브 / 복합 밸브 선정 및 계산 ~!
	 * @param modules
	 * @param pipes
	 */
	public void calcValve(TechParam techParam, SQLite db){
		
		
		//밸브 리스트
		ValveListService vls = new ValveListService();
		
		//펌프 모듈만 가져오기 - 펌프에 따른 밸브 계산을 위해서
		ModuleService ms = new ModuleService();
		List<Module> pump_modules = ms.getPumpModules(db);
		
		List<ControlValve> controlValves = new ArrayList<ControlValve>();
		List<BalancingValve> balancingValves = new ArrayList<BalancingValve>();
		List<CombinedCtrlBalValve> combinedValves = new ArrayList<CombinedCtrlBalValve>();
		
		//pipe DB Service
		PipeService ps = new PipeService();
		
		//밸브 DB Service 
		ControlValveService cvs = new ControlValveService();
		BalancingValveService bvs = new BalancingValveService();
		CombinedCtrlBalValveService cbvs = new CombinedCtrlBalValveService();
		ValveTempService vts = new ValveTempService();
		cvs.createTable(db);
		bvs.createTable(db);
		cbvs.createTable(db);
		vts.createTable(db);
		
		
		
		//temp control valve list
		List<ControlValveList> tempControlValveLists = new ArrayList<ControlValveList>();
		
		
		/* 
		 * 1. 펌프 모듈 리스트 가져오기
		 * 2. 리스트 별로 돌리면서 펌프와 같은 p_seq 모듈 리스트 가져오기
		 * 3. 펌프 모듈에 head 값 파악하여 경우의 수 조건 절 주기
		 * 4. 계산 로직
		*/
		for(Module pump_module : pump_modules){
			
			List<Module> modules = ms.getModulesByPSeq(db, pump_module.getP_seq());
			
			Collections.reverse(modules); // 모듈명 역순 -- 하위 레벨(terminal unit) 부터
			
			if(pump_module.getBalancing_type().equals("Manual Balancing")){
				
				// 밸런싱 밸브 선정
				for(Module module : modules){
					
					List<Pipe> pipes = ps.getPipesByMSeq(db, module.getSeq());
					
					Pipe dis_pipe = null;
					Pipe cir_pipe = null;
					
					boolean c_v_flag = true;
					boolean b_v_flag = true;
					
					ControlValve cv = new ControlValve();
					BalancingValve bv = new BalancingValve();
					
					for(Pipe pipe : pipes){
						if(pipe.getType().equals("distribution")){
							dis_pipe = pipe;
						}else{
							cir_pipe = pipe;
						}
					}
					
					
					if(module.getLevel() != 1 && (module.getCircuit_type().equals("Distribution circuit") || module.getCircuit_type().equals("2-way control circuit")) ){
						// pump 가 아닐경우
						// Distribution circuit, 2-way control circuit 일 경우만 밸브 존재
						
						double[] balancing_device = this.getValveListByDevice(cir_pipe, techParam, "balancing", db);
						List<BalancingValveList> balancingValveLists = vls.getBalancingValveList(db, cir_pipe.getFlow(), balancing_device[1], balancing_device[2], techParam.getManufacturer());
						System.out.println(balancing_device[0] + "    " + balancing_device[1] + "    " + balancing_device[2]);
						
						for(BalancingValveList bvl : balancingValveLists){
							//device 및 유량으로 걸러 낸 리스트가 temp 목록 그 자체 
							bvl.setM_seq(module.getSeq());
							vts.createBalancingValveTemp(bvl, db);
							double b_valve_dp = 0.0d;
							if(techParam.getManufacturer().equals("IMI")){
								b_valve_dp = this.calcBalancingValveDp(bvl, cir_pipe.getFlow());
								b_valve_dp = Double.parseDouble(String.format("%.2f", b_valve_dp));
								//b_valve_dp = 3; // IMI 3Kpa 로 Default
							}else{
								b_valve_dp = 30.0; // SY 30Kpa 로 Default 
							}
							if(bvl.getSize() == balancing_device[0]){
								if(b_v_flag){
									//밸런싱밸브는 컨트롤과 다르게 바로 선택 - DB 순서 임의로.
									bv.setM_seq(module.getSeq());
									bv.setB_seq(bvl.getSeq());
									bv.setPre_valve_dp(b_valve_dp);
									bvl.setM_seq(module.getSeq());
									b_v_flag = false;
									balancingValves.add(bv);
								}
							}
						}// balancing valve list for 문
						
						
					}// module 이 pump 가 아닐 경우
					
					
					
					module.setDis_pipe(dis_pipe);
					module.setCir_pipe(cir_pipe);
					module.setBalancingValve(bv);
					
				} // 밸런싱 밸브 선정
				
				// 가상의 양정값 계산
				double imagine_h_a = this.calcKv(modules, techParam.getMin_authority(), db);
				
				// 컨트롤 밸브 선정
				for(Module module : modules){
					
					List<Pipe> pipes = ps.getPipesByMSeq(db, module.getSeq());
					
					Pipe dis_pipe = null;
					Pipe cir_pipe = null;
					
					boolean c_v_flag = true;
					boolean b_v_flag = true;
					
					ControlValve cv = new ControlValve();
					BalancingValve bv = new BalancingValve();
					
					for(Pipe pipe : pipes){
						if(pipe.getType().equals("distribution")){
							dis_pipe = pipe;
						}else{
							cir_pipe = pipe;
						}
					}
					
					
					if(module.getLevel() != 1 && (module.getCircuit_type().equals("Distribution circuit") || module.getCircuit_type().equals("2-way control circuit")) ){
						// pump 가 아닐경우
						// Distribution circuit, 2-way control circuit 일 경우만 밸브 존재
						
						double[] control_device = this.getValveListByDevice(cir_pipe, techParam, "control", db);
						List<ControlValveList> controlValveLists = vls.getControlValveList(db, control_device[1], control_device[2], techParam.getManufacturer());
						System.out.println(control_device[0] + "    " + control_device[1] + "    " + control_device[2]);
						
						for(ControlValveList cvl : controlValveLists){
							
							double c_valve_dp = 100 * (cir_pipe.getDensity() / 1000) * Math.pow((cir_pipe.getFlow() / cvl.getKv()), 2);
							c_valve_dp = Double.parseDouble(String.format("%.2f", c_valve_dp));
							
							if(techParam.getMin_authority() < c_valve_dp / imagine_h_a){
								cvl.setM_seq(module.getSeq());
								vts.createControlValveTemp(cvl, db);
								
								if(c_v_flag){
									if(cvl.getSize() == control_device[0]){
										cv.setC_seq(cvl.getSeq());
										cv.setM_seq(module.getSeq());
										cv.setKv(cvl.getKv());
										cv.setValve_dp(c_valve_dp);
										c_v_flag = false;
										controlValves.add(cv);
									}
								}
							}
						} // control valve list for 문
						
						
					}// module 이 pump 가 아닐 경우
					
					
					
					module.setDis_pipe(dis_pipe);
					module.setCir_pipe(cir_pipe);
					module.setControlValve(cv);
					
				} // 컨트롤 밸브 선정
				
				
				//같은 댑스 배관 중 이전 배관 부손실 값 찾기
				for(Module module : modules){ //원 모듈 리스트
					
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() != 1){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
					
						double pre_accessory_dp = 0.0; //이전 배관 부손실 dp 값 알기위한 변수
						
						
						for(Module target_module : modules){ //타겟 모듈 리스트
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".") ){
								if(module.getName().substring(index+1) != "1"){
									if(target_module.getName().equals(top_module_name + "." + (Integer.parseInt(module.getName().substring(index+1))-1))){
										pre_accessory_dp = target_module.getDis_pipe().getAccessory_dp();
									}
								}
							}
							
						} // target_modules
						module.setPre_accessory_dp(pre_accessory_dp);
						
					}
					
					
				} //modules
					
				for(Module module : modules){
					
					String top_module_name = "";
					int index = 0;
					double dis_pipe_dp_sum = 0.0d;
					if(module.getLevel() != 1){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						for(Module target_module : modules){
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_pipe_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
						}
						
						
						if(module.getTerminal_unit_exist().equals("O")){
							double dp_sum = dis_pipe_dp_sum + module.getDp() 
							+ module.getControlValve().getValve_dp() + module.getBalancingValve().getPre_valve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
							module.setDp_sum(dp_sum);
						}else{
							double dp_sum = dis_pipe_dp_sum + module.getDis_pipe().getH_available()
							+ module.getControlValve().getValve_dp() + module.getBalancingValve().getPre_valve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
							module.setDp_sum(dp_sum);
							//dp update sql
							ms.updateDp(module, db);
						}
						
					
						for(Module target_module : modules){
							if(target_module.getName().equals(top_module_name) && target_module.getDis_pipe().getH_available() < module.getDp_sum()){
								target_module.getDis_pipe().setH_available(module.getDp_sum());
								target_module.setDp(module.getDp_sum());
							}
						}
					
					}
					
				}
				
				for(Module module : modules){
					
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() != 1){
					
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
								if(target_module.getDis_pipe().getH_available() - module.getDp_sum() != 0){
									System.out.println("target: "+ target_module.getDis_pipe().getH_available());
									System.out.println("-------------------------------------------------");
									module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
								}else{
									module.getBalancingValve().setValve_dp(module.getBalancingValve().getPre_valve_dp());
								}
							}
							
						}
						
					}
					//h_available update sql
					ps.updateHA(module.getDis_pipe(), db);
					
					System.out.println("name: "+module.getName());
					//System.out.println(module.getDis_pipe().getTotal_dp() + module.getPre_accessory_dp() );
					//System.out.println(module.getDp());
					System.out.println("dpsum: "+module.getDp_sum());
					//System.out.println(module.getControlValve().getMin_authority());
					System.out.println("h_available: "+module.getDis_pipe().getH_available());
					System.out.println("valve_dp: "+module.getBalancingValve().getValve_dp());
					System.out.println("------------------------------------------------------");
					
					
					// Pump balancing valve 존재 여부에 따른 밸브 선정 아직 안했음.
					
				}
				
				if(pump_module.getP_head() != 0){
					// 펌프 해드가 존재 할 경우의 level  == 2 부터 계산 다시 
					for(Module module : modules){
						String top_module_name = "";
						int index = 0;
						if(module.getLevel() == 2){
							index = module.getName().lastIndexOf(".");
							top_module_name = module.getName().substring(0, index);
							
							double dis_dp_sum = 0.0d;
							
							for(Module target_module : modules){
								
								if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
										&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
										&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
									dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
								}
								
								if(target_module.getName().equals(top_module_name)){
									//pump
									module.setDp(target_module.getP_head() - dis_dp_sum);
									module.getDis_pipe().setH_available(target_module.getP_head() - dis_dp_sum);
									target_module.getDis_pipe().setH_available(target_module.getP_head());
									module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
									if(module.getBalancingValve().getValve_dp() != module.getBalancingValve().getPre_valve_dp()){
										module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
									}
									System.out.println("============================");
									System.out.println(module.getName());
									System.out.println(module.getDp());
									System.out.println("============================");
								}
								
							}
							
						}
						
						ms.updateDp(module, db);
						ps.updateHA(module.getDis_pipe(), db);
						
					}
					
					for(Module module : modules){
						String top_module_name = "";
						int index = 0;
						if(module.getLevel() == 3){
							index = module.getName().lastIndexOf(".");
							top_module_name = module.getName().substring(0, index);
							
							double dis_dp_sum = 0.0d;
							
							for(Module target_module : modules){
								
								if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
										&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
										&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
									dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
								}
								
								if(target_module.getName().equals(top_module_name)){
									//pump
									module.setDp(target_module.getDis_pipe().getH_available() - dis_dp_sum);
									module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
									target_module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available());
									module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
									if(module.getBalancingValve().getValve_dp() != module.getBalancingValve().getPre_valve_dp()){
										module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
									}
									System.out.println("============================");
									System.out.println(module.getName());
									System.out.println(module.getDp());
									System.out.println("============================");
								}
								
							}
							
						}
						ms.updateDp(module, db);
						ps.updateHA(module.getDis_pipe(), db);
						
					}
					
				}
				
			
			// manual balancing type
			}else{
				//diff. 
				
					for(Module module : modules){
					
					List<Pipe> pipes = ps.getPipesByMSeq(db, module.getSeq());
					
					Pipe dis_pipe = null;
					Pipe cir_pipe = null;
					
					boolean cp_v_flag = true; 
					
					CombinedCtrlBalValve cpv = new CombinedCtrlBalValve();
					
					for(Pipe pipe : pipes){
						if(pipe.getType().equals("distribution")){
							dis_pipe = pipe;
						}else{
							cir_pipe = pipe;
						}
					}
					
					if(module.getLevel() != 1 && (module.getCircuit_type().equals("Distribution circuit") || module.getCircuit_type().equals("2-way control circuit")) ){
						
						double[] combined_device = this.getValveListByDevice(cir_pipe, techParam, "combined", db);
						List<CombinedCtrlBalValveList> combinedValveLists = vls.getCombinedValveList(db, cir_pipe.getFlow(), combined_device[1], combined_device[2], techParam.getManufacturer());
						System.out.println(combined_device[0] + "    " + combined_device[1] + "    " + combined_device[2]);
						
						for(CombinedCtrlBalValveList cbvl : combinedValveLists){
							
							cbvl.setM_seq(module.getSeq());
							vts.createCombinedTemp(cbvl, db);
							if(cbvl.getSize() == combined_device[0]){
								if(cp_v_flag){
									cpv.setCp_seq(cbvl.getSeq());
									cpv.setM_seq(module.getSeq());
									cpv.setValve_dp(cbvl.getDp());
									combinedValves.add(cpv);
									cp_v_flag = false;
								}
							}
						}
						if(cp_v_flag){
							combinedValveLists.get(0).setM_seq(module.getSeq());
							vts.createCombinedTemp(combinedValveLists.get(0), db);
							cpv.setCp_seq(combinedValveLists.get(0).getSeq());
							cpv.setM_seq(module.getSeq());
							cpv.setValve_dp(combinedValveLists.get(0).getDp());
							combinedValves.add(cpv);
							cp_v_flag = false;
						}
						
					}
					
					module.setDis_pipe(dis_pipe);
					module.setCir_pipe(cir_pipe);
					module.setCombinedValve(cpv);
					
				} // modules ~
				
					
				//같은 댑스 배관 중 이전 배관 부손실 값 찾기
				for(Module module : modules){ //원 모듈 리스트
					
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() != 1){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
					
						double pre_accessory_dp = 0.0; //이전 배관 부손실 dp 값 알기위한 변수
						
						
						for(Module target_module : modules){ //타겟 모듈 리스트
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")){
								if(module.getName().substring(index+1) != "1"){
									if(target_module.getName().equals(top_module_name + "." + (Integer.parseInt(module.getName().substring(index+1))-1))){
										pre_accessory_dp = target_module.getDis_pipe().getAccessory_dp();
									}
								}
							}
							
						} // target_modules
						module.setPre_accessory_dp(pre_accessory_dp);
						
					}
					
					
				} //modules	
				
				
				for(Module module : modules){
					
					String top_module_name = "";
					int index = 0;
					double dis_pipe_dp_sum = 0.0d;
					if(module.getLevel() != 1){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						for(Module target_module : modules){
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_pipe_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
						}
						
						
						if(module.getTerminal_unit_exist().equals("O")){
							double dp_sum = dis_pipe_dp_sum + module.getDp() 
							+ module.getCombinedValve().getValve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
							module.setDp_sum(dp_sum);
						}else{
							double dp_sum = dis_pipe_dp_sum + module.getDis_pipe().getH_available()
							+ module.getCombinedValve().getValve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
							module.setDp_sum(dp_sum);
							//dp update sql
							ms.updateDp(module, db);
						}
						
					
						for(Module target_module : modules){
							if(target_module.getName().equals(top_module_name) && target_module.getDis_pipe().getH_available() < module.getDp_sum()){
								target_module.getDis_pipe().setH_available(module.getDp_sum());
								target_module.setDp(module.getDp_sum());
							}
						}
					
					}
					
				}
				
				
				for(Module module : modules){
					
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() != 1){
					
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								System.out.println(module.getName());
								System.out.println(top_module_name);
								System.out.println(target_module.getDis_pipe().getH_available());
								System.out.println(dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								if(target_module.getDis_pipe().getH_available() - module.getDp_sum() != 0){
									System.out.println("target: "+ target_module.getDis_pipe().getH_available());
									System.out.println("-------------------------------------------------");
									//module.getCombinedValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getCombinedValve().getValve_dp()));
								}
							}
							
						}
						
					}
					//h_available update sql
					ps.updateHA(module.getDis_pipe(), db);
					
					System.out.println("name: "+module.getName());
					//System.out.println(module.getDis_pipe().getTotal_dp() + module.getPre_accessory_dp() );
					//System.out.println(module.getDp());
					System.out.println("dpsum: "+module.getDp_sum());
					//System.out.println(module.getControlValve().getMin_authority());
					System.out.println("h_available: "+module.getDis_pipe().getH_available());
					//System.out.println("valve_dp: "+module.getBalancingValve().getValve_dp());
					System.out.println("------------------------------------------------------");
					
					
					// Pump balancing valve 존재 여부에 따른 밸브 선정 아직 안했음.
					
				}
				
				
				if(pump_module.getP_head() != 0){
					// 펌프 해드가 존재 할 경우의 level  == 2 부터 계산 다시 
					for(Module module : modules){
						String top_module_name = "";
						int index = 0;
						if(module.getLevel() == 2){
							index = module.getName().lastIndexOf(".");
							top_module_name = module.getName().substring(0, index);
							
							double dis_dp_sum = 0.0d;
							
							for(Module target_module : modules){
								
								if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
										&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
									dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
								}
								
								if(target_module.getName().equals(top_module_name)){
									//pump
									module.setDp(target_module.getP_head() - dis_dp_sum);
									module.getDis_pipe().setH_available(target_module.getP_head() - dis_dp_sum);
									target_module.getDis_pipe().setH_available(target_module.getP_head());
									System.out.println("============================");
									System.out.println(module.getName());
									System.out.println(module.getDp());
									System.out.println("============================");
								}
								
							}
							
						}
						ms.updateDp(module, db);
						ps.updateHA(module.getDis_pipe(), db);
						
					}
					
					for(Module module : modules){
						String top_module_name = "";
						int index = 0;
						if(module.getLevel() == 3){
							index = module.getName().lastIndexOf(".");
							top_module_name = module.getName().substring(0, index);
							
							double dis_dp_sum = 0.0d;
							
							for(Module target_module : modules){
								
								if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
										&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
										&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
									dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
								}
								
								if(target_module.getName().equals(top_module_name)){
									//pump
									module.setDp(target_module.getDis_pipe().getH_available() - dis_dp_sum);
									module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
									//target_module.getDis_pipe().setH_available(target_module.getP_head());
									System.out.println("============================");
									System.out.println(module.getName());
									System.out.println(module.getDp());
									System.out.println("============================");
								}
								
							}
							
						}
						ms.updateDp(module, db);
						ps.updateHA(module.getDis_pipe(), db);
						
					}
					
				}
				
				
			}
			
		} // module pump 개수 for 문
		
		// DB 생성 및 데이터 추가
		cvs.createControlValve(controlValves, db);
		bvs.createBalancingValve(balancingValves, db);
		cbvs.createCombinedValve(combinedValves, db);
	}
	
	
	/**
	 * 밸브 변경 시 알고리즘
	 * @param techParam // IMI, SY
	 * @param module_seq // 
	 * @param valve_seq
	 * @param valve_type // control, balancing, combined
	 * @param db
	 */
	public void updateCalcValve(TechParam techParam, String module_seq, String valve_seq, String valve_type, SQLite db){
		
		ModuleService ms = new ModuleService();
		PipeService ps = new PipeService();
		ValveListService vls = new ValveListService();
		ControlValveService cvs = new ControlValveService();
		BalancingValveService bvs = new BalancingValveService();
		CombinedCtrlBalValveService cbvs = new CombinedCtrlBalValveService();
		ValveTempService vts = new ValveTempService();
		
		Module param_module = ms.getModuleBySeq(db, module_seq);
		
		List<Module> modules = ms.getModulesByPSeq(db, param_module.getP_seq());
		
		boolean p_head_exist = false;
		Collections.reverse(modules);
		for(Module module : modules){
			
			List<Pipe> pipes = ps.getPipesByMSeq(db, module.getSeq());
			
			if(module.getLevel() == 1 && module.getP_head() != 0){
				p_head_exist = true;
			}
			
			Pipe dis_pipe = null;
			Pipe cir_pipe = null;
			ControlValve cv = new ControlValve();
			BalancingValve bv = new BalancingValve();
			CombinedCtrlBalValve cbv = new CombinedCtrlBalValve();
			
			for(Pipe pipe : pipes){
				if(pipe.getType().equals("distribution")){
					dis_pipe = pipe;
				}else{
					cir_pipe = pipe;
				}
			}
			
			if(module.getLevel() != 1){
			
				if(module.getBalancing_type().equals("Manual Balancing")){
					if(valve_type.equals("control")){
						cv = cvs.getControlValveByMSeq(module.getSeq(), db);
						bv = bvs.getBalancingValveByMSeq(module_seq, db);
					}else{
						bv = bvs.getBalancingValveByMSeq(module_seq, db);
						//컨트롤 밸브 / 컨트롤 밸브 선택 가능한 목록  DB 는 다시 선정해야 하므로 초기화
						cvs.createTable(db);
						vts.createControlTable(db);
					}
				}else{
					cbv = cbvs.getCombinedValveByMSeq(module_seq, db);
				}
			
			}
			
			module.setDis_pipe(dis_pipe);
			module.setCir_pipe(cir_pipe);
			module.setControlValve(cv);
			module.setBalancingValve(bv);
			module.setCombinedValve(cbv);
			
		}
		
		
		if(valve_type.equals("control")){
			//선택 된 밸브로 변경 후 H_A 계산부터 다시 계산
			System.out.println("==================");
			System.out.println("Control ValveChange");
			System.out.println("==================");
			for(Module module : modules){
				if(module.getSeq().equals(param_module.getSeq())){
					
					ControlValve cv = new ControlValve();
					
					ControlValveList cvl = vls.getControlValveBySeq(db, valve_seq);
					double c_valve_dp = 100 * (module.getCir_pipe().getDensity() / 1000) * Math.pow((module.getCir_pipe().getFlow() / cvl.getKv()), 2);
					c_valve_dp = Double.parseDouble(String.format("%.2f", c_valve_dp));
					
					cv.setC_seq(cvl.getSeq());
					cv.setM_seq(module_seq);
					cv.setKv(cvl.getKv());
					cv.setValve_dp(c_valve_dp);
					
					cvs.updateControlValve(cv, db);
					
					module.setControlValve(cv);
				}
			}
			
			//같은 댑스 배관 중 이전 배관 부손실 값 찾기
			for(Module module : modules){ //원 모듈 리스트
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
				
					double pre_accessory_dp = 0.0; //이전 배관 부손실 dp 값 알기위한 변수
					
					
					for(Module target_module : modules){ //타겟 모듈 리스트
						
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".") ){
							if(module.getName().substring(index+1) != "1"){
								if(target_module.getName().equals(top_module_name + "." + (Integer.parseInt(module.getName().substring(index+1))-1))){
									pre_accessory_dp = target_module.getDis_pipe().getAccessory_dp();
								}
							}
						}
						
					} // target_modules
					module.setPre_accessory_dp(pre_accessory_dp);
					
				}
				
				
			} //modules
				
			for(Module module : modules){
				
				String top_module_name = "";
				int index = 0;
				double dis_pipe_dp_sum = 0.0d;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
					
					for(Module target_module : modules){
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
								&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
							dis_pipe_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
						}
					}
					
					
					if(module.getTerminal_unit_exist().equals("O")){
						double dp_sum = dis_pipe_dp_sum + module.getDp() 
						+ module.getControlValve().getValve_dp() + module.getBalancingValve().getPre_valve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
						module.setDp_sum(dp_sum);
					}else{
						double dp_sum = dis_pipe_dp_sum + module.getDis_pipe().getH_available()
						+ module.getControlValve().getValve_dp() + module.getBalancingValve().getPre_valve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
						module.setDp_sum(dp_sum);
						//dp update sql
						ms.updateDp(module, db);
					}
					
				
					for(Module target_module : modules){
						if(target_module.getName().equals(top_module_name) && target_module.getDis_pipe().getH_available() < module.getDp_sum()){
							target_module.getDis_pipe().setH_available(module.getDp_sum());
							target_module.setDp(module.getDp_sum());
						}
					}
				
				}
				
			}
			
			for(Module module : modules){
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
				
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
					
					double dis_dp_sum = 0.0d;
					
					for(Module target_module : modules){
						
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
								&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
							dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
						}
						
						if(target_module.getName().equals(top_module_name)){
							module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
							module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
							if(target_module.getDis_pipe().getH_available() - module.getDp_sum() != 0){
								System.out.println("target: "+ target_module.getDis_pipe().getH_available());
								System.out.println("-------------------------------------------------");
								module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
							}else{
								module.getBalancingValve().setValve_dp(module.getBalancingValve().getPre_valve_dp());
							}
						}
						
					}
					
				}
				//h_available update sql
				ps.updateHA(module.getDis_pipe(), db);
				
				System.out.println("name: "+module.getName());
				//System.out.println(module.getDis_pipe().getTotal_dp() + module.getPre_accessory_dp() );
				//System.out.println(module.getDp());
				System.out.println("dpsum: "+module.getDp_sum());
				//System.out.println(module.getControlValve().getMin_authority());
				System.out.println("h_available: "+module.getDis_pipe().getH_available());
				System.out.println("valve_dp: "+module.getBalancingValve().getValve_dp());
				System.out.println("------------------------------------------------------");
				
				
				// Pump balancing valve 존재 여부에 따른 밸브 선정 아직 안했음.
				
			}
			
			if(p_head_exist){
				// 펌프 해드가 존재 할 경우의 level  == 2 부터 계산 다시 
				for(Module module : modules){
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() == 2){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								//pump
								module.setDp(target_module.getP_head() - dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getP_head() - dis_dp_sum);
								target_module.getDis_pipe().setH_available(target_module.getP_head());
								module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
								if(module.getBalancingValve().getValve_dp() != module.getBalancingValve().getPre_valve_dp()){
									module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
								}
								System.out.println("============================");
								System.out.println(module.getName());
								System.out.println(module.getDp());
								System.out.println("============================");
							}
							
						}
						
					}
					
					ms.updateDp(module, db);
					ps.updateHA(module.getDis_pipe(), db);
					
				}
				
				for(Module module : modules){
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() == 3){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								//pump
								module.setDp(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								target_module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available());
								module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
								if(module.getBalancingValve().getValve_dp() != module.getBalancingValve().getPre_valve_dp()){
									module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
								}
								System.out.println("============================");
								System.out.println(module.getName());
								System.out.println(module.getDp());
								System.out.println("============================");
							}
							
						}
						
					}
					ms.updateDp(module, db);
					ps.updateHA(module.getDis_pipe(), db);
					
				}
				
			}
			
			cvs.updateMinAuthority(modules, db);
			bvs.updateValveDp(modules, db);
			
		}else if(valve_type.equals("balancing")){
			// IMI, SY 제조사 별로 다름
			
			List<ControlValve> controlValves = new ArrayList<ControlValve>();
			
			for(Module module : modules){
				if(module.getSeq().equals(param_module.getSeq())){
					BalancingValve bv = new BalancingValve();
					
					BalancingValveList bvl = vls.getBalancingValveBySeq(db, valve_seq);
					
					double b_valve_dp = 0.0d;
					if(techParam.getManufacturer().equals("IMI")){
						b_valve_dp = this.calcBalancingValveDp(bvl, module.getCir_pipe().getFlow()); //추세식 계산
						b_valve_dp = Double.parseDouble(String.format("%.2f", b_valve_dp));
						//b_valve_dp = 3; // IMI 3Kpa 로 Default
					}else{
						b_valve_dp = 30.0; // SY 30Kpa 로 Default 
					}
					
					bv.setB_seq(bvl.getSeq());
					bv.setM_seq(module.getSeq());
					bv.setPre_valve_dp(b_valve_dp);
					
					bvs.updateBalancingValve(bv, db);
					System.out.println("valve_dp : " + b_valve_dp);
					module.setBalancingValve(bv);
					
				}
			}
			
			// 가상의 양정값 계산
			double imagine_h_a = this.calcKv(modules, techParam.getMin_authority(), db);
			
			// 컨트롤 밸브 선정
			for(Module module : modules){
				
				boolean c_v_flag = true;
				ControlValve cv = new ControlValve();
				
				if(module.getLevel() != 1 && (module.getCircuit_type().equals("Distribution circuit") || module.getCircuit_type().equals("2-way control circuit")) ){
					// pump 가 아닐경우
					// Distribution circuit, 2-way control circuit 일 경우만 밸브 존재
					
					double[] control_device = this.getValveListByDevice(module.getCir_pipe(), techParam, "control", db);
					List<ControlValveList> controlValveLists = vls.getControlValveList(db, control_device[1], control_device[2], techParam.getManufacturer());
					System.out.println(control_device[0] + "    " + control_device[1] + "    " + control_device[2]);
					
					for(ControlValveList cvl : controlValveLists){
						
						double c_valve_dp = 100 * (module.getCir_pipe().getDensity() / 1000) * Math.pow((module.getCir_pipe().getFlow() / cvl.getKv()), 2);
						c_valve_dp = Double.parseDouble(String.format("%.2f", c_valve_dp));
						
						if(techParam.getMin_authority() < c_valve_dp / imagine_h_a){
							cvl.setM_seq(module.getSeq());
							vts.createControlValveTemp(cvl, db);
							
							if(c_v_flag){
								if(cvl.getSize() == control_device[0]){
									cv.setC_seq(cvl.getSeq());
									cv.setM_seq(module.getSeq());
									cv.setKv(cvl.getKv());
									cv.setValve_dp(c_valve_dp);
									c_v_flag = false;
									controlValves.add(cv);
								}
							}
						}
					} // control valve list for 문
					
					
				}// module 이 pump 가 아닐 경우
				
				
				module.setControlValve(cv);
				
			} // 컨트롤 밸브 선정
			
			
			//같은 댑스 배관 중 이전 배관 부손실 값 찾기
			for(Module module : modules){ //원 모듈 리스트
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
				
					double pre_accessory_dp = 0.0; //이전 배관 부손실 dp 값 알기위한 변수
					
					
					for(Module target_module : modules){ //타겟 모듈 리스트
						
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".") ){
							if(module.getName().substring(index+1) != "1"){
								if(target_module.getName().equals(top_module_name + "." + (Integer.parseInt(module.getName().substring(index+1))-1))){
									pre_accessory_dp = target_module.getDis_pipe().getAccessory_dp();
								}
							}
						}
						
					} // target_modules
					module.setPre_accessory_dp(pre_accessory_dp);
					
				}
				
				
			} //modules
				
			for(Module module : modules){
				
				String top_module_name = "";
				int index = 0;
				double dis_pipe_dp_sum = 0.0d;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
					
					for(Module target_module : modules){
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
								&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
							dis_pipe_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
						}
					}
					
					
					if(module.getTerminal_unit_exist().equals("O")){
						double dp_sum = dis_pipe_dp_sum + module.getDp() 
						+ module.getControlValve().getValve_dp() + module.getBalancingValve().getPre_valve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
						module.setDp_sum(dp_sum);
					}else{
						double dp_sum = dis_pipe_dp_sum + module.getDis_pipe().getH_available()
						+ module.getControlValve().getValve_dp() + module.getBalancingValve().getPre_valve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
						module.setDp_sum(dp_sum);
						//dp update sql
						ms.updateDp(module, db);
					}
					
				
					for(Module target_module : modules){
						if(target_module.getName().equals(top_module_name) && target_module.getDis_pipe().getH_available() < module.getDp_sum()){
							target_module.getDis_pipe().setH_available(module.getDp_sum());
							target_module.setDp(module.getDp_sum());
						}
					}
				
				}
				
			}
			
			for(Module module : modules){
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
				
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
					
					double dis_dp_sum = 0.0d;
					
					for(Module target_module : modules){
						
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
								&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
							dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
						}
						
						if(target_module.getName().equals(top_module_name)){
							module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
							module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
							if(target_module.getDis_pipe().getH_available() - module.getDp_sum() != 0){
								System.out.println("target: "+ target_module.getDis_pipe().getH_available());
								System.out.println("-------------------------------------------------");
								module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
							}else{
								module.getBalancingValve().setValve_dp(module.getBalancingValve().getPre_valve_dp());
							}
						}
						
					}
					
				}
				//h_available update sql
				ps.updateHA(module.getDis_pipe(), db);
				
				System.out.println("name: "+module.getName());
				//System.out.println(module.getDis_pipe().getTotal_dp() + module.getPre_accessory_dp() );
				//System.out.println(module.getDp());
				System.out.println("dpsum: "+module.getDp_sum());
				//System.out.println(module.getControlValve().getMin_authority());
				System.out.println("h_available: "+module.getDis_pipe().getH_available());
				System.out.println("valve_dp: "+module.getBalancingValve().getValve_dp());
				System.out.println("------------------------------------------------------");
				
				
				// Pump balancing valve 존재 여부에 따른 밸브 선정 아직 안했음.
				
			}
			
			if(p_head_exist){
				// 펌프 해드가 존재 할 경우의 level  == 2 부터 계산 다시 
				for(Module module : modules){
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() == 2){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								//pump
								module.setDp(target_module.getP_head() - dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getP_head() - dis_dp_sum);
								target_module.getDis_pipe().setH_available(target_module.getP_head());
								module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
								if(module.getBalancingValve().getValve_dp() != module.getBalancingValve().getPre_valve_dp()){
									module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
								}
								System.out.println("============================");
								System.out.println(module.getName());
								System.out.println(module.getDp());
								System.out.println("============================");
							}
							
						}
						
					}
					
					ms.updateDp(module, db);
					ps.updateHA(module.getDis_pipe(), db);
					
				}
				
				for(Module module : modules){
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() == 3){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								//pump
								module.setDp(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								target_module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available());
								module.getControlValve().setMin_authority(module.getControlValve().getValve_dp()/target_module.getDis_pipe().getH_available());
								if(module.getBalancingValve().getValve_dp() != module.getBalancingValve().getPre_valve_dp()){
									module.getBalancingValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getBalancingValve().getPre_valve_dp()));
								}
								System.out.println("============================");
								System.out.println(module.getName());
								System.out.println(module.getDp());
								System.out.println("============================");
							}
							
						}
						
					}
					ms.updateDp(module, db);
					ps.updateHA(module.getDis_pipe(), db);
					
				}
				
			}
			
			cvs.createControlValve(controlValves, db);
			bvs.updateValveDp(modules, db);
			
		}else if(valve_type.equals("combined")){
			//복합 밸브 경우엔 제품 변경 이 후에 양정값이 변할 수 있으므로 계산 로직 다시 계산
			for(Module module : modules){
				if(module.getSeq().equals(param_module.getSeq())){
					CombinedCtrlBalValve cbv = new CombinedCtrlBalValve();
					
					CombinedCtrlBalValveList cbvl = vls.getCombinedValveBySeq(db, valve_seq);
					
					cbv.setCp_seq(cbvl.getSeq());
					cbv.setM_seq(module.getSeq());
					cbv.setValve_dp(cbvl.getDp());
					
					cbvs.updateCombinedValve(cbv, db);
					
					module.setCombinedValve(cbv);
					
				}
			}
			
			//같은 댑스 배관 중 이전 배관 부손실 값 찾기
			for(Module module : modules){ //원 모듈 리스트
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
				
					double pre_accessory_dp = 0.0; //이전 배관 부손실 dp 값 알기위한 변수
					
					
					for(Module target_module : modules){ //타겟 모듈 리스트
						
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")){
							if(module.getName().substring(index+1) != "1"){
								if(target_module.getName().equals(top_module_name + "." + (Integer.parseInt(module.getName().substring(index+1))-1))){
									pre_accessory_dp = target_module.getDis_pipe().getAccessory_dp();
								}
							}
						}
						
					} // target_modules
					module.setPre_accessory_dp(pre_accessory_dp);
					
				}
				
				
			} //modules	
			
			
			for(Module module : modules){
				
				String top_module_name = "";
				int index = 0;
				double dis_pipe_dp_sum = 0.0d;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
					
					for(Module target_module : modules){
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
								&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
							dis_pipe_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
						}
					}
					
					
					if(module.getTerminal_unit_exist().equals("O")){
						double dp_sum = dis_pipe_dp_sum + module.getDp() 
						+ module.getCombinedValve().getValve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
						module.setDp_sum(dp_sum);
					}else{
						double dp_sum = dis_pipe_dp_sum + module.getDis_pipe().getH_available()
						+ module.getCombinedValve().getValve_dp() + module.getCir_pipe().getTotal_dp() + module.getCir_pipe().getAccessory_dp();
						module.setDp_sum(dp_sum);
						//dp update sql
						ms.updateDp(module, db);
					}
					
				
					for(Module target_module : modules){
						if(target_module.getName().equals(top_module_name) && target_module.getDis_pipe().getH_available() < module.getDp_sum()){
							target_module.getDis_pipe().setH_available(module.getDp_sum());
							target_module.setDp(module.getDp_sum());
						}
					}
				
				}
				
			}
			
			
			for(Module module : modules){
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
				
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
					
					double dis_dp_sum = 0.0d;
					
					for(Module target_module : modules){
						
						if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
								&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
								&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
							dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
						}
						
						if(target_module.getName().equals(top_module_name)){
							System.out.println(module.getName());
							System.out.println(top_module_name);
							System.out.println(target_module.getDis_pipe().getH_available());
							System.out.println(dis_dp_sum);
							module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
							if(target_module.getDis_pipe().getH_available() - module.getDp_sum() != 0){
								System.out.println("target: "+ target_module.getDis_pipe().getH_available());
								System.out.println("-------------------------------------------------");
								//module.getCombinedValve().setValve_dp(target_module.getDis_pipe().getH_available() - (module.getDp_sum() - module.getCombinedValve().getValve_dp()));
							}
						}
						
					}
					
				}
				//h_available update sql
				ps.updateHA(module.getDis_pipe(), db);
				
				System.out.println("name: "+module.getName());
				//System.out.println(module.getDis_pipe().getTotal_dp() + module.getPre_accessory_dp() );
				//System.out.println(module.getDp());
				System.out.println("dpsum: "+module.getDp_sum());
				//System.out.println(module.getControlValve().getMin_authority());
				System.out.println("h_available: "+module.getDis_pipe().getH_available());
				//System.out.println("valve_dp: "+module.getBalancingValve().getValve_dp());
				System.out.println("------------------------------------------------------");
				
				
				// Pump balancing valve 존재 여부에 따른 밸브 선정 아직 안했음.
				
			}
			
			
			if(p_head_exist){
				// 펌프 해드가 존재 할 경우의 level  == 2 부터 계산 다시 
				for(Module module : modules){
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() == 2){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								//pump
								module.setDp(target_module.getP_head() - dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getP_head() - dis_dp_sum);
								target_module.getDis_pipe().setH_available(target_module.getP_head());
								System.out.println("============================");
								System.out.println(module.getName());
								System.out.println(module.getDp());
								System.out.println("============================");
							}
							
						}
						
					}
					ms.updateDp(module, db);
					ps.updateHA(module.getDis_pipe(), db);
					
				}
				
				for(Module module : modules){
					String top_module_name = "";
					int index = 0;
					if(module.getLevel() == 3){
						index = module.getName().lastIndexOf(".");
						top_module_name = module.getName().substring(0, index);
						
						double dis_dp_sum = 0.0d;
						
						for(Module target_module : modules){
							
							if(module.getLevel() == target_module.getLevel() && target_module.getName().indexOf(top_module_name) > -1 
									&& target_module.getName().substring(top_module_name.length(), top_module_name.length()+1).equals(".")
									&& Integer.parseInt(module.getName().substring(index+1)) >= Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) ){
								dis_dp_sum += target_module.getDis_pipe().getTotal_dp() + target_module.getPre_accessory_dp();
							}
							
							if(target_module.getName().equals(top_module_name)){
								//pump
								module.setDp(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								module.getDis_pipe().setH_available(target_module.getDis_pipe().getH_available() - dis_dp_sum);
								//target_module.getDis_pipe().setH_available(target_module.getP_head());
								System.out.println("============================");
								System.out.println(module.getName());
								System.out.println(module.getDp());
								System.out.println("============================");
							}
							
						}
						
					}
					ms.updateDp(module, db);
					ps.updateHA(module.getDis_pipe(), db);
					
				}
				
			}
			
		}
		
	}
	
	
	
	//밸런싱 밸브 추세식 
	public Double calcBalancingValveDp(BalancingValveList bvl, double flow){
		
		double dp = 0.0d;
		
		switch(bvl.getName()+bvl.getSize_name()){
			case "STADDN15":
				dp = (15.747 * Math.pow(flow, 2)) + (3 * Math.pow(10, -13) * flow) + Math.pow(10, -13);
				break;
			case "STADDN20":
				dp = (3.0779 * Math.pow(flow, 2)) + (4 * Math.pow(10, -13) * flow) - Math.pow(10, -12);
				break;
			case "STADDN25":
				dp = (1.3212 * Math.pow(flow, 2)) - (2 * Math.pow(10, -13) * flow) + 2 * Math.pow((10), -13);
				break;
			case "STADDN32":
				dp = (0.4959 * Math.pow(flow, 2)) - (2 * Math.pow(10, -14) * flow) + 2 * Math.pow((10), -14);
				break;
			case "STADDN40":
				dp = (0.2713 * Math.pow(flow, 2)) + (Math.pow(10, -13) * flow) - 4 * Math.pow((10), -13);
				break;
			case "STADDN50":
				dp = (0.0918 * Math.pow(flow, 2)) - (7 * Math.pow(10, -15) * flow) + 5 * Math.pow((10), -13);
				break;
			case "STAFDN65":
				dp = (0.0111 * Math.pow(flow, 2)) - (4 * Math.pow(10, -15) * flow) + 5 * Math.pow((10), -13);
				break;
			case "STAFDN80":
				dp = (0.0069 * Math.pow(flow, 2)) - (3 * Math.pow(10, -14) * flow) + 2 * Math.pow((10), -12);
				break;
			case "STAFDN100":
				dp = (0.0028 * Math.pow(flow, 2)) - (6 * Math.pow(10, -16) * flow) + Math.pow((10), -12);
				break;
			case "STAFDN125":
				dp = (0.0011 * Math.pow(flow, 2)) + (2 * Math.pow(10, -15) * flow) - Math.pow((10), -12);
				break;
			case "STAFDN150":
				dp = (0.0006 * Math.pow(flow, 2)) - (5 * Math.pow(10, -15) * flow) + 7 * Math.pow((10), -13);
				break;
			case "STAFDN200":
				dp = (0.0006 * Math.pow(flow, 2)) - (0.0678 * flow) + 2.0242;
				break;
			case "STAFDN250":
				dp = (7 * Math.pow(10, -5) * Math.pow(flow, 2)) + (8 * Math.pow(10, -16) * flow) + 2 * Math.pow((10), -13);
				break;
			case "STAFDN300":
				dp = (5 * Math.pow(10, -5) * Math.pow(flow, 2)) - (Math.pow(10, -15) * flow) + 5 * Math.pow((10), -13);
				break;
			case "STAF-SGDN20":
				dp = (3.0779 * Math.pow(flow, 2)) + (4 * Math.pow(10, -13) * flow) - Math.pow((10), -12);
				break;
			case "STAF-SGDN25":
				dp = (1.3211 * Math.pow(flow, 2)) - (0.0002 * flow) + 9 * Math.pow((10), -9);
				break;
			case "STAF-SGDN32":
				dp = (0.496 * Math.pow(flow, 2)) - (0.0005 * flow) + Math.pow((10), -7);
				break;
			case "STAF-SGDN40":
				dp = (0.2713 * Math.pow(flow, 2)) - (0.0003 * flow) + 7 * Math.pow((10), -8);
				break;
			case "STAF-SGDN50":
				dp = (0.0918 * Math.pow(flow, 2)) - (5 * Math.pow(10, -5) * flow) + 7 * Math.pow((10), -9);
				break;
			case "STAF-SGDN65":
				dp = (0.0138 * Math.pow(flow, 2)) + (1 * Math.pow(10, -5) * flow) + 3 * Math.pow((10), -9);
				break;
			case "STAF-SGDN80":
				dp = (0.0069 * Math.pow(flow, 2)) - (5 * Math.pow(10, -6) * flow) + Math.pow((10), -9);
				break;
			case "STAF-SGDN100":
				dp = (0.0028 * Math.pow(flow, 2)) + (Math.pow(10, -7) * flow) + 2 * Math.pow((10), -12);
				break;
			case "STAF-SGDN125":
				dp = (0.0011 * Math.pow(flow, 2)) - (Math.pow(10, -6) * flow) + 3 * Math.pow((10), -10);
				break;
			case "STAF-SGDN150":
				dp = (0.0006 * Math.pow(flow, 2)) + (2 * Math.pow(10, -7) * flow) + 2 * Math.pow((10), -11);
				break;
			case "STAF-SGDN200":
				dp = (0.0002 * Math.pow(flow, 2)) - (6 * Math.pow(10, -8) * flow) + 7 * Math.pow((10), -12);
				break;
			case "STAF-SGDN250":
				dp = (7 * Math.pow(10, -5) * Math.pow(flow, 2)) + (2 * Math.pow(10, -9) * flow) + 4 * Math.pow((10), -13);
				break;
			case "STAF-SGDN300":
				dp = (5 * Math.pow(10, -5) * Math.pow(flow, 2)) + (4 * Math.pow(10, -8) * flow) + Math.pow((10), -11);
				break;
			case "STAF-SGDN350":
				dp = (2 * Math.pow(10, -5) * Math.pow(flow, 2)) + (9 * Math.pow(10, -9) * flow) + Math.pow((10), -13);
				break;	
			case "STAF-SGDN400":
				dp = (Math.pow(10, -5) * Math.pow(flow, 2)) + (5 * Math.pow(10, -9) * flow) + 8 * Math.pow((10), -13);
				break;	
			case "TBVDN15LF":
				dp = (123.5 * Math.pow(flow, 2)) - (0.0345 * flow) + 2 * Math.pow((10), -6);
				break;	
			case "TBVDN15NF":
				dp = (30.874 * Math.pow(flow, 2)) - (0.0172 * flow) + 2 * Math.pow((10), -6);
				break;	
			case "TBVDN20":
				dp = (8.6527 * Math.pow(flow, 2)) - (0.0073 * flow) + Math.pow((2 * 10), -6);
				break;		
			default:
				dp = 0.0;
				break;
		}
		
		
		return dp;
	}
	
	
	public double[] getValveListByDevice(Pipe pipe, TechParam techParam, String valve_type, SQLite db){
		
		double[] device = new double[3]; // 0 : 기준 DN,  1 : below 에 해당하는 DN,  2 : above 에 해당하는 DN
		Map<Integer, Double> array_inner_diameter = new HashMap<Integer, Double>();
		ValveListService vls = new ValveListService();
		
		if(valve_type.equals("control")){
			
			List<ControlValveList> cvls = vls.getControlInnerDiameters(db, techParam.getManufacturer());
			double min_inner_diameter = 0.0d;
			double[] standard_inner_diameter = new double[2]; // 0 : map key (count) 1 : value (inner_diameter)
			int above = 0;
			int below = 0;
			
			
			for(int i = 0; i < cvls.size(); i++){
				array_inner_diameter.put(i, cvls.get(i).getSize());
				if(min_inner_diameter == 0 || min_inner_diameter > Math.abs(pipe.getInner_diameter() - cvls.get(i).getSize())){
					min_inner_diameter = Math.abs(pipe.getInner_diameter() - cvls.get(i).getSize());
					standard_inner_diameter[0] = i;
					standard_inner_diameter[1] = cvls.get(i).getSize();
				}
			}
			
			if(standard_inner_diameter[0] - techParam.getDevice_below() < 0){
				below = 0;
			}else{
				below = (int)standard_inner_diameter[0] - techParam.getDevice_below();
			}
			
			if(standard_inner_diameter[0] + techParam.getDevice_above() > cvls.size() - 1){
				above = cvls.size() - 1;
			}else{
				above = (int)standard_inner_diameter[0] + techParam.getDevice_above();
			}
			
			device[0] = standard_inner_diameter[1];
			device[1] = array_inner_diameter.get(below);
			device[2] = array_inner_diameter.get(above);
			
		}else if(valve_type.equals("balancing")){
			
			List<BalancingValveList> bvls = vls.getBalancingInnerDiameters(db, techParam.getManufacturer());
			double min_inner_diameter = 0.0d;
			double[] standard_inner_diameter = new double[2]; // 0 : map key (count) 1 : value (inner_diameter)
			int above = 0;
			int below = 0;
			
			for(int i = 0; i < bvls.size(); i++){
				array_inner_diameter.put(i, bvls.get(i).getSize());
				if(min_inner_diameter == 0 || min_inner_diameter > Math.abs(pipe.getInner_diameter() - bvls.get(i).getSize())){
					min_inner_diameter = Math.abs(pipe.getInner_diameter() - bvls.get(i).getSize());
					standard_inner_diameter[0] = i;
					standard_inner_diameter[1] = bvls.get(i).getSize();
				}
			}
			
			if(standard_inner_diameter[0] - techParam.getDevice_below() < 0){
				below = 0;
			}else{
				below = (int)standard_inner_diameter[0] - techParam.getDevice_below();
			}
			
			if(standard_inner_diameter[0] + techParam.getDevice_above() > bvls.size() - 1){
				above = bvls.size() - 1;
			}else{
				above = (int)standard_inner_diameter[0] + techParam.getDevice_above();
			}
			
			device[0] = standard_inner_diameter[1];
			device[1] = array_inner_diameter.get(below);
			device[2] = array_inner_diameter.get(above);
			
		}else if(valve_type.equals("combined")){
			
			List<CombinedCtrlBalValveList> cbvls = vls.getCombinedInnerDiameters(db, techParam.getManufacturer());
			double min_inner_diameter = 0.0d;
			double[] standard_inner_diameter = new double[2]; // 0 : map key (count) 1 : value (inner_diameter)
			int above = 0;
			int below = 0;
			
			for(int i = 0; i < cbvls.size(); i++){
				array_inner_diameter.put(i, cbvls.get(i).getSize());
				if(min_inner_diameter == 0 || min_inner_diameter > Math.abs(pipe.getInner_diameter() - cbvls.get(i).getSize())){
					min_inner_diameter = Math.abs(pipe.getInner_diameter() - cbvls.get(i).getSize());
					standard_inner_diameter[0] = i;
					standard_inner_diameter[1] = cbvls.get(i).getSize();
				}
			}
			
			if(standard_inner_diameter[0] - techParam.getDevice_below() < 0){
				below = 0;
			}else{
				below = (int)standard_inner_diameter[0] - techParam.getDevice_below();
			}
			
			if(standard_inner_diameter[0] + techParam.getDevice_above() > cbvls.size() - 1){
				above = cbvls.size() - 1;
			}else{
				above = (int)standard_inner_diameter[0] + techParam.getDevice_above();
			}
			
			device[0] = standard_inner_diameter[1];
			device[1] = array_inner_diameter.get(below);
			device[2] = array_inner_diameter.get(above);
			
		}
		
		
		
		
		return device;
	}
	
	
	/**
	 * 가상의 양정값 계산하여 Kv 값 범위 찾기
	 * @param modules
	 * @return
	 */
	public double calcKv(List<Module> modules, double tech_min_authrity, SQLite db){
		
		double result_h_a = 0.0d;
		
		// 1. Terminal Unit 존재 여부를 찾는다.  - 맨 마지막 배관의 경우는 터미널 유닛을 가지고 있으므로 
		// 2. 터미널 유닛 있는 배관에 부모 배관을 반복적으로 찾는다.
		// 3. DP 를 지속적으로 더해준다.
		// 4. 가장 큰 DP 값을 가진 터미널 유닛의 이름을 기억한다.
		// 5. 4번에 걸린 터미널 유닛의 이름을 위로 올리면서 밸브의 개수를 파악
		
		for(Module module : modules){
			if(module.getTerminal_unit_exist().equals("O")){
				
				double dp = 0.0d; // pipe dp
				double a_dp = 0.0d; // accessory dp 
				double dp_sum = 0.0d; // 전체 파이프 합산
				double bv_dp = 0.0d;
				int cv_count = 0;
				String module_name = "";
				int index = 0;
				
				for(int i = module.getLevel(); i >= 2; i--){
					
					if(i == module.getLevel()){
						module_name = module.getName();
					}else{
						if(i == 2){
							module_name = module.getName().substring(0, 3);
						}else{
							index = module.getName().lastIndexOf(".") + (i - (module.getLevel()-1)) * 2;
							module_name = module.getName().substring(0, index);
						}
					}
					
					for(Module target_module : modules){
						if(target_module.getName().equals(module_name)){
							dp = target_module.getDis_pipe().getTotal_dp() + target_module.getCir_pipe().getTotal_dp() + module.getDp();
							if(!target_module.getCircuit_type().equals("Straight")){
								cv_count += 1;
								bv_dp += target_module.getBalancingValve().getPre_valve_dp();
							}
						}
						
						if( target_module.getLevel() > 1 
							&&target_module.getName().substring(0, target_module.getName().lastIndexOf(".")).equals(module_name.substring(0, module_name.lastIndexOf(".")))
							&&	Integer.parseInt(target_module.getName().substring(target_module.getName().lastIndexOf(".")+1)) <= Integer.parseInt(module_name.substring(module_name.lastIndexOf(".")+1)) ){
							a_dp += target_module.getDis_pipe().getAccessory_dp() + target_module.getCir_pipe().getAccessory_dp();
						}
					}
					dp_sum += dp + a_dp + bv_dp;
				} // level for 
				
				if(result_h_a < dp_sum / (1 - (cv_count * tech_min_authrity))){
					result_h_a = dp_sum / (1 - (cv_count * tech_min_authrity));
				}
				System.out.println(dp_sum);
				System.out.println(cv_count);
				System.out.println(result_h_a);
			}
		}
		
		System.out.println("result : "+result_h_a);
		
		return result_h_a;
	}
	
	
}

