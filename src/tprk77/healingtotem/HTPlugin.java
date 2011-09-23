package tprk77.healingtotem;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author tim
 */
public final class HTPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");

	private HTConfigManager configmanager;
	private HTTotemManager totemmanager;
	private HTBlockListener blocklistener;
	private HTHealerRunnable healerrunnable;

	@Override
	public void onEnable(){

		this.log("is enabled!");

		this.configmanager = new HTConfigManager(this);
		this.configmanager.loadConfigOrDefault();

		this.totemmanager = new HTTotemManager(this);
		this.totemmanager.loadTotemTypesOrDefault();
		this.totemmanager.loadTotems();

		this.blocklistener = new HTBlockListener(this);
		this.blocklistener.registerEvents();

		this.healerrunnable = new HTHealerRunnable(this);
		this.healerrunnable.schedule();
	}

	@Override
	public void onDisable(){

		this.healerrunnable.cancel();

		this.totemmanager.saveTotems();

		this.log("is disabled!");
	}

	public HTTotemManager getTotemManager(){
		return this.totemmanager;
	}

	public HTConfigManager getConfigManager(){
		return this.configmanager;
	}

	public void log(String message){
		this.log(Level.INFO, message);
	}

	public void log(Level level, String message){
		PluginDescriptionFile desc = this.getDescription();
		HTPlugin.log.log(level, "[" + desc.getName() + " v" + desc.getVersion() + "] " + message);
	}

	public void warn(String message){
		this.log(Level.WARNING, message);
	}

	public static void dumblog(String message){
		HTPlugin.log.log(Level.INFO, message);
	}
}
