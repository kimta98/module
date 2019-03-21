package module.core;

import java.util.ArrayList;
import java.util.List;

import module.db.SQLite;
import module.domain.Accessory;
import module.domain.Module;
import module.domain.Pipe;
import module.domain.TechParam;
import module.sql.service.AccessoryService;
import module.sql.service.PipeService;

//부손실 T, 90Elbow 
public class AccessoryCore {
	
	/**
	 * 부손실 계산
	 * @param modules
	 * @param pipes
	 * @param db
	 */
	public void calcLoss(List<Module> modules, List<Pipe> pipes, TechParam techParam, SQLite db){
		
		double density = ( 1 - ( ( techParam.getTemp() / 94266d ) * Math.pow(techParam.getTemp(), 0.8d) ) ) * 1000; //밀도
		
		List<Accessory> accessorys = new ArrayList<Accessory>();
		PipeService ps = new PipeService();
		
		for(Module module : modules){
			System.out.println("module: "+ module.getName() );
			double self_d_velocity = 0.0d; //자신의 분배 유속
			double self_c_velocity = 0.0d; //자신의 circuit 유속
			double self_c_inner_diameter = 0.0d; // 자신의 circuit 내경
			double next_d_velocity = 0.0d; // 다음번째 분배 유속
			double next_d_inner_diameter = 0.0d; // 다음번째 분배 내경
			double sub_d_velocity = 0.0d; //댑스 안에 있는 첫번째 배관 유속
			double sub_d_inner_diameter = 0.0d; // 댑스 안에 있는 첫번째 배관 분배 내경
			boolean terminal_unit_exist = false; // 터미널 유닛 존재 여부
			String next_m_name = ""; //다음번째 모듈 이름
			String sub_m_name = ""; //자기 자신 댑스 안에 있는 첫번째 배관 이름
			
			
			//결과 데이터
			double tee_d_dp = 0.0d;
			double tee_c_dp = 0.0d;
			double elbow_dp = 0.0d;
			
			if(module.getLevel() == 1){
				//펌프 계산 식 없음.
				for(int i = 0; i < 2; i++){
					if(i == 0){
						Accessory accessory = new Accessory();
						accessory.setP_seq(module.getP_seq());
						accessory.setM_seq(module.getSeq());
						accessory.setM_name(module.getName());
						accessory.setType("distribution");
						accessory.setDescription("none");
						accessory.setDp(0.0d);
						accessorys.add(accessory);
					}else{
						Accessory accessory = new Accessory();
						accessory.setP_seq(module.getP_seq());
						accessory.setM_seq(module.getSeq());
						accessory.setM_name(module.getName());
						accessory.setType("circuit");
						accessory.setDescription("none");
						accessory.setDp(0.0d);
						accessorys.add(accessory);
					}
				}
			}else{
				//펌프가 아닌 경우
				
				// pipe 유속, 내경 값 셋팅
						
				// 터미널 유닛 존재 여부
				if(module.getTerminal_unit_exist().equals("O")){
					terminal_unit_exist = true;
				}
				
				String top_module_name = "";
				int index = 0;
				if(module.getLevel() != 1){
					index = module.getName().lastIndexOf(".");
					top_module_name = module.getName().substring(0, index);
				}
				
				next_m_name = top_module_name + "."+ (Integer.parseInt(module.getName().substring(index+1)) + 1);
				sub_m_name = module.getName() + ".1";
				
				
				Pipe self_d_pipe = ps.getPipeByName(module.getName(), "distribution", db);
				Pipe self_c_pipe = ps.getPipeByName(module.getName(), "circuit", db);
				Pipe next_d_pipe = ps.getPipeByName(next_m_name, "distribution", db);
				Pipe sub_m_pipe = ps.getPipeByName(sub_m_name, "distribution", db);
				
				// 계산에 필요한 유속 값
				self_d_velocity = self_d_pipe.getStream_velocity();
				self_c_velocity = self_c_pipe.getStream_velocity();
				self_c_inner_diameter = self_c_pipe.getInner_diameter();
				next_d_velocity = next_d_pipe.getStream_velocity();
				next_d_inner_diameter = next_d_pipe.getInner_diameter();
				sub_d_velocity = sub_m_pipe.getStream_velocity();
				sub_d_inner_diameter = sub_m_pipe.getInner_diameter();
				
				
				for(int i = 0; i < 2; i++){ // 1 : distribution  2 : circuit
					
					//실제 계산
					if(terminal_unit_exist){
						//터미널 유닛 존재
						/*tee_d_dp = density * (Math.pow(self_d_velocity, 2) + 0.2 * Math.pow(next_d_velocity, 2) + 0.8 * Math.pow(self_c_velocity, 2)) 
								* ( 6.13881 - 2.09785 * (next_d_inner_diameter/self_c_inner_diameter) ) * Math.pow(10, -4) ;*/
						if(next_d_velocity != 0){
							//다음 터미널유닛배관? 존재
							if(i == 0){
								//distribution
								tee_d_dp = density * (Math.pow(self_d_velocity, 2) + 0.2 * Math.pow(next_d_velocity, 2) + 0.8 * Math.pow(self_c_velocity, 2)) 
										* ( 6.13881 - 2.09785 * (next_d_inner_diameter/self_c_inner_diameter) ) * Math.pow(10, -4) ;
								tee_d_dp = this.dp_check(tee_d_dp);
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("distribution");
								accessory.setDescription("2tee");
								accessory.setDp(tee_d_dp);
								accessorys.add(accessory);
							}else{
								//circuit
								tee_c_dp = density * (Math.pow(self_d_velocity, 2) + 0.5 * Math.pow(next_d_velocity, 2) + 0.5 * Math.pow(self_c_velocity, 2)) * 0.00154;
								tee_c_dp = this.dp_check(tee_c_dp);
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("circuit");
								accessory.setDescription("2tee");
								accessory.setDp(tee_c_dp);
								accessorys.add(accessory);
							}
						}else{
							//없음 마지막유닛
							if(i == 0){
								//distribution
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("distribution");
								accessory.setDescription("none");
								accessory.setDp(0.0d);
								accessorys.add(accessory);
							}else{
								//circuit
								elbow_dp = (density * Math.pow(self_c_velocity, 2)) / 1000;
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("circuit");
								accessory.setDescription("90elbow");
								accessory.setDp(elbow_dp);
								accessorys.add(accessory);
								
							}
						}
						
					}else{
						if(next_d_velocity != 0){
							//다음
							if(i == 0){
								//distribution
								tee_d_dp = density * (Math.pow(self_d_velocity, 2) + 0.2 * Math.pow(next_d_velocity, 2) + 0.8 * Math.pow(sub_d_velocity, 2)) 
										* ( 6.13881 - 2.09785 * (next_d_inner_diameter/sub_d_inner_diameter) ) * Math.pow(10, -4) ;
								tee_d_dp = this.dp_check(tee_d_dp);
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("distribution");
								accessory.setDescription("2tee");
								accessory.setDp(tee_d_dp);
								accessorys.add(accessory);
							}else{
								//circuit
								tee_c_dp = density * (Math.pow(self_d_velocity, 2) + 0.5 * Math.pow(next_d_velocity, 2) + 0.5 * Math.pow(self_c_velocity, 2)) * 0.00154;
								tee_c_dp = this.dp_check(tee_c_dp);
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("circuit");
								accessory.setDescription("2tee");
								if(module.getC_length() == 0){
									accessory.setDp(0.0d);
								}else{
									accessory.setDp(tee_c_dp);
								}
								accessorys.add(accessory);
							}
						}else{
							//없음 마지막유닛
							if(i == 0){
								//distribution
								Accessory accessory = new Accessory();
								accessory.setP_seq(module.getP_seq());
								accessory.setM_seq(module.getSeq());
								accessory.setM_name(module.getName());
								accessory.setType("distribution");
								accessory.setDescription("none");
								accessory.setDp(0.0d);
								accessorys.add(accessory);
							}else{
								//circuit
								if(Integer.parseInt(module.getName().substring(module.getName().lastIndexOf(".")+1)) == 1){
									
									Accessory accessory = new Accessory();
									accessory.setP_seq(module.getP_seq());
									accessory.setM_seq(module.getSeq());
									accessory.setM_name(module.getName());
									accessory.setType("circuit");
									accessory.setDescription("none");
									if(module.getC_length() == 0){
										accessory.setDp(0.0d);
									}else{
										accessory.setDp(tee_c_dp);
									}
									accessorys.add(accessory);
									
								}else{
									
									elbow_dp = (density * Math.pow(self_c_velocity, 2)) / 1000;
									Accessory accessory = new Accessory();
									accessory.setP_seq(module.getP_seq());
									accessory.setM_seq(module.getSeq());
									accessory.setM_name(module.getName());
									accessory.setType("circuit");
									accessory.setDescription("90elbow");
									if(module.getC_length() == 0){
										accessory.setDp(0.0d);
									}else{
										accessory.setDp(elbow_dp);
									}
									accessorys.add(accessory);
									
								}
								
							}
							
						}
					}
				}
			} 
			
		} // modules
		AccessoryService as = new AccessoryService();
		as.createTable(db);
		as.createAccessorys(accessorys, db);
		
	} // calcLoss
	
	
	/**
	 * dp 가 0보다 작을 경우 0으로
	 * @param tee_dp
	 * @return
	 */
	public double dp_check(double tee_dp){
		
		if(tee_dp < 0){
			tee_dp = 0;
		}
		
		return tee_dp;
	}

}
