//Created by Maurice H. at 30.01.2025
package eu.lotusgaming.mc.game.command.punishments;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Prefix;
import eu.lotusgaming.mc.misc.util.LotusPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WarnCommand extends Command {

	JDA jda;
	public WarnCommand(String name, JDA jda) {
		super(name);
		this.jda = jda;
	}
	
	/*
	 *  Usage: /warn <Player> <Reason>
	 */

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			Main.logger.info("This command is only for ingame usage!");
		}else {
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer)sender;
			LotusController lc = new LotusController();
			if(args.length >= 1) {
				if(proxiedPlayer.hasPermission("lgc.punishments.warn")) {
					if(args[0].length() >= 3 && args[0].length() <= 16) {
						ProxiedPlayer target = Main.main.getProxy().getPlayer(args[0]);
						if(target != null) {
							StringBuilder sb = new StringBuilder();
							for (int i = 1; i < args.length; i++) {
								sb.append(args[i] + " ");
							}
							String reason = sb.toString();
							if(reason.isEmpty()) {
								Main.logger.info("Reason cannot be empty.");
							}else {
								//target.sendMessage("you have been warned for " + reason);
								//proxiedPlayer.sendMessage("you warned " + target.getName() + " for " + reason); 
								target.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(target, "system.punishment.warn.received").replace("%reason%", reason).replace("%issuer%", proxiedPlayer.getName())));
								proxiedPlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy((lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(target, "system.punishment.warn.received").replace("%reason%", reason).replace("%player%", target.getName()))));
								sendPunishmentToDiscord(proxiedPlayer, target, reason);
								addPunishmentToDB(proxiedPlayer, target, reason);
							}
						}else {
							//proxiedPlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(proxiedPlayer, "general.playerOffline")));
							LotusPlayer lp = new LotusPlayer(args[0]);
							StringBuilder sb = new StringBuilder();
							for (int i = 1; i < args.length; i++) {
								sb.append(args[i] + " ");
							}
							String reason = sb.toString();
							if(reason.isEmpty()) {
								Main.logger.info("Reason cannot be empty.");
							}else {
								//target.sendMessage("you have been warned for " + reason);
								//proxiedPlayer.sendMessage("you warned " + target.getName() + " for " + reason); 
								//target.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(target, "system.punishment.warn.received").replace("%reason%", reason).replace("%issuer%", proxiedPlayer.getName())));
								proxiedPlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy((lc.getPrefix(Prefix.System) + lc.sendMessageToFormat(target, "system.punishment.warn.received").replace("%reason%", reason).replace("%player%", lp.getName()))));
								sendPunishmentToDiscord(proxiedPlayer, lp, reason);
								addPunishmentToDB(proxiedPlayer, lp, reason);
							}
						}
					}else {
						proxiedPlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(proxiedPlayer, "system.punishment.offsetNamelength")));
					}
				}else {
					lc.noPerm(proxiedPlayer, "lgc.punishments.warn");
				}
			}else {
				proxiedPlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + lc.sendMessageToFormat(proxiedPlayer, "global.args") + "§7/warn <Player> <Reason>"));
			}
		}
	}
	
	void sendPunishmentToDiscord(ProxiedPlayer issuer, ProxiedPlayer target, String reason) {
		TextChannel channel = jda.getGuildById(1066812641768640542L).getTextChannelById(1334496986988019712L);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(issuer.getName(), null, "https://minotar.net/avatar/" + issuer.getUniqueId().toString().replace("-", "") + "/256.png");
		eb.setThumbnail("https://minotar.net/avatar/" + target.getUniqueId().toString().replace("-", "") + "/256.png");
		eb.setTitle(issuer.getName() + " warned " + target.getName());
		eb.setDescription(reason);
		LotusPlayer lp = new LotusPlayer(issuer);
		eb.setColor(Color.decode(lp.getColorCode()));
		channel.sendMessageEmbeds(eb.build()).queue();
	}
	
	void sendPunishmentToDiscord(ProxiedPlayer issuer, LotusPlayer lp1, String reason) {
		TextChannel channel = jda.getGuildById(1066812641768640542L).getTextChannelById(1334496986988019712L);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(issuer.getName(), null, "https://minotar.net/avatar/" + issuer.getUniqueId().toString().replace("-", "") + "/256.png");
		eb.setThumbnail("https://minotar.net/avatar/" + lp1.getUuid().replace("-", "") + "/256.png");
		eb.setTitle(issuer.getName() + " warned " + lp1.getName());
		eb.setDescription(reason);
		LotusPlayer lp = new LotusPlayer(issuer);
		eb.setColor(Color.decode(lp.getColorCode()));
		channel.sendMessageEmbeds(eb.build()).queue();
	}
	
	void addPunishmentToDB(ProxiedPlayer issuer, ProxiedPlayer target, String reason) {
		try(PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_punishments (uuid_issuer, uuid_banned, issuedAt, expireAt, reason, isActive, issuedWhere, punishmentType) VALUES (?, ?,?,?,?,?,?,?)")) {
            ps.setString(1, issuer.getUniqueId().toString());
            ps.setString(2, target.getUniqueId().toString());
            ps.setLong(3, System.currentTimeMillis());
            ps.setLong(4, -1);
            ps.setString(5, reason);
            ps.setBoolean(6, true);
            ps.setInt(7, 1); //1 is referenced to an ingame-ban, 2 is referenced to website ban
            ps.setString(8, "WARN");
            ps.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void addPunishmentToDB(ProxiedPlayer issuer, LotusPlayer target, String reason) {
		try(PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_punishments (uuid_issuer, uuid_banned, issuedAt, expireAt, reason, isActive, issuedWhere, punishmentType) VALUES (?, ?,?,?,?,?,?,?)")) {
            ps.setString(1, issuer.getUniqueId().toString());
            ps.setString(2, target.getUuid());
            ps.setLong(3, System.currentTimeMillis());
            ps.setLong(4, -1);
            ps.setString(5, reason);
            ps.setBoolean(6, true);
            ps.setInt(7, 1); //1 is referenced to an ingame-ban, 2 is referenced to website ban
            ps.setString(8, "WARN");
            ps.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
}