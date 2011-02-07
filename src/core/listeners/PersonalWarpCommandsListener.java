package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;
import core.WarpList;

public class PersonalWarpCommandsListener extends CommandListener {

	public PersonalWarpCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("/setsecret")){
			if(sourcePlayer == null){
				Kikkit.MinecraftLog.info("Nonplayer tried to run a player specific command: " + cmd.Name + ".");
				return true;
			}
			
			if(!canUseCommand(cmd.Sender, "/setsecret")) return true;
			
			WarpList list = getPlugin().getSecretWarpList();
			
			list.set(sourcePlayer.getName(), sourcePlayer.getLocation());
			
			sourcePlayer.sendMessage(ChatColor.RED + "Secret warp has been set.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/secret")){
			if(sourcePlayer == null){
				Kikkit.MinecraftLog.info("Nonplayer tried to run a player specific command: " + cmd.Name + ".");
				return true;
			}
			
			if(!canUseCommand(cmd.Sender, "/secret")) return true;
			
			WarpList list = getPlugin().getSecretWarpList();
			
			WarpList.WarpPoint wp = list.get(sourcePlayer.getName());
			
			if(wp != null){
				sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
				
				sourcePlayer.sendMessage(ChatColor.RED + "Secret Whoosh!");
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "You must set a secret warp first with /setsecret");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/sethome")){
			if(sourcePlayer == null){
				Kikkit.MinecraftLog.info("Nonplayer tried to run a player specific command: " + cmd.Name + ".");
				return true;
			}
			
			if(!canUseCommand(cmd.Sender, "/sethome")) return true;
			
			WarpList list = getPlugin().getHomeWarpList();
			
			list.set(sourcePlayer.getName(), sourcePlayer.getLocation());
			
			sourcePlayer.sendMessage(ChatColor.RED + "Home warp has been set.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/home")){
			if(sourcePlayer == null){
				Kikkit.MinecraftLog.info("Nonplayer tried to run a player specific command: " + cmd.Name + ".");
				return true;
			}
			
			if(!canUseCommand(cmd.Sender, "/home")) return true;
			
			WarpList list = getPlugin().getHomeWarpList();
			
			WarpList.WarpPoint wp = list.get(sourcePlayer.getName());
			
			if(wp != null){
				sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
				
				sourcePlayer.sendMessage(ChatColor.RED + "Whoosh to home!");
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "You must set a home warp first with /sethome");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		return false;
	}

}
