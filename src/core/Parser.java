package core;

public class Parser {
	public static int TryParseInt(String input, int defaultValue){
		try{
			return Integer.parseInt(input);
		}
		catch (Exception ex){
			return defaultValue; 
		}
	}
}
