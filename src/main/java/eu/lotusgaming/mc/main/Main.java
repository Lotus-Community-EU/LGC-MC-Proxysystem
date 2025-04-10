package eu.lotusgaming.mc.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Serverupdater;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
	
	public static Main main;
	public static Logger logger;
	public static String consoleSend = "§cPlease execute this command inGame!";
	//for verification
	public static HashMap<String, String> hashName = new HashMap<>();
	public static HashMap<String, Long> hashId = new HashMap<>();
	
	public void onEnable() {
		main = this;
		getProxy().registerChannel("lgc:dccb");
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		
		LotusManager lm = new LotusManager();
		lm.preInit();
		lm.init();
		lm.postInit();
		Serverupdater.setOnlineStatus(true);
		restartScheduler();
	}
	
	public void onDisable() {
		main = null;
		Serverupdater.setOnlineStatus(false);
		MySQL.disconnect();
	}
	
	private void restartScheduler() {
		getProxy().getScheduler().schedule(main, new Runnable() {
			@Override
			public void run() {
				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				if(time.matches("03:00:02")) {
					getProxy().stop();
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
	
}