package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;

public class AdminCommandsListener extends CommandListener {

	public AdminCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData,
			Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/time") || cmdData[0].equalsIgnoreCase("/day") || cmdData[0].equalsIgnoreCase("/night"))
    	{
    		if(!canUseCommand(sourcePlayer, "/time")){
    			// Error!
    			return true;
    		}
    		
    		// Usage case
    		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sets the time of day to day, night of a number.");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /time <day, night, (long: 0-24000)>; /day; /night");
    			
    			setCommandHandled(event, true);
				return true;
    		}
    			
    		try{
				if(cmdData[0].equalsIgnoreCase("/day") || (cmdData.length == 2 && cmdData[1].equalsIgnoreCase("day")))
					getServer().setTime(Kikkit.DAY);
				else if(cmdData[0].equalsIgnoreCase("/night") || (cmdData.length == 2 && cmdData[1].equalsIgnoreCase("night")))
					getServer().setTime(Kikkit.NIGHT);
				else{
					if(cmdData.length > 1) getServer().setTime(Long.parseLong(cmdData[1]));
				}
				
				setCommandHandled(event, true);
				return true;
    		}
    		catch(Exception ex){}
    	}
    	else if(cmdData[0].equalsIgnoreCase("/debug")){
    		if(!canUseCommand(sourcePlayer, "/debug")){
    			// Error!
				return true;
			}
    		
    		Kikkit.IsDebugging = !Kikkit.IsDebugging;
    		
    		setCommandHandled(event, true);
			return true;
    	}
    	else if(cmdData[0].equalsIgnoreCase("/kick")){
    		if(!canUseCommand(sourcePlayer, "/kick")){
				// Error
				return true;
			}
    		
    		if(cmdData.length >= 2){
	    		
    			if(cmdData[1].equalsIgnoreCase("?")){
    				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Kicks a player from the game.");
    				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /kick <player name> [reason]");
    				setCommandHandled(event, true);
    				return true;
    			}
    			
	    		String reason = "";
	    		if(cmdData.length > 2) {
	    			for(int k = 2; k < cmdData.length; k++)
	    				reason += cmdData[k] + " ";
	    		}
	    		
	    		Player playerToKick = getServer().getPlayer(cmdData[1]);
	    		
	    		if(playerToKick != null){ 
	    			playerToKick.kickPlayer(reason);
	    		
	    			getPlugin().broadcast(playerToKick.getName() + " has been kicked.");
	    			
	    			setCommandHandled(event, true);
	    		}
	    		
	    		return true;
    		}
    	}
    	else if(cmdData[0].equalsIgnoreCase("/murder") || cmdData[0].equalsIgnoreCase("/kill")){
    		if(!canUseCommand(sourcePlayer, "/murder")){
    			return true;
    		}
    		
    		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Kills a player.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /murder [target player]");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Synonyms: /kill");
				
				setCommandHandled(event, true);
				return true;
    		}
    		
    		if(cmdData.length < 2){
    			sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
    			
    			setCommandHandled(event, true);
    			return true;
    		}
    		
    		Player target = getServer().getPlayer(cmdData[1]);
    		
    		if(target == null){
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
    			
    			setCommandHandled(event, true);
    			return true;
    		}
    		
    		target.setHealth(0);
    		
    		sourcePlayer.sendMessage(ChatColor.RED + "You have killed " + target.getName());
    		
    		setCommandHandled(event, true);
			return true;
    	}
    	else if(cmdData[0].equalsIgnoreCase("/clearinventory") || cmdData[0].equalsIgnoreCase("/ci")){
    		if(!canUseCommand(sourcePlayer, "/clearinventory")){
    			// Error!
    			return true;
    		}
    		
    		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Clears inventory of [target player]. Defaults to yourself.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /clearinventory [target player]");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Shortcuts: /ci");
				
				setCommandHandled(event, true);
				return true;
    		}
    		
    		Player target = sourcePlayer;
    		
    		if(cmdData.length >= 2){
    			Player p = getServer().getPlayer(cmdData[1]);
    			
    			if(p != null) target = p;
    		}
    		
    		target.getInventory().clear();
    		
    		sourcePlayer.sendMessage(ChatColor.RED + "Cleared the inventory!");
    		
    		if(!target.getName().equalsIgnoreCase(sourcePlayer.getName())) 
    			target.sendMessage(ChatColor.RED + sourcePlayer.getName() + " has cleared your inventory.");
    		
    		setCommandHandled(event, true);
			return true;
    	}
		
		return false;
	}

}
