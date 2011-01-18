package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import core.CommandListener;
import core.Kikkit;
import core.bukkit.ItemConstants;

public class ItemCommandsListener extends CommandListener {

	public ItemCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/item") || cmdData[0].equalsIgnoreCase("/i")){
			if(!canUseCommand(sourcePlayer, "/item")){
				// Error (want the user to see "Unknown command")
				return true;
			}
			
			// Check for usage question
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Gives an item to a player.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /item <item id> <amount> [player name]");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Shortcuts: /i");
				
				setCommandHandled(event, true);
				return true;
			}
			
			// /item <id> <amount> [player] = 4
			if(cmdData.length >= 3){
				// Get the player
				Player playerToGiveTo = sourcePlayer;
				
				// If a player is specified, try to parse the player name
				if(cmdData.length >= 4) playerToGiveTo = getServer().getPlayer(cmdData[3]);
				
				int id = Integer.parseInt(cmdData[1]);
				int amount = Integer.parseInt(cmdData[2]);
				
				if(playerToGiveTo != null){
					ItemStack itemStack = new ItemStack(id, amount);
					playerToGiveTo.getInventory().addItem(itemStack);
					
					sourcePlayer.sendMessage(ChatColor.RED + "Gift given!");
					if(!sourcePlayer.getName().equalsIgnoreCase(playerToGiveTo.getName())) playerToGiveTo.sendMessage(ChatColor.RED + "Enjoy your gift!");
					
					Kikkit.MinecraftLog.info(sourcePlayer.getName() + " gave " + id + " to " + playerToGiveTo.getName());
					
					setCommandHandled(event, true);
					return true;
				}
			}
		}
		else if(cmdData[0].equalsIgnoreCase("/getid")){
			if(!canUseCommand(sourcePlayer, "/getid")){
				// Errror!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Retrieves the internal Id for a Minecraft item.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /getid <item name>");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Example: /getid cobblestone");
				
				setCommandHandled(event, true);
				return true;
			}
			
			if(cmdData.length >= 2){
				int id = ItemConstants.ConvertToId(getLastFromIndex(cmdData, 1));
				
				Kikkit.MinecraftLog.info(sourcePlayer.getName() + " is looking for the id of " + getLastFromIndex(cmdData, 1));
				
				if(id < 0) sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
				else sourcePlayer.sendMessage(ChatColor.RED + "The Id for " + cmdData[1] + " is " + id);
				
				setCommandHandled(event, true);
				return true;
			}
		}
		
		return false;
	}
}
