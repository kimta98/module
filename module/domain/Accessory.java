package module.domain;

public class Accessory {
    //부손실

	private String p_seq;
	private String m_seq; // 모듈 seq
	private String m_name; // 모듈명
	private String type; // circuit , distribution 
	private String description; //부손실 설명 - tee, 90elbow
	private double dp = 0.0d; // 부손실 dp
	
	
	public String getP_seq() {
		return p_seq;
	}
	public void setP_seq(String p_seq) {
		this.p_seq = p_seq;
	}
	public String getM_seq() {
		return m_seq;
	}
	public void setM_seq(String m_seq) {
		this.m_seq = m_seq;
	}
	public String getM_name() {
		return m_name;
	}
	public void setM_name(String m_name) {
		this.m_name = m_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getDp() {
		return dp;
	}
	public void setDp(double dp) {
		this.dp = dp;
	}
	
	
	
	
}
