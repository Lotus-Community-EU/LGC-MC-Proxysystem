//Created by Maurice H. at 09.04.2025
package eu.lotusgaming.mc.misc.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import eu.lotusgaming.mc.misc.MySQL;

public class MaintenanceInfo {
	
	private String reason;
	private boolean state;
	private List<String> players;
	
	public MaintenanceInfo() {
		try (PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT isEnabled,reason FROM mc_maintenance WHERE mc_uuid=?")) {
			ps.setString(1, "ENABLED_MAINTENANCE");
			ResultSet rs = ps.executeQuery();
			rs.next();
			this.reason = rs.getString("reason");
			this.state = rs.getBoolean("isEnabled");
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT mc_uuid,isEnabled,id FROM mc_maintenance")) {
            ResultSet rs = ps.executeQuery();
            this.players = new ArrayList<>();
            while (rs.next()) {
            	if(rs.getInt("id") != 1) {
            		if(rs.getBoolean("isEnabled")) {
            			this.players.add(rs.getString("mc_uuid"));
            		}
            	}
            }
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getReason() {
		return reason;
	}
	
	public boolean getState() {
		return state;
	}
	
	public List<String> getAllowedUniqueIDs(){
		return players;
	}
}