package core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWrapper {
	public CommandSender Sender;
	public Command Command;
	public String Label;
	public String[] Args;
	public boolean IsCancelled;
	
	public String Name;
	
	public void msg(String message){
		if(Sender instanceof Player){
			((Player)Sender).sendMessage(message);
		}
		else Kikkit.MinecraftLog.info(message);
	}
}
