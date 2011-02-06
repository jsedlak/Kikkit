package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;

public class ChatCommandsListener extends CommandListener {

	public ChatCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("/v")){
			if(!canUseCommand(sourcePlayer, "/v")) return true;
			
			String msg = this.getLastFromIndex(cmd.Args, 0);
			
			if(msg.isEmpty()){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "You forgot to include a message.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			Player[] players = getServer().getOnlinePlayers();
			
			String sourceName = "Server";
			if(sourcePlayer != null) sourceName = sourcePlayer.getName();
			
			for(Player player : players){
				if(canUseCommand(player, "/v")){
					player.sendMessage(ChatColor.GOLD + "[" + sourceName + "] " + getLastFromIndex(cmd.Args, 0));
				}
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/a")){
			if(!canUseCommand(sourcePlayer, "/a")) return true;
			
			String msg = this.getLastFromIndex(cmd.Args, 0);
			
			if(msg.isEmpty()){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "You forgot to include a message.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			Player[] players = getServer().getOnlinePlayers();
			
			String sourceName = "Server";
			if(sourcePlayer != null) sourceName = sourcePlayer.getName();
			
			for(Player player : players){
				if(canUseCommand(player, "/a")){
					player.sendMessage(ChatColor.DARK_RED + "[" + sourceName + "] " + getLastFromIndex(cmd.Args, 0));
				}
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		return false;
	}

}
