package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;
import core.WarpList;

public class PersonalWarpCommandsListener extends CommandListener {

	public PersonalWarpCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/setsecret")){
			if(!canUseCommand(sourcePlayer, "/setsecret")) {
				// Error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /setsecret");
				
				setCommandHandled(event, true);
				return true;
			}
			
			WarpList list = getPlugin().getSecretWarpList();
			
			list.set(sourcePlayer.getName(), sourcePlayer.getLocation());
			
			sourcePlayer.sendMessage(ChatColor.RED + "Secret warp has been set.");

			setCommandHandled(event, true);
			return true;
		}
    	else if(cmdData[0].equalsIgnoreCase("/secret")){
			if(!canUseCommand(sourcePlayer, "/secret")){
				// Error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /secret");
				
				setCommandHandled(event, true);
				return true;
			}
			
			WarpList list = getPlugin().getSecretWarpList();
			
			WarpList.WarpPoint wp = list.get(sourcePlayer.getName());
			
			if(wp != null){
				sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
				
				sourcePlayer.sendMessage(ChatColor.RED + "Secret Whoosh!");
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "You must set a secret warp first with /setsecret");
			}
			
			setCommandHandled(event, true);
			return true;
		}
    	else if(cmdData[0].equalsIgnoreCase("/sethome")){
			if(!canUseCommand(sourcePlayer, "/sethome")) {
				// Error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /sethome");
			}
			else{
				WarpList list = getPlugin().getHomeWarpList();
				
				list.set(sourcePlayer.getName(), sourcePlayer.getLocation());
				
				sourcePlayer.sendMessage(ChatColor.RED + "Home warp has been set.");
			}
			
			setCommandHandled(event, true);
			return true;
		}
    	else if(cmdData[0].equalsIgnoreCase("/home")){
			if(!canUseCommand(sourcePlayer, "/home")){
				// Error!
				return false;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /home");
			}
			else {
				WarpList list = getPlugin().getHomeWarpList();
				
				WarpList.WarpPoint wp = list.get(sourcePlayer.getName());
				
				if(wp != null){
					sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
					
					sourcePlayer.sendMessage(ChatColor.RED + "Whoosh to home!");
				}
				else{
					sourcePlayer.sendMessage(ChatColor.RED + "You must set a home warp first with /sethome");
				}
    		}
			
			setCommandHandled(event, true);
			return true;
		}
		
		return false;
	}

}
