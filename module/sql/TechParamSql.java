package module.sql;


public class TechParamSql {
	
	public final static String Query(String name){
		String result = "";
		
		switch(name){
			case "getTechParam":
				result = "SELECT * FROM TECH_PARAM;";
				break;
			default:
				result = "";
				break;
		}
		
		return result;
	}

}
