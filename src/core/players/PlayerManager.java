package core.players;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerEvent;

import core.Kikkit;

public class PlayerManager {
	private ArrayList<PlayerData> playerData = new ArrayList<PlayerData>();
	private Kikkit plugin;
	
	public PlayerManager(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
	}
	
	public void onPlayerJoin(PlayerEvent event){
		PlayerData data = get(event.getPlayer().getName());
		
		if(data == null){
			data = new PlayerData(event.getPlayer().getName());
			
			playerData.add(data);
		}
		
		data.loggedOn();
		
		event.getPlayer().sendMessage(ChatColor.GREEN + "Balance: " + data.getCredits() + " " + plugin.getMarket().getCurrencyName());
		event.getPlayer().sendMessage(ChatColor.GREEN + "Hours Logged: " + data.getHours() + " hours");
	}
	
	public void onPlayerQuit(PlayerEvent event){
		PlayerData data = get(event.getPlayer().getName());
		
		if(data == null){
			return;
		}
		
		data.loggedOff();
	}
	
	public PlayerData get(String playerName){
		for(int k = 0; k < playerData.size(); k++){
			PlayerData cur = playerData.get(k);
			
			if(cur.getName().equalsIgnoreCase(playerName)) return cur;
		}
		
		return null;
	}
}
