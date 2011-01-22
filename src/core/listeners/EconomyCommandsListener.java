package core.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import core.Kikkit;
import core.economy.*;
import core.players.*;
import core.bukkit.*;
import core.CommandListener;

public class EconomyCommandsListener extends CommandListener {

	public EconomyCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/balance")){
			if(!canUseCommand(sourcePlayer, "/balance")){
				// Security error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Checks your current balance.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /balance");
				
				setCommandHandled(event, true);
				return true;
			}
			
			PlayerManager pm = getPlugin().getPlayerManager();
			PlayerData pd = pm.get(sourcePlayer.getName());
			
			sourcePlayer.sendMessage(ChatColor.GREEN + "Your balance is " + pd.getCredits() + " " + getMarket().getCurrencyName() + ".");
			
			setCommandHandled(event, true);
			return true;
		}
		else if(cmdData[0].equalsIgnoreCase("/getprice") || cmdData[0].equalsIgnoreCase("/gp")){
			if(!canUseCommand(sourcePlayer, "/getprice")){
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Returns the price of an item.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /getprice [b,s] cobblestone");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Shortcuts: /gp [b, s]");
				
				setCommandHandled(event, true);
				return true;
			}
			
			if(cmdData.length < 3){
				sourcePlayer.sendMessage(ChatColor.RED + "Incorrect syntax. Please see the usage by running /gp ?");
				return true;
			}
			
			String type = cmdData[1];
			String itemText = getLastFromIndex(cmdData, 2);
			int itemId;
			
			try{
				itemId = Integer.parseInt(itemText);
			}
			catch(Exception ex){
				itemId = ItemConstants.ConvertToId(itemText);
			}
			
			// Try one last time
			if(itemId < 0) itemId = ItemConstants.ConvertToId(itemText);
			
			if(itemId < 0){
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown item.");
				
				setCommandHandled(event, true);
				return true;
			}

			if(type.equalsIgnoreCase("s"))
				sourcePlayer.sendMessage(ChatColor.RED + "The price is set at " + getPlugin().getMarket().getGoods(itemId).getSellPrice() + " " + getMarket().getCurrencyName() + ".");
			else
				sourcePlayer.sendMessage(ChatColor.RED + "The price is set at " + getPlugin().getMarket().getGoods(itemId).getBuyPrice() + " " + getMarket().getCurrencyName() + ".");
			
			setCommandHandled(event, true);
			return true;
		}
		else if(cmdData[0].equalsIgnoreCase("/sell")){
			if(!canUseCommand(sourcePlayer, "/sell")){
				// Error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sells an item in your inventory to the market.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /sell <amount> <item id, item name>");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Example: /sell 64 cobblestone");
				
				setCommandHandled(event, true);
				return true;
			}
			
			if(cmdData.length >= 3){
				String itemText = getLastFromIndex(cmdData, 2);
				int amount;
				int itemId;
				
				try{
					itemId = Integer.parseInt(itemText);
				}
				catch(Exception ex){
					itemId = ItemConstants.ConvertToId(itemText);
				}
				
				// Try one last time
				if(itemId < 0) itemId = ItemConstants.ConvertToId(itemText);
				
				if(itemId < 0){
					sourcePlayer.sendMessage(ChatColor.RED + "Unknown item.");
					
					setCommandHandled(event, true);
					return true;
				}
				
				try{
					amount = Integer.parseInt(cmdData[1]);
				}
				catch(NumberFormatException nfe){
					sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount. Did you mean /sell " + cmdData[2] + " " + cmdData[1] + "?");
					sourcePlayer.sendMessage(ChatColor.RED + "For more information: /sell ?");
					
					setCommandHandled(event, true);
					return true;
				}
				
				if(amount <= 0){
					sourcePlayer.sendMessage(ChatColor.RED + "Invalid amount.");
					
					setCommandHandled(event, true);
					return true;
				}
				else if(amount > 64) amount = 64;
				
				//sourcePlayer.sendMessage(ChatColor.RED + "This isn't finished yet.");
				
				ItemStack itemStack = new ItemStack(itemId, amount);
				
				PlayerInventory inv = sourcePlayer.getInventory();
				
				// Try to remove the items
				HashMap<Integer, ItemStack> returnedHash = inv.removeItem(itemStack);
		
				for(Integer i : returnedHash.keySet()){
					//sourcePlayer.sendMessage(ChatColor.DARK_PURPLE + i.toString() + ": " + returnedHash.get(i).getType().name() + "(" + returnedHash.get(i).getAmount() + ")");
					ItemStack returnedStack = returnedHash.get(i);
					
					if(returnedStack.getTypeId() == itemId){
						amount -= returnedStack.getAmount();
					}
				}
				
				Market market = getPlugin().getMarket();
				MarketedGood goods = market.getGoods(itemId);
				
				int price = goods.getSellPrice();
				int amountSold = goods.sell(price, amount);
				
				// Return unsold items
				
				if(amountSold < amount){
					inv.addItem(new ItemStack(itemId, amount - amountSold));
				}
				
				PlayerData playerData = getPlugin().getPlayerManager().get(sourcePlayer.getName());

				// TODO: Custom prices
				playerData.setCredits(playerData.getCredits() + amountSold * price);

				sourcePlayer.sendMessage(ChatColor.RED + "Sold " + amountSold + " at a price of " + price + " " + getMarket().getCurrencyName());
				
				setCommandHandled(event, true);
				return true;
			}
		}
		else if(cmdData[0].equalsIgnoreCase("/buy")){
			if(!canUseCommand(sourcePlayer, "/buy")){
				// Error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Buys an item in from the market.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /buy <amount> <item id, item name>");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] To get price: /gp b <item id, item name>");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Example: /buy 64 cobblestone");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] NOTE: You are responsible for having the necessary free space. No refunds.");
				
				setCommandHandled(event, true);
				return true;
			}
			
			if(cmdData.length >= 3){
				String itemText = getLastFromIndex(cmdData, 2);
				int amount;
				int itemId;
				
				try{
					itemId = Integer.parseInt(itemText);
				}
				catch(Exception ex){
					itemId = ItemConstants.ConvertToId(itemText);
				}
				
				// Try one last time
				if(itemId < 0) itemId = ItemConstants.ConvertToId(itemText);
				
				if(itemId < 0){
					sourcePlayer.sendMessage(ChatColor.RED + "Unknown item.");
					
					setCommandHandled(event, true);
					return true;
				}
				
				try{
					amount = Integer.parseInt(cmdData[1]);
				}
				catch(NumberFormatException nfe){
					sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount. Did you mean /sell " + cmdData[2] + " " + cmdData[1] + "?");
					sourcePlayer.sendMessage(ChatColor.RED + "For more information: /sell ?");
					
					setCommandHandled(event, true);
					return true;
				}
				
				if(amount <= 0){
					sourcePlayer.sendMessage(ChatColor.RED + "Invalid amount.");
					
					setCommandHandled(event, true);
					return true;
				}
				else if(amount > 64) amount = 64;
				
				Market market = getPlugin().getMarket();
				MarketedGood goods = market.getGoods(itemId);
				PlayerData playerData = getPlugin().getPlayerManager().get(sourcePlayer.getName());
				
				int price = goods.getBuyPrice();
				
				if(price * amount > playerData.getCredits()) amount = (int) (playerData.getCredits() / price);
				if(amount <= 0){
					sourcePlayer.sendMessage(ChatColor.RED + "You do not have enough " + getMarket().getCurrencyName() + ".");
					
					setCommandHandled(event, true);
					return true;
				}
				
				int amountSold = goods.buy(price, amount);
				
				

				// TODO: Custom prices
				playerData.setCredits(playerData.getCredits() - amountSold * price);

				sourcePlayer.sendMessage(ChatColor.RED + "Bought " + amountSold + " at a price of " + price + " " + getMarket().getCurrencyName());
				sourcePlayer.getInventory().addItem(new ItemStack(itemId, amountSold));
				
				setCommandHandled(event, true);
				return true;
			}
		}
		
		return false;
	}
	
	private Market getMarket(){
		return getPlugin().getMarket();
	}

}
