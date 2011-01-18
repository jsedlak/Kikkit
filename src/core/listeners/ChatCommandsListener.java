package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;

public class ChatCommandsListener extends CommandListener {

	public ChatCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/v")){
			if(!canUseCommand(sourcePlayer, "/v")) return true;
			
			if(cmdData.length > 1){
				Player players[] = getServer().getOnlinePlayers();
				
				for(Player player : players){
					if(canUseCommand(player, "/v")){
						player.sendMessage(ChatColor.GOLD + "[" + sourcePlayer.getName() + "] " + getLastFromIndex(cmdData, 1));
					}
				}
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /v <message>");
			}
			
			setCommandHandled(event, true);
			return true;
		}
		else if(cmdData[0].equalsIgnoreCase("/a")){
			if(!canUseCommand(sourcePlayer, "/a")) return true;
			
			if(cmdData.length > 1){
				Player players[] = getServer().getOnlinePlayers();
				
				for(Player player : players){
					if(canUseCommand(player, "/a")){
						player.sendMessage(ChatColor.GOLD + "[" + sourcePlayer.getName() + "] " + getLastFromIndex(cmdData, 1));
					}
				}
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /a <message>");
			}
			
			setCommandHandled(event, true);
			return true;
		}
		
		return false;
	}

}
