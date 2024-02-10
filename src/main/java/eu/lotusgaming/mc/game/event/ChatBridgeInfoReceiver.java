//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.game.event;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import eu.lotusgaming.mc.misc.ChatBridgeUtils;
import net.dv8tion.jda.api.JDA;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatBridgeInfoReceiver implements Listener{
	
	JDA jda;
	public ChatBridgeInfoReceiver(JDA jda) {
		this.jda = jda;
	}
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		if(event.getTag().equalsIgnoreCase("lgc:dccb")) {
			ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
			String[] data = input.readUTF().split("-;-");
			String old_long = data[0];
			String message = data[1];
			if(old_long.matches("^[0-9]+$")) {
				long channelId = Long.parseLong(old_long);
				jda.getGuildById(ChatBridgeUtils.public_guild).getTextChannelById(channelId).sendMessage(message).queue();
			}
		}
	}

}

