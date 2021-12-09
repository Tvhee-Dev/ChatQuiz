package me.tvhee.chatquiz.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.tvhee.chatquiz.ChatQuizPlugin;
import me.tvhee.chatquiz.quiz.question.Question;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class Quiz
{
	private final ChatQuizPlugin plugin;
	private final String name;
	private final int timeout;
	private final List<Question> questions;
	private final List<UUID> completedPlayers;

	public Quiz(ChatQuizPlugin plugin, String name, int timeout, List<Question> questions, List<UUID> completedPlayers)
	{
		this.plugin = plugin;
		this.name = name;
		this.timeout = timeout;
		this.questions = questions;
		this.completedPlayers = completedPlayers;
	}

	public void startQuiz(Player player)
	{
		if(QuizzedPlayer.isPlaying(player))
			return;

		for(int i = 0; i < 500; i++)
			player.sendMessage(" ");

		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 100));
		QuizzedPlayer quizzedPlayer = new QuizzedPlayer(plugin, player, this);
		quizzedPlayer.askNextQuestion();
	}

	public abstract boolean parseAnswer(QuizzedPlayer player, String answer);

	public boolean stopQuiz(QuizzedPlayer player, QuizStopReason stopReason)
	{
		if(!QuizzedPlayer.isPlaying(player.getPlayer()))
			return false;

		if(player.getPlayer() != null && player.getPlayer().isOnline())
		{
			for(int i = 0; i < 500; i++)
				player.getPlayer().sendMessage(" ");

			if(stopReason == QuizStopReason.RELOAD)
				player.getPlayer().sendMessage(ChatColor.GREEN + "The plugin is reloading, your quiz is cancelled!");
			else if(stopReason == QuizStopReason.TIMEOUT)
				player.getPlayer().sendMessage(ChatColor.RED + "You did not answer in time, your quiz is cancelled!");
			else if(stopReason == QuizStopReason.FINISHED)
				player.getPlayer().sendMessage(ChatColor.GOLD + "You have successfully finished the quiz!");
			else if(stopReason == QuizStopReason.QUIT)
				player.getPlayer().sendMessage(ChatColor.RED + "You have successfully quit the quiz!");

			player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
		}

		player.destroy();

		this.completedPlayers.add(player.getPlayer().getUniqueId());
		return true;
	}

	public String getName()
	{
		return name;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public List<Question> getQuestions()
	{
		return questions;
	}

	public List<UUID> getCompletedPlayers()
	{
		return completedPlayers;
	}

	public List<String> getCompletedPlayersAsStringList()
	{
		List<String> uuidStrings = new ArrayList<>();

		for(UUID uuid : completedPlayers)
			uuidStrings.add(uuid.toString());

		return uuidStrings;
	}

	public enum QuizStopReason
	{
		FINISHED, LEFT, TIMEOUT, RELOAD, QUIT
	}
}
