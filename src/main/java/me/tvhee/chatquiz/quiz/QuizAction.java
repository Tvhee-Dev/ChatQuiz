package me.tvhee.chatquiz.quiz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class QuizAction
{
	private final String command;
	private final String message;

	public QuizAction(String command, String message)
	{
		this.command = command;
		this.message = message;
	}

	public void execute(QuizzedPlayer player)
	{
		if(command != null)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getPlayer().getName()));

		if(message != null)
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message.replaceAll("%player%", player.getPlayer().getName())));
	}

	public String getCommand()
	{
		return command;
	}

	public String getMessage()
	{
		return message;
	}
}
