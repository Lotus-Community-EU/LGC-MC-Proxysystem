//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Playerdata;
import eu.lotusgaming.mc.misc.Prefix;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LotusController {
	
	//Language System
		private static HashMap<String, HashMap<String, String>> langMap = new HashMap<>();
		public static HashMap<String, String> playerLanguages = new HashMap<>();
		private static List<String> availableLanguages = new ArrayList<>();
	
	//Prefix System
	private static HashMap<String, String> prefix = new HashMap<>();
	private static boolean useSeasonalPrefix = false;
	
	public boolean initLanguageSystem() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM core_translations");
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd =  rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			int languageStrings = 0;
			int colToStartFrom = 0;
			if(rs.next()) {
				for(int i = 1; i <= columnCount; i++) {
					String name = rsmd.getColumnName(i);
					if(name.equals("German")) {
						colToStartFrom = i;
						break;
					}
				}
				HashMap<String, String> map;
				for(int i = colToStartFrom; i <= columnCount; i++) {
					String name = rsmd.getColumnName(i);
					availableLanguages.add(name);
					Main.logger.info("Logged language " + name + " to List");
					PreparedStatement ps1 = MySQL.getConnection().prepareStatement("SELECT path," + name + ",isGame FROM core_translations");
					ResultSet rs1 = ps1.executeQuery();
					map = new HashMap<>();
					int subLangStrings = 0;
					while(rs1.next()) {
						if(rs1.getBoolean("isGame")) {
							subLangStrings++;
							//Only get Strings, which are for the game (what would we do with website/bot string, right?)
							map.put(rs1.getString("path"), rs1.getString(name));
						}
					}
					languageStrings = subLangStrings;
					langMap.put(name, map);
				}
				Main.logger.info("langMap logged " + langMap.size() + " entries with each " + languageStrings + " entries per language.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return langMap.isEmpty();
	}
	
	public List<String> getAvailableLanguages() {
		return availableLanguages;
	}
	
	public boolean initPlayerLanguages() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT mcuuid,language FROM mc_users");
			ResultSet rs = ps.executeQuery();
			int count = 0;
			while(rs.next()) {
				count++;
				playerLanguages.put(rs.getString("mcuuid"), rs.getString("language"));
			}
			rs.close();
			ps.close();
			Main.logger.info("Initialised " + count + " users for the language system. | Source: LotusController#initPlayerLanguages();");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerLanguages.isEmpty();
	}
	
	//only used, when a never-joined player joins the network.
	public void addPlayerLanguageWhenRegistered(ProxiedPlayer player) {
		playerLanguages.put(player.getUniqueId().toString(), "English");
		Main.logger.info("Added " + player.getName() + " to the languageMap with default. | Source: LotusController#addPlayerLanguageWhenRegistered(PLAYER);");
	}
	
	//This method is used if no spaceholders needs to be translated additionally.
	public void sendMessageReady(ProxiedPlayer player, String path) {
		player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(getPrefix(Prefix.MAIN) + sendMessageToFormat(player, path)));
		//player.sendMessage(getPrefix(Prefix.MAIN) + sendMessageToFormat(player, path));
	}
	
	//This method is used if spaceholders needs to be translated before sending (or if the target is NOT a player).
	public String sendMessageToFormat(ProxiedPlayer player, String path) {
		String toReturn = returnString(returnLanguage(player), path);
		if(toReturn.equalsIgnoreCase("none")) {
			return returnString("English", path);
		}else {
			return toReturn;
		}
	}
	
	//This method is returns the player's selected language.
	public String returnLanguage(ProxiedPlayer player) {
		String defaultLanguage = "English";
		if(playerLanguages.containsKey(player.getUniqueId().toString())) {
			defaultLanguage = playerLanguages.get(player.getUniqueId().toString());
		}
		return defaultLanguage;
	}
	
	//This method is just for one string, the NoPerm one
	public void noPerm(ProxiedPlayer player, String lackingPermissionNode) {
		player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy(getPrefix(Prefix.System) + sendMessageToFormat(player, "global.noPermission").replace("%permissionNode%", lackingPermissionNode)));
		//player.sendMessage(getPrefix(Prefix.System) + sendMessageToFormat(player, "global.noPermission").replace("%permissionNode%", lackingPermissionNode));
	}
	
	//This method returns the String from the language selected.
	private String returnString(String language, String path) {
		if(langMap.containsKey(language)) {
			HashMap<String, String> localMap = langMap.get(language);
			if(localMap.containsKey(path)) {
				return ChatColor.translateAlternateColorCodes('&', localMap.get(path));
			}else {
				return "The path '" + path + "' does not exist!";
			}
		}else {
			return "The language '" + language + "' does not exist!";
		}
	}
	
	// < - - - END OF LANGUAGE SYSTEM - - - >
	
	public void initPrefixSystem() {
		if(!prefix.isEmpty()) prefix.clear();
		
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_prefix");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				if(rs.getString("type").equalsIgnoreCase("UseSeason")) {
					useSeasonalPrefix = translateToBool(rs.getString("prefix"));
					if(useSeasonalPrefix) {
						Main.logger.info("Using Seasonal Prefix | Source: LotusController#initPrefixSystem()");
					}else {
						Main.logger.info("Using Normal Prefix | Source: LotusController#initPrefixSystem()");
					}
				}
				prefix.put(rs.getString("type"), rs.getString("prefix").replace('&', '§'));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean translateToBool(String input) {
		switch(input) {
		case "TRUE": return true;
		case "FALSE": return false;
		default: return false;
		}
	}
	
	//get Prefix with the Enum class "eu.lotusgc.mc.misc.Prefix"
	public String getPrefix(Prefix prefixType) {
		String toReturn = "";
		switch(prefixType) {
		case MAIN: if(useSeasonalPrefix) { toReturn = prefix.get("SEASONAL_MAIN"); } else { toReturn = prefix.get("MAIN"); }
			break;
		case PMSYS: toReturn = prefix.get("PMSYS");
			break;
		case SCOREBOARD: if(useSeasonalPrefix) { toReturn = prefix.get("SEASONAL_SB"); } else { toReturn = prefix.get("SCOREBOARD"); }
			break;
		case System: toReturn = prefix.get("SYSTEM");
			break;
		default: toReturn = prefix.get("MAIN");
			break;
		}
		return toReturn;
	}
	
	public String getPlayerData(ProxiedPlayer player, Playerdata data) {
		String toReturn = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT " + data.getColumnName() + " FROM mc_users WHERE mcuuid = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				toReturn = rs.getString(data.getColumnName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	
	public boolean translateBoolean(String input) {
		switch(input) {
		case "0": return false;
		case "false": return false;
		case "1": return true;
		case "true": return true;
		default: Main.logger.severe("Error in LotusController#translateBoolean() - expected 0,1,true,false but got " + input); return false;
		}
	}
}