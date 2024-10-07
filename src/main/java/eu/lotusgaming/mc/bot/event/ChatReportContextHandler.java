//Created by Maurice H. at 07.10.2024
package eu.lotusgaming.mc.bot.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.ReportData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ChatReportContextHandler extends ListenerAdapter {
	
	static HashMap<Member, ReportData> map = new HashMap<>();
	
	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		if(event.isFromGuild()) {
			Guild guild = event.getGuild();
			Member member = event.getMember();
			if(event.getName().equals("Report this!")) {
				if(guild.getIdLong() == 1153419306789507125L) {
					long messageId = event.getTarget().getIdLong();
					if(isMCMessage(messageId)) {
						if(isReported(messageId)) {
							event.deferReply(true).addContent("This message is already reported.").queue();
						}else {
							ReportData rd = new ReportData(messageId, event.getTarget().getJumpUrl());
							map.put(member, rd);
							TextInput reasonInput = TextInput.create("mdl_dcbreport_reason", "Reason", TextInputStyle.PARAGRAPH)
									.setRequiredRange(16, 512)
									.setPlaceholder("Describe as good as you can, why you are reporting this message.")
									.build();
							Modal modal = Modal.create("mdl_dcbreport", "Report A Message")
									.addComponents(ActionRow.of(reasonInput))
									.build();
							event.replyModal(modal).queue();
						}
					}else {
						event.deferReply(true).addContent("This message is not a chatbridge message.").queue();
					}
				}else {
					event.deferReply(true).addContent(guild.getName() + " is not enabled for reports.").queue();
				}
			}
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		Member member = event.getMember();
		if(event.getModalId().equals("mdl_dcbreport")) {
			ReportData rd = null;
			if(map.containsKey(member)) {
				rd = map.get(member);
			}
			long messageId = rd.getMessageId();
			String jumpUrl = rd.getMessageJumpURL();
			String reason = event.getValue("mdl_dcbreport_reason").getAsString();
			TextChannel targetChannel = event.getGuild().getTextChannelById(1205859980645769236L);
			markAsReported(member.getIdLong(), messageId, reason);
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(member.getColor());
			eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
			eb.setDescription(jumpUrl + " has been reported");
			eb.addField("Reason", reason, false);
			targetChannel.sendMessageEmbeds(eb.build()).addActionRow(
					Button.primary("claim_chatrep", "Claim Report"),
					Button.danger("invalidate_chatrep", "Invalidate Report")
					).queue();
			map.remove(member);
			event.deferReply(true).addContent("Report has been sent!").queue();
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		if(event.getComponentId().equals("claim_chatrep")) {
			event.deferReply(true).addContent("This function is in development. Please bear with me :)\nETA is in max 4 - 6 weeks.").queue();
		}else if(event.getComponentId().equals("invalidate_chatrep")) {
			event.deferReply(true).addContent("This function is in development. Please bear with me :)\nETA is in max 4 - 6 weeks.").queue();
		}
	}
	
	private boolean isMCMessage(long messageId) {
		boolean isMCMessage = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT dc_messageId FROM mc_chatlog WHERE dc_messageId = ?");
			ps.setLong(1, messageId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isMCMessage = true;
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isMCMessage;
	}
	
	private boolean isReported(long messageId) {
		boolean isReported = true;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT hasBeenReported FROM mc_chatlog WHERE dc_messageId = ?");
			ps.setLong(1, messageId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isReported = rs.getBoolean("hasBeenReported");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isReported;
	}
	
	private void markAsReported(long userId, long messageId, String reason) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_chatlog SET hasBeenReported = ?, reportedBy = ?, reportedAt = ?, reportReason = ?, reportStatus = ? WHERE dc_messageId = ?");
			ps.setBoolean(1, true);
			ps.setLong(2, userId);
			ps.setLong(3, System.currentTimeMillis());
			ps.setString(4, reason);
			ps.setString(5, "REPORTED");
			ps.setLong(6, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}