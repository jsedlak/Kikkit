import java.io.*;
import java.util.Scanner;

public class Whitelist {
	private String[] internalList;
	
	public Whitelist(String filename){
		load(filename);
	}
	
	public boolean isOnList(String playerName){
		for(String str : internalList){
			if(str.equalsIgnoreCase(playerName)) return true;
		}
		
		return false;
	}
	
	public void load(String filename){
		try{
			FileInputStream inputStream = new FileInputStream(filename);
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			
			Scanner scanner = new Scanner(reader);
		
			String data = "";
			while(scanner.hasNextLine()){
				data = data.concat(scanner.nextLine());
				
				if(scanner.hasNextLine())
					data = data.concat(",");
			}
			
			internalList = data.split(",");
		}
		catch (Exception f){
			Kikkit.MinecraftLog.warning("Could not load the whitelist file.");
		}
		finally{}
	}
}