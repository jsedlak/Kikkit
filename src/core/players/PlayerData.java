package core.players;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

import core.Kikkit;

public class PlayerData {
	private String name;
	private String filename;
	private long credits = 100;
	private double hoursLogged = 0;
	private Date loggedOnTimeStamp;
	
	public PlayerData(String playerName){
		name = playerName;
		
		load();
	}
	
	public void loggedOn(){
		loggedOnTimeStamp = new Date();
	}
	
	public void loggedOff(){
		Date newDate = new Date();
		
		hoursLogged += (newDate.getTime() - loggedOnTimeStamp.getTime()) * 0.000000278;
		
		save();
	}
	
	private void load(){
		filename = "players/" + name + ".dat";
		
		FileInputStream inputStream;
		InputStreamReader reader;
		Scanner scanner = null;
		
		try{
			inputStream = new FileInputStream(filename);
			reader = new InputStreamReader(inputStream);
			
			scanner = new Scanner(reader);
			
			String currentLine = "";
			while(scanner.hasNextLine()){
				currentLine = scanner.nextLine();
				
				if(currentLine.startsWith("credits=")){
					credits = Long.parseLong(currentLine.substring("credits=".length()));
				}
				else if(currentLine.startsWith("hours=")){
					hoursLogged = Double.parseDouble(currentLine.substring("hours=".length()));
				}
			}
		}
		catch(FileNotFoundException fnf){
			save();
		}
		finally{
			if(scanner != null) scanner.close();
		}
	}
	
	private void save(){
		FileWriter outputFile;
		
		try {		
			outputFile = new FileWriter(filename, false);
			
			outputFile.write("user=" + getName() + "\n");
			outputFile.write("credits=" + getCredits() + "\n");
			outputFile.write("hours=" + getHours() + "\n");
			
			outputFile.close();
		}
		catch (IOException e) {
			Kikkit.MinecraftLog.info(e.getMessage());
		}
	}
	
	public String getName() { return name; }
	
	public long getCredits() { return credits; }
	public void setCredits(long value) { 
		credits = value; 
		save(); 
	}
	
	public double getHours() { return hoursLogged; }
}
