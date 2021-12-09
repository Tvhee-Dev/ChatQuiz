package me.tvhee.chatquiz.quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.tvhee.chatquiz.ChatQuizPlugin;
import me.tvhee.chatquiz.quiz.question.Question;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class QuizzedPlayer
{
	private static final Map<UUID, QuizzedPlayer> playing = new HashMap<>();
	private final ChatQuizPlugin plugin;
	private final Player player;
	private final Quiz quiz;
	private BukkitTask timeoutTask;
	private int currentQuestion = 0;
	private int answeredRight = 0;

	public QuizzedPlayer(ChatQuizPlugin plugin, Player player, Quiz quiz)
	{
		this.plugin = plugin;
		this.player = player;
		this.quiz = quiz;
		playing.put(player.getUniqueId(), this);
	}

	public static boolean isPlaying(Player player)
	{
		return playing.containsKey(player.getUniqueId());
	}

	public static QuizzedPlayer getQuizzedPlayer(Player player)
	{
		return playing.get(player.getUniqueId());
	}

	public static List<QuizzedPlayer> getPlayingPlayers()
	{
		return new ArrayList<>(playing.values());
	}

	public Player getPlayer()
	{
		return player;
	}

	public int getAnsweredRight()
	{
		return answeredRight;
	}

	public int getCurrentQuestion()
	{
		return currentQuestion;
	}

	public void destroy()
	{
		playing.remove(player.getUniqueId());
	}

	public void processAnswer(String answer)
	{
		if(timeoutTask != null)
			timeoutTask.cancel();

		boolean right = quiz.parseAnswer(this, answer);

		if(right)
			answeredRight++;

		currentQuestion++;
		askNextQuestion();
	}

	public void askNextQuestion()
	{
		if(currentQuestion >= quiz.getQuestions().size())
		{
			quiz.stopQuiz(this, Quiz.QuizStopReason.FINISHED);
			return;
		}

		Question question = quiz.getQuestions().get(currentQuestion);
		List<TextComponent> questions = new ArrayList<>();

		for(String answer : question.getChoises())
		{
			TextComponent answerPart = new TextComponent("[" + ChatColor.translateAlternateColorCodes('&', answer.trim()) + ChatColor.RESET + "] ");
			answerPart.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quizanswer " + ChatColor.stripColor(answer)));
			questions.add(answerPart);
		}

		TextComponent closeButton = new TextComponent(ChatColor.RED + "[QUIT]");
		closeButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quizquit"));
		questions.add(closeButton);

		for(int i = 0; i < 500; i++)
			player.sendMessage(" ");

		player.spigot().sendMessage(new TextComponent("Question " + (currentQuestion + 1) + ": " + ChatColor.translateAlternateColorCodes('&', question.getQuestion().replaceAll("%player%", player.getName()) + " ")));
		player.spigot().sendMessage(questions.toArray(new TextComponent[0]));
		timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> quiz.stopQuiz(this, Quiz.QuizStopReason.TIMEOUT), 1200L * quiz.getTimeout());
	}

	public void stopQuiz(Quiz.QuizStopReason reason)
	{
		quiz.stopQuiz(this, reason);
	}
}
