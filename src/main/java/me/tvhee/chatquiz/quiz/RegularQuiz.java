package me.tvhee.chatquiz.quiz;

import java.util.List;
import java.util.UUID;
import me.tvhee.chatquiz.ChatQuizPlugin;
import me.tvhee.chatquiz.quiz.question.Question;
import org.bukkit.ChatColor;

public class RegularQuiz extends Quiz
{
	private final QuizAction passAction;
	private final QuizAction failAction;
	private final int passAmount;

	public RegularQuiz(ChatQuizPlugin plugin, String name, QuizAction passAction, QuizAction failAction, int passAmount, int timeout, List<Question> questions, List<UUID> completedPlayers)
	{
		super(plugin, name, timeout, questions, completedPlayers);
		this.passAction = passAction;
		this.failAction = failAction;
		this.passAmount = passAmount;
	}

	public int getPassAmount()
	{
		return passAmount;
	}

	@Override
	public boolean parseAnswer(QuizzedPlayer player, String answer)
	{
		return ChatColor.stripColor(getQuestions().get(player.getCurrentQuestion()).getRightAnswer()).equalsIgnoreCase(answer);
	}

	@Override
	public boolean stopQuiz(QuizzedPlayer player, QuizStopReason stopReason)
	{
		boolean finished = super.stopQuiz(player, stopReason);

		if(finished && stopReason == QuizStopReason.FINISHED)
		{
			QuizAction action;

			if(player.getAnsweredRight() >= passAmount)
				action = passAction;
			else
				action = failAction;

			if(action != null)
				action.execute(player);

			return true;
		}

		return finished;
	}
}
