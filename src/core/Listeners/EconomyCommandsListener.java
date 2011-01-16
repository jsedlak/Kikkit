package core.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import core.Kikkit;
import core.Players.*;
import core.CommandListener;

public class EconomyCommandsListener extends CommandListener {

	public EconomyCommandsListener(Kikkit plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(PlayerChatEvent event, String[] cmdData, Player sourcePlayer) {
		if(cmdData[0].equalsIgnoreCase("/balance")){
			if(!canUseCommand(sourcePlayer, "/balance")){
				// Security error!
				return true;
			}
			
			if(cmdData.length >= 2 && cmdData[1].equalsIgnoreCase("?")){
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] Checks your current balance.");
				sourcePlayer.sendMessage(ChatColor.RED + "[USAGE] /balance");
				
				setCommandHandled(event, true);
				return true;
			}
			
			PlayerManager pm = getPlugin().getPlayerManager();
			PlayerData pd = pm.get(sourcePlayer.getName());
			
			sourcePlayer.sendMessage(ChatColor.GREEN + "Your balance is " + pd.getCredits() + " kredits.");
			
			setCommandHandled(event, true);
			return true;
		}
		
		return false;
	}

}
