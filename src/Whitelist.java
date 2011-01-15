import java.io.*;
import java.util.*;

public class Whitelist {
	private ArrayList<String> internalList = new ArrayList<String>();
	private boolean isOverriden;
	private String internalFilename;
	
	public Whitelist(String filename){
		load(filename);
	}
	
	public boolean isOnList(String playerName){
		if(isOverriden) return true;
		
		for(String str : internalList){
			if(str.equalsIgnoreCase(playerName)) return true;
		}
		
		return false;
	}
	
	public void load(String filename){
		try{			
			internalFilename = filename;
			
			FileInputStream inputStream = new FileInputStream(filename);
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			
			Scanner scanner = new Scanner(reader);
		
			String data = "";
			internalList.clear();
			while(scanner.hasNextLine()){
				data = scanner.nextLine();
				
				internalList.add(data);
			}
			
			Kikkit.MinecraftLog.info("Whitelist Loaded from " + filename);
		}
		catch (Exception f){
			Kikkit.MinecraftLog.warning("Could not load the whitelist file: " + filename);
		}
		finally{}
	}
	
	public void save(){
		FileWriter outputFile = null;
		
		try {		
			outputFile = new FileWriter(internalFilename, false);
			
			//PrintWriter out = new PrintWriter(outputFile);
			
			//for(String line : internalList){
			for(int i = 0; i < internalList.size(); i++){
				//out.println(line);
				String person = internalList.get(i);
				
				outputFile.write(person + "\n");
			}
			
			
		}
		catch (IOException e) {
			Kikkit.MinecraftLog.info(e.getMessage());
		}	
		finally{
			// TODO: Why do we need to do this?
			try{
				if(outputFile != null)
					outputFile.close();
			}
			catch(Exception ex) {}
		}
	}
	
	public void add(String user){
		for(String str : internalList){
			if(str.equalsIgnoreCase(user)) return;
		}
		
		internalList.add(user);
		
		save();
	}
	
	public void remove(String user){
		for(int i = internalList.size() - 1; i >= 0; i--){
			if(internalList.get(i).equalsIgnoreCase(user)){
				internalList.remove(i);
				break;
			}
		}
		
		save();
	}
	
	public boolean has(String user){
		for(int i = internalList.size() - 1; i >= 0; i--){
			if(internalList.get(i).equalsIgnoreCase(user)) return true;
		}
		
		return false;
	}
	
	public boolean getIsOverriden(){
		return isOverriden;
	}
	
	public void setIsOverriden(boolean value){
		isOverriden = value;
	}
}