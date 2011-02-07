package core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import core.CommandListener;
import core.CommandWrapper;
import core.Kikkit;
import core.WarpList;

public class PublicWarpCommandsListener extends CommandListener {

	public PublicWarpCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandWrapper cmd) {
		Player sourcePlayer = null;
		if(cmd.Sender instanceof Player){ sourcePlayer = (Player)cmd.Sender; }
		
		if(cmd.Name.equalsIgnoreCase("/setspawn")){
			if(!canUseCommand(cmd.Sender, "/setspawn")) return true;
			
			WarpList list = getPlugin().getServerModWarps();
			
			list.set("spawn", sourcePlayer.getLocation());
			
			cmd.msg(ChatColor.RED + "Spawn has been set.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/spawn")){
			if(!canUseCommand(cmd.Sender, "/spawn")) return true;
			
			if(sourcePlayer == null){
				cmd.msg(ChatColor.RED + "Can't warp to spawn. Please use /warpto instead.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			WarpList list = getPlugin().getServerModWarps();
			
			WarpList.WarpPoint wp = list.get("spawn");
			
			if(wp != null){
				sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
				
				sourcePlayer.sendMessage(ChatColor.RED + "Whoosh!");
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown warp.");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/setwarp") || cmd.Name.equalsIgnoreCase("/swarp")){
			if(!canUseCommand(cmd.Sender, "/setwarp")) return true;
			
			WarpList list = getPlugin().getServerModWarps();
			
			list.set(cmd.Args[0], sourcePlayer.getLocation());
			
			cmd.msg(ChatColor.RED + cmd.Args[0] + " warp has been set.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/removewarp") || cmd.Name.equalsIgnoreCase("/rmwarp")){
			if(!canUseCommand(cmd.Sender, "/setwarp")) return true;
			
			WarpList list = getPlugin().getServerModWarps();
			
			if(list.remove(cmd.Args[0])) cmd.msg(ChatColor.RED + cmd.Args[0]+ " warp has been removed.");
			else cmd.msg(ChatColor.RED + cmd.Args[0] + " warp could not be found.");
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/warp")){
			if(!canUseCommand(cmd.Sender, "/warp")) return true;
			
			if(sourcePlayer == null) {
				cmd.msg(ChatColor.RED + "Cannot warp self, please use /warpto instead.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			if(cmd.Args.length == 0){
				cmd.msg(ChatColor.RED + "No warp specified, please correct and try again.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			WarpList list = getPlugin().getServerModWarps();
			
			WarpList.WarpPoint wp = list.get(cmd.Args[0]);
			
			if(wp != null){
				sourcePlayer.teleportTo(wp.getLocation(sourcePlayer.getWorld()));
				
				sourcePlayer.sendMessage(ChatColor.RED + "Whoosh!");
			}
			else{
				sourcePlayer.sendMessage(ChatColor.RED + "Unknown warp.");
			}
			
			setCommandHandled(cmd, true);
			return true;
		}
		else if(cmd.Name.equalsIgnoreCase("/warplist") || cmd.Name.equalsIgnoreCase("/listwarps")){
			if(!canUseCommand(cmd.Sender, "/warplist")) return true;
			
			WarpList list = getPlugin().getServerModWarps();
			WarpList.WarpPoint[] array = list.toArray();
			
			// Check for the empty case
			if(array == null || array.length == 0){
				cmd.msg(ChatColor.GRAY + "No warps have been created to speak of.");
				
				setCommandHandled(cmd, true);
				return true;
			}
			
			String str = "";
			for(int k = 0; k < array.length; k++){
				str += array[k].Username;
				
				if(k < array.length - 1) str += ", ";
			}
			
			cmd.msg(ChatColor.GRAY + str);
			
			setCommandHandled(cmd, true);
			return true;
		}

		return false;
	}

}
