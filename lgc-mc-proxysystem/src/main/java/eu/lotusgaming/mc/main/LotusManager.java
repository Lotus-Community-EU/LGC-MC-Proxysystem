package eu.lotusgaming.mc.main;

import java.io.File;
import java.io.IOException;

import org.simpleyaml.configuration.file.YamlFile;

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
			cfg.options().copyDefaults(true);
			cfg.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
		Main.logger.info("Pre-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void init() {
		long current = System.currentTimeMillis();
		Main.logger.info("Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void postInit() {
		long current = System.currentTimeMillis();
		Main.logger.info("Post-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}

}
