package module.domain;

public class Pipe {
	//선정 된 파이프
	
	
	private String p_seq;
	private String m_seq;
	private String m_name;
	private String type;
	private String pipe_seq;
	private String pipe_name; // 배관 제품명
	private double inner_diameter = 0.0d; //내경
	private double roughness = 0.0d; // 거칠기
	private double flow = 0.0d; //유량
	private double stream_velocity = 0.0d; // 유속
	private double viscosity = 0.0d; // 점성
	private double reynolds = 0.0d; // 레이놀즈수
	private double darcy = 0.0d; // darcy 계수
	private double density = 0.0d; // 밀도
	private double dp = 0.0d; // 길이당 dp
	private double total_dp = 0.0d; // 총 dp
	
	
	// 부손실 값
	private String description;
	private double accessory_dp = 0.0d;
	
	//h_available
	private double h_available = 0.0d;
	
	
	public String getP_seq() {
		return p_seq;
	}
	public void setP_seq(String p_seq) {
		this.p_seq = p_seq;
	}
	public String getM_name() {
		return m_name;
	}
	public void setM_name(String m_name) {
		this.m_name = m_name;
	}
	public String getM_seq() {
		return m_seq;
	}
	public void setM_seq(String m_seq) {
		this.m_seq = m_seq;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPipe_seq() {
		return pipe_seq;
	}
	public void setPipe_seq(String pipe_seq) {
		this.pipe_seq = pipe_seq;
	}
	public String getPipe_name() {
		return pipe_name;
	}
	public void setPipe_name(String pipe_name) {
		this.pipe_name = pipe_name;
	}
	public double getInner_diameter() {
		return inner_diameter;
	}
	public void setInner_diameter(double inner_diameter) {
		this.inner_diameter = inner_diameter;
	}
	public double getRoughness() {
		return roughness;
	}
	public void setRoughness(double roughness) {
		this.roughness = roughness;
	}
	public double getFlow() {
		return flow;
	}
	public void setFlow(double flow) {
		this.flow = flow;
	}
	public double getStream_velocity() {
		return stream_velocity;
	}
	public void setStream_velocity(double stream_velocity) {
		this.stream_velocity = stream_velocity;
	}
	public double getViscosity() {
		return viscosity;
	}
	public void setViscosity(double viscosity) {
		this.viscosity = viscosity;
	}
	public double getReynolds() {
		return reynolds;
	}
	public void setReynolds(double reynolds) {
		this.reynolds = reynolds;
	}
	public double getDarcy() {
		return darcy;
	}
	public void setDarcy(double darcy) {
		this.darcy = darcy;
	}
	public double getDensity() {
		return density;
	}
	public void setDensity(double density) {
		this.density = density;
	}
	public double getDp() {
		return dp;
	}
	public void setDp(double dp) {
		this.dp = dp;
	}
	public double getTotal_dp() {
		return total_dp;
	}
	public void setTotal_dp(double total_dp) {
		this.total_dp = total_dp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAccessory_dp() {
		return accessory_dp;
	}
	public void setAccessory_dp(double accessory_dp) {
		this.accessory_dp = accessory_dp;
	}
	public double getH_available() {
		return h_available;
	}
	public void setH_available(double h_available) {
		this.h_available = h_available;
	}

	
	
	
}
