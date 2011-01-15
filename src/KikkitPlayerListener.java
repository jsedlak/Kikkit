import org.bukkit.ItemStack;
import org.bukkit.Player;
import org.bukkit.event.player.*;

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
	
	public void onPlayerItem(PlayerItemEvent event){
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		
		if(item.getTypeID() == ItemConstants.LavaBucketId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(/*Colors.Red + */player.getName() + " has tried using the lava bucket, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.getType().name() + " with Id " + item.getTypeID() + ".");
				
				if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
					player.kickPlayer("You have been kicked for attempting to grief.");
					plugin.broadcast(/*Colors.Purple + "[Kikkit] " + */player.getName() + " has been kicked for trying to place lava.");
				}
				
				event.setCancelled(true);
			}
		}
		else if(item.getTypeID() == ItemConstants.TntId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(/*Colors.Red + */player.getName() + " has tried placing TNT, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.getType().name() + " with Id " + item.getTypeID() + ".");
				
				if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
					player.kickPlayer("You have been kicked for attempting to grief.");
					plugin.broadcast(/*Colors.Purple + "[Kikkit] " + */player.getName() + " has been kicked for trying to use TNT.");
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
				player.sendMessage(/*Colors.Gold + */"[" + Kikkit.getPluginName() + " by Kr1sc]");
				player.sendMessage(/*Colors.Gold + */"Welcome back, " + player.getName() + ".");
				player.sendMessage(/*Colors.Gold + */"We are currently in a whiteout, but you made the list!");
			}
			// Otherwise kick them.
			else{
				Kikkit.MinecraftLog.info(player.getName() + " has been kicked for not being on the temporary whitelist.");
				player.kickPlayer("You were kicked because you are not on the whitelist, check back in a few hours.");
				plugin.broadcast(/*Colors.Gold + */player.getName() + " was kicked for not being on the whitelist.");
			}
		}
		// If we are not in a whiteout, notify them of the rules.
		else{
			player.sendMessage(/*Colors.Gold + */"[" + Kikkit.getPluginName() + " by Kr1sc]");
			player.sendMessage(/*Colors.Gold + */"Please respect others' property and no griefing.");
			player.sendMessage(/*Colors.Gold + */"Fire and lava are in a whiteout.");
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
					player.sendMessage(/*Colors.Red + */"Can't find warp: " + split[2]);
					setCommandHandled(event, true);
					return;
				}
				
				Player target = plugin.getServer().getPlayer(split[1]);
				
				if(target == null){
					player.sendMessage(/*Colors.Red + */"Can't find player: " + split[1]);
					setCommandHandled(event, true);
					return;
				}
				else{
					target.teleportTo(wp.getLocation());
					/*target.setX(wp.X);
					target.setY(wp.Y);
					target.setZ(wp.Z);*/
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
				
				if(fireList.getIsOverriden()) player.sendMessage(/*Colors.Red + */"Firelist override is ON. Players can use fire.");
				else player.sendMessage(/*Colors.Red + */"Firelist override is OFF. Players can't use fire.");
			}
			else if(split.length == 3){
				if(split[1].equalsIgnoreCase("add")){
					fireList.add(split[2]);
					player.sendMessage(/*Colors.Red + */split[2] + " has been added to the Fire/Lava whitelist.");
				}
				else if(split[1].equalsIgnoreCase("remove")){
					fireList.remove(split[2]);
					player.sendMessage(/*Colors.Red + */split[2] + " has been removed from the Fire/Lava whitelist.");
				}
				else if(split[1].equalsIgnoreCase("check")){
					if(fireList.has(split[2])) player.sendMessage(/*Colors.Red + */split[2] + " is on the Fire/Lava whitelist.");
					else player.sendMessage(/*Colors.Red + */split[2] + " is NOT on the Fire/Lava whitelist.");
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
				
				if(tempList.getIsOverriden()) player.sendMessage(/*Colors.Red + */"Whitelist override is now ON. (Players can join freely)");
				else player.sendMessage(/*Colors.Red + */"Whitelist override is now OFF.");
			}
			else if(split.length == 2){
				if(split[1].equalsIgnoreCase("?")){
					player.sendMessage(/*Colors.Red + */"[USAGE] /tempwl <command> <player name>");
					player.sendMessage(/*Colors.Red + */"[USAGE] Commands: add, remove, check");
				}
			}
			else if(split.length == 3){
				if(split[1].equalsIgnoreCase("add")){
					tempList.add(split[2]);
					player.sendMessage(/*Colors.Red + */split[2] + " has been added to the Temp whitelist.");
				}
				else if(split[1].equalsIgnoreCase("remove")){
					tempList.remove(split[2]);
					player.sendMessage(/*Colors.Red + */split[2] + " has been removed from the Temp whitelist.");
				}
				else if(split[1].equalsIgnoreCase("check")){
					if(tempList.has(split[2])) player.sendMessage(/*Colors.Red + */split[2] + " is on the Temp whitelist.");
					else player.sendMessage(/*Colors.Red + */split[2] + " is NOT on the Temp whitelist.");
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
			
			player.sendMessage(/*Colors.Red + */"Secret warp has been set.");

		}
    	else if(split[0].equalsIgnoreCase("/secret")){
			if(!plugin.canUseCommand(player, "/secret")){
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getSecretWarpList();
			
			WarpList.WarpPoint wp = list.get(player.getName());
			
			if(wp != null){
				/*player.setX(wp.X);
				player.setY(wp.Y);
				player.setZ(wp.Z);
				*/
				player.teleportTo(wp.getLocation());
				
				player.sendMessage(/*Colors.Red + */"Secret Whoosh!");
			}
			else{
				player.sendMessage(/*Colors.Red + */"You must set a secret warp first with /setsecret");
			}
		}
    	else if(split[0].equalsIgnoreCase("/sethome")){
			if(!plugin.canUseCommand(player, "/sethome")) {
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getHomeWarpList();
			
			list.set(player.getName(), player.getLocation());
			
			player.sendMessage(/*Colors.Red + */"Home warp has been set.");

		}
    	else if(split[0].equalsIgnoreCase("/home")){
			if(!plugin.canUseCommand(player, "/home")){
				setCommandHandled(event, false);
				return;
			}
			
			WarpList list = plugin.getHomeWarpList();
			
			WarpList.WarpPoint wp = list.get(player.getName());
			
			if(wp != null){
				player.teleportTo(wp.getLocation());
				
				player.sendMessage(/*Colors.Red + */"Whoosh to home!");
			}
			else{
				player.sendMessage(/*Colors.Red + */"You must set a home warp first with /sethome");
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
		
		setCommandHandled(event, true);
    }
}
