package core;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

import core.bukkit.ItemConstants;


public class KikkitBlockListener extends BlockListener {
	private Kikkit plugin;
	private KickCounter igniteKickCounter;
	
	public KikkitBlockListener(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
		
		igniteKickCounter = plugin.getIgnitionKickCounter();
	}
	
	@Override
    public void onBlockCanBuild(BlockCanBuildEvent event) {
		Kikkit.MinecraftLog.info("onBlockCanBuild(" + event.getMaterialId() + ")");
		
		//Player player = event.getPlayer();
		//event.setBuildable(true);
		
		if(event.getMaterialId() == ItemConstants.TntId || event.getBlock().getTypeId() == ItemConstants.TntId){
			/*if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(ChatColor.RED + player.getName() + " has tried placing TNT, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use TNT.");
				
				if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
					player.kickPlayer("You have been kicked for attempting to grief.");
					plugin.broadcast(ChatColor.DARK_PURPLE + player.getName() + " has been kicked for trying to use TNT.");
				}
				
				event.setCancelled(true);
			}*/
			Kikkit.MinecraftLog.info("Someone has tried to use TNT.");
			//plugin.broadcast(ChatColor.RED + "Someone has tried placing TNT, but has been blocked!");
			//event.setBuildable(false);
		}
    }
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event){
		event.setCancelled(false);
		
		//Kikkit.MinecraftLog.info("onBlockCanPlace");
		Kikkit.MinecraftLog.info("onBlockPlace(block: " + event.getBlock().getTypeId() + ", placed: " + event.getBlockPlaced().getTypeId() + ")");
		
		Player player = event.getPlayer();
		
		if(event.getBlockPlaced().getTypeId() == ItemConstants.TntId || event.getBlock().getTypeId() == ItemConstants.TntId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(ChatColor.RED + player.getName() + " has tried placing TNT, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use TNT.");
				
				if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
					player.kickPlayer("You have been kicked for attempting to grief.");
					plugin.broadcast(ChatColor.DARK_PURPLE + player.getName() + " has been kicked for trying to use TNT.");
				}
				
				event.setCancelled(true);
			}
		}
	}
	
	@Override
	public void onBlockRightClick(BlockRightClickEvent event){
		if(event.getItemInHand().getTypeId() == ItemConstants.LavaBucketId){
			event.getPlayer().setItemInHand(new ItemStack(1, 1));
		}
	}
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event){
		if(event.isCancelled()) return;
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		// This method gets called ever second, regardless of whether or not
		// an ignition is occuring. So block it out when there is no actual
		// ignite event.
		if(block == null || player == null){
			//event.setCancelled(true);
			return;
		}
		
		// Check if the player can ignite stuff
		if(!plugin.canPlayerIgnite(player)){
			plugin.broadcast(ChatColor.RED + player.getName() + " is trying to set something on fire!");
			
			if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
				player.kickPlayer("You have been kicked for attempting to grief.");
				plugin.broadcast(ChatColor.DARK_PURPLE + player.getName() + " has been kicked for trying to ignite something.");
			}
			
			event.setCancelled(true);
		}
	}
}
