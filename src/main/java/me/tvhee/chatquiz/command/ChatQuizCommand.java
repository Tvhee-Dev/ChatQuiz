package me.tvhee.chatquiz.command;

import java.util.List;
import me.tvhee.chatquiz.ChatQuizPlugin;
import me.tvhee.chatquiz.quiz.Quiz;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ChatQuizCommand implements CommandExecutor, TabCompleter
{
	private final ChatQuizPlugin plugin;

	public ChatQuizCommand(ChatQuizPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
			return true;
		}

		if(args.length > 0)
		{
			Quiz quiz = plugin.getQuiz(args[0]);

			if(quiz == null)
			{
				sender.sendMessage(ChatColor.RED + "Quiz " + args[0] + " not found!");
				return true;
			}
			else if(!sender.hasPermission("chatquiz.quiz." + quiz.getName()))
			{
				sender.sendMessage(ChatColor.RED + "You don't have permission (chatquiz.quiz." + quiz.getName() + ") to attend this quiz!");
				return true;
			}
			else if(quiz.getCompletedPlayers().contains(((Player) sender).getUniqueId()) && !sender.hasPermission("chatquiz.redoquiz"))
			{
				sender.sendMessage(ChatColor.RED + "You have already finished quiz " + args[0] + "!");
				return true;
			}

			quiz.startQuiz((Player) sender);
			return true;
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "/chatquiz <quiz>");
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length == 1)
			return plugin.getQuizzes();

		return null;
	}
}
