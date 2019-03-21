package module.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import module.domain.Module;

public class FileUtil {
	
	public List<Module> File() {
		
		List<Module> modules = new ArrayList<Module>();
		Module module;
		try {
			
			//File csv = new File("C:\\Users\\ktw\\Desktop\\" + "pipec_D.csv");
			File csv = new File("C:\\Users\\Woong\\Desktop\\" + "pipe_B.csv");
			//File csv = new File("C:\\Users\\ktw\\Desktop\\" + "pipe_spec_b_withbv.csv");
			//File csv = new File("C:\\Users\\ktw\\Desktop\\" + "pipe_spec_c_withbv.csv");
			//BufferedReader br = new BufferedReader(new FileReader(csv));
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));
			String line = "";
			int row = 0;
			int count; // 점 개수
			
			while ((line = br.readLine()) != null){
				if(row > 2){ // 제목 단 빼기 위한
					count = 0;
					String[] token = line.split(",", -1);
					module = new Module();
					if(token[0].equals("")){
						break;
					}
					
					for(int i = 0; i < token.length; i++){
						if(token[i].equals("") && i != 1 && i != 3 && i != 9 && i != 14){ // 컬럼 인덱스  문자열 경우에 0으로 변경할 이유없으므로... 나중에 변경
							token[i] = "0";
						}
						
						if(token[i] == null){
							token[i] = "";
						}
						
					}
					
					for(int i = 0; i < token[0].length(); i++){
						if(token[0].charAt(i) == '.'){
							count++;
						}
					}
					
					module.setName(token[0]);
					module.setDescription(token[1]);
					module.setBalancing_type(token[2]);
					module.setTerminal_unit_exist(token[3]);
					module.setCircuit_type(token[4]);
					module.setLevel(count+1);
					// type 에 따른 컬럼 위치 지정...좋은 방법 찾아야함
					if(token[4].equals("Pump")){
						module.setP_head(Double.parseDouble(token[5]));
					}else if(token[4].equals("Straight")){
						module.setD_length(Double.parseDouble(token[6]));
					}else if(token[4].equals("Distribution circuit")){
						module.setD_length(Double.parseDouble(token[7]));
						module.setC_length(Double.parseDouble(token[8]));
						module.setT_description(token[9]);
						module.setC_flow(Double.parseDouble(token[10]));
						module.setDp(Double.parseDouble(token[11]));
					}else if(token[4].equals("2-way control circuit")){
						module.setD_length(Double.parseDouble(token[12]));
						module.setC_length(Double.parseDouble(token[13]));
						module.setT_description(token[14]);
						module.setC_flow(Double.parseDouble(token[15]));
						module.setDp(Double.parseDouble(token[16]));
					}
					modules.add(module);
				}
				row++;
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return modules;
	}
	
	
}
