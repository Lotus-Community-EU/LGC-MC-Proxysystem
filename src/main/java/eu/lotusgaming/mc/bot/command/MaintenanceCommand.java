//Created by Maurice H. at 31.03.2025
package eu.lotusgaming.mc.bot.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.util.MaintenancePlayer;
import eu.lotusgaming.mc.misc.util.MojangPlayer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MaintenanceCommand extends ListenerAdapter{
	
	static HashMap<Long, MaintenancePlayer> map = new HashMap<>();
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getName().equals("maintenance")) {
			if (event.getSubcommandName().equals("addplayer")) {
				String playername = event.getOption("playername").getAsString();
				String reason = event.getOption("reason").getAsString();
				MojangPlayer player = getMojangPlayerByName(playername);
				MaintenancePlayer mp = new MaintenancePlayer(player.getUniqueID(), reason, player.getName(), System.currentTimeMillis());
				map.put(event.getMember().getIdLong(), mp);
				event.reply("Player " + player.getName() + "/" + player.getUniqueID() + " has been found. Would you like to add that player?").addActionRow(
						Button.success("btn-addMaintenance", "Correct"),
						Button.danger("btn-noMaintenance", "Wrong")
						).queue();
			}else if (event.getSubcommandName().equals("removeplayer")) {
				String playername = event.getOption("playername").getAsString();
				String reason = event.getOption("reason").getAsString();
				MojangPlayer player = getMojangPlayerByName(playername);
				MaintenancePlayer mp = new MaintenancePlayer(player.getUniqueID(), reason, player.getName(), System.currentTimeMillis());
				map.put(event.getMember().getIdLong(), mp);
				event.reply("Player " + player.getName() + "/" + player.getUniqueID() + " has been found. Would you like to remove that player?").addActionRow(
						Button.success("btn-remMaintenance", "Correct"),
						Button.danger("btn-noMaintenance", "Wrong")
						).queue();
			}else if (event.getSubcommandName().equals("maintenance")) {
				boolean state = event.getOption("state").getAsBoolean();
				String reason = "No reason provided";
				if(event.getOption("reason") != null) {
                    reason = event.getOption("reason").getAsString();
				}
				if (state) {
					event.reply("Maintenance has been enabled! Reason: " + reason).queue();
				} else {
					event.reply("Maintenance has been disabled!").queue();
				}
				updateMaintenance(state, reason, event.getMember().getIdLong());
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		if(event.getComponentId().equals("btn-addMaintenance")) {
            MaintenancePlayer mp = map.get(event.getMember().getIdLong());
            updatePlayer(mp.getUUID(), true, mp.getReason(), event.getMember().getIdLong());
            event.reply("Player " + mp.getUUID() + " has been added to the maintenance system.").queue();
            event.getMessage().editMessageComponents().queue();
		}else if(event.getComponentId().equals("btn-remMaintenance")) {
            MaintenancePlayer mp = map.get(event.getMember().getIdLong());
            updatePlayer(mp.getUUID(), false, mp.getReason(), event.getMember().getIdLong());
            event.reply("Player " + mp.getUUID() + " has been removed from the maintenance system.").queue();
            event.getMessage().editMessageComponents().queue();
		} else if (event.getComponentId().equals("btn-noMaintenance")) {
			event.reply("Cancelled.").setEphemeral(true).queue();
			event.getMessage().editMessageComponents().queue();
			MaintenancePlayer mp = map.get(event.getMember().getIdLong());
			event.getMessage().editMessage("Cancelled adding/removing ``" + mp.getName() + "``.").queue();
			map.remove(event.getMember().getIdLong());
		}
	}
	
	void updatePlayer(String uuid, boolean state, String reason, long userId) {
		try (PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_maintenance(isEnabled, reason, dateChanged, changedBy, mc_uuid) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE isEnabled = ?, reason = ?, dateChanged = ?, changedBy = ?")) {
			ps.setBoolean(1, state);
			ps.setString(2, reason);
			ps.setLong(3, System.currentTimeMillis());
			ps.setLong(4, userId);
			ps.setString(5, uuid);
			ps.setBoolean(6, state);
			ps.setString(7, reason);
			ps.setLong(8, System.currentTimeMillis());
			ps.setLong(9, userId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void updateMaintenance(boolean state, String reason, long userId){
		try(PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_maintenance SET isEnabled = ?, reason = ?, dateChanged = ?, changedBy = ? WHERE mc_uuid = ?")){
			ps.setBoolean(1, state);
			ps.setString(2, reason);
			ps.setLong(3, System.currentTimeMillis());
			ps.setLong(4, userId);
			ps.setString(5, "ENABLED_MAINTENANCE");
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	MojangPlayer getMojangPlayerByName(String name) {
		MojangPlayer player = null;
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = "";
			String jsonS = "";
			while ((inputLine = in.readLine()) != null) {
				jsonS += inputLine;
			}
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
			player = new MojangPlayer(jsonObject.get("name").getAsString(), jsonObject.get("id").getAsString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return player;
	}
}