package core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandWrapper {
	public CommandSender Sender;
	public Command Command;
	public String CommandLabel;
	public String[] Arguments;
	public boolean IsCancelled;
}
