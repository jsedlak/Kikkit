package core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandWrapper {
	public CommandSender Sender;
	public Command Command;
	public String Label;
	public String[] Args;
	public boolean IsCancelled;
	
	public String Name;
}
