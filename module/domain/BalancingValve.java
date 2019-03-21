package module.domain;

public class BalancingValve {

	private String m_seq;
	private String b_seq;
	private double pre_valve_dp = 0.0d; // 추세식으로 DP
	private double valve_dp = 0.0d; // 계산 된 DP
	
	
	
	public String getM_seq() {
		return m_seq;
	}
	public void setM_seq(String m_seq) {
		this.m_seq = m_seq;
	}
	public String getB_seq() {
		return b_seq;
	}
	public void setB_seq(String b_seq) {
		this.b_seq = b_seq;
	}
	public double getPre_valve_dp() {
		return pre_valve_dp;
	}
	public void setPre_valve_dp(double pre_valve_dp) {
		this.pre_valve_dp = pre_valve_dp;
	}
	public double getValve_dp() {
		return valve_dp;
	}
	public void setValve_dp(double valve_dp) {
		this.valve_dp = valve_dp;
	}
	
	
	
	
}
