import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class KikkitPlayerListener extends PlayerListener {
	private Kikkit plugin;
	private KickCounter igniteKickCounter;
	
	public KikkitPlayerListener(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
		
		igniteKickCounter = plugin.getIgnitionKickCounter();
	}
	
	private void setCommandHandled(PlayerChatEvent event, boolean wasCommandHandled){
		if(wasCommandHandled){
			event.setCancelled(true);
		}
		
	}
	
	public void onPlayerChat(PlayerChatEvent event){
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		if(plugin.getSecurityManager().isInGroup(player, Groups.Admin)){			
			String playerText = "<" + ChatColor.RED + event.getPlayer().getName() + ChatColor.WHITE + "> ";
			
			Kikkit.MinecraftLog.info("<" + player.getName() + "> " + msg);
			
			//event.setMessage(msg);
			event.setCancelled(true);
			
			plugin.broadcast(playerText + msg);
		}
	}
	
	public void onPlayerItem(PlayerItemEvent event){
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		
		if(item.getTypeId() == ItemConstants.LavaBucketId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(ChatColor.RED + player.getName() + " has tried using the lava bucket, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.getType().name() + " with Id " + item.getTypeId() + ".");
				
				if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
					player.kickPlayer("You have been kicked for attempting to grief.");
					plugin.broadcast(ChatColor.DARK_PURPLE + player.getName() + " has been kicked for trying to place lava.");
				}
				
				event.setCancelled(true);
			}
		}
		else if(item.getTypeId() == ItemConstants.TntId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(ChatColor.RED + player.getName() + " has tried placing TNT, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.getType().name() + " with Id " + item.getTypeId() + ".");
				
				if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
					player.kickPlayer("You have been kicked for attempting to grief.");
					plugin.broadcast(ChatColor.DARK_PURPLE + player.getName() + " has been kicked for trying to use TNT.");
				}
				
				event.setCancelled(true);
			}
		}
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event){
		Player player = event.getPlayer();
		
		// If the plugin isn't enabled, then just return
		if(!plugin.getIsEnabled() || player == null) return;
		
		// If we are in a whiteout, check if the player can login
		if(plugin.getTemporaryWhitelist().getIsEnabled()){
			// If the player can login, welcome him/her back and notify them that they are on the list.
			if(plugin.canPlayerLogin(player)){
				player.sendMessage(ChatColor.GOLD + "[" + Kikkit.getPluginName() + " by Kr1sc]");
				player.sendMessage(ChatColor.GOLD + "Welcome back, " + player.getName() + ".");
				player.sendMessage(ChatColor.GOLD + "We are currently in a whiteout, but you made the list!");
			}
			// Otherwise kick them.
			else{
				Kikkit.MinecraftLog.info(player.getName() + " has been kicked for not being on the temporary whitelist.");
				player.kickPlayer("You were kicked because you are not on the whitelist, check back in a few hours.");
				plugin.broadcast(ChatColor.GOLD + player.getName() + " was kicked for not being on the whitelist.");
			}
		}
		// If we are not in a whiteout, notify them of the rules.
		else{
			player.sendMessage(ChatColor.GOLD + "[" + Kikkit.getPluginName() + " by Kr1sc]");
			player.sendMessage(ChatColor.GOLD + "Please respect others' property and no griefing.");
			player.sendMessage(ChatColor.GOLD + "Fire and lava are in a whiteout.");
		}
	}
	
	@Override
    public void onPlayerQuit(PlayerEvent event) {
    }

    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
    	String[] split = event.getMessage().split(" ");
    	Player player = event.getPlayer();
    	
    	// Command: warpto [player name] [warp name]
		if(split[0].equalsIgnoreCase("/warpto")){
			if(!plugin.canUseCommand(player, "/warpto")) {
				setCommandHandled(event, false);
				return;
			}
			
			if(split.length == 3){
				WarpList wl = plugin.getServerModWarps();
				
				WarpList.WarpPoint wp = wl.get(split[2]);
				
				if(wp == null){
					player.sendMessage(ChatColor.RED + "Can't find warp: " + split[2]);
					setCommandHandled(event, true);
					return;
				}
				
				Player target = plugin.getServer().getPlayer(split[1]);
				
				if(target == null){
					player.sendMessage(ChatColor.RED + "Can't find player: " + split[1]);
					setCommandHandled(event, true);
					return;
				}
				else{
					target.teleportTo(wp.getLocation());
				}
			};
		}
		// Command /fire
		else if(split[0].equalsIgnoreCase("/fire")){
			if(!plugin.canUseCommand(player, "/fire")){
				setCommandHandled(event, false);
				return;
			}
			
			Whitelist fireList = plugin.getFireWhitelist();

			if(split.length == 1){
				fireList.setIsOverriden(!fireList.getIsOverriden());
				
				if(fireList.getIsOverriden()) player.sendMessage(ChatColor.RED + "Firelist override is ON. Players can use fire.");
				else player.sendMessage(ChatColor.RED + "Firelist override is OFF. Players can't use fire.");
			}
			else if(split.length == 3){
				if(split[1].equalsIgnoreCase("add")){
					fireList.add(split[2]);
					player.sendMessage(ChatColor.RED + split[2] + " has been added to the Fire/Lava whitelist.");
				}
				else if(split[1].equalsIgnoreCase("remove")){
					fireList.remove(split[2]);
					player.sendMessage(ChatColor.RED + split[2] + " has been removed from the Fire/Lava whitelist.");
				}
				else if(split[1].equalsIgnoreCase("check")){
					if(fireList.has(split[2])) player.sendMessage(ChatColor.RED + split[2] + " is on the Fire/Lava whitelist.");
					else player.sendMessage(ChatColor.RED + split[2] + " is NOT on the Fire/Lava whitelist.");
				}
			}
		}
		else if(split[0].equalsIgnoreCase("/tempwl")){
			if(!plugin.canUseCommand(player, "/tempwl")){
				setCommandHandled(event, false);
				return;
			}
			
			TemporaryWhitelist tempList = plugin.getTemporaryWhitelist();
			
			if(split.length == 1){
				tempList.setIsOverriden(!tempList.getIsOverriden());
				
				if(tempList.getIsOverriden()) player.sendMessage(ChatColor.RED + "Whitelist override is now ON. (Players can join freely)");
				else player.sendMessage(ChatColor.RED + "Whitelist override is now OFF.");
			}
			else if(split.length == 2){
				if(split[1].equalsIgnoreCase("?")){
					player.sendMessage(ChatColor.RED + "[USAGE] /tempwl <command> <player name>");
					player.sendMessage(ChatColor.RED + "[USAGE] Commands: add, remove, check");
				}
			}
			else if(split.length == 3){
				if(split[1].equalsIgnoreCase("add")){
					tempList.add(split[2]);
					player.sendMessage(ChatColor.RED + split[2] + " has been added to the Temp whitelist.");
				}
				else if(split[1].equalsIgnoreCase("remove")){
					tempList.remove(split[2]);
					player.sendMessage(ChatColor.RED + split[2] + " has been removed from the Temp whitelist.");
				}
				else if(split[1].equalsIgnoreCase("check")){
					if(tempList.has(split[2])) player.sendMessage(ChatColor.RED + split[2] + " is on the Temp whitelist.");
					else player.sendMessage(ChatColor.RED + split[2] + " is NOT on the Temp whitelist.");
				}
			}
			
			return;
		}
		else if(split[0].equalsIgnoreCase("/setsecret")){
			if(!plugin.canUseCommand(player, "/setsecret")) {
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getSecretWarpList();
			
			list.set(player.getName(), player.getLocation());
			
			player.sendMessage(ChatColor.RED + "Secret warp has been set.");

		}
    	else if(split[0].equalsIgnoreCase("/secret")){
			if(!plugin.canUseCommand(player, "/secret")){
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getSecretWarpList();
			
			WarpList.WarpPoint wp = list.get(player.getName());
			
			if(wp != null){
				player.teleportTo(wp.getLocation(player.getWorld()));
				
				player.sendMessage(ChatColor.RED + "Secret Whoosh!");
			}
			else{
				player.sendMessage(ChatColor.RED + "You must set a secret warp first with /setsecret");
			}
		}
    	else if(split[0].equalsIgnoreCase("/sethome")){
			if(!plugin.canUseCommand(player, "/sethome")) {
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getHomeWarpList();
			
			list.set(player.getName(), player.getLocation());
			
			player.sendMessage(ChatColor.RED + "Home warp has been set.");

		}
    	else if(split[0].equalsIgnoreCase("/home")){
			if(!plugin.canUseCommand(player, "/home")){
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getHomeWarpList();
			
			WarpList.WarpPoint wp = list.get(player.getName());
			
			if(wp != null){
				player.teleportTo(wp.getLocation(player.getWorld()));
				
				player.sendMessage(ChatColor.RED + "Whoosh to home!");
			}
			else{
				player.sendMessage(ChatColor.RED + "You must set a home warp first with /sethome");
			}
		}
    	else if(split[0].equalsIgnoreCase("/setwarp")){
    		if(!plugin.canUseCommand(player, "/setwarp")){
    			setCommandHandled(event, false);
    			return;
    		}
    		
    		if(split.length > 1){
				WarpList list = plugin.getServerModWarps();
				
				list.set(split[1], player.getLocation());
				
				player.sendMessage(ChatColor.RED + split[1] + " warp has been set.");
    		}
    	}
    	else if(split[0].equalsIgnoreCase("/warp")){
			if(!plugin.canUseCommand(player, "/warp")){
				setCommandHandled(event, false);
				return;
			}
			
			if(split.length > 1){
				WarpList list = plugin.getServerModWarps();
				
				WarpList.WarpPoint wp = list.get(split[1]);
				
				if(wp != null){
					player.teleportTo(wp.getLocation(player.getWorld()));
					
					player.sendMessage(ChatColor.RED + "Whoosh!");
				}
				else{
					player.sendMessage(ChatColor.RED + "Unknown warp.");
				}
			}
		}
    	else if(split[0].equalsIgnoreCase("/time") || split[0].equalsIgnoreCase("/day") || split[0].equalsIgnoreCase("/night"))
    	{
    		if(!plugin.canUseCommand(player, "/time")){
    			setCommandHandled(event, false);
    			return;
    		}
    			
    		try{
				if(split[0].equalsIgnoreCase("/day") || (split.length == 2 && split[1].equalsIgnoreCase("day")))
					plugin.getServer().setTime(Kikkit.DAY);
				else if(split[0].equalsIgnoreCase("/night") || (split.length == 2 && split[1].equalsIgnoreCase("night")))
					plugin.getServer().setTime(Kikkit.NIGHT);
				else{
					if(split.length > 1) plugin.getServer().setTime(Long.parseLong(split[1]));
				}
    		}
    		catch(Exception ex){}
    	}
    	else if(split[0].equalsIgnoreCase("/setspawn")){
    		if(!plugin.canUseCommand(player, "/setspawn")){
    			setCommandHandled(event, false);
    			return;
    		}
    		
			WarpList list = plugin.getServerModWarps();
			
			list.set("spawn", player.getLocation());
			
			player.sendMessage(ChatColor.RED + "Spawn has been set.");
    	}
    	else if(split[0].equalsIgnoreCase("/spawn")){
    		if(!plugin.canUseCommand(player, "/spawn")){
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getServerModWarps();
			
			WarpList.WarpPoint wp = list.get("spawn");
			
			if(wp != null){
				player.teleportTo(wp.getLocation(player.getWorld()));
				
				player.sendMessage(ChatColor.RED + "Whoosh!");
			}
			else{
				player.sendMessage(ChatColor.RED + "Unknown warp.");
			}
    	}
    	else if(split[0].equalsIgnoreCase("/debug")){
    		if(!plugin.canUseCommand(player, "/debug")){
				setCommandHandled(event, false);
				return;
			}
    		
    		Kikkit.IsDebugging = !Kikkit.IsDebugging;
    	}
    	else if(split[0].equalsIgnoreCase("/playerlist")){
    		if(!plugin.canUseCommand(player, "/playerlist")){
				setCommandHandled(event, false);
				return;
			}
    		
    		Player[] players = plugin.getServer().getOnlinePlayers();
    		
    		String total = "Players: ";
    		for(int k = 0; k < players.length; k++){
    			total += players[k].getName();
    			if(k < players.length - 1) total += ", ";
    		}
    		
    		player.sendMessage(total);
    	}
    	else if(split[0].equalsIgnoreCase("/kick")){
    		if(!plugin.canUseCommand(player, "/kick")){
				setCommandHandled(event, false);
				return;
			}
    		
    		if(split.length >= 2){
	    		
	    		String reason = "";
	    		if(split.length > 2) {
	    			for(int k = 2; k < split.length; k++)
	    				reason += split[k] + " ";
	    		}
	    		
	    		Player playerToKick = plugin.getServer().getPlayer(split[1]);
	    		
	    		if(playerToKick != null){ 
	    			playerToKick.kickPlayer(reason);
	    		
	    			plugin.broadcast(playerToKick.getName() + " has been kicked.");
	    		}
    		}
    	}
    	else if(split[0].equalsIgnoreCase("/msg")){
    		if(!plugin.canUseCommand(player, "/msg")){
    			setCommandHandled(event, false);
    			return;
    		}
    		
    		Player playerToMsg = plugin.getServer().getPlayer(split[2]);
    		
    		String message = "";
    		if(split.length > 2) {
    			for(int k = 2; k < split.length; k++)
    				message += split[k] + " ";
    		}
    		
    		playerToMsg.sendMessage("[" + player.getName() + "]" + message);
    	}
		else if(split[0].equalsIgnoreCase("/item")){
			if(!plugin.canUseCommand(player, "/item")){
				setCommandHandled(event, false);
				return;
			}
			
			if(split.length >= 3){
				Player playerToGiveTo = player;
				if(split.length >= 4){
					playerToGiveTo = plugin.getServer().getPlayer(split[3]);
				}
				
				int id = Integer.parseInt(split[1]);
				int amount = Integer.parseInt(split[2]);
				
				if(playerToGiveTo != null){
					ItemStack itemStack = new ItemStack(id, amount);
					playerToGiveTo.getInventory().addItem(itemStack);
					
					Kikkit.MinecraftLog.info(player.getName() + " gave " + id + " to " + playerToGiveTo.getName());
				}
			}
		}
    	else if(split[0].equalsIgnoreCase("/tp")){
    		if(!plugin.canUseCommand(player, "/tp")){
    			setCommandHandled(event, false);
    			return;
    		}
    		
    		if(split.length >= 2){
    			Player destination = plugin.getServer().getPlayer(split[1]);
    			
    			Player source = player;
    			if(split.length >= 3) source = plugin.getServer().getPlayer(split[2]);
    			
    			
    			if(source != null && destination != null){
    				source.teleportTo(destination);
    				
    				source.sendMessage(ChatColor.RED + "Whooooosh!");
    				
    				Kikkit.MinecraftLog.info(player.getName() + " made " + source.getName() + " teleport to " + destination.getName());
    			}
    				
    		}
    	}
	
		setCommandHandled(event, true);
    }
}
