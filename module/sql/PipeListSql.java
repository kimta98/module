package module.sql;

public class PipeListSql {

public final static String Query(String name){
		
		String result = "";
		
		switch(name){
		case "getPipeList":
			result = "SELECT * FROM PIPE_LIST WHERE (series = 'GeneralPipeSeries' OR series = ? ) ORDER BY inner_diameter;";
			break;
		case "getPipeListBySeq":
			result = "SELECT * FROM PIPE_LIST WHERE seq = ?";
			break;
		default:
			result = "";
			break;
	}
		
		return result;
	}
	
}
