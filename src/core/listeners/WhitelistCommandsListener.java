package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.CommandListener;
import core.Kikkit;
import core.TemporaryWhitelist;
import core.Whitelist;

public class WhitelistCommandsListener extends CommandListener {

	public WhitelistCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData,
			Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/fire")){
			if(!canUseCommand(sourcePlayer, "/fire")){
				// Error!
				return true;
			}
			
			Whitelist fireList = getPlugin().getFireWhitelist();

			if(cmdData.length == 1){
				fireList.setIsOverriden(!fireList.getIsOverriden());
				
				if(fireList.getIsOverriden()) sourcePlayer.sendMessage(ChatColor.RED + "Firelist override is ON. Players can use fire.");
				else sourcePlayer.sendMessage(ChatColor.RED + "Firelist override is OFF. Players can't use fire.");
				
				setCommandHandled(event, true);
			}
			else if(cmdData.length == 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /fire <add, remove, check> <player name>");
				
				setCommandHandled(event, true);
			}
			else if(cmdData.length == 3){
				if(cmdData[1].equalsIgnoreCase("add")){
					fireList.add(cmdData[2]);
					sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " has been added to the Fire/Lava whitelist.");
				}
				else if(cmdData[1].equalsIgnoreCase("remove")){
					fireList.remove(cmdData[2]);
					sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " has been removed from the Fire/Lava whitelist.");
				}
				else if(cmdData[1].equalsIgnoreCase("check")){
					if(fireList.has(cmdData[2])) sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " is on the Fire/Lava whitelist.");
					else sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " is NOT on the Fire/Lava whitelist.");
				}
				
				setCommandHandled(event, true);
			}
			
			return true;
		}
		else if(cmdData[0].equalsIgnoreCase("/tempwl")){
			if(!canUseCommand(sourcePlayer, "/tempwl")){
				// Error!
				return true;
			}
			
			TemporaryWhitelist tempList = getPlugin().getTemporaryWhitelist();
			
			if(cmdData.length == 1){
				tempList.setIsOverriden(!tempList.getIsOverriden());
				
				if(tempList.getIsOverriden()) sourcePlayer.sendMessage(ChatColor.RED + "Whitelist override is now ON. (Players can join freely)");
				else sourcePlayer.sendMessage(ChatColor.RED + "Whitelist override is now OFF.");
				
				setCommandHandled(event, true);
			}
			else if(cmdData.length == 2){
				if(cmdData[1].equalsIgnoreCase("?")){
					sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /tempwl <command> <player name>");
					sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Commands: add, remove, check");
				}
				
				setCommandHandled(event, true);
			}
			else if(cmdData.length == 3){
				if(cmdData[1].equalsIgnoreCase("add")){
					tempList.add(cmdData[2]);
					sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " has been added to the Temp whitelist.");
				}
				else if(cmdData[1].equalsIgnoreCase("remove")){
					tempList.remove(cmdData[2]);
					sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " has been removed from the Temp whitelist.");
				}
				else if(cmdData[1].equalsIgnoreCase("check")){
					if(tempList.has(cmdData[2])) sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " is on the Temp whitelist.");
					else sourcePlayer.sendMessage(ChatColor.RED + cmdData[2] + " is NOT on the Temp whitelist.");
				}
				
				setCommandHandled(event, true);
			}
			
			return true;
		}
		
		return false;
	}

}
