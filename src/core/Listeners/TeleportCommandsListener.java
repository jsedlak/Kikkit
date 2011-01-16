package core.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;
import core.WarpList;

public class TeleportCommandsListener extends CommandListener {

	public TeleportCommandsListener(Kikkit plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		// Command: warpto [player name] [warp name]
		if(cmdData[0].equalsIgnoreCase("/warpto")){
			if(!canUseCommand(sourcePlayer, "/warpto")) {
				// Error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /warpto <player name> <warp name>");
				
				setCommandHandled(event, true);
				return true;
			}
			
			if(cmdData.length == 3){
				WarpList wl = getPlugin().getServerModWarps();
				
				WarpList.WarpPoint wp = wl.get(cmdData[2]);
				
				if(wp == null){
					sourcePlayer.sendMessage(ChatColor.RED + "Can't find warp: " + cmdData[2]);
					
					setCommandHandled(event, true);
					return true;
				}
				
				Player target = getServer().getPlayer(cmdData[1]);
				
				if(target == null) sourcePlayer.sendMessage(ChatColor.RED + "Can't find player: " + cmdData[1]);
				else {
					target.sendMessage(ChatColor.RED + "Pooooosh!");
					sourcePlayer.sendMessage(ChatColor.RED + "Player has been warped.");
					
					target.teleportTo(wp.getLocation());
				}
				
				setCommandHandled(event, true);
				return true;
			};
		}
		else if(cmdData[0].equalsIgnoreCase("/tp")){
    		if(!canUseCommand(sourcePlayer, "/tp")){
    			// Error!
    			return true;
    		}
    		
    		if(cmdData.length >= 2){
    			if(cmdData[1].equalsIgnoreCase("?")){
    				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] <destination player> [source player]");
    			}
    			else{
	    			Player destination = getServer().getPlayer(cmdData[1]);
	    			
	    			Player source = sourcePlayer;
	    			if(cmdData.length >= 3) source = getServer().getPlayer(cmdData[2]);
	    			
	    			
	    			if(source != null && destination != null){
	    				source.teleportTo(destination);
	    				
	    				source.sendMessage(ChatColor.RED + "Telepooooorsh!");
	    				sourcePlayer.sendMessage(ChatColor.RED + "Teleported player.");
	    				
	    				Kikkit.MinecraftLog.info(sourcePlayer.getName() + " made " + source.getName() + " teleport to " + destination.getName());
	    			}
    				
    			}
    			
    			setCommandHandled(event, true);
    			return true;
    		}
    	}
		
		return false;
	}

}
