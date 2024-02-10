//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.bot.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.lotusgaming.mc.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DC_MessageReport extends ListenerAdapter{
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		long msgId = event.getMessageIdLong();
		Member member = event.getMember();
		if(event.getComponentId().equals("repgamemsg")) {
			if(isReported(msgId)) {
				event.deferReply(true).addContent("This message has already been reported.").queue();
			}else {
				markAsReported(member.getIdLong(), msgId);
				event.deferReply(true).addContent("Thank you for your report!").queue();
				TextChannel targetChannel = event.getGuild().getTextChannelById(1205859980645769236l);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(member.getColor());
				eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
				eb.setDescription(event.getMessage().getJumpUrl() + " has been reported.");
				targetChannel.sendMessageEmbeds(eb.build()).queue();
			}
		}
	}
	
	void markAsReported(long userId, long messageId) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_chatlog SET hasBeenReported = ?, reportedBy = ?, reportedAt = ? WHERE dc_messageId = ?");
			ps.setBoolean(1, true);
			ps.setLong(2, userId);
			ps.setLong(3, System.currentTimeMillis());
			ps.setLong(4, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	boolean isReported(long messageId) {
		boolean isReported = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT hasBeenReported FROM mc_chatlog WHERE dc_messageId = ?");
			ps.setLong(1, messageId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isReported = rs.getBoolean("hasBeenReported");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isReported;
	}
}