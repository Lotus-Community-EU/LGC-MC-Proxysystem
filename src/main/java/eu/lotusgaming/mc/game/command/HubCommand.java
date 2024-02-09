//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.game.command;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.Prefix;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command{

	public HubCommand(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			Main.logger.info("This command is only for ingame usage!");
		}else {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			LotusController lc = new LotusController();
			if(player.getServer().getInfo().getName().equalsIgnoreCase("lobby")) {
				player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§yYou're already on the lobby server."));
			}else {
				player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§aSending to the lobby server..."));
				ServerInfo info = ProxyServer.getInstance().getServerInfo("lobby");
				player.connect(info);
			}
		}
	}
}