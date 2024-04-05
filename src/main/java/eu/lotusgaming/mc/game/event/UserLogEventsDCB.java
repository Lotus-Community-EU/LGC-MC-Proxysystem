//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.game.event;

import java.util.HashMap;

import eu.lotusgaming.mc.misc.ChatBridgeUtils;
import eu.lotusgaming.mc.misc.ChatbridgeEnums;
import net.dv8tion.jda.api.JDA;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class UserLogEventsDCB implements Listener{
	
	JDA jda;
	public UserLogEventsDCB(JDA jda) {
		this.jda = jda;
	}
	
	
	
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
		String server_old = "";
		String playername = event.getPlayer().getName();
		if(event.getFrom() != null) {
			server_old = event.getFrom().getName();
		}
		HashMap<ChatbridgeEnums, Boolean> map = ChatBridgeUtils.getChatbridgeSettings(event.getPlayer().getUniqueId());
		String server_new = event.getPlayer().getServer().getInfo().getName();
		if(server_old.isBlank()) {
			if(map.get(ChatbridgeEnums.SHOW_JOIN)) {
				jda.getGuildById(ChatBridgeUtils.public_guild).getTextChannelById(ChatBridgeUtils.translateStringToLong(server_new)).sendMessage("**" + playername + "** has joined the server.").queue();
			}
		}else {
			if(map.get(ChatbridgeEnums.SHOW_SERVERCHANGE)) {
				jda.getGuildById(ChatBridgeUtils.public_guild).getTextChannelById(ChatBridgeUtils.translateStringToLong(server_old)).sendMessage("**" + playername + "** switched to **" + ChatBridgeUtils.translateBCKeyToFancyName(server_new) + "**").queue();
				jda.getGuildById(ChatBridgeUtils.public_guild).getTextChannelById(ChatBridgeUtils.translateStringToLong(server_new)).sendMessage("**" + playername + "** switched from **" + ChatBridgeUtils.translateBCKeyToFancyName(server_old) + "**").queue();
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		String playername = event.getPlayer().getName();
		String server = "";
		HashMap<ChatbridgeEnums, Boolean> map = ChatBridgeUtils.getChatbridgeSettings(event.getPlayer().getUniqueId());
		if(event.getPlayer().getServer() != null) {
			server = event.getPlayer().getServer().getInfo().getName();
			if(map.get(ChatbridgeEnums.SHOW_QUIT)) {
				jda.getGuildById(ChatBridgeUtils.public_guild).getTextChannelById(ChatBridgeUtils.translateStringToLong(server)).sendMessage("**" + playername + "** has left the server.").queue();
			}
		}
	}
}