package me.tvhee.chatquiz.listeners;

import me.tvhee.chatquiz.quiz.Quiz;
import me.tvhee.chatquiz.quiz.QuizzedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();

		if(QuizzedPlayer.isPlaying(player))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		Player player = e.getPlayer();

		if(QuizzedPlayer.isPlaying(player))
		{
			e.setCancelled(true);
			return;
		}

		e.getRecipients().removeIf(QuizzedPlayer::isPlaying);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		if(QuizzedPlayer.isPlaying(player))
			QuizzedPlayer.getQuizzedPlayer(player).stopQuiz(Quiz.QuizStopReason.LEFT);
	}

	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent e)
	{
		Player player = e.getPlayer();
		String message = e.getMessage();
		String[] args = message.split(" ");

		if(!QuizzedPlayer.isPlaying(player))
			return;

		QuizzedPlayer quizzedPlayer = QuizzedPlayer.getQuizzedPlayer(player);

		if(e.getMessage().startsWith("/quizanswer") && args.length == 2)
			quizzedPlayer.processAnswer(args[1]);
		else if(e.getMessage().startsWith("/quizquit") && args.length == 1)
			quizzedPlayer.stopQuiz(Quiz.QuizStopReason.QUIT);

		e.setCancelled(true);
	}
}
