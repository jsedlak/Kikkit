package core.Listeners;

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
		
		return false;
	}

}
