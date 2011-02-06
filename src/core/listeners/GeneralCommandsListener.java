package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;

public class GeneralCommandsListener extends CommandListener {

	public GeneralCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("/playerlist") || cmd.Name.equalsIgnoreCase("/listplayers") || cmd.Name.equalsIgnoreCase("/pl")){
			if(!canUseCommand(sourcePlayer, "/playerlist")) return true;
			
			Player[] players = getServer().getOnlinePlayers();
			
			String total = ChatColor.RED + "Players: " + ChatColor.GRAY;
			
			for(int i = 0; i < players.length; i++){
				if(players[i] == cmd.Sender) continue;
				
				total += players[i].getName();
				
				if(i < players.length - 1) total += ", ";
			}
			
			if(sourcePlayer != null) sourcePlayer.sendMessage(total);
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/msg") || cmd.Name.equalsIgnoreCase("/m")){
			if(!canUseCommand(sourcePlayer, "/msg")) return true;
			
			if(cmd.Args.length < 2){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Invalid syntax.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			Player playerToMsg = getServer().getPlayer(cmd.Args[0]);
			String msg = getLastFromIndex(cmd.Args, 1);
			String sourceName = "Server";
			
			if(sourcePlayer != null) sourceName = sourcePlayer.getName();
			
			if(playerToMsg == null || msg.isEmpty()){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Invalid syntax.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			playerToMsg.sendMessage(ChatColor.AQUA + "[" + sourceName + "] " + msg);
			if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.AQUA + "[" + sourceName + "] (" + playerToMsg.getName() + ") " + msg);
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		
		return false;
	}

}
