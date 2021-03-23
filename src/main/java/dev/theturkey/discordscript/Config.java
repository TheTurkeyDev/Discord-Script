package dev.theturkey.discordscript;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config
{
	public static String discordBotToken = "";

	public static void load()
	{
		try
		{
			Properties properties = new Properties();
			File file = new File("settings.prop");
			if(!file.exists())
			{
				file.createNewFile();
				System.err.println("Credentials file generated. Please enter information in file and restart the plugin.");
				return;
			}
			FileInputStream iStream = new FileInputStream(file);
			properties.load(iStream);
			discordBotToken = properties.getProperty("DiscordBotToken");
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
