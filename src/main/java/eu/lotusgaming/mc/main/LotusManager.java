package eu.lotusgaming.mc.main;

import java.io.File;
import java.io.IOException;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.mc.game.command.HubCommand;
import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Serverupdater;

public class LotusManager {
	
	public static File mainFolder = new File("plugins/LotusGaming");
	public static File mainConfig = new File("plugins/LotusGaming/config.yml");
	
	public void preInit() {
		long current = System.currentTimeMillis();
		
		if(!mainFolder.exists()) mainFolder.mkdirs();
		if(!mainConfig.exists()) try { mainConfig.createNewFile(); } catch (Exception ex) { };
		
		try {
			YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
			cfg.addDefault("MySQL.Host", "127.0.0.1");
			cfg.addDefault("MySQL.Port", "3306");
			cfg.addDefault("MySQL.Database", "TheDataBaseTM");
			cfg.addDefault("MySQL.Username", "user");
			cfg.addDefault("MySQL.Password", "pass");
			cfg.addDefault("System.Bottoken", "The Bottoken goes in here.");
			cfg.addDefault("System.HashPass", "SomeRandompassword");
			cfg.options().copyDefaults(true);
			cfg.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
			if(!cfg.getString("MySQL.Password").equalsIgnoreCase("pass")) {
				MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Main.logger.info("Pre-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void init() {
		long current = System.currentTimeMillis();
		
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new HubCommand("hub"));
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new HubCommand("lobby"));
		Main.main.getProxy().getPluginManager().registerCommand(Main.main, new HubCommand("l"));
		
		Main.logger.info("Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void postInit() {
		long current = System.currentTimeMillis();
		
		BotMain.startBot();
		Serverupdater.startScheduler();
		
		Main.logger.info("Post-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}

}
