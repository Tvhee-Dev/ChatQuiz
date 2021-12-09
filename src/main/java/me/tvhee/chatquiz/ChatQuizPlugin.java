package me.tvhee.chatquiz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.tvhee.chatquiz.api.CommentLoader;
import me.tvhee.chatquiz.command.ChatQuizCommand;
import me.tvhee.chatquiz.command.ChatQuizReloadCommand;
import me.tvhee.chatquiz.listeners.PlayerListener;
import me.tvhee.chatquiz.quiz.QuizAction;
import me.tvhee.chatquiz.quiz.DecideQuiz;
import me.tvhee.chatquiz.quiz.Quiz;
import me.tvhee.chatquiz.quiz.question.Question;
import me.tvhee.chatquiz.quiz.question.DecideQuestion;
import me.tvhee.chatquiz.quiz.QuizzedPlayer;
import me.tvhee.chatquiz.quiz.RegularQuiz;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatQuizPlugin extends JavaPlugin
{
    private final Map<String, Quiz> quizzes = new HashMap<>();

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        readConfig();

        ChatQuizCommand chatQuizCommand = new ChatQuizCommand(this);
        PluginCommand chatQuizPluginCommand = getCommand("chatquiz");
        chatQuizPluginCommand.setExecutor(chatQuizCommand);
        chatQuizPluginCommand.setTabCompleter(chatQuizCommand);

        getCommand("chatquizreload").setExecutor(new ChatQuizReloadCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getLogger().info("has been enabled!");
        getLogger().info("made by Tvhee for padfoot");
    }

    public void readConfig()
    {
        quizzes.clear();
        ConfigurationSection regularQuizzes = getConfig().getConfigurationSection("plugin.regular-quizzes");

        if(regularQuizzes != null)
        {
            for(String quizName : regularQuizzes.getKeys(false))
            {
                QuizAction passAction = new QuizAction(getConfig().getString("plugin.regular-quizzes." + quizName + ".pass-actions.run-command"), getConfig().getString("plugin.regular-quizzes." + quizName + ".pass-actions.broadcast"));
                QuizAction failAction = new QuizAction(getConfig().getString("plugin.regular-quizzes." + quizName + ".fail-actions.run-command"), getConfig().getString("plugin.regular-quizzes." + quizName + ".fail-actions.broadcast"));
                int passAmount = getConfig().getInt("plugin.regular-quizzes." + quizName + ".pass-amount");
                int timeout = getConfig().getInt("plugin.regular-quizzes." + quizName + ".timeout");
                List<UUID> completed = new ArrayList<>();

                for(String uuid : getConfig().getStringList("plugin.regular-quizzes." + quizName + ".completed"))
                    completed.add(UUID.fromString(uuid));

                List<Question> questions = new ArrayList<>();
                ConfigurationSection questionsSection = getConfig().getConfigurationSection("plugin.regular-quizzes." + quizName + ".questions");

                if(questionsSection != null)
                {
                    for(String questionName : questionsSection.getKeys(false))
                    {
                        String question = getConfig().getString("plugin.regular-quizzes." + quizName + ".questions." + questionName + ".question");
                        List<String> choices = getConfig().getStringList("plugin.regular-quizzes." + quizName + ".questions." + questionName + ".choices");
                        String answer = getConfig().getString("plugin.regular-quizzes." + quizName + ".questions." + questionName + ".answer");
                        questions.add(new Question(question, answer, choices));
                    }
                }

                quizzes.put(quizName, new RegularQuiz(this, quizName, passAction, failAction, passAmount, timeout, questions, completed));
            }
        }

        ConfigurationSection decideQuizzes = getConfig().getConfigurationSection("plugin.deciding-quizzes");

        if(decideQuizzes != null)
        {
            for(String quizName : decideQuizzes.getKeys(false))
            {
                QuizAction finishAction = new QuizAction(getConfig().getString("plugin.deciding-quizzes." + quizName + ".finish-actions.run-command"), getConfig().getString("plugin.deciding-quizzes." + quizName + ".finish-actions.broadcast"));
                int timeout = getConfig().getInt("plugin.deciding-quizzes." + quizName + ".timeout");
                List<UUID> completed = new ArrayList<>();

                for(String uuid : getConfig().getStringList("plugin.deciding-quizzes." + quizName + ".completed"))
                    completed.add(UUID.fromString(uuid));

                List<DecideQuestion> questions = new ArrayList<>();
                ConfigurationSection questionsSection = getConfig().getConfigurationSection("plugin.deciding-quizzes." + quizName + ".questions");

                if(questionsSection != null)
                {
                    for(String questionName : questionsSection.getKeys(false))
                    {
                        String question = getConfig().getString("plugin.deciding-quizzes." + quizName + ".questions." + questionName + ".question");
                        List<String> choices = getConfig().getStringList("plugin.deciding-quizzes." + quizName + ".questions." + questionName + ".choices");
                        Map<String, QuizAction> answerActions = new HashMap<>();

                        ConfigurationSection answerActionSection = getConfig().getConfigurationSection("plugin.deciding-quizzes." + quizName + ".questions." + questionName + ".answer-actions");

                        if(answerActionSection != null)
                        {
                            for(String answerActionName : answerActionSection.getKeys(false))
                            {
                                String answer = getConfig().getString("plugin.deciding-quizzes." + quizName + ".questions." + questionName + ".answer-actions." + answerActionName + ".answer");
                                String command = getConfig().getString("plugin.deciding-quizzes." + quizName + ".questions." + questionName + ".answer-actions." + answerActionName + ".answer.actions.run-command");
                                String broadcast = getConfig().getString("plugin.deciding-quizzes." + quizName + ".questions." + questionName + ".answer-actions." + answerActionName + ".answer.actions.broadcast");
                                answerActions.put(answer, new QuizAction(command, broadcast));
                            }
                        }

                        questions.add(new DecideQuestion(question, choices, answerActions));
                    }
                }

                quizzes.put(quizName, new DecideQuiz(this, quizName, timeout, finishAction, questions, completed));
            }
        }
    }

    @Override
    public void saveConfig()
    {
        File configFile = new File(getDataFolder(), "config.yml");
        CommentLoader commentLoader = new CommentLoader();

         if(configFile.exists())
            commentLoader.load(configFile);

        super.saveConfig();

        if(configFile.exists())
            commentLoader.apply(configFile);
    }

    public Quiz getQuiz(String name)
    {
        return this.quizzes.get(name);
    }

    public List<String> getQuizzes()
    {
        return new ArrayList<>(this.quizzes.keySet());
    }

    @Override
    public void onDisable()
    {
        for(QuizzedPlayer player : QuizzedPlayer.getPlayingPlayers())
            player.stopQuiz(Quiz.QuizStopReason.RELOAD);

        for(Quiz quiz : this.quizzes.values())
        {
            if(quiz instanceof DecideQuiz)
                getConfig().set("plugin.deciding-quizzes." + quiz.getName() + ".completed", quiz.getCompletedPlayersAsStringList());
            else
                getConfig().set("plugin.regular-quizzes." + quiz.getName() + ".completed", quiz.getCompletedPlayersAsStringList());
        }

        saveConfig();

        getLogger().info("has been disabled!");
        getLogger().info("made by Tvhee for padfoot");
    }
}
