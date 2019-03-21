package module.domain;

public class BalancingValveList {
	
	private String seq;
	private String m_seq;
	private String product;
	private String name;
	private String size_name;
	private double size = 0.0d;
	private double min_flow = 0.0d;
	private double max_flow = 0.0d;
	
	
	
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
	public double getMin_flow() {
		return min_flow;
	}
	public void setMin_flow(double min_flow) {
		this.min_flow = min_flow;
	}
	public double getMax_flow() {
		return max_flow;
	}
	public void setMax_flow(double max_flow) {
		this.max_flow = max_flow;
	}

	
	

}
