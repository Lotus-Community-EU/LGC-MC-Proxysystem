//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.bot.event;

import eu.lotusgaming.mc.bot.command.MaintenanceCommand;
import eu.lotusgaming.mc.main.LotusManager;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.CommandAdder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyClass extends ListenerAdapter{
	
	public void onReady(ReadyEvent event) {
		Main.logger.info("Bot has started as " + event.getJDA().getSelfUser().getEffectiveName() + " on " + event.getJDA().getGuilds().size() + " Guilds.");
		CommandAdder.addCommands(event.getJDA());
		event.getJDA().addEventListener(new ChatReportContextHandler());
		event.getJDA().addEventListener(new MaintenanceCommand());
		new LotusManager().init(event.getJDA());
	}

}

