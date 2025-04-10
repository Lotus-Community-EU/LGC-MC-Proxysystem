//Created by Maurice H. at 06.04.2025
package eu.lotusgaming.mc.game.event;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.Prefix;
import eu.lotusgaming.mc.misc.util.MaintenanceInfo;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MaintenanceHandler implements Listener{
	
	@EventHandler
	public void onLogin(LoginEvent event) {
		String uuid = event.getConnection().getUniqueId().toString().replace("-", "");
		Main.logger.info(event.getConnection().getUniqueId() + " tried to log in.");
		MaintenanceInfo mi = new MaintenanceInfo();
		LotusController lc = new LotusController();
		if (mi.getState()) {
			if (mi.getAllowedUniqueIDs().contains(uuid)) {
				Main.logger.info("Player " + uuid + " is allowed to join.");
				event.setCancelled(false);
				return;
			}
			event.setCancelled(true);
			event.getConnection().disconnect(TextComponent.fromLegacy(lc.getPrefix(Prefix.SCOREBOARD) + "\n \n§cThe Server is currently in maintenance mode. \n§7Reason: \n§f" + mi.getReason()));
			//event.getConnection().disconnect("§cServer is currently in maintenance mode.\n§7Reason: " + mi.getReason());
			Main.logger.info("Player " + uuid + " was kicked for maintenance.");
		}
	}
	
	
}