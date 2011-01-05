public class KikkitListener extends PluginListener{
	private Kikkit plugin;
	
	public KikkitListener(Kikkit plugin){
		this.plugin = plugin;
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
			plugin.broadcast(Colors.Red + player.getName() + " has tried igniting something, but has been blocked! ATTACK THEM!");
			return true;
		}
		return false;
	}
	
	public boolean onCommand(Player player, String [] split){
		if(split[0].equalsIgnoreCase("/wlon") || split[0].equalsIgnoreCase("/wloff")){
			if(!player.isAdmin()){
				player.sendMessage(Colors.Red + "Unknown command.");
				return false;
			}
			
			boolean wlon = split[0].equalsIgnoreCase("/wlon");
			
			plugin.setWhitelistOverride(!wlon);
			
			if(!wlon) player.sendMessage(Colors.Red + "Whitelist override is now ON. (Whitelist is disabled)");
			else player.sendMessage(Colors.Red + "Whitelist override is now OFF.");
			
			return true;
		}
		
		return true;
	}
	
	public void onLogin(Player player){
		// If the plugin isn't enabled, then just return
		if(!plugin.getIsEnabled() || player == null) return;
		
		// If we are in a whiteout, check if the player can login
		if(plugin.getIsWhitelistEnabled()){
			// If the player can login, welcome him/her back and notify them that they are on the list.
			if(plugin.canPlayerLogin(player)){
				player.sendMessage(Colors.Gold + "Welcome back, " + player.getName() + ". You have been whitelisted.");
			}
			// Otherwise kick them.
			else{
				Kikkit.MinecraftLog.info(player.getName() + " has been kicked for not being on the temporary whitelist.");
				player.kick("You have been kicked because the server is currently using a whitelist that you are not on.");
			}
		}
		// If we are not in a whiteout, notify them of the rules.
		else{
			player.sendMessage(Colors.Gold + "Welcome to our server. Please respect others' property and no griefing. Fire and lava are in a whiteout.");
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
		
		plugin.broadcast(Colors.Green + player.getName() + " has joined the server!");
	}
}