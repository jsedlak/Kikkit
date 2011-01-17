package core.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;
import core.WarpList;

public class PublicWarpCommandsListener extends CommandListener {

	public PublicWarpCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/setspawn")){
    		if(!canUseCommand(sourcePlayer, "/setspawn")){
    			// Error
    			return true;
    		}
    		
    		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /setspawn");
    		}
    		else{
				WarpList list = getPlugin().getServerModWarps();
				
				list.set("spawn", sourcePlayer.getLocation());
				
				sourcePlayer.sendMessage(ChatColor.RED + "Spawn has been set.");
    		}
    		
			setCommandHandled(event, true);
			return true;
    	}
    	else if(cmdData[0].equalsIgnoreCase("/spawn")){
    		if(!canUseCommand(sourcePlayer, "/spawn")){
				// Error!
				return true;
			}
			
    		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /spawn");
    		}
    		else{
				WarpList list = getPlugin().getServerModWarps();
				
				WarpList.WarpPoint wp = list.get("spawn");
				
				if(wp != null){
					sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
					
					sourcePlayer.sendMessage(ChatColor.RED + "Whoosh!");
				}
				else{
					sourcePlayer.sendMessage(ChatColor.RED + "Unknown warp.");
				}
    		}
    		
    		setCommandHandled(event, true);
			return true;
    	}
    	else if(cmdData[0].equalsIgnoreCase("/setwarp")){
    		if(!canUseCommand(sourcePlayer, "/setwarp")){
    			// Error!
    			return true;
    		}
    		
    		if(cmdData.length > 1){
    			if(cmdData[1].equalsIgnoreCase("?")){
    				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /setwarp <warp name>");
    			}
    			else{
					WarpList list = getPlugin().getServerModWarps();
					
					list.set(cmdData[1], sourcePlayer.getLocation());
					
					sourcePlayer.sendMessage(ChatColor.RED + cmdData[1] + " warp has been set.");
    			}
    			
    			setCommandHandled(event, true);
    			return true;
    		}
    	}
    	else if(cmdData[0].equalsIgnoreCase("/warp")){
			if(!canUseCommand(sourcePlayer, "/warp")){
				// Error
				return true;
			}
			
			if(cmdData.length > 1){
				if(cmdData[1].equalsIgnoreCase("?")){
					sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /warp <warp name>");
				}
				else{
					WarpList list = getPlugin().getServerModWarps();
					
					WarpList.WarpPoint wp = list.get(cmdData[1]);
					
					if(wp != null){
						sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
						
						sourcePlayer.sendMessage(ChatColor.RED + "Whoosh!");
					}
					else{
						sourcePlayer.sendMessage(ChatColor.RED + "Unknown warp.");
					}
				}
				
				setCommandHandled(event, true);
				return true;
			}
		}
    	else if(cmdData[0].equalsIgnoreCase("/warplist") || cmdData[0].equalsIgnoreCase("/listwarps")){
    		if(!canUseCommand(sourcePlayer, "/warplist")){
    			// Error!
    			return true;
    		}
    		
    		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Returns a list of warps.");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /warplist");
    			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Alternatives: /listwarps");
    		}
    		else{
    			WarpList list = getPlugin().getServerModWarps();
    			WarpList.WarpPoint[] array = list.toArray();
    			
    			// Check for the empty case
    			if(array == null || array.length == 0){
    				sourcePlayer.sendMessage(ChatColor.GRAY + "No warps have been created to speak of.");
    				
    				setCommandHandled(event, true);
    				return true;
    			}
    			
    			String str = "";
    			for(int k = 0; k < array.length; k++){
    				str += array[k].Username;
    				
    				if(k < array.length - 1) str += ", ";
    			}
    			
    			sourcePlayer.sendMessage(ChatColor.GRAY + str);
    		}
    		
    		setCommandHandled(event, true);
    		return true;
    	}
		
		return false;
	}

}
