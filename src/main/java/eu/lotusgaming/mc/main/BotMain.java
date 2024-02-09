//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.main;

import java.io.IOException;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.mc.bot.event.ReadyClass;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class BotMain {
	
	public static void startBot() {
		String token = null;
		try {
			YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
			token = cfg.getString("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(token != null && !token.equalsIgnoreCase("The Bottoken goes in here.")) {
			JDABuilder builder = JDABuilder.createDefault(token);
			builder.setActivity(Activity.playing("Minecraft"));
			builder.setStatus(OnlineStatus.ONLINE);
			builder.addEventListeners(new ReadyClass());
			builder.build();
		}else {
			Main.logger.severe("Invalid or no bot token provided!");
		}
	}

}

