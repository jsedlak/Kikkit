package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;
import core.WarpList;

public class TeleportCommandsListener extends CommandListener {

	public TeleportCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("warpto")){
			if(!canUseCommand(cmd.Sender, "warpto")) return true;
			
			if(cmd.Args.length < 2 && sourcePlayer == null){
				cmd.msg(ChatColor.RED + "Cannot warp, please specify a player.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			Player playerToWarp = sourcePlayer;
			
			if(cmd.Args.length >= 2) playerToWarp = getServer().getPlayer(cmd.Args[0]);
			
			WarpList wl = getPlugin().getServerModWarps();
			WarpList.WarpPoint wp = wl.get(cmd.Args[cmd.Args.length - 1]);
			
			if(playerToWarp == null) cmd.msg(ChatColor.RED + "Unknown player.");
			else {
				playerToWarp.sendMessage(ChatColor.RED + "Pooooosh!");
				cmd.msg(ChatColor.RED + "Player has been warped.");
				
				playerToWarp.teleportTo(wp.getLocation());
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("tphere") || cmd.Name.equalsIgnoreCase("tph")){
			if(!canUseCommand(cmd.Sender, "tphere")) return true;
			
			if(sourcePlayer == null){
				cmd.msg(ChatColor.RED + "Can't teleport here, use /tp instead.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			Player source = getServer().getPlayer(cmd.Args[0]);
			Player destination = sourcePlayer;
			
			if(source != null && destination != null){
				if(source.getName().equalsIgnoreCase(destination.getName())){
					getPlugin().broadcast(ChatColor.RED + sourcePlayer.getName() + " tried to teleport their self to their self.");
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				source.teleportTo(destination);
				
				source.sendMessage(ChatColor.RED + "Telepooooorsh!");
				sourcePlayer.sendMessage(ChatColor.RED + "Teleported player.");
				
				Kikkit.MinecraftLog.info(sourcePlayer.getName() + " made " + source.getName() + " teleport to " + destination.getName());
			}
			else if(source == null){
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown player.");
			}
		}
		else if(cmd.Name.equalsIgnoreCase("tp")){
			if(cmd.Args.length < 1){
				cmd.msg("Incorrect usage, please check your syntax.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			Player destination = getServer().getPlayer(cmd.Args[0]);
			
			Player source = sourcePlayer;
			if(cmd.Args.length >= 2) source = getServer().getPlayer(cmd.Args[1]);
			
			if(source != null && destination != null){
				source.teleportTo(destination);
				
				source.sendMessage(ChatColor.RED + "Telepooooorsh!");
				cmd.msg(ChatColor.RED + "Teleported player.");
				
				if(sourcePlayer != null)
					Kikkit.MinecraftLog.info(sourcePlayer.getName() + " made " + source.getName() + " teleport to " + destination.getName());
			}
			else{
				cmd.msg(ChatColor.RED + "Unknown player.");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}

		
		return false;
	}

}
