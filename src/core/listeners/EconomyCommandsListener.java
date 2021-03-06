package core.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import core.CommandWrapper;
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
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		if(cmd.Sender instanceof Player) sourcePlayer = (Player)cmd.Sender;
		
		if(cmd.Name.equalsIgnoreCase("balance")){
			if(!canUseCommand(cmd.Sender, "balance")) return true;
			
			if(sourcePlayer == null){
				cmd.msg(ChatColor.RED + "You cannot use this command right now.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			PlayerManager pm = getPlugin().getPlayerManager();
			PlayerData pd = pm.get(sourcePlayer.getName());
			
			sourcePlayer.sendMessage(ChatColor.GREEN + "Your balance is " + pd.getCredits() + " " + getMarket().getCurrencyName() + ".");
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("stock")){
			if(!canUseCommand(cmd.Sender, "stock")) return true;
			
			try{
				int itemId = Parser.TryParseInt(cmd.Args[0], -1);
				int amount = Parser.TryParseInt(cmd.Args[1], 500);
				
				if(itemId < 0){
					cmd.msg(ChatColor.RED + "That item could not be found.");
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				getPlugin().getMarket().getGoods(itemId).setAmount(amount);
				cmd.msg(ChatColor.RED + "Set amount of " + itemId + " to " + amount);
			}
			catch(Exception ex){
				cmd.msg(ChatColor.RED + "Incorrect usage.");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("setprice") || cmd.Name.equalsIgnoreCase("sp")){
			if(!canUseCommand(cmd.Sender, "setprice")) return true;
			
			if(cmd.Args.length >= 3){
				int itemId = 0, price = 0 ;
				
				try{
					itemId = Integer.parseInt(cmd.Args[1]);
					price = Integer.parseInt(cmd.Args[2]);
				}
				catch(NumberFormatException nfe){
					cmd.msg(ChatColor.RED + "Syntax error. Please check the usage for more information.");
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				if(itemId <= 0 || price <= 0){
					cmd.msg(ChatColor.RED + "Syntax error. Please check the usage for more information.");
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				Market market = getMarket();
				MarketedGood good = market.getGoods(itemId);
				
				if(good == null){
					cmd.msg(ChatColor.RED + "Syntax error. Please check the usage for more information.");
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				if(cmd.Args[0].equalsIgnoreCase("b")) good.setBuyPrice(price);
				else if(cmd.Args[0].equalsIgnoreCase("s")) good.setSellPrice(price);
				
				market.save();
				
				cmd.msg(ChatColor.RED + "Set price of " + itemId + " to " + price);
			}
			else cmd.msg(ChatColor.RED + "Incorrect syntax.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		if(cmd.Name.equalsIgnoreCase("getprice") || cmd.Name.equalsIgnoreCase("gp")){
			if(!canUseCommand(cmd.Sender, "getprice")) return true;
			
			if(cmd.Args.length < 2){
				sourcePlayer.sendMessage(ChatColor.RED + "Incorrect syntax. Please see the usage by running /gp ?");
				return true;
			}
			
			String type = cmd.Args[0];
			String itemText = getLastFromIndex(cmd.Args, 1);
			
			Material itemMaterial = null;
			
			int itemId = Parser.TryParseInt(itemText, -1);
			
			if(itemId > 0) itemMaterial = Material.getMaterial(itemId);
			else itemMaterial = Parser.ParseMaterial(itemText);
			
			Market market = getPlugin().getMarket();
			
			if(itemMaterial == null) cmd.msg(ChatColor.RED + "Unknown item.");
			else if(market.isBanned(itemMaterial.getId())) cmd.msg(ChatColor.RED + "That item is banned.");
			else{
				if(type.equalsIgnoreCase("s"))
					cmd.msg(ChatColor.RED + "The price is set at " + market.getGoods(itemMaterial.getId()).getSellPrice() + " " + market.getCurrencyName() + ".");
				else
					cmd.msg(ChatColor.RED + "The price is set at " + market.getGoods(itemMaterial.getId()).getBuyPrice() + " " + market.getCurrencyName() + ".");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		

		else if(cmd.Name.equalsIgnoreCase("sell")){
			if(!canUseCommand(cmd.Sender, "sell") || sourcePlayer == null) return true;
			
			return HandleSale(cmd, cmd.Args, sourcePlayer);
		}
		else if(cmd.Name.equalsIgnoreCase("buy")){
			if(!canUseCommand(sourcePlayer, "/buy") || sourcePlayer == null) return true;
			
			return HandlePurchase(cmd, cmd.Args, sourcePlayer);
		}
		
		return false;
	}
	
	private boolean HandleSale(CommandWrapper cmd, String[] cmdData, Player sourcePlayer){
		if(cmdData.length == 1 && cmdData[0].equalsIgnoreCase("?")){
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Sells an item in your inventory to the market.");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /sell <amount> <item id, item name>");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Example: /sell 64 cobblestone");
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		if(cmdData.length >= 2){
			String itemText = getLastFromIndex(cmdData, 1);
			Market market = getPlugin().getMarket();
			
			Material itemMaterial = null;
			
			int amount = Parser.TryParseInt(cmdData[0], -1);
			int itemId = Parser.TryParseInt(itemText, -1);
			
			if(itemId > 0) itemMaterial = Material.getMaterial(itemId);
			else itemMaterial = Parser.ParseMaterial(itemText);
			
			if(itemMaterial == null){
				// TODO: Provide nearest results
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
			}
			else if(amount < 1){
				sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount. Did you mean /sell " + cmdData[1] + " " + cmdData[0] + "?");
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
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		return false;
	}
	
	private boolean HandlePurchase(CommandWrapper cmd, String[] cmdData, Player sourcePlayer){
		if(cmdData.length == 1 && cmdData[0].equalsIgnoreCase("?")){
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Buys an item in from the market.");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /buy <amount> <item id, item name>");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] To get price: /gp b <item id, item name>");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Example: /buy 64 cobblestone");
			sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] NOTE: You are responsible for having the necessary free space. No refunds.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		if(cmdData.length >= 2){
			String itemText = getLastFromIndex(cmdData, 1);
			Market market = getPlugin().getMarket();
			
			Material itemMaterial = null;
			
			int amount = Parser.TryParseInt(cmdData[0], -1);
			int itemId = Parser.TryParseInt(itemText, -1);
			
			if(itemId > 0) itemMaterial = Material.getMaterial(itemId);
			else itemMaterial = Parser.ParseMaterial(itemText);
			
			if(itemMaterial == null){
				// TODO: Provide nearest results
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown item or material.");
			}
			else if(amount < 1){
				sourcePlayer.sendMessage(ChatColor.RED + "Couldn't parse the amount. Did you mean /buy " + cmdData[1] + " " + cmdData[0] + "?");
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
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				int price = goods.getBuyPrice();
				
				if(price * amount > playerData.getCredits()) amount = (int) (playerData.getCredits() / price);
				if(amount <= 0){
					sourcePlayer.sendMessage(ChatColor.RED + "You do not have enough " + getMarket().getCurrencyName() + ".");
					
					setCommandHandled(cmd, true);
					return true;
				}
				
				int amountSold = goods.buy(price, amount);

				// TODO: Custom prices
				playerData.setCredits(playerData.getCredits() - amountSold * price);

				sourcePlayer.sendMessage(ChatColor.RED + "Bought " + amountSold + " at a price of " + price + " " + getMarket().getCurrencyName());
				sourcePlayer.getInventory().addItem(new ItemStack(itemMaterial.getId(), amountSold));
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		
		return false;
	}
	
	private Market getMarket(){
		return getPlugin().getMarket();
	}

}
