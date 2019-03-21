package module.domain;

public class PipeList {
	// 배관 DB 리스트
	
	private String seq;
	private String m_seq; // temp list 를 위한
	private String type; // temp list 를 위한
	private String series;
	private String name;
	private double inner_diameter = 0.0d; //내경
	private double Roughness = 0.0d; // 거칠기
	//private double max_pressure = 0.0d;
	//private double max_temp = 0.0d;
	
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getInner_diameter() {
		return inner_diameter;
	}
	public void setInner_diameter(double inner_diameter) {
		this.inner_diameter = inner_diameter;
	}
	public double getRoughness() {
		return Roughness;
	}
	public void setRoughness(double roughness) {
		Roughness = roughness;
	}
}
