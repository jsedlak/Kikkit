package core;

import org.bukkit.Material;

public class Parser {
	public static int TryParseInt(String input, int defaultValue){
		try{
			return Integer.parseInt(input);
		}
		catch (Exception ex){
			return defaultValue; 
		}
	}
	
	public static Material ParseMaterial(String input){
		for(Material mat : Material.values()){
			if(mat.name().equalsIgnoreCase(input)){
				return mat;
			}
		}
		
		return null;
	}
}
