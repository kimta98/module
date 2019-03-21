package module.domain;

public class TechParam {
	// 사용자 설정 테크니컬 파라메터

	//private String p_seq;
	//private double pump_capacity = 0.0d; // 펌프 용량
	private String pipe_series; // 파이프 시리즈 
	private String manufacturer; // 제조사
	private double pressure_drop_min = 0.0d; // 압력강하 최소 값
	private double pressure_drop_max = 0.0d; // 압력강하 최대 값
	private double velocity_min = 0.0d; // 유속 최소 값
	private double velocity_max = 0.0d; // 유속 최대 값
	private double pipe_safe_rate = 0.0d; // 배관안전율
	private double control_valve_min = 0.0d; // 컨트롤 밸브 최소
	private double control_valve_max = 0.0d; // 컨트롤 밸브 최대
	private double balancing_valve_min = 0.0d; // 밸런싱 밸브 최소
	private double balancing_valve_max = 0.0d; // 밸런싱 밸브 최대
	private double temp = 0.0d; // 온도
	private double min_authority = 0.0d; //
	private double t_value_branch = 0.0d; // Tee 관 브런치 값
	private double t_value_inline = 0.0d; // tee 관 인라인 값
	private int device_above = 0;
	private int device_below = 0;
	
	
	
	/*public String getP_seq() {
		return p_seq;
	}
	public void setP_seq(String p_seq) {
		this.p_seq = p_seq;
	}*/
	/*public double getPump_capacity() {
		return pump_capacity;
	}
	public void setPump_capacity(double pump_capacity) {
		this.pump_capacity = pump_capacity;
	}*/
	public String getPipe_series() {
		return pipe_series;
	}
	public void setPipe_series(String pipe_series) {
		this.pipe_series = pipe_series;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public double getPressure_drop_min() {
		return pressure_drop_min;
	}
	public void setPressure_drop_min(double pressure_drop_min) {
		this.pressure_drop_min = pressure_drop_min;
	}
	public double getPressure_drop_max() {
		return pressure_drop_max;
	}
	public void setPressure_drop_max(double pressure_drop_max) {
		this.pressure_drop_max = pressure_drop_max;
	}
	public double getVelocity_min() {
		return velocity_min;
	}
	public void setVelocity_min(double velocity_min) {
		this.velocity_min = velocity_min;
	}
	public double getVelocity_max() {
		return velocity_max;
	}
	public void setVelocity_max(double velocity_max) {
		this.velocity_max = velocity_max;
	}
	public double getPipe_safe_rate() {
		return pipe_safe_rate;
	}
	public void setPipe_safe_rate(double pipe_safe_rate) {
		this.pipe_safe_rate = pipe_safe_rate;
	}
	public double getControl_valve_min() {
		return control_valve_min;
	}
	public void setControl_valve_min(double control_valve_min) {
		this.control_valve_min = control_valve_min;
	}
	public double getControl_valve_max() {
		return control_valve_max;
	}
	public void setControl_valve_max(double control_valve_max) {
		this.control_valve_max = control_valve_max;
	}
	public double getBalancing_valve_min() {
		return balancing_valve_min;
	}
	public void setBalancing_valve_min(double balancing_valve_min) {
		this.balancing_valve_min = balancing_valve_min;
	}
	public double getBalancing_valve_max() {
		return balancing_valve_max;
	}
	public void setBalancing_valve_max(double balancing_valve_max) {
		this.balancing_valve_max = balancing_valve_max;
	}
	public double getTemp() {
		return temp;
	}
	public void setTemp(double temp) {
		this.temp = temp;
	}
	public double getMin_authority() {
		return min_authority;
	}
	public void setMin_authority(double min_authority) {
		this.min_authority = min_authority;
	}
	public double getT_value_branch() {
		return t_value_branch;
	}
	public void setT_value_branch(double t_value_branch) {
		this.t_value_branch = t_value_branch;
	}
	public double getT_value_inline() {
		return t_value_inline;
	}
	public void setT_value_inline(double t_value_inline) {
		this.t_value_inline = t_value_inline;
	}
	public int getDevice_above() {
		return device_above;
	}
	public void setDevice_above(int device_above) {
		this.device_above = device_above;
	}
	public int getDevice_below() {
		return device_below;
	}
	public void setDevice_below(int device_below) {
		this.device_below = device_below;
	}
	
	
	
	
}
