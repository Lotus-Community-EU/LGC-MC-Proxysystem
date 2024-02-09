//Created by Chris Wille at 07.02.2024
package eu.lotusgaming.mc.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu.lotusgaming.mc.main.Main;

public class MySQL {
	
	static Connection connection;
	static String sqlprefix = "MySQL - ";
	
	public static void connect(String host, String port, String database, String username, String password) {
		if(!isConnected()) {
			try {
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
				Main.logger.info(sqlprefix + "Connected successfully.");
			}catch (SQLException e) {
				Main.logger.severe(sqlprefix + "Couldn't connect to DB-Server. Error: " + e.getMessage());
			}
		}
	}
	
	public static void disconnect() {
		if(isConnected()) {
			try {
				connection.close();
				Main.logger.info(sqlprefix + "Disconnected successfully.");
			}catch (SQLException e) {
				Main.logger.severe(sqlprefix + "Couldn't disconnect from DB-Server. Error: " + e.getMessage());
			}
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return connection;
	}
	
	public static boolean isConnected() {
		return (connection == null ? false : true);
	}

}

