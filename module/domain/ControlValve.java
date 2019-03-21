package module.domain;

public class ControlValve {
	// 선정 된 컨트롤 밸브 
	
	private String m_seq;
	private String c_seq; // control_valve_list seq 값
	private double kv = 0.0d; // kv 값
	private double valve_dp = 0.0d; // 계산 된 밸브 dp
	private double min_authority = 0.0d; // 계산 된 min_authority
	
	
	
	public String getM_seq() {
		return m_seq;
	}
	public void setM_seq(String m_seq) {
		this.m_seq = m_seq;
	}
	public String getC_seq() {
		return c_seq;
	}
	public void setC_seq(String c_seq) {
		this.c_seq = c_seq;
	}
	public double getKv() {
		return kv;
	}
	public void setKv(double kv) {
		this.kv = kv;
	}
	public double getValve_dp() {
		return valve_dp;
	}
	public void setValve_dp(double valve_dp) {
		this.valve_dp = valve_dp;
	}
	public double getMin_authority() {
		return min_authority;
	}
	public void setMin_authority(double min_authority) {
		this.min_authority = min_authority;
	}
	
	
	

}
