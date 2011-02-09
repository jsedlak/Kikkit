package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;
import core.TemporaryWhitelist;
import core.Whitelist;

public class WhitelistCommandsListener extends CommandListener {

	public WhitelistCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("fire")){
			if(!canUseCommand(cmd.Sender, "fire")) return true;
			
			Whitelist fireList = getPlugin().getFireWhitelist();

			if(cmd.Args.length == 0){
				fireList.setIsOverriden(!fireList.getIsOverriden());
				
				if(fireList.getIsOverriden()) sourcePlayer.sendMessage(ChatColor.RED + "Firelist override is ON. Players can use fire.");
				else sourcePlayer.sendMessage(ChatColor.RED + "Firelist override is OFF. Players can't use fire.");
				
				setCommandHandled(cmd, true);
			}
			else if(cmd.Args.length == 2){
				if(cmd.Args[0].equalsIgnoreCase("add")){
					fireList.add(cmd.Args[1]);
					sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " has been added to the Fire/Lava whitelist.");
				}
				else if(cmd.Args[0].equalsIgnoreCase("remove")){
					fireList.remove(cmd.Args[1]);
					sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " has been removed from the Fire/Lava whitelist.");
				}
				else if(cmd.Args[0].equalsIgnoreCase("check")){
					if(fireList.has(cmd.Args[1])) sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " is on the Fire/Lava whitelist.");
					else sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " is NOT on the Fire/Lava whitelist.");
				}
				
				setCommandHandled(cmd, true);
			}
			
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("tempwl") || cmd.Name.equalsIgnoreCase("wl")){
			if(!canUseCommand(cmd.Sender, "tempwl")) return true;
			
			TemporaryWhitelist tempList = getPlugin().getTemporaryWhitelist();
			
			if(cmd.Args.length == 0){
				tempList.setIsOverriden(!tempList.getIsOverriden());
				
				if(tempList.getIsOverriden()) sourcePlayer.sendMessage(ChatColor.RED + "Whitelist override is now ON. (Players can join freely)");
				else sourcePlayer.sendMessage(ChatColor.RED + "Whitelist override is now OFF.");
				
				setCommandHandled(cmd, true);
			}
			else if(cmd.Args.length == 2){
				if(cmd.Args[0].equalsIgnoreCase("add")){
					tempList.add(cmd.Args[1]);
					sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " has been added to the Temp whitelist.");
				}
				else if(cmd.Args[0].equalsIgnoreCase("remove")){
					tempList.remove(cmd.Args[1]);
					sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " has been removed from the Temp whitelist.");
				}
				else if(cmd.Args[0].equalsIgnoreCase("check")){
					if(tempList.has(cmd.Args[1])) sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " is on the Temp whitelist.");
					else sourcePlayer.sendMessage(ChatColor.RED + cmd.Args[1] + " is NOT on the Temp whitelist.");
				}
				
				setCommandHandled(cmd, true);
			}
			
			return true;
		}
		
		return false;
	}

}
