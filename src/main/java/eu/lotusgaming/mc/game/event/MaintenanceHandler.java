//Created by Maurice H. at 06.04.2025
package eu.lotusgaming.mc.game.event;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.Prefix;
import eu.lotusgaming.mc.misc.util.MaintenanceInfo;
import net.kyori.adventure.text.Component;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;

public class MaintenanceHandler {
	
	@Subscribe
	public void onLogin(LoginEvent event) {
		String uuid = event.getPlayer().getUniqueId().toString().replace("-", "");
		Main.logger.info(event.getPlayer().getUniqueId() + " tried to log in.");
		MaintenanceInfo mi = new MaintenanceInfo();
		LotusController lc = new LotusController();
		if (mi.getState()) {
			if (mi.getAllowedUniqueIDs().contains(uuid)) {
				Main.logger.info("Player " + uuid + " is allowed to join.");
				return;
			}
			event.setResult(LoginEvent.ComponentResult.denied(Component.text(lc.getPrefix(Prefix.SCOREBOARD) + "\n \n§cThe Server is currently in maintenance mode. \n§7Reason: \n§f" + mi.getReason())));
			Main.logger.info("Player " + uuid + " was kicked for maintenance.");
		}
	}
	
	
}
