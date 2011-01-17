package core;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import core.Listeners.*;
import core.bukkit.ItemConstants;

public class KikkitPlayerListener extends PlayerListener {
	private Kikkit plugin;
	private KickCounter igniteKickCounter;
	private CommandListenerCollection listeners = new CommandListenerCollection();
	
	public KikkitPlayerListener(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
		
		igniteKickCounter = plugin.getIgnitionKickCounter();
		
		listeners.add(new AdminCommandsListener(plugin));
		listeners.add(new GeneralCommandsListener(plugin));
		listeners.add(new ItemCommandsListener(plugin));
		listeners.add(new PersonalWarpCommandsListener(plugin));
		listeners.add(new PublicWarpCommandsListener(plugin));
		listeners.add(new TeleportCommandsListener(plugin));
		listeners.add(new WhitelistCommandsListener(plugin));
		listeners.add(new EconomyCommandsListener(plugin));
	}
	
	/*
	private void setCommandHandled(PlayerChatEvent event, boolean wasCommandHandled){
		if(wasCommandHandled){
			event.setCancelled(true);
		}
		
	}
	*/
	
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
		
		plugin.getPlayerManager().onPlayerJoin(event);
	}
	
	@Override
    public void onPlayerQuit(PlayerEvent event) {
		plugin.getPlayerManager().onPlayerQuit(event);
    }

    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
    	String[] split = event.getMessage().split(" ");
    	Player player = event.getPlayer();
    	
    	// Loop through all the command listeners
    	for(CommandListener listener : listeners){
    		boolean result = listener.onCommand(event, split, player);
    		
    		if(event.isCancelled()) return;
    		if(result) break;
    	}
    	
    	player.sendMessage(ChatColor.RED + "Unknown command.");		
    }
}
