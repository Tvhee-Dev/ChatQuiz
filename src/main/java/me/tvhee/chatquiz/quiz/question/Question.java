package me.tvhee.chatquiz.quiz.question;

import java.util.List;

public class Question
{
	private final String question;
	private final String rightAnswer;
	private final List<String> choises;

	public Question(String question, String rightAnswer, List<String> choises)
	{
		this.question = question;
		this.rightAnswer = rightAnswer;
		this.choises = choises;
	}

	public String getQuestion()
	{
		return question;
	}

	public String getRightAnswer()
	{
		return rightAnswer;
	}

	public List<String> getChoises()
	{
		return choises;
	}
}
