package core.players;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

import org.bukkit.ChatColor;

import core.Kikkit;

public class PlayerData {
	private static final long OneDay = 86400000;
	
	private String name;
	private String filename;
	private long credits = 100;
	private double hoursLogged = 0;
	private Date loggedOnTimeStamp;
	
	private Date lastDayLoggedOn = new Date();
	private int consecutiveDaysOn = 0;
	
	public PlayerData(String playerName){
		name = playerName;
		
		load();
	}
	
	public void loggedOn(){
		loggedOnTimeStamp = new Date();
		
		long diff = loggedOnTimeStamp.getTime() - lastDayLoggedOn.getTime();
		
		if(diff >= OneDay && diff <= OneDay * 2) consecutiveDaysOn++;
		else if(diff > OneDay * 2){
			consecutiveDaysOn = 0;
		}
		
		if(consecutiveDaysOn > 0 && (consecutiveDaysOn % 10) == 0){
			int amount = consecutiveDaysOn * 10;
			credits += amount;
			Kikkit.Current.getServer().getPlayer(name).sendMessage(ChatColor.GREEN + "You've earned " + amount + " " + Kikkit.Current.getMarket().getCurrencyName() + " for being on so much!");
		}
		
		lastDayLoggedOn = loggedOnTimeStamp;
		save();
	}
	
	public void loggedOff(){
		Date newDate = new Date();
		
		hoursLogged += (newDate.getTime() - loggedOnTimeStamp.getTime()) * 0.000000278;
		
		save();
	}
	
	@SuppressWarnings("deprecation")
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
				else if(currentLine.startsWith("lastLoggedOn=")){
					lastDayLoggedOn.setTime(
							Date.parse(currentLine.substring("lastLoggedOn=".length()))
					);
				}
				else if(currentLine.startsWith("consecutiveDays=")){
					consecutiveDaysOn = Integer.parseInt(currentLine.substring("consecutiveDays=".length()));
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
			outputFile.write("lastLoggedOn=" + lastDayLoggedOn + "\n");
			outputFile.write("consecutiveDays=" + consecutiveDaysOn + "\n");
			
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
	public int getConsecutiveDays() { return consecutiveDaysOn; }
}
