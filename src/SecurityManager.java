//import java.io.*;
import java.util.*;

import org.bukkit.Player;

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
		
		loadGroups();
		loadPlayers();
	}
	
	private void loadGroups(){
		Scanner fileReader = null;
		try {
			fileReader = new Scanner(currentGroupFileName);
			
			String line;
			while(fileReader.hasNextLine()){
				line = fileReader.nextLine();
				
				// Check for comments.
				if(line.startsWith("#") || line.startsWith("//")) continue;
				
				String[] data = line.split(":");
				
				if(data.length < 2) continue;
				
				if(data.length == 2){
					Group newGroup = new Group(data[0]);
					
					String[] commands = data[1].split("/");
					
					// TODO: Why must we do a foreach here?
					// Because java doesn't understand IEnumerable
					for(String str : commands){
						newGroup.Commands.add(str);
					}
					
					groups.add(newGroup);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		finally{
			if(fileReader != null) fileReader.close();
		}
	}
	
	private void loadPlayers(){
		Scanner fileReader = null;
		try {
			fileReader = new Scanner(currentPlayerFileName);
			
			String line;
			while(fileReader.hasNextLine()){
				line = fileReader.nextLine();
				
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
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		finally{
			if(fileReader != null) fileReader.close();
		}
	}
	
	public boolean canUseCommand(String player, String command){
		for(Group group : groups){
			if(group.isInGroup(player) && group.canUseCommand(command)) return true;
		}
		
		return false;
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
		for(Group group : groups){
			if(group.getName().equalsIgnoreCase(groupName)){
				return group.isInGroup(playerName);
			}
		}
		
		return false;
	}
	
	public class Group{
		public static final String ADMIN_OVERRIDE = "*";
		
		private String name;
		
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
				if(command.equalsIgnoreCase(cmd) || command.equalsIgnoreCase(ADMIN_OVERRIDE)) return true;
			}
			
			return false;
		}
		
		public boolean isInGroup(String playerName){
			for(String user : Players){
				if(user.equalsIgnoreCase(playerName)) return true;
			}
			
			return false;
		}
	}
}
