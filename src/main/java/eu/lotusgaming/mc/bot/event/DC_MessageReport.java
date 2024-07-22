//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.bot.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import eu.lotusgaming.mc.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DC_MessageReport extends ListenerAdapter{
	
	static HashMap<Long, Long> mapping = new HashMap<>();
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		long msgId = event.getMessageIdLong();
		Member member = event.getMember();
		if(event.getComponentId().equals("repgamemsg")) {
			if(isReported(msgId)) {
				event.deferReply(true).addContent("This message has already been reported.").queue();
			}else {
				TextInput reasonInput = TextInput.create("mdl_dcbreport_reason", "Reason", TextInputStyle.PARAGRAPH)
						.setRequiredRange(16, 512)
						.setPlaceholder("Describe as good as you can, why you are reporting this message.")
						.build();
				Modal modal = Modal.create("mdl_dcbreport", "Report A Message")
						.addComponents(ActionRow.of(reasonInput))
						.build();
				event.replyModal(modal).queue();
				mapping.put(member.getIdLong(), msgId);
			}
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		Member member = event.getMember();
		if(event.getModalId().equals("mdl_dcbreport")) {
			String reason = event.getValue("mdl_dcbreport_reason").getAsString();
			long msgId = mapping.get(member.getIdLong());
			markAsReported(member.getIdLong(), msgId, reason);
			event.deferReply(true).addContent("Thank you for your report!").queue();
			TextChannel targetChannel = event.getGuild().getTextChannelById(1205859980645769236l);
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(member.getColor());
			eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
			eb.setDescription(event.getMessage().getJumpUrl() + " has been reported for the reason: " + reason);
			targetChannel.sendMessageEmbeds(eb.build()).queue();
			mapping.remove(member.getIdLong());
		}
	}
	
	void markAsReported(long userId, long messageId, String reason) {
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