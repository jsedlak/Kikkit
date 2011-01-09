public class KikkitListener extends PluginListener{
	private Kikkit plugin;
	
	public KikkitListener(Kikkit plugin){
		this.plugin = plugin;
	}
	
	public void onArmSwing(Player player) {
        
    }
	
	public boolean onBlockPhysics(Block block, boolean placed) {
		//plugin.broadcast(block.blockType.name() + " physics, placed: " + placed);
		return false;
    }
	
	public boolean onFlow(Block blockFrom, Block blockTo) {
		//plugin.broadcast("flow from " + blockFrom.blockType.name() + " to " + blockTo.blockType.name());
        return false;
    }
	
	public boolean onBlockCreated(Player player, Block block) {
		//plugin.broadcast(Colors.Red + player.getName() + " has created " + block.blockType.name() + ".");
        return false;
    }
	
	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		//plugin.broadcast(Colors.Red + player.getName() + " has right clicked " + blockClicked.blockType.name() + " with item " + item.itemType.name() + ".");
		
		if(item.itemType == Item.Type.LavaBucket){
			item.setType(Item.Type.Torch);
		}
    }
	
	public boolean onComplexBlockChange(Player player, ComplexBlock block) {
		//plugin.broadcast(block.getBlock().blockType.name() + " was changed.");
        return false;
    }
	
	public boolean onSendComplexBlock(Player player, ComplexBlock block) {
		//plugin.broadcast(block.getBlock().blockType.name() + " was sent.");
        return false;
    }
	
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		//plugin.broadcast(Colors.Gold + player.getName() + " has placed " + blockPlaced.blockType.name() + " on " + blockClicked.blockType.name() + " using item " + itemInHand.itemType.name());
        if(itemInHand.itemType == Item.Type.LavaBucket || blockPlaced.blockType == Block.Type.Lava){
        	return onIgnite(blockPlaced, player);
        }
        
        return false;
    }
	
	public boolean onItemUse(Player player, Item item) {
		//plugin.broadcast(Colors.Gold + player.getName() + " has used " + item.itemType.name() + ".");
		
		if(item.itemType == Item.Type.LavaBucket){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(Colors.Red + player.getName() + " has tried using the lava bucket, but has been blocked!");
				return true;
			}
		}
        return false;
    }
	
	public boolean onItemPickUp(Player player, Item item) {
		//plugin.broadcast(Colors.Gold + player.getName() + " has picked up " + item.itemType.name() + ".");
		if(item.itemType == Item.Type.LavaBucket && !plugin.canPlayerIgnite(player)){
			//plugin.broadcast(Colors.Red + player.getName() + " has tried to pick up a lava bucket but has been blocked.");
			//return true;
		}
		return false;
	}
	
	public boolean onEquipmentChange(Player player) {
        return false;
    }
	
	/*
	 * Ignite - Returns true to block the ignition
	 * @see PluginListener#onIgnite(Block, Player)
	 */
	public boolean onIgnite(Block block, Player player){
		// This method gets called ever second, regardless of whether or not
		// an ignition is occuring. So block it out when there is no actual
		// ignite event.
		if(block == null || player == null) return true;
		
		// We will always allow furnace's to be ignited
		// TODO: Not sure if we need this?
		if(block.blockType == Block.Type.Furnace) return false;
		
		// Check if the player can ignite stuff
		if(!plugin.canPlayerIgnite(player)){
			plugin.broadcast(Colors.Red + player.getName() + " is trying to set something on fire!");
			return true;
		}
		return false;
	}
	
	public boolean onCommand(Player player, String [] split){
		if(split[0].equalsIgnoreCase("/fire")){
			if(!player.isAdmin()) return false;
			
			plugin.setFireListOverride(!plugin.getFireListOverride());
			
			if(plugin.getFireListOverride()) player.sendMessage(Colors.Red + "Firelist override is ON. Players can use fire.");
			else player.sendMessage(Colors.Red + "Firelist override is OFF. Players can't use fire.");
			
			return true;
		}
		
		if(split[0].equalsIgnoreCase("/wlon") || split[0].equalsIgnoreCase("/wloff")){
			if(!player.isAdmin()){
				//player.sendMessage(Colors.Red + "Unknown command.");
				return false;
			}
			
			boolean wloff = split[0].equalsIgnoreCase("/wloff");
			
			plugin.setWhitelistOverride(wloff);
			
			if(wloff) player.sendMessage(Colors.Red + "Whitelist override is now ON. (Whitelist is disabled, players can join freely)");
			else player.sendMessage(Colors.Red + "Whitelist override is now OFF.");
			
			return true;
		}
		
		return false;
	}
	
	public void onLogin(Player player){
		// If the plugin isn't enabled, then just return
		if(!plugin.getIsEnabled() || player == null) return;
		
		// If we are in a whiteout, check if the player can login
		if(plugin.getIsWhitelistEnabled()){
			// If the player can login, welcome him/her back and notify them that they are on the list.
			if(plugin.canPlayerLogin(player)){
				player.sendMessage(Colors.Gold + "Welcome back, " + player.getName() + ".");
				player.sendMessage(Colors.Gold + "We are currently in a whiteout, but you made the list!");
			}
			// Otherwise kick them.
			else{
				Kikkit.MinecraftLog.info(player.getName() + " has been kicked for not being on the temporary whitelist.");
				player.kick("You were kicked because you are not on the whitelist,\ncheck back in a few hours.");
				plugin.broadcast(Colors.Red + player.getName() + " was kicked for not being on the whitelist.");
			}
		}
		// If we are not in a whiteout, notify them of the rules.
		else{
			player.sendMessage(Colors.Gold + "Please respect others' property and no griefing.");
			player.sendMessage(Colors.Gold + "Fire and lava are in a whiteout.");
		}
		
		/*
		if(plugin.canLogin(player)){
			player.sendMessage(Colors.Gold + "Welcome back, " + player.getName() + ". You have been whitelisted.");
		}
		else{
			if(plugin.getIsWhitelistEnabled() && plugin.canLogin(player)){
				player.sendMessage(Colors.Yellow + "Welcome to our server. Please respect others' property and no griefing.");
			}
			else{
				//etc.getServer()
				EnterMod.logger.info(player.getName() + " has been kicked for not being on the temporary whitelist.");
				player.kick("You have been kicked because the server is currently using a whitelist that you are not on.");
				return;
			}
		}*/
		
		//plugin.broadcast(Colors.Green + player.getName() + " has joined the server!");
	}
}