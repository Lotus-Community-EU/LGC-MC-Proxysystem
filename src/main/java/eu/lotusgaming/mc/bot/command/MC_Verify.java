//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.bot.command;

import java.util.Random;

import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.ChatBridgeUtils;
import eu.lotusgaming.mc.misc.VerifyUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MC_Verify extends ListenerAdapter{
	
	static JDA jda;
	public MC_Verify(JDA jda) {
		MC_Verify.jda = jda;
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("verify")) {
			if(event.getOption("service") != null) {
				String service = event.getOption("service").getAsString();
				if(service.equalsIgnoreCase("website")) {
					event.deferReply(true).addContent("The Website Verification is not yet implemented!").queue();
				}else if(service.equalsIgnoreCase("minecraft")) {
					if(event.getOption("playername") != null) {
						String data = VerifyUtils.isVerified(event.getUser().getIdLong());
						if(data != null) {
							String[] splitData = data.split(";");
							String uuid = splitData[0];
							String name = splitData[1];
							event.deferReply(true).addContent("Hey, this Discord Account is already connected with a Minecraft Account!\nThe Account is ``" + name + "`` / ``" + uuid + "``").queue();
						}else {
							String playername = event.getOption("playername").getAsString();
							ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playername);
							if(target == null) {
								event.deferReply(true).addContent("It seems you are not online - you need to be online in order to verify yourself!").queue();
							}else {
								String code = random(1000, 9999) + "-" + random(1000, 9999);
								event.reply("You are online! Verify yourself now with this verification code: ``" + code + "``.\nThe command to verify is ``/verify discord " + code + "``.").queue();
								Main.hashName.put(target.getName(), code);
								Main.hashId.put(target.getName(), event.getUser().getIdLong());
							}
						}
					}
				}else {
					event.deferReply(true).addContent("Please choose as service either 'minecraft' or 'website'!").queue();
				}
			}
		}
	}
	
	public static void sendMessageSuccess(String playername, String uuid, long userId) {
		Guild guild = jda.getGuildById(ChatBridgeUtils.public_guild);
		Role verified = guild.getRoleById(1203442341117952100l);
		Member member = guild.getMemberById(userId);
		guild.addRoleToMember(member, verified).complete();
		new VerifyUtils().verifyUserInDB(playername, userId);
		member.getUser().openPrivateChannel().queue(ra -> {
			ra.sendMessage("Hey " + playername + ", the verification process has been finished successfully. You have now full access to the Minecraft Related Chats on the guild.").queue();
		});
	}
	
	public static void sendMessageError(String pname, long uid) {
		Guild guild = jda.getGuildById(ChatBridgeUtils.public_guild);
		Member member = guild.getMemberById(uid);
		member.getUser().openPrivateChannel().queue(ra -> {
			ra.sendMessage("Hey " + pname + ", the verification process has been cancelled.\nIf you want to get verified, just restart it.").queue();
		});
	}
	
	int random(int min, int max) {
		Random random = new Random();
		int i = random.nextInt(max);
		while (i < min) {
			i = random.nextInt(max);
		}
		return i;
	}
}