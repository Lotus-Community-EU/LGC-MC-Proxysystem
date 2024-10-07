package eu.lotusgaming.mc.game.command;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.misc.ChatBridgeUtils;
import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Prefix;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReportCommand extends Command{

	JDA jda;
	public ReportCommand(String name, JDA jda) {
		super(name);
		this.jda = jda;
	}
	
	//TODO continuation of report system.

	@SuppressWarnings("unused")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer)sender;
			LotusController lc = new LotusController();
			if(args.length == 0) {
				player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§7Usage: /report <Player> <Reason>"));
			}else {
				String arg = args[0];
				if(arg.length() >= 3 && arg.length() <= 16) {
					if(arg.equals(player.getName())) {
						player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cYou can't report yourself."));
					}else {
						ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arg);
						boolean isOnline = false;
						if(target != null) {
							isOnline = true;
						}
						StringBuilder sb = new StringBuilder();
						for(int i = 1; i < args.length; i++) {
							sb.append(args[i]).append(" ");
						}
						String reason = sb.toString().substring(0, sb.toString().length() - 1);
						
					}
				}else {
					player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cThe username must be at least 3 and not longer than 16 chars long."));
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private int sendEmbed(ProxiedPlayer issuer, String targetName, String reason, boolean online) {
		Guild staffGuild = jda.getGuildById(ChatBridgeUtils.staff_guild);
		return 0;
	}
	
	void addReportToDB(ProxiedPlayer reporter, String targetName, String targetUUID, String reason, String server, boolean type, long msgid) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_reports(reporter_uuid, reporter_name, target_uuid, target_name, time, reason, server, type, dc_msgid) VALUES (?,?,?,?,?,?,?,?,?)");
			ps.setString(1, reporter.getUniqueId().toString());
			ps.setString(2, reporter.getName());
			ps.setString(3, targetUUID);
			ps.setString(4, targetName);
			ps.setLong(5, System.currentTimeMillis());
			ps.setString(6, reason);
			ps.setBoolean(7, type);
			ps.setLong(8, msgid);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}