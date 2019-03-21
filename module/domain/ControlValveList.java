package module.domain;

public class ControlValveList {
	// 컨트롤 밸브 DB 리스트 - 따로 도메인 

	private String seq;
	private String m_seq;
	private String product;
	private String name; // 컨트롤 밸브 이름
	private String size_name;
	private double size = 0.0d;
	private double turns_pos = 0.0d;
	private double kv = 0.0d; // kv
	
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getKv() {
		return kv;
	}
	public void setKv(double kv) {
		this.kv = kv;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
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
	public double getTurns_pos() {
		return turns_pos;
	}
	public void setTurns_pos(double turns_pos) {
		this.turns_pos = turns_pos;
	}
	
	
	
}
