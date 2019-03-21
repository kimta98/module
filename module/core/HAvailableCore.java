package module.core;

import java.util.List;

import module.db.SQLite;
import module.domain.CombinedCtrlBalValve;
import module.domain.Module;
import module.domain.Pipe;

public class HAvailableCore {
	
	/**
	 * H_available 계산
	 * @param modules
	 * @param pipes
	 * @param db
	 */
	public void calcHAvailable(List<Module> modules, List<Pipe> pipes, List<CombinedCtrlBalValve> compositeValve, SQLite db){
		System.out.println("h_available 처리 부분");
		/*
		 * **** 구한 h available 과 unit dp 등을 기억 할 도메인 하나 생성하자....
		 * 1. 모듈 for 문 
		 *   1-1.파이브 for 문
		 *     1-1-1. 복합밸브랑 일치 시 값 ( terminal unit 일 경우 ) 
		 *        dp 합산  =  unit dp + 복합밸브 dp + circuit pipe dp + 부손실 dp 값 + distribution dp
		 *        dp 합산이 가장 큰 배관이 dp min 으로 선정
		 *     1-1-2. circuit H available 값 구하기. ( terminal unit 일 경우의 값만)
		 *     1-1-3. terminal unit 이 아닌 배관인 경우 하위 배관의 1번째 H_available + distribution dp 더한 값을 circuit unit dp 로 넣는다. 
		 *           - 그 unit dp 값을 이용하여 1-1-1 마찬가지로 dp min 을 구하고 circuit h available 을 구한다. (반복)
		 *           
		 *     펌프는 펌프 바로 위 하위 1번째 배관의  H_available + distribution dp 값을 펌프 유량에서 뺀 값이 밸런싱 밸브 dp 값이 된다. -- 밸브 선정은 미정~     
		 *       
		 */
		
		
	}

}
