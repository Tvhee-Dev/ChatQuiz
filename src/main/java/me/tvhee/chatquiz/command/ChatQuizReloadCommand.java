package me.tvhee.chatquiz.command;

import me.tvhee.chatquiz.ChatQuizPlugin;
import me.tvhee.chatquiz.quiz.Quiz;
import me.tvhee.chatquiz.quiz.QuizzedPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChatQuizReloadCommand implements CommandExecutor
{
	private final ChatQuizPlugin plugin;

	public ChatQuizReloadCommand(ChatQuizPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		for(QuizzedPlayer player : QuizzedPlayer.getPlayingPlayers())
			player.stopQuiz(Quiz.QuizStopReason.RELOAD);

		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		plugin.readConfig();
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6ChatQuiz &aPlugin has been reloaded!"));
		return true;
	}
}
