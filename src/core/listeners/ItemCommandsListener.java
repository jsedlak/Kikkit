package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;
import core.Parser;

public class ItemCommandsListener extends CommandListener {

	public ItemCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("/item") || cmd.Name.equalsIgnoreCase("/i")){
			if(!canUseCommand(cmd.Sender, "/item")) return true;
			
			Player playerToGiveTo = sourcePlayer;
			int amount = 1;
			int id;
			
			if(cmd.Args.length == 0){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the arguments. Please check your syntax.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			try{
				id = Integer.parseInt(cmd.Args[0]);
			}
			catch(NumberFormatException ex){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the item Id. Please check your syntax.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			// If a player is specified, try to retrieve them
			if(cmd.Args.length >= 3) playerToGiveTo = getServer().getPlayer(cmd.Args[2]);
			if(cmd.Args.length >= 2) {
				try{
					amount = Integer.parseInt(cmd.Args[1]);
				}
				catch(NumberFormatException ex){
					if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount, using 1 instead.");
					amount = 1;
				}
				
				if(amount < 1) amount = 1;
			}
			
			if(playerToGiveTo == null){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Can't find that player.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			ItemStack itemStack = new ItemStack(id, amount);
			
			// Give the item
			playerToGiveTo.getInventory().addItem(itemStack);
			
			// Send messages
			if(sourcePlayer != null){
				sourcePlayer.sendMessage(ChatColor.RED + "Gift given!");
			}
			
			if(sourcePlayer == null || !sourcePlayer.getName().equalsIgnoreCase(playerToGiveTo.getName())) 
				playerToGiveTo.sendMessage(ChatColor.RED + "Enjoy your gift!");
			
			Kikkit.MinecraftLog.info(sourcePlayer.getName() + " gave " + id + " to " + playerToGiveTo.getName());
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/getid")){
			if(!canUseCommand(cmd.Sender, "/getid")) return true;
			
			String itemName = getLastFromIndex(cmd.Args, 0);

			Material material = Parser.ParseMaterial(itemName);
			
			if(material == null){
				if(sourcePlayer != null) sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			int id = material.getId();
			
			Kikkit.MinecraftLog.info(sourcePlayer.getName() + " is looking for the id of " + itemName + " (" + id + ")");
			
			if(sourcePlayer != null){
				if(id < 0) sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
				else sourcePlayer.sendMessage(ChatColor.RED + "The Id for " + itemName + " is " + id);
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		return false;
	}
}
