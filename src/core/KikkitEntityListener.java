package core;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class KikkitEntityListener extends EntityListener {
	private Kikkit plugin;
	private ArrayList<String> players = new ArrayList<String>();
	
	public KikkitEntityListener(Kikkit plugin){
		this.plugin = plugin;
	}
	
	private boolean canBroadcast(Player player){
		for(String pName : players){
			if(pName.equalsIgnoreCase(player.getName())) return false;
		}
		
		return true;
	}
	
	@Override 
	public void onEntityDamage(EntityDamageEvent event){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("onEntityDamage(...)");
		
		if(event.getEntity() instanceof Player){
			Player player = (Player)event.getEntity();
			
			if(event.getDamage() < player.getHealth() || player.getHealth() < 0) return;
			if(!canBroadcast(player)) return;
			
			DamageCause cause = event.getCause();
			
			players.add(player.getName());
			
			if(cause == DamageCause.FALL){
				getPlugin().broadcast(ChatColor.GRAY + player.getName() + " fell off a great ledge.");
			}
			else if(cause == DamageCause.LAVA){
				getPlugin().broadcast(ChatColor.GRAY + player.getName() + " has stepped on some hot rocks.");
			}
			else if(cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK)
				getPlugin().broadcast(ChatColor.GRAY + player.getName() + " has caught fire.");
			else if(cause == DamageCause.DROWNING) 
				getPlugin().broadcast(ChatColor.GRAY + player.getName() + " is swimmin' with the fishes.");
			else if(cause == DamageCause.SUFFOCATION)
				getPlugin().broadcast(ChatColor.GRAY + player.getName() + " has suffocated their self in a last minute attempt of gaining social approval.");
			else if(cause == DamageCause.ENTITY_EXPLOSION)
				getPlugin().broadcast(ChatColor.GRAY + "The creepers got " + player.getName() + ".");
			else
				getPlugin().broadcast(ChatColor.GRAY + player.getName() + " has died.");
			
			if(Kikkit.IsDebugging){
				Kikkit.MinecraftLog.info("Health: " + player.getHealth());
				Kikkit.MinecraftLog.info("Damage: " + event.getDamage());
				Kikkit.MinecraftLog.info("Cause: " + event.getCause().name());
			}
		}
	}
	
	@Override
	public void onEntityCombust(EntityCombustEvent event){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("onEntityCombust(...)");
	}
	
	@Override
	public void onEntityExplode(EntityExplodeEvent event){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("onEntityExplode(...)");
	}
	
	
	@Override
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("onEntityDamageByEntity(...)");
	}
	
	@Override
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("onEntityDamageByBlock(...)");
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event){
		if(Kikkit.IsDebugging) Kikkit.MinecraftLog.info("onEntityDeath(...)");
		
		
		if(event.getEntity() instanceof Player){
			Player player = (Player)event.getEntity();
			
			if(canBroadcast(player)){
				// If they died, but weren't added then the above handlers didn't fire
				getPlugin().broadcast(ChatColor.GRAY + ((Player)event.getEntity()).getName() + " has died!");
			}
			else{
				// Otherwise, remove the player
				for(int i = players.size() - 1; i >= 0; i--){
					if(players.get(i).equalsIgnoreCase(player.getName())){
						players.remove(i);
						break;
					}
				}
			}
				
		}
	}
	
	public Kikkit getPlugin() { return plugin; }
}
