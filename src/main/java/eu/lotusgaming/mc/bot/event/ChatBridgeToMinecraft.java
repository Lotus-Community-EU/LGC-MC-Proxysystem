//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.bot.event;

import eu.lotusgaming.mc.misc.ChatBridgeUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatBridgeToMinecraft extends ListenerAdapter{
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isFromGuild()) {
			Guild guild = event.getGuild();
			Member member = event.getMember();
			if(event.isFromType(ChannelType.TEXT)) {
				TextChannel channel = event.getChannel().asTextChannel();
				String srvName = ChatBridgeUtils.translateLongToString(channel.getIdLong());
				if(!event.getAuthor().isBot()) {
					if(srvName.equalsIgnoreCase("noChanAssigned")) {
						if(guild.getIdLong() == ChatBridgeUtils.staff_guild) {
							if(channel.getIdLong() == ChatBridgeUtils.staff_chat) {
								if(event.getMessage().getContentStripped().length() <= 300) {
									int players = 0;
									for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
										players++;
										all.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy("§7[§cDCB§7] §a" + member.getRoles().get(0).getName() + " §7- §6" + member.getEffectiveName() + "§7: " + event.getMessage().getContentStripped()));
										if(players == 0) {
											event.getMessage().addReaction(Emoji.fromFormatted("<:deny:1204482005065146428>")).queue();
										}else {
											event.getMessage().addReaction(Emoji.fromFormatted("<:accept:1204482009355911168>")).queue();
										}
									}
								}
							}
						}
					}else {
						if(guild.getIdLong() == ChatBridgeUtils.public_guild) {
							if(event.getMessage().getContentStripped().length() <= 300) {
								int players = 0;
								for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
									if(all.getServer().getInfo().getName().equalsIgnoreCase(srvName)) {
										players++;
										all.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy("§7[§2DCB§7] §a" + member.getRoles().get(0).getName() + " §7- §6" + member.getEffectiveName() + "§7: " + event.getMessage().getContentStripped()));
									}
									if(players == 0) {
										event.getMessage().addReaction(Emoji.fromFormatted("<:deny:1204482005065146428>")).queue();
									}else {
										event.getMessage().addReaction(Emoji.fromFormatted("<:accept:1204482009355911168>")).queue();
									}
								}
							}else {
								event.getMessage().addReaction(Emoji.fromFormatted("<:deny:1204482005065146428>")).queue();
							}
						}
					}
				}
			}
		}
	}
}