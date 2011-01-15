import org.bukkit.Player;
import org.bukkit.Block;
import org.bukkit.event.block.*;

public class KikkitBlockListener extends BlockListener {
	private Kikkit plugin;
	private KickCounter igniteKickCounter;
	
	public KikkitBlockListener(Kikkit kikkitPlugin){
		plugin = kikkitPlugin;
		
		igniteKickCounter = plugin.getIgnitionKickCounter();
	}
	
	@Override
    public void onBlockCanBuild(BlockCanBuildEvent event) {
    }
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event){
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
			plugin.broadcast(/*Colors.Red + */player.getName() + " is trying to set something on fire!");
			
			if(igniteKickCounter.checkAndSet(player.getName()) >= Kikkit.MAX_IGNITE_ATTEMPTS){
				player.kickPlayer("You have been kicked for attempting to grief.");
				plugin.broadcast(/*Colors.Purple + "[Kikkit] " + */player.getName() + " has been kicked for trying to ignite something.");
			}
			
			event.setCancelled(true);
		}
	}
}
