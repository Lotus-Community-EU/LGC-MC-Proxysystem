package eu.lotusgaming.mc.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
	
	public static Main main;
	public static Logger logger;
	public static String consoleSend = "§cPlease execute this command inGame!";
	
	public void onEnable() {
		main = this;
		getProxy().registerChannel("lgc:dccb");
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		
		LotusManager lm = new LotusManager();
		lm.preInit();
		lm.init();
		lm.postInit();
	}
	
	public void onDisable() {
		main = null;
	}

}
