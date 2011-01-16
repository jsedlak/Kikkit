package core.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import core.CommandListener;
import core.Kikkit;

public class ItemCommandsListener extends CommandListener {

	public ItemCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/item")){
			if(!canUseCommand(sourcePlayer, "/item")){
				// Error (want the user to see "Unknown command")
				return true;
			}
			
			// Check for usage question
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /item <item id> <amount> [player name]");
				
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
					
					Kikkit.MinecraftLog.info(sourcePlayer.getName() + " gave " + id + " to " + playerToGiveTo.getName());
					
					setCommandHandled(event, true);
					return true;
				}
			}
		}
		
		return false;
	}
}
