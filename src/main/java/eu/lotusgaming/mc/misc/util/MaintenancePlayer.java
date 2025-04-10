//Created by Maurice H. at 06.04.2025
package eu.lotusgaming.mc.misc.util;

public class MaintenancePlayer {
	
	private String uuid, reason, name;
	private long timestamp;
	
	public MaintenancePlayer(String uuid, String reason, String name, long timestamp) {
		this.uuid = uuid;
        this.reason = reason;
        this.timestamp = timestamp;
        this.name = name;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String getReason() {
		return reason;
	}
	
	public String getName() {
		return name;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}