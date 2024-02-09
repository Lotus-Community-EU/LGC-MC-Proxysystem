//Created by Chris Wille at 07.02.2024
package eu.lotusgaming.mc.misc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import eu.lotusgaming.mc.main.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Serverupdater {
	
	public static void startScheduler() {
		ProxyServer.getInstance().getScheduler().schedule(Main.main, () -> {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_serverstats SET currentPlayers = ?, currentStaffs = ?, maxPlayers = ?, ram_usage = ?, ram_alloc = ?, lastUpdated = ? WHERE servername = ?");
				int allPlayers = 0;
				int staffPlayers = 0;
				@SuppressWarnings("deprecation")
				int maxPlayers = ProxyServer.getInstance().getConfig().getPlayerLimit();
				for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
					allPlayers++;
					if(all.hasPermission("lgc.isStaff")) {
						staffPlayers++;
					}
				}
				ps.setInt(1, allPlayers);
				ps.setInt(2, staffPlayers);
				ps.setInt(3, maxPlayers);
				ps.setString(4, getRAMInfo(RAMInfo.USING));
				ps.setString(5, getRAMInfo(RAMInfo.ALLOCATED));
				ps.setLong(6, System.currentTimeMillis());
				ps.setString(7, "BungeeCord");
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}, 0, 10, TimeUnit.SECONDS);
	}
	
	public static void setOnlineStatus(boolean status) {
		PreparedStatement ps;
		if(status) {
			try {
				ps = MySQL.getConnection().prepareStatement("UPDATE mc_serverstats SET isOnline = ?, onlineSince = ? WHERE servername = ?");
				ps.setBoolean(1, status);
				ps.setLong(2, System.currentTimeMillis());
				ps.setString(3, "BungeeCord");
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			try {
				ps = MySQL.getConnection().prepareStatement("UPDATE mc_serverstats SET isOnline = ? WHERE servername = ?");
				ps.setBoolean(1, status);
				ps.setString(2, "BungeeCord");
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getRAMInfo(RAMInfo type) {
		String toReturn = "";
		Runtime runtime = Runtime.getRuntime();
		if(type == RAMInfo.ALLOCATED) {
			toReturn = runtime.totalMemory() / 1048576L + "";
		}else if(type == RAMInfo.USING) {
			toReturn = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + "";
		}else if(type == RAMInfo.FREE) {
			toReturn = runtime.freeMemory() / 1048576L + "";
		}
		return toReturn;
	}
	
	enum RAMInfo {
		ALLOCATED,
		USING,
		FREE;
	}
}