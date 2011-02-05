package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;

public class AdminCommandsListener extends CommandListener {

	public AdminCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	//PlayerChatEvent event, String[] cmdData, Player sourcePlayer
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		
		if(cmd.Sender instanceof Player){ sourcePlayer = (Player)cmd.Sender; }
			
		if(cmd.Command.getName().equalsIgnoreCase("/time") || cmd.Command.getName().equalsIgnoreCase("/day") || cmd.Command.getName().equalsIgnoreCase("/night"))
    	{
    		if(!canUseCommand(sourcePlayer, "/time")){
    			// Error!
    			return true;
    		}
    		
    		// Usage case
    		/*if(cmd.Arguments.length >= 2 && cmd.Arguments[1].equalsIgnoreCase("?")){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sets the time of day to day, night of a number.");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /time <day, night, (long: 0-24000)>; /day; /night");
    			
    			setCommandHandled(cmd, true);
				return true;
    		}*/
    			
    		try{
				if(cmd.Command.getName().equalsIgnoreCase("/day") || (cmd.Arguments.length == 2 && cmd.Arguments[1].equalsIgnoreCase("day")))
					getServer().getWorlds()[0].setTime(Kikkit.DAY);
				else if(cmd.Command.getName().equalsIgnoreCase("/night") || (cmd.Arguments.length == 2 && cmd.Arguments[1].equalsIgnoreCase("night")))
					getServer().getWorlds()[0].setTime(Kikkit.NIGHT);
				else{
					if(cmd.Arguments.length == 1) getServer().getWorlds()[0].setTime(Long.parseLong(cmd.Arguments[0]));
				}
				
				setCommandHandled(cmd, true);
				return true;
    		}
    		catch(Exception ex){}
    	}
    	else if(cmd.Command.getName().equalsIgnoreCase("/debug")){
    		if(!canUseCommand(sourcePlayer, "/debug")){
    			// Error!
				return true;
			}
    		
    		Kikkit.IsDebugging = !Kikkit.IsDebugging;
    		
    		if(Kikkit.IsDebugging) getPlugin().broadcast("Debug set to true.");
    		else getPlugin().broadcast("Debug set to false.");
    		
    		setCommandHandled(cmd, true);
			return true;
    	}
    	else if(cmd.Command.getName().equalsIgnoreCase("/kick")){
    		if(!canUseCommand(sourcePlayer, "/kick")){
				// Error
				return true;
			}
    		
    		if(cmd.Arguments.length >= 1){
	    		/*
    			if(cmd.Arguments[1].equalsIgnoreCase("?")){
    				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Kicks a player from the game.");
    				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /kick <player name> [reason]");
    				setCommandHandled(cmd, true);
    				return true;
    			}
    			*/
    			
	    		String reason = "";
	    		if(cmd.Arguments.length > 1) {
	    			for(int k = 1; k < cmd.Arguments.length; k++)
	    				reason += cmd.Arguments[k] + " ";
	    		}
	    		
	    		Player playerToKick = getServer().getPlayer(cmd.Arguments[0]);
	    		
	    		if(playerToKick != null){ 
	    			playerToKick.kickPlayer(reason);
	    		
	    			getPlugin().broadcast(playerToKick.getName() + " has been kicked.");
	    			
	    			setCommandHandled(cmd, true);
	    		}
	    		
	    		return true;
    		}
    	}
    	else if(cmd.Command.getName().equalsIgnoreCase("/murder") || cmd.Command.getName().equalsIgnoreCase("/kill")){
    		if(!canUseCommand(sourcePlayer, "/murder")){
    			return true;
    		}
    		
    		/*
    		if(cmd.Arguments.length >= 2 && cmd.Arguments[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Kills a player.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /murder [target player]");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Synonyms: /kill");
				
				setCommandHandled(cmd, true);
				return true;
    		}*/
    		
    		if(cmd.Arguments.length == 0){
    			sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
    			
    			setCommandHandled(cmd, true);
    			return true;
    		}
    		
    		Player target = getServer().getPlayer(cmd.Arguments[0]);
    		
    		if(target == null){
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
    			
    			setCommandHandled(cmd, true);
    			return true;
    		}
    		
    		target.setHealth(0);
    		
    		sourcePlayer.sendMessage(ChatColor.RED + "You have killed " + target.getName());
    		
    		setCommandHandled(cmd, true);
			return true;
    	}
    	else if(cmd.Command.getName().equalsIgnoreCase("/clearinventory") || cmd.Command.getName().equalsIgnoreCase("/ci")){
    		if(!canUseCommand(sourcePlayer, "/clearinventory")){
    			// Error!
    			return true;
    		}
    		
    		/*
    		if(cmd.Arguments.length >= 2 && cmd.Arguments[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Clears inventory of [target player]. Defaults to yourself.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /clearinventory [target player]");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Shortcuts: /ci");
				
				setCommandHandled(cmd, true);
				return true;
    		}*/
    		
    		Player target = sourcePlayer;
    		
    		if(cmd.Arguments.length >= 1){
    			Player p = getServer().getPlayer(cmd.Arguments[0]);
    			
    			if(p != null) target = p;
    		}
    		
    		target.getInventory().clear();
    		
    		sourcePlayer.sendMessage(ChatColor.RED + "Cleared the inventory!");
    		
    		if(!target.getName().equalsIgnoreCase(sourcePlayer.getName())) 
    			target.sendMessage(ChatColor.RED + sourcePlayer.getName() + " has cleared your inventory.");
    		
    		setCommandHandled(cmd, true);
			return true;
    	}
		
		return false;
	}

}
