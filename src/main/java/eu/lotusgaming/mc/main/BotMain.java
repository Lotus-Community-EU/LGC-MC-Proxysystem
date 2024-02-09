//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.main;

import java.io.IOException;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.mc.bot.event.ChatBridgeToMinecraft;
import eu.lotusgaming.mc.bot.event.ReadyClass;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BotMain {
	
	public static void startBot() {
		String token = null;
		try {
			YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
			token = cfg.getString("System.Bottoken");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(token != null && !token.equalsIgnoreCase("The Bottoken goes in here.")) {
			JDABuilder builder = JDABuilder.createDefault(token);
			builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS);
			builder.setActivity(Activity.playing("Minecraft"));
			builder.setStatus(OnlineStatus.ONLINE);
			builder.addEventListeners(new ReadyClass());
			builder.addEventListeners(new ChatBridgeToMinecraft());
			builder.build();
		}else {
			Main.logger.severe("Invalid or no bot token provided!");
		}
	}

}

