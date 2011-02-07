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
			
		if(cmd.Name.equalsIgnoreCase("/time") || cmd.Name.equalsIgnoreCase("/day") || cmd.Name.equalsIgnoreCase("/night"))
    	{
    		if(!canUseCommand(cmd.Sender, "/time")){
    			return true;
    		}
    		
    		// Usage case
    		/*if(cmd.Arguments.length >= 2 && cmd.Arguments[1].equalsIgnoreCase("?")){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sets the time of day to day, night of a number.");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /time <day, night, (long: 0-24000)>; /day; /night");
    			
    			setCommandHandled(cmd, true);
				return true;
    		}*/
    		
    		if(cmd.Name.equalsIgnoreCase("/day")){
    			Kikkit.getCurrentWorld().setTime(Kikkit.DAY);
    			
    			setCommandHandled(cmd, true);
    			return true;
    		}
    		else if(cmd.Name.equalsIgnoreCase("/night")){
    			Kikkit.getCurrentWorld().setTime(Kikkit.NIGHT);
    			
    			setCommandHandled(cmd, true);
    			return true;
    		}
    	
    		// If there are no arguments, we can't parse the data into a long
    		if(cmd.Args.length == 0) return true;
    		
    		String timeData = cmd.Args[0];
    		long time = Kikkit.DAY;
    		
    		try{
    			time = Long.parseLong(timeData);

    			Kikkit.getCurrentWorld().setTime(time);
    		}
    		catch(NumberFormatException nfe){
    			Kikkit.MinecraftLog.info("There was a problem parsing the time command's data.");
    			
    			if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the time.");
    		}
    			
    		setCommandHandled(cmd, true);
			return true;
    	}
    	else if(cmd.Name.equalsIgnoreCase("/debug")){
    		if(!canUseCommand(cmd.Sender, "/debug")){
    			// Error!
				return true;
			}
    		
    		Kikkit.IsDebugging = !Kikkit.IsDebugging;
    		
    		if(Kikkit.IsDebugging) getPlugin().broadcast("Debug set to true.");
    		else getPlugin().broadcast("Debug set to false.");
    		
    		setCommandHandled(cmd, true);
			return true;
    	}
    	else if(cmd.Name.equalsIgnoreCase("/kick")){
    		if(!canUseCommand(cmd.Sender, "/kick")){
				// Error
				return true;
			}
    		
    		/*
			if(cmd.Arguments[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Kicks a player from the game.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /kick <player name> [reason]");
				setCommandHandled(cmd, true);
				return true;
			}
			*/
    		
    		Player playerToKick = null;
    		String reason = "Please see the rules.";
    		
    		if(cmd.Args.length == 0) return true;
    		
    		if(cmd.Args.length >= 1) playerToKick = getServer().getPlayer(cmd.Args[0]);
    		if(cmd.Args.length > 1) reason = getLastFromIndex(cmd.Args, 1);
    		
    		if(playerToKick == null){
    			if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Can't find player " + cmd.Args[0]);
    			return true;
    		}
    		
    		playerToKick.kickPlayer(reason);
    		
    		getPlugin().broadcast(ChatColor.RED + playerToKick.getName() + " has been kicked from the game.");
    		
    		setCommandHandled(cmd, true);
    		return true;
    	}
    	else if(cmd.Name.equalsIgnoreCase("/murder") || cmd.Command.getName().equalsIgnoreCase("/kill")){
    		if(!canUseCommand(cmd.Sender, "/murder")){
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
    		
    		Player target = null;
    		
    		if(cmd.Args.length > 0) target = getServer().getPlayer(cmd.Args[0]);
    		
    		if(target == null){
    			if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
    			
    			setCommandHandled(cmd, true);
    			return true;
    		}
    		
    		target.setHealth(0);
    		
    		if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "You have murdered " + target.getName());
    		
    		setCommandHandled(cmd, true);
    		return true;
    	}
    	else if(cmd.Name.equalsIgnoreCase("/clearinventory") || cmd.Command.getName().equalsIgnoreCase("/ci")){
    		if(!canUseCommand(cmd.Sender, "/clearinventory")){
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
    		
    		if(cmd.Args.length > 0){
    			// Special check
    			if(!canUseCommand(cmd.Sender, "/citarget"))
    				return true;
    			
    			target = getServer().getPlayer(cmd.Args[0]);
    		}
    		
    		if(target == null){
    			if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
    			
    			setCommandHandled(cmd, true);
    			return true;
    		}
    		
    		target.getInventory().clear();
    		target.sendMessage(ChatColor.RED + "Your inventory has been cleared.");
    		
    		if(sourcePlayer != null && sourcePlayer != target) sourcePlayer.sendMessage(ChatColor.RED + "Cleared inventory.");
    		
    		setCommandHandled(cmd, true);
			return true;
    	}
		
		return false;
	}

}
