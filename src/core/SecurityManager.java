package core;
//import java.io.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class SecurityManager {
	private static final String DefaultGroupsConfig = "config/groups.txt";
	private static final String DefaultPlayersConfig = "config/players.txt";
	
	private ArrayList<Group> groups = new ArrayList<Group>();
	
	private String currentGroupFileName;
	private String currentPlayerFileName;
	
	public SecurityManager(String groupsConfig, String playersConfig){
		load(groupsConfig, playersConfig);
	}
	
	public SecurityManager(){ 
		this(DefaultGroupsConfig, DefaultPlayersConfig);
	}
	
	public void load(String groupsConfigFile, String playersConfigFile){
		currentGroupFileName = groupsConfigFile;
		currentPlayerFileName = playersConfigFile;
		
		Kikkit.MinecraftLog.info("Loading security");
		Kikkit.MinecraftLog.info("    Groups: " + groupsConfigFile);
		Kikkit.MinecraftLog.info("    Players: " + playersConfigFile);
		
		loadGroups();
		loadPlayers();
	}
	
	private void loadGroups(){
		FileInputStream inputStream;
		InputStreamReader reader;
		Scanner scanner = null;
		try {
			inputStream = new FileInputStream(currentGroupFileName);
			reader = new InputStreamReader(inputStream, "UTF-8");
			
			scanner = new Scanner(reader);

			String line = "";
			while(scanner.hasNextLine()){
				line = scanner.nextLine();
				
				// Check for comments.
				if(line.startsWith("#") || line.startsWith("//")) continue;
				
				String[] data = line.split(":");
				
				if(data.length < 2) continue;
				
				if(data.length >= 2){
					Group newGroup = new Group(data[0]);
					
					groups.add(newGroup);
					
					newGroup.setColor(ChatColor.getByCode(Integer.parseInt(data[1])));
					
					if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("    Added group: " + newGroup.getName());
					
					if(data.length > 2){
						String[] commands = data[2].split(",");
						
						// TODO: Why must we do a foreach here?
						// Because java doesn't understand IEnumerable
						for(String str : commands){
							if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("        command: " + str);
							newGroup.Commands.add(str);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Kikkit.MinecraftLog.info(e.getMessage());
		}
		finally{
			if(scanner != null) scanner.close();
		}
	}
	
	private void loadPlayers(){
		FileInputStream inputStream;
		InputStreamReader reader;
		Scanner scanner = null;
		try {
			inputStream = new FileInputStream(currentPlayerFileName);
			reader = new InputStreamReader(inputStream, "UTF-8");
			
			scanner = new Scanner(reader);
			
			String line = "";
			while(scanner.hasNextLine()){
				line = scanner.nextLine();
				
				// Check for comments.
				if(line.startsWith("#") || line.startsWith("//")) continue;
				
				String[] data = line.split(":");
				
				if(data.length < 2) continue;
				
				if(data.length == 2){
					Group group = getGroup(data[1]);
					
					if(group == null){
						// TODO: Log here
						continue;
					}
					
					group.Players.add(data[0]);
					
					if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("    Added player to " + group.getName() + ": " + data[0]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Kikkit.MinecraftLog.info(e.getMessage());
		}
		finally{
			if(scanner != null) scanner.close();
		}
	}
	
	public boolean canUseCommand(String player, String command){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("Groups to search: " + groups.size());
		
		for(Group group : groups){
			if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("    canUseCommand is looking at " + group.getName());
			
			boolean ingroup = group.isInGroup(player);
			boolean canuse = group.canUseCommand(command);
			
			if(Kikkit.IsDebugging) {
				Kikkit.MinecraftLog.info("        isInGroup: " + ingroup);
				Kikkit.MinecraftLog.info("        canUseCommand: " + canuse);
			}
			
			//if(group.isInGroup(player) && group.canUseCommand(command)) return true;
			if(ingroup && canuse) return true;
		}
		
		return false;
	}
	
	public Group getGroupForPlayer(String playerName){
		for(Group group : groups){
			if(group.isInGroup(playerName)) return group;
		}
		
		return null;
	}
	
	public Group getGroup(String groupName){
		for(Group g : groups){
			if(g.getName().equalsIgnoreCase(groupName)) return g;
		}
		
		return null;
	}
	
	public boolean isInGroup(Player player, String groupName){
		return isInGroup(player.getName(), groupName);
	}
	
	public boolean isInGroup(String playerName, String groupName){
		//boolean returnValue = false;
		for(Group group : groups){
			if(group.getName().equalsIgnoreCase(groupName)){
				return group.isInGroup(playerName);
			}
		}
		
		//Kikkit.MinecraftLog.info("isInGroup(" + playerName +", " + groupName + "): " + returnValue);
		
		return false;
	}
	
	public class Group{
		public static final String ADMIN_OVERRIDE = "*";
		
		private String name;
		private ChatColor color;
		
		public ArrayList<String> Players = new ArrayList<String>();
		public ArrayList<String> Commands = new ArrayList<String>();
		
		public Group(String groupName){
			name = groupName;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean canUseCommand(String cmd){
			for(String command : Commands){
				if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("            internal command check: " + command);
				
				if(command.equalsIgnoreCase(cmd) || command.equalsIgnoreCase(ADMIN_OVERRIDE)) return true;
			}
			
			return false;
		}
		
		public boolean isInGroup(String playerName){
			// Provide the base case - a default group.
			if(getName().equalsIgnoreCase(ADMIN_OVERRIDE)) return true;
			
			for(String user : Players){
				if(user.equalsIgnoreCase(playerName)) return true;
			}
			
			return false;
		}
		
		public ChatColor getColor() { return color; }
		public void setColor(ChatColor value) { color = value; }
	}
}
