
public class KikkitListener extends PluginListener{
	private Kikkit plugin;
	
	public KikkitListener(Kikkit plugin){
		this.plugin = plugin;
	}
	
	public boolean onItemUse(Player player, Block blockPlaced, Block blockCLicked, Item item) {
		//Kikkit.MinecraftLog.info(player.getName() + " has used " + item.itemType.name() + " with Id " + item.getItemId() + ".");
		
		if(item.getItemId() == ItemConstants.LavaBucketId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(Colors.Red + player.getName() + " has tried using the lava bucket, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.itemType.name() + " with Id " + item.getItemId() + ".");
				return true;
			}
		}
		
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
			
			Whitelist fireList = plugin.getFireWhitelist();

			if(split.length == 1){
				fireList.setIsOverriden(!fireList.getIsOverriden());
				
				if(fireList.getIsOverriden()) player.sendMessage(Colors.Red + "Firelist override is ON. Players can use fire.");
				else player.sendMessage(Colors.Red + "Firelist override is OFF. Players can't use fire.");
			}
			else if(split.length == 3){
				if(split[1].equalsIgnoreCase("add")){
					fireList.add(split[2]);
					player.sendMessage(Colors.Red + split[2] + " has been added to the Fire/Lava whitelist.");
				}
				else if(split[1].equalsIgnoreCase("remove")){
					fireList.remove(split[2]);
					player.sendMessage(Colors.Red + split[2] + " has been removed from the Fire/Lava whitelist.");
				}
			}
			
			return true;
		}
		
		if(split[0].equalsIgnoreCase("/tempwl")){
			if(!player.isAdmin()){
				//player.sendMessage(Colors.Red + "Unknown command.");
				return false;
			}
			
			TemporaryWhitelist tempList = plugin.getTemporaryWhitelist();
			
			if(split.length == 1){
				tempList.setIsOverriden(!tempList.getIsOverriden());
				
				if(tempList.getIsOverriden()) player.sendMessage(Colors.Red + "Whitelist override is now ON. (Players can join freely)");
				else player.sendMessage(Colors.Red + "Whitelist override is now OFF.");
			}
			else if(split.length == 3){
				if(split[1].equalsIgnoreCase("add")){
					tempList.add(split[2]);
					player.sendMessage(Colors.Red + split[2] + " has been added to the Temp whitelist.");
				}
				else if(split[1].equalsIgnoreCase("remove")){
					tempList.remove(split[2]);
					player.sendMessage(Colors.Red + split[2] + " has been removed from the Temp whitelist.");
				}
			}
			
			return true;
		}
		
		if(split[0].equalsIgnoreCase("/setsecret")){
			if(!player.isAdmin() || !player.isInGroup(Groups.Moderator) || !player.isInGroup(Groups.Vip)){
				return false;
			}
			
			WarpList list = plugin.getSecretWarpList();
			
			list.set(player.getName(), player.getX(), player.getY(), player.getZ());
			
			player.sendMessage(Colors.Red + "Secret warp has been set.");
			
			return true;
		}
		
		if(split[0].equalsIgnoreCase("/secret")){
			if(!player.isAdmin() || !player.isInGroup(Groups.Moderator) || !player.isInGroup(Groups.Vip)){
				return false;
			}
			
			WarpList list = plugin.getSecretWarpList();
			
			WarpList.WarpPoint wp = list.get(player.getName());
			
			if(wp != null){
				player.setX(wp.X);
				player.setY(wp.Y);
				player.setZ(wp.Z);
				
				player.sendMessage(Colors.Red + "Secret Whoosh!");
			}
			else{
				player.sendMessage(Colors.Red + "You must set a secret warp first with /setsecret");
			}
			return true;
		}
		
		return false;
	}
	
	public void onLogin(Player player){
		// If the plugin isn't enabled, then just return
		if(!plugin.getIsEnabled() || player == null) return;
		
		// If we are in a whiteout, check if the player can login
		if(plugin.getTemporaryWhitelist().getIsEnabled()){
			// If the player can login, welcome him/her back and notify them that they are on the list.
			if(plugin.canPlayerLogin(player)){
				player.sendMessage(Colors.Gold + "[" + Kikkit.getPluginName() + " by Kr1sc]");
				player.sendMessage(Colors.Gold + "Welcome back, " + player.getName() + ".");
				player.sendMessage(Colors.Gold + "We are currently in a whiteout, but you made the list!");
			}
			// Otherwise kick them.
			else{
				Kikkit.MinecraftLog.info(player.getName() + " has been kicked for not being on the temporary whitelist.");
				player.kick("You were kicked because you are not on the whitelist, check back in a few hours.");
				plugin.broadcast(Colors.Red + player.getName() + " was kicked for not being on the whitelist.");
			}
		}
		// If we are not in a whiteout, notify them of the rules.
		else{
			player.sendMessage(Colors.Gold + "[" + Kikkit.getPluginName() + " by Kr1sc]");
			player.sendMessage(Colors.Gold + "Please respect others' property and no griefing.");
			player.sendMessage(Colors.Gold + "Fire and lava are in a whiteout.");
		}
	}
}