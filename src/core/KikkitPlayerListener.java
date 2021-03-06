package core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
	
	public void onPlayerChat(PlayerChatEvent event){
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		SecurityManager.Group group = plugin.getSecurityManager().getGroupForPlayer(player.getName());
		
		if(group != null){			
			String playerText = "<" + group.getColor() + event.getPlayer().getName() + ChatColor.WHITE + "> ";
			
			Kikkit.MinecraftLog.info("<" + player.getName() + "> " + msg);
			
			//event.setMessage(msg);
			event.setCancelled(true);
			
			plugin.broadcast(playerText + msg);
		}

	}
	
	public void onPlayerItem(PlayerItemEvent event){
		if(event.isCancelled()) return;
		
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		
		if(item.getTypeId() == Material.LAVA_BUCKET.getId()){
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
		else if(item.getTypeId() == Material.TNT.getId()){
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
			if(plugin.getMotd().length() > 0){
				for(String line : plugin.getMotd().split("/n")){
					player.sendMessage(line);
				}
			}
			else{
				player.sendMessage(ChatColor.GOLD + "[" + Kikkit.getPluginName() + " by Kr1sc]");
				player.sendMessage(ChatColor.GOLD + "Please respect others' property and no griefing.");
				player.sendMessage(ChatColor.GOLD + "Fire and lava are in a whiteout.");
			}
		}
		
		plugin.getPlayerManager().onPlayerJoin(event);
	}
	
	@Override
    public void onPlayerQuit(PlayerEvent event) {
		plugin.getPlayerManager().onPlayerQuit(event);
    }

    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
    	if(event.isCancelled()) return;
    	
    	
    	
    	//player.sendMessage(ChatColor.RED + "Unknown command.");	
    }
}
