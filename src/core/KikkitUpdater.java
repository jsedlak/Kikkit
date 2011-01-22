package core;

import java.util.Date;
import org.bukkit.event.player.*;

public class KikkitUpdater extends PlayerListener {
	// How often the plugin should update
	public static final long UPDATE_INTERVAL = 30000;
	
	private Kikkit plugin;
	private Date lastCheck;
	
	public KikkitUpdater(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
		lastCheck = new Date();
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event){
		Date newCheck = new Date();
		
		long diff = newCheck.getTime() - lastCheck.getTime();
		if(diff > UPDATE_INTERVAL){
			lastCheck = newCheck;
			plugin.update();
		}
	}
}