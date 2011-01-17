package core;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public abstract class CommandListener {
	private Kikkit kikkitPlugin = null;
	
	public CommandListener(Kikkit plugin){
		kikkitPlugin = plugin;
	}
	
	public abstract boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer);
	
	protected Kikkit getPlugin(){
		return kikkitPlugin;
	}
	
	protected boolean canUseCommand(Player player, String command){
		return kikkitPlugin.canUseCommand(player, command);
	}
	
	protected Server getServer(){
		return kikkitPlugin.getServer();
	}
	
	protected void setCommandHandled(PlayerChatEvent event, boolean wasCommandHandled){
		if(wasCommandHandled){
			event.setCancelled(true);
		}
	}
	
	protected String getLastFromIndex(String[] data, int startIndex){
		if(startIndex > data.length - 1) return "";
		
		String returnString = "";
		for(int k = startIndex; k < data.length; k++){
			returnString += data[k];
			
			if(k < data.length - 1) returnString += " ";
		}
		
		return returnString;
	}
}
