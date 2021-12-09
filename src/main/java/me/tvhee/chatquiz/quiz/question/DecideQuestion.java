package me.tvhee.chatquiz.quiz.question;

import java.util.List;
import java.util.Map;
import me.tvhee.chatquiz.quiz.QuizAction;

public class DecideQuestion extends Question
{
	private final Map<String, QuizAction> actions;

	public DecideQuestion(String question, List<String> choises, Map<String, QuizAction> actions)
	{
		super(question, "", choises);
		this.actions = actions;
	}

	public Map<String, QuizAction> getActions()
	{
		return actions;
	}
}
