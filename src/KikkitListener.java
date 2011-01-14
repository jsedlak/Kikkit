
public class KikkitListener extends PluginListener{
	private Kikkit plugin;
	
	
	public KikkitListener(Kikkit plugin){
		this.plugin = plugin;
		
		
	}
	
	public boolean onItemUse(Player player, Block blockPlaced, Block blockCLicked, Item item) {
		//Kikkit.MinecraftLog.info(player.getName() + " has used " + item.itemType.name() + " with Id " + item.getItemId() + ".");
		
		if(item.getItemId() == ItemConstants.LavaBucketId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(Colors.Red + player.getName() + " has tried using the lava bucket, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.itemType.name() + " with Id " + item.getItemId() + ".");
				
				if(igniteKickCounter.checkAndSet(player.getName()) > MAX_IGNITE_ATTEMPTS){
					player.kick("You have been kicked for attempting to grief.");
					plugin.broadcast(Colors.Purple + "[Kikkit] " + player.getName() + " has been kicked for trying to place lava.");
				}
				
				return true;
			}
		}
		else if(item.getItemId() == ItemConstants.TntId){
			if(!plugin.canPlayerIgnite(player)){
				plugin.broadcast(Colors.Red + player.getName() + " has tried placing TNT, but has been blocked!");
				Kikkit.MinecraftLog.info(player.getName() + " has tried to use " + item.itemType.name() + " with Id " + item.getItemId() + ".");
				
				if(igniteKickCounter.checkAndSet(player.getName()) > MAX_IGNITE_ATTEMPTS){
					player.kick("You have been kicked for attempting to grief.");
					plugin.broadcast(Colors.Purple + "[Kikkit] " + player.getName() + " has been kicked for trying to use TNT.");
				}
				
				return true;
			}
		}
		
        return false;
    }
}