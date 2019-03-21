package module.domain;

public class CombinedCtrlBalValveList {
	//복합밸브 DB 리스트 도메인 - 따로 도메인 만듬
	
	private String seq;
	private String m_seq;
	private String product;
	private String name; // 복합밸브 명
	private String size_name;
	private double size = 0.0d;
	private double setting = 0.0d;
	private double kv = 0.0d; // 내경
	private double q_max = 0.0d; // --추후 변경 가능  복합밸브 선정 시 필요한 유량
	private double dp = 0.0d; // 복합밸브 dp
	
	
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getM_seq() {
		return m_seq;
	}
	public void setM_seq(String m_seq) {
		this.m_seq = m_seq;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize_name() {
		return size_name;
	}
	public void setSize_name(String size_name) {
		this.size_name = size_name;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	public double getSetting() {
		return setting;
	}
	public void setSetting(double setting) {
		this.setting = setting;
	}
	public double getKv() {
		return kv;
	}
	public void setKv(double kv) {
		this.kv = kv;
	}
	public double getQ_max() {
		return q_max;
	}
	public void setQ_max(double q_max) {
		this.q_max = q_max;
	}
	public double getDp() {
		return dp;
	}
	public void setDp(double dp) {
		this.dp = dp;
	}
	
	

}
