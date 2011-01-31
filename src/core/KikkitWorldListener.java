package core;

import org.bukkit.event.world.WorldListener;

public class KikkitWorldListener extends WorldListener {
	private Kikkit plugin;
	
	public KikkitWorldListener(Kikkit plugin){
		this.plugin = plugin;
	}
	
	public Kikkit getPlugin() { return plugin; }
}
