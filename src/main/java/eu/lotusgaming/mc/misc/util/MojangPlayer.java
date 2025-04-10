//Created by Maurice H. at 06.04.2025
package eu.lotusgaming.mc.misc.util;

public class MojangPlayer {
	
	private String uuid, name;
	
	public MojangPlayer(String name, String uuid) {
		this.uuid = uuid;
		this.name = name;
	}
	
	public String getUniqueID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
}

