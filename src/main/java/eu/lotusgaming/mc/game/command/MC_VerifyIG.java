//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.game.command;

import eu.lotusgaming.mc.bot.command.MC_Verify;
import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.Prefix;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MC_VerifyIG extends Command{

	public MC_VerifyIG(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			Main.logger.info("Please execute this command ingame.");
		}else {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			LotusController lc = new LotusController();
			if(args.length == 2) {
				String service = args[0];
				String code = args[1];
				if(service.equalsIgnoreCase("discord")) {
					if(Main.hashName.containsKey(player.getName())) {
						String codeToMatch = Main.hashName.get(player.getName());
						long userId = Main.hashId.get(player.getName());
						if(code.equalsIgnoreCase(codeToMatch)) {
							String uuid = player.getUniqueId().toString();
							MC_Verify.sendMessageSuccess(player.getName(), uuid, userId);
							player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "You've been verified successfully."));
						}else {
							player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cThe code you entered is wrong. Please restart the verification process."));
							MC_Verify.sendMessageError(player.getName(), userId);
							Main.hashId.remove(player.getName());
							Main.hashName.remove(player.getName());
						}
					}else {
						player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cYou don't have an outstanding verification process."));
					}
				}else if(service.equalsIgnoreCase("website")) {
					player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy("§cThe Website Verification is not yet implemented!"));
				}else {
					player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy("§7Usage: /verify <discord|website> <Code>"));
				}
			}else {
				player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy("§7Usage: /verify <discord|website> <Code>"));
			}
		}
	}
}