package module.domain;

public class Module{

	private String seq;
	private String p_seq;
	private String name; // 모듈 이름
	private String p_bv_exist; // 밸런싱 밸브
	private String description; // 모듈 설명
	private String balancing_type; //밸런싱 타입
	private String terminal_unit_exist; // 터미널 유닛 존재 여부
	private String circuit_type; // circuit type
	private int level; // 모듈 이름에 대한 레벨 댑스
	private double p_head = 0.0d; // 펌프 head
	private double p_flow = 0.0d; // 펌프 유량
	private double d_length = 0.0d; // distribution pipe length
	private double c_length = 0.0d; // circuit pipe length
	private double d_flow = 0.0d; // distribution pipe flow
	private double c_flow = 0.0d; // circuit pipe flow and terminal unit flow
	private String t_description; //터미널 유닛 설명
	private double dp = 0.0d; //터미널 유닛 dp
	private String created;
	private String enabled;
	
	
	//해당 값들
	private Pipe cir_pipe;
	private Pipe dis_pipe;
	private ControlValve controlValve;
	private BalancingValve balancingValve;
	private CombinedCtrlBalValve combinedValve;
	
	//dp min 표시??
	private double pre_accessory_dp = 0.0d;
	private double dp_sum = 0.0d;
	
	//level count
	private int count = 0;
	
	
	
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getP_seq() {
		return p_seq;
	}
	public void setP_seq(String p_seq) {
		this.p_seq = p_seq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getP_bv_exist() {
		return p_bv_exist;
	}
	public void setP_bv_exist(String p_bv_exist) {
		this.p_bv_exist = p_bv_exist;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBalancing_type() {
		return balancing_type;
	}
	public void setBalancing_type(String balancing_type) {
		this.balancing_type = balancing_type;
	}
	public String getTerminal_unit_exist() {
		return terminal_unit_exist;
	}
	public void setTerminal_unit_exist(String terminal_unit_exist) {
		this.terminal_unit_exist = terminal_unit_exist;
	}
	public String getCircuit_type() {
		return circuit_type;
	}
	public void setCircuit_type(String circuit_type) {
		this.circuit_type = circuit_type;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public double getP_head() {
		return p_head;
	}
	public void setP_head(double p_head) {
		this.p_head = p_head;
	}
	public double getP_flow() {
		return p_flow;
	}
	public void setP_flow(double p_flow) {
		this.p_flow = p_flow;
	}
	public double getD_length() {
		return d_length;
	}
	public void setD_length(double d_length) {
		this.d_length = d_length;
	}
	public double getC_length() {
		return c_length;
	}
	public void setC_length(double c_length) {
		this.c_length = c_length;
	}
	public double getD_flow() {
		return d_flow;
	}
	public void setD_flow(double d_flow) {
		this.d_flow = d_flow;
	}
	public double getC_flow() {
		return c_flow;
	}
	public void setC_flow(double c_flow) {
		this.c_flow = c_flow;
	}
	public String getT_description() {
		return t_description;
	}
	public void setT_description(String t_description) {
		this.t_description = t_description;
	}
	public double getDp() {
		return dp;
	}
	public void setDp(double dp) {
		this.dp = dp;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public Pipe getCir_pipe() {
		return cir_pipe;
	}
	public void setCir_pipe(Pipe cir_pipe) {
		this.cir_pipe = cir_pipe;
	}
	public Pipe getDis_pipe() {
		return dis_pipe;
	}
	public void setDis_pipe(Pipe dis_pipe) {
		this.dis_pipe = dis_pipe;
	}
	public ControlValve getControlValve() {
		return controlValve;
	}
	public void setControlValve(ControlValve controlValve) {
		this.controlValve = controlValve;
	}
	public BalancingValve getBalancingValve() {
		return balancingValve;
	}
	public void setBalancingValve(BalancingValve balancingValve) {
		this.balancingValve = balancingValve;
	}
	
	public CombinedCtrlBalValve getCombinedValve() {
		return combinedValve;
	}
	public void setCombinedValve(CombinedCtrlBalValve combinedValve) {
		this.combinedValve = combinedValve;
	}
	public double getPre_accessory_dp() {
		return pre_accessory_dp;
	}
	public void setPre_accessory_dp(double pre_accessory_dp) {
		this.pre_accessory_dp = pre_accessory_dp;
	}
	public double getDp_sum() {
		return dp_sum;
	}
	public void setDp_sum(double dp_sum) {
		this.dp_sum = dp_sum;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
