package me.tvhee.chatquiz.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentLoader
{
	private final Map<String, List<String>> comments = new HashMap<>();

	public void load(File configFile)
	{
		comments.clear();

		try
		{
			FileReader fileReader = new FileReader(configFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> commentsForKeys = new ArrayList<>();
			Parser parser = new Parser();

			for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
			{
				String parsed = parser.parse(line);

				if(parser.isComment())
				{
					commentsForKeys.add(parsed);
				}
				else
				{
					if(!commentsForKeys.isEmpty())
					{
						comments.put(parsed, commentsForKeys);
						commentsForKeys = new ArrayList<>();
					}
				}
			}

			fileReader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void apply(File configFile)
	{
		try
		{
			FileReader fileReader = new FileReader(configFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuilder newFile = new StringBuilder();
			Parser parser = new Parser();

			List<String> header = this.comments.get("");

			if(header != null)
			{
				for(String comment : header)
					newFile.append(parser.withSpaces(comment)).append("\n");
			}

			for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
			{
				String parsed = parser.parse(line).trim();

				if(comments.containsKey(parsed))
				{
					List<String> comments = this.comments.get(parsed);

					for(String comment : comments)
						newFile.append(parser.withSpaces(comment)).append("\n");
				}

				newFile.append(line).append("\n");
			}

			fileReader.close();
			String newFileContent = newFile.toString();

			FileWriter fileWriter = new FileWriter(configFile, false);
			fileWriter.write(newFileContent);
			fileWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private static final class Parser
	{
		private final YamlKey yamlKey = new YamlKey();
		private int spaces;
		private boolean comment;

		public String parse(String line)
		{
			spaces = 0;
			comment = false;

			String withoutSpaces = line.trim();

			if(withoutSpaces.startsWith("#") || withoutSpaces.isEmpty())
			{
				this.comment = true;
				return withoutSpaces;
			}

			String key = line.split(":")[0].replaceAll(" ", "");

			for(int i = 0; i < line.length(); i++)
			{
				if(line.charAt(i) == ' ')
					spaces += 1;
				else
					break;
			}

			return yamlKey.append(key, spaces / 2);
		}

		public boolean isComment()
		{
			return comment;
		}

		public String withSpaces(String line)
		{
			if(spaces == 0)
				return line;

			StringBuilder spaceBuilder = new StringBuilder();

			for(int i = 0; i < spaces; i++)
				spaceBuilder.append(" ");

			return spaceBuilder.append(line).toString();
		}
	}

	private static final class YamlKey
	{
		private String key = "";

		public String append(String subKey, int newLength)
		{
			String[] subkeys = this.key.split("\\.");

			if(newLength != 0 && subkeys[0] != null)
			{
				StringBuilder keyBuilder = new StringBuilder(subkeys[0]);

				for(int i = 1; i < newLength - 1; i++)
					keyBuilder.append(".").append(subkeys[i]);

				this.key = keyBuilder.toString();
			}
			else
			{
				this.key = "";
			}

			if(subKey.startsWith(".") || this.key.equals(""))
				this.key = this.key + subKey;
			else
				this.key = this.key + "." + subKey;

			if(this.key.startsWith("\\."))
				this.key = this.key.replaceFirst("\\.", "");

			return this.key;
		}
	}
}
