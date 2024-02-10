//Created by Chris Wille at 10.02.2024
package eu.lotusgaming.mc.misc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifyUtils {
	
	//returns a multi-data string. UUID;MCName or null if not verified.
	public static String isVerified(long userId) {
		String uuid = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT mcuuid,name FROM mc_users WHERE discordId = ?");
			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				uuid = rs.getString("mcuuid") + ";" + rs.getString("name");
			}else {
				uuid = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uuid;
	}
	
	public void verifyUserInDB(String name, long userId) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET discordId = ? WHERE name = ?");
			ps.setLong(1, userId);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}