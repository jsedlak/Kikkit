package core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;


public class KikkitBlockListener extends BlockListener {
	private Kikkit plugin;
	private KickCounter igniteKickCounter;
	
	public KikkitBlockListener(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
		
		igniteKickCounter = plugin.getIgnitionKickCounter();
	}
	
	@Override
    public void onBlockCanBuild(BlockCanBuildEvent event) {
		//Kikkit.MinecraftLog.info("onBlockCanBuild(" + event.getMaterialId() + ")");
		
		//Player player = event.getPlayer();
		//event.setBuildable(true);
		
		if(event.getMaterialId() == Material.TNT.getId() || event.getBlock().getTypeId() == Material.TNT.getId()){
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
		//Kikkit.MinecraftLog.info("onBlockPlace(block: " + event.getBlock().getTypeId() + ", placed: " + event.getBlockPlaced().getTypeId() + ")");
		
		Player player = event.getPlayer();
		
		if(event.getBlockPlaced().getTypeId() == Material.TNT.getId() || event.getBlock().getTypeId() == Material.TNT.getId()){
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
		Player sourcePlayer = event.getPlayer();
		
		if(event.getItemInHand().getTypeId() == Material.LAVA_BUCKET.getId()){
			if(!plugin.canPlayerIgnite(sourcePlayer)){
				sourcePlayer.setItemInHand(new ItemStack(Material.BUCKET));
				
				plugin.broadcast(ChatColor.RED + sourcePlayer.getName() + " has tried using laval, but has been blocked!");
				
				Kikkit.MinecraftLog.info(sourcePlayer.getName() + " has tried using lava.");
				
				if(igniteKickCounter.checkAndSet(sourcePlayer.getName()) > Kikkit.MAX_IGNITE_ATTEMPTS){
					sourcePlayer.kickPlayer("You have been kicked for attempting to grief.");
					
					plugin.broadcast(ChatColor.DARK_PURPLE + sourcePlayer.getName() + " has been kicked for trying to use lava.");
				}
			}
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
