//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandAdder {
	
	public static void addCommands(JDA jda) {
		for(Guild guilds : jda.getGuilds()) {
			guilds.updateCommands().addCommands(
					Commands.slash("verify", "Verify yourself with Minecraft or the Website")
					.addOption(OptionType.STRING, "service", "On what service would you verify yourself? Choose 'website' or 'minecraft'!")
					.addOption(OptionType.STRING, "playername", "Enter your Minecraft Name in here."),
					
					Commands.context(Type.MESSAGE, "Report this! (DCB)"),
					
					Commands.slash("maintenance", "Main command for the Maintenance System on Discord")
					.setContexts(InteractionContextType.GUILD)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
					.addSubcommands(
							new SubcommandData("addplayer", "Exempts a player from the Maintenance System.")
							.addOption(OptionType.STRING, "playername", "The Minecraft Player Name", true)
							.addOption(OptionType.STRING, "reason", "The reason why to add that player", true),
							
							new SubcommandData("removeplayer", "Un-Exempts a player from the Maintenance System")
							.addOption(OptionType.STRING, "playername", "The Minecraft Player Name", true)
							.addOption(OptionType.STRING, "reason", "The reason why to remove that player", true),
							
							new SubcommandData("maintenance", "De/Activates the Maintenance System")
							.addOption(OptionType.BOOLEAN, "state", "Whether to activate or deactivate the maintenance", true)
							.addOption(OptionType.STRING, "reason", "Reason why the maintenance has been toggled (Message will only be seen when activated)")
							)
					).queue();
		}
	}

}

