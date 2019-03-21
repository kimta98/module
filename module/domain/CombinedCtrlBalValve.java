package module.domain;

public class CombinedCtrlBalValve {
	//선정 된 복합 밸브
	
	private String m_seq; // 모듈 seq
	private String cp_seq; // 복합밸브 이름
	private double valve_dp = 0.0d; // 복합 밸브 dp
	
	
	public String getM_seq() {
		return m_seq;
	}
	public void setM_seq(String m_seq) {
		this.m_seq = m_seq;
	}
	public String getCp_seq() {
		return cp_seq;
	}
	public void setCp_seq(String cp_seq) {
		this.cp_seq = cp_seq;
	}
	public double getValve_dp() {
		return valve_dp;
	}
	public void setValve_dp(double valve_dp) {
		this.valve_dp = valve_dp;
	}
	
	
	
}
