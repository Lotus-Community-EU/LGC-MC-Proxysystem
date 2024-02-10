//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.misc;

import java.util.HashMap;
import java.util.Map;

public class ChatBridgeUtils {
	
	// channel ids for CB
	public static long lobby = 1203443094490718218l;
	public static long gameslobby = 1203443451019010079l;
	public static long creative = 1203443323600113727l;
	public static long creativehx = 1203443364146450462l;
	public static long survival = 1203443341757251635l;
	public static long survivalhx = 1203443391623471114l;
	public static long skyblock = 1203443412481744966l;
	public static long farmserver = 1203443429863063552l;
	public static long staff_chat = 1205610796021981274l; //This channel is on the STAFF guild!
	
	public static long staff_guild = 1066812641768640542l;
	public static long public_guild = 1153419306789507125l;
	
	public static String translateLongToString(long input) {
		HashMap<Long, String> map = currentServers();
		if(map.containsKey(input)) {
			return map.get(input);
		}else {
			return "noChanAssigned";
		}
	}
	
	public static long translateStringToLong(String input) {
		HashMap<String, Long> map = new HashMap<>();
		for(Map.Entry<Long, String> entry : currentServers().entrySet()) {
			map.put(entry.getValue(), entry.getKey());
		}
		if(map.containsKey(input)) {
			return map.get(input);
		}else {
			return 0;
		}
	}
	
	public static String translateBCKeyToFancyName(String input) {
		HashMap<String, String> map = new HashMap<>();
		map.put("lobby", "Lobby");
		map.put("gameslobby", "Gameslobby");
		map.put("creative", "Creative");
		map.put("creativehx", "Creative HX");
		map.put("survival", "Survival");
		map.put("survivalhx", "Survival HX");
		map.put("skyblock", "SkyBlock");
		map.put("farmserver", "Farmserver");
		if(map.containsKey(input)) {
			return map.get(input);
		}else {
			return input;
		}
	}
	
	private static HashMap<Long, String> currentServers() {
		HashMap<Long, String> map = new HashMap<>();
		map.put(lobby, "lobby");
		map.put(gameslobby, "gameslobby");
		map.put(creative, "creative");
		map.put(creativehx, "creativehx");
		map.put(survival, "survival");
		map.put(survivalhx, "survivalhx");
		map.put(skyblock, "skyblock");
		map.put(farmserver, "farmserver");
		return map;
	}
}