package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;

public class GeneralCommandsListener extends CommandListener {

	public GeneralCommandsListener(Kikkit plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/playerlist")){
    		if(!canUseCommand(sourcePlayer, "/playerlist")){
    			// Error!
				return true;
			}
    		
    		Player[] players = getServer().getOnlinePlayers();
    		
    		String total = "Players: ";
    		for(int k = 0; k < players.length; k++){
    			// Don't add the player that is asking
    			if(players[k].getName().equalsIgnoreCase(sourcePlayer.getName())) continue;
    			
    			total += players[k].getName();
    			if(k < players.length - 1) total += ", ";
    		}
    		
    		sourcePlayer.sendMessage(ChatColor.GRAY + total);
    		
    		setCommandHandled(event, true);
    		return true;
    	}
    	else if(cmdData[0].equalsIgnoreCase("/msg") || cmdData[0].equalsIgnoreCase("/m")){
    		if(!canUseCommand(sourcePlayer, "/msg")){
    			// Error!
    			return true;
    		}
    		
    		// Usage case
    		if(cmdData.length <= 2 || (cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?"))){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sends a private message to a player.");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /msg <player name> <message>");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Shortcuts: /m");
    			
    			setCommandHandled(event, true);
    			return true;
    		}
    		
    		Player playerToMsg = getServer().getPlayer(cmdData[1]);
    		
    		String message = "";
			for(int k = 2; k < cmdData.length; k++)
				message += cmdData[k] + " ";
    		
    		if(playerToMsg != null){
    			playerToMsg.sendMessage(ChatColor.AQUA + "[" + sourcePlayer.getName() + "]" + message);
    			sourcePlayer.sendMessage(ChatColor.AQUA + "[" + sourcePlayer.getName() + "]" + message);
    		}
    		else sourcePlayer.sendMessage(ChatColor.RED + "Can't find player " + cmdData[1]);
    		
    		setCommandHandled(event, true);
    		return true;
    	}
		
		return false;
	}

}
