package core.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import core.Kikkit;
import core.Parser;
import core.economy.*;
import core.players.*;
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
		else if(cmdData[0].equalsIgnoreCase("/stock")){
			if(!canUseCommand(sourcePlayer, "/stock")){
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Restocks an item in the marketplace.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /stock <item id> <amount>");
				
				setCommandHandled(event, true);
				return true;
			}
			
			try{
				int itemId = Parser.TryParseInt(cmdData[1], -1);
				int amount = Parser.TryParseInt(cmdData[2], 500);
				
				if(itemId < 0){
					sourcePlayer.sendMessage(ChatColor.RED + "That item could not be found.");
					
					setCommandHandled(event, true);
					return true;
				}
				
				getPlugin().getMarket().getGoods(itemId).setAmount(amount);
				
				sourcePlayer.sendMessage(ChatColor.RED + "Set amount of " + itemId + " to " + amount);
			
			}
			catch(Exception ex){
				sourcePlayer.sendMessage(ChatColor.RED + "Incorrect usage.");
			}
			
			setCommandHandled(event, true);
			return true;
			
		}
		else if(cmdData[0].equalsIgnoreCase("/setprice") || cmdData[0].equalsIgnoreCase("/sp")){
			Kikkit.MinecraftLog.info("/setprice body");
			
			if(!canUseCommand(sourcePlayer, "/setprice")){
				Kikkit.MinecraftLog.info("Security error.");
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sets the price of an item.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /setprice <b, s> <item id> <price>");
				
				setCommandHandled(event, true);
				return true;
			}
				
			if(cmdData.length >= 4){
				int itemId = 0, price = 0 ;
				
				try{
					itemId = Integer.parseInt(cmdData[2]);
					price = Integer.parseInt(cmdData[3]);
				}
				catch(NumberFormatException nfe){
					sourcePlayer.sendMessage(ChatColor.RED + "Syntax error. Please check the usage for more information.");
					
					setCommandHandled(event, true);
					return true;
				}
				
				if(itemId <= 0 || price <= 0){
					sourcePlayer.sendMessage(ChatColor.RED + "Syntax error. Please check the usage for more information.");
					
					setCommandHandled(event, true);
					return true;
				}
				
				Market market = getMarket();
				MarketedGood good = market.getGoods(itemId);
				
				if(good == null){
					sourcePlayer.sendMessage(ChatColor.RED + "Syntax error. Please check the usage for more information.");
					
					setCommandHandled(event, true);
					return true;
				}
				
				if(cmdData[1].equalsIgnoreCase("b")) good.setBuyPrice(price);
				else if(cmdData[1].equalsIgnoreCase("s")) good.setSellPrice(price);
				
				market.save();
				
				sourcePlayer.sendMessage(ChatColor.RED + "Set price of " + itemId + " to " + price);
				
				setCommandHandled(event, true);
				return true;
			}
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
			
			Material itemMaterial = null;
			
			int itemId = Parser.TryParseInt(itemText, -1);
			
			if(itemId > 0) itemMaterial = Material.getMaterial(itemId);
			else itemMaterial = Parser.ParseMaterial(itemText);
			
			Market market = getPlugin().getMarket();
			
			if(itemMaterial == null) sourcePlayer.sendMessage(ChatColor.RED + "Unknown item.");
			else if(market.isBanned(itemMaterial.getId())) sourcePlayer.sendMessage(ChatColor.RED + "That item is banned.");
			else{
				if(type.equalsIgnoreCase("s"))
					sourcePlayer.sendMessage(ChatColor.RED + "The price is set at " + market.getGoods(itemMaterial.getId()).getSellPrice() + " " + market.getCurrencyName() + ".");
				else
					sourcePlayer.sendMessage(ChatColor.RED + "The price is set at " + market.getGoods(itemMaterial.getId()).getBuyPrice() + " " + market.getCurrencyName() + ".");
			}
			
			setCommandHandled(event, true);
			return true;
		}
		else if(cmdData[0].equalsIgnoreCase("/sell")){
			if(!canUseCommand(sourcePlayer, "/sell")){
				// Error!
				return true;
			}
			
			return HandleSale(event, cmdData, sourcePlayer);
		}
		else if(cmdData[0].equalsIgnoreCase("/buy")){
			if(!canUseCommand(sourcePlayer, "/buy")){
				// Error!
				return true;
			}
			
			return HandlePurchase(event, cmdData, sourcePlayer);
		}
		
		return false;
	}
	
	private boolean HandleSale(PlayerChatEvent event, String[] cmdData, Player sourcePlayer){
		if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sells an item in your inventory to the market.");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /sell <amount> <item id, item name>");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Example: /sell 64 cobblestone");
			
			setCommandHandled(event, true);
			return true;
		}
		
		if(cmdData.length >= 3){
			String itemText = getLastFromIndex(cmdData, 2);
			Market market = getPlugin().getMarket();
			
			Material itemMaterial = null;
			
			int amount = Parser.TryParseInt(cmdData[1], -1);
			int itemId = Parser.TryParseInt(itemText, -1);
			
			if(itemId > 0) itemMaterial = Material.getMaterial(itemId);
			else itemMaterial = Parser.ParseMaterial(itemText);
			
			if(itemMaterial == null){
				// TODO: Provide nearest results
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
			}
			else if(amount < 1){
				sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount. Did you mean /sell " + cmdData[2] + " " + cmdData[1] + "?");
				sourcePlayer.sendMessage(ChatColor.RED + "For more information: /sell ?");
			}
			else if(market.isBanned(itemMaterial.getId())){
				sourcePlayer.sendMessage(ChatColor.RED + "That item has been banned from the market.");
			}
			else{
				// Cap the amount to 64
				// TODO: Do we need to do this?
				// if(amount > 64) amount = 64;
				
				ItemStack itemStack = new ItemStack(itemMaterial.getId(), amount);
				
				// Get the inventory
				PlayerInventory inv = sourcePlayer.getInventory();
				
				// Try to remove the items
				HashMap<Integer, ItemStack> returnedHash = inv.removeItem(itemStack);
		
				// For all the returned stacks, we need to reduce the amount to sell
				for(Integer i : returnedHash.keySet()){
					ItemStack returnedStack = returnedHash.get(i);
					
					if(returnedStack.getTypeId() == itemMaterial.getId()){
						amount -= returnedStack.getAmount();
					}
				}
				
				// Get the goods
				MarketedGood goods = market.getGoods(itemMaterial.getId());
				
				int price = goods.getSellPrice();
				int amountSold = goods.sell(price, amount);
				
				// Return unsold items
				if(amountSold < amount){
					inv.addItem(new ItemStack(itemMaterial.getId(), amount - amountSold));
				}
				
				// Get the player data and update their credits
				PlayerData playerData = getPlugin().getPlayerManager().get(sourcePlayer.getName());
				playerData.setCredits(playerData.getCredits() + amountSold * price);
	
				sourcePlayer.sendMessage(ChatColor.RED + "Sold " + amountSold + " at a price of " + price + " " + getMarket().getCurrencyName());
			}
			
			setCommandHandled(event, true);
			return true;
		}
		
		return false;
	}
	
	private boolean HandlePurchase(PlayerChatEvent event, String[] cmdData, Player sourcePlayer){
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
			Market market = getPlugin().getMarket();
			
			Material itemMaterial = null;
			
			int amount = Parser.TryParseInt(cmdData[1], -1);
			int itemId = Parser.TryParseInt(itemText, -1);
			
			if(itemId > 0) itemMaterial = Material.getMaterial(itemId);
			else itemMaterial = Parser.ParseMaterial(itemText);
			
			if(itemMaterial == null){
				// TODO: Provide nearest results
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
			}
			else if(amount < 1){
				sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount. Did you mean /buy " + cmdData[2] + " " + cmdData[1] + "?");
				sourcePlayer.sendMessage(ChatColor.RED + "For more information: /buy ?");
			}
			else if(market.isBanned(itemMaterial.getId())){
				sourcePlayer.sendMessage(ChatColor.RED + "That item has been banned from the market.");
			}
			else{
				MarketedGood goods = market.getGoods(itemMaterial.getId());
				PlayerData playerData = getPlugin().getPlayerManager().get(sourcePlayer.getName());
				
				if(goods == null || goods.getAmount() == 0){
					sourcePlayer.sendMessage(ChatColor.RED + "Sold out of!");
					
					setCommandHandled(event, true);
					return true;
				}
				
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
				sourcePlayer.getInventory().addItem(new ItemStack(itemMaterial.getId(), amountSold));
			}
			
			setCommandHandled(event, true);
			return true;
		}
		
		return false;
	}
	
	private Market getMarket(){
		return getPlugin().getMarket();
	}

}
