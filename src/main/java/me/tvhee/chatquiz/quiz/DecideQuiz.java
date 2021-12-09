package me.tvhee.chatquiz.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.tvhee.chatquiz.ChatQuizPlugin;
import me.tvhee.chatquiz.quiz.question.DecideQuestion;

public class DecideQuiz extends Quiz
{
	private final QuizAction finishAction;

	public DecideQuiz(ChatQuizPlugin plugin, String name, int timeout, QuizAction finishAction, List<DecideQuestion> questions, List<UUID> completedPlayers)
	{
		super(plugin, name, timeout, new ArrayList<>(questions), completedPlayers);
		this.finishAction = finishAction;
	}

	@Override
	public boolean stopQuiz(QuizzedPlayer player, QuizStopReason stopReason)
	{
		boolean finished = super.stopQuiz(player, stopReason);

		if(finished && finishAction != null && stopReason == QuizStopReason.FINISHED)
			finishAction.execute(player);

		return finished;
	}

	@Override
	public boolean parseAnswer(QuizzedPlayer player, String answer)
	{
		DecideQuestion question = (DecideQuestion) getQuestions().get(player.getCurrentQuestion());
		QuizAction action = question.getActions().get(answer);

		if(action != null)
			action.execute(player);

		return true;
	}
}
