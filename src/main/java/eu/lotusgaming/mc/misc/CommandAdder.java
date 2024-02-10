//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandAdder {
	
	public static void addCommands(JDA jda) {
		for(Guild guilds : jda.getGuilds()) {
			guilds.updateCommands().addCommands(
					Commands.slash("verify", "Verify yourself with Minecraft or the Website")
					.addOption(OptionType.STRING, "service", "On what service would you verify yourself? Choose 'website' or 'minecraft'!")
					.addOption(OptionType.STRING, "playername", "Enter your Minecraft Name in here.")
					).queue();
		}
	}

}

