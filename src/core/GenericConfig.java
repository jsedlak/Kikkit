package core;
import java.io.*;
import java.util.*;

public class GenericConfig {
	private ArrayList<NameValueObject> internalList = new ArrayList<NameValueObject>();
	
	public GenericConfig(String filename){
		load(filename);
	}
	
	public boolean hasKey(String key){
		for(NameValueObject nvo : internalList){
			if(nvo.Name.equalsIgnoreCase(key)) return true;
		}
		
		return false;
	}
	
	public String getValue(String key){
		for(NameValueObject nvo : internalList){
			if(nvo.Name.equalsIgnoreCase(key)) return nvo.Value;
		}
		
		return "";
	}
	
	public void load(String filename){
		try{
			internalList.clear();
			
			FileInputStream inputStream = new FileInputStream(filename);
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			
			Scanner scanner = new Scanner(reader);
		
			String data = "";
			while(scanner.hasNextLine()){
				data = scanner.nextLine();
				
				String[] splitData = data.split("=");
				
				if(splitData.length < 2) continue;
				
				internalList.add(new NameValueObject(splitData[0], splitData[1]));
			}
		}
		catch(Exception ex){}
		finally{}
	}
	
	public class NameValueObject {
		public String Name;
		public String Value;
		
		public NameValueObject(){
			Name = "";
			Value = "";
		}
		
		public NameValueObject(String name, String value){
			Name = name;
			Value = value;
		}
	}
}
