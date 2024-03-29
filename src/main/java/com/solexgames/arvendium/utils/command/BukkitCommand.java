package com.solexgames.arvendium.utils.command;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BukkitCommand extends Command {
	private Plugin owningPlugin;
	private CommandExecutor executor;
	protected BukkitCompleter completer;

	protected BukkitCommand(String label, CommandExecutor executor, Plugin owner) {
		super(label);
		this.executor = executor;
		this.owningPlugin = owner;
		this.usageMessage = "";
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		boolean success = false;
		if (!this.owningPlugin.isEnabled()) {
			return false;
		}
		if (!this.testPermission(sender)) {
			return true;
		}
		try {
			success = this.executor.onCommand(sender, this, commandLabel, args);
		} catch (Throwable ex) {
			throw new CommandException(
					"Unhandled exception executing commands '" + commandLabel
							+ "' in plugin "
							+ this.owningPlugin.getDescription().getFullName(),
					ex);
		}
		if (!success && this.usageMessage.length() > 0) {
			for (String line : this.usageMessage.replace("<commands>",
					commandLabel).split("\n")) {
				sender.sendMessage(line);
			}
		}
		return success;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias,
			String[] args) throws CommandException, IllegalArgumentException {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(args, "Arguments cannot be null");
		Validate.notNull(alias, "Alias cannot be null");
		List<String> completions = null;
		try {
			if (this.completer != null) {
				completions = this.completer.onTabComplete(sender, this, alias,
						args);
			}
			if (completions == null && this.executor instanceof TabCompleter) {
				completions = ((TabCompleter) (this.executor))
						.onTabComplete(sender, this, alias, args);
			}
		} catch (Throwable ex) {
			StringBuilder message = new StringBuilder();
			message.append(
					"Unhandled exception during tab completion for commands '/")
					.append(alias).append(' ');
			for (String arg : args) {
				message.append(arg).append(' ');
			}
			message.deleteCharAt(message.length() - 1).append("' in plugin ")
					.append(this.owningPlugin.getDescription().getFullName());
			throw new CommandException(message.toString(), ex);
		}
		if (completions == null) {
			return super.tabComplete(sender, alias, args);
		}
		return completions;
	}
}
