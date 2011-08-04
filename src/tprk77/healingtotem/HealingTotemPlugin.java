package tprk77.healingtotem;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author tim
 */
public final class HealingTotemPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");

	HealingTotemManager manager;
	HealingTotemBlockListener blocklistener;
	HealingTotemHealer healer;

	@Override
	public void onEnable(){

		// print hello
		this.log("is enabled!");

		// load the config (and data)
		this.manager = new HealingTotemManager(this);
		this.manager.loadTotemTypes();
		this.manager.loadTotems();

		// register events
		this.blocklistener = new HealingTotemBlockListener(this);
		this.blocklistener.registerEvents();

		// hook into the server thread
		this.healer = new HealingTotemHealer(this);
		this.healer.schedule();
	}

	@Override
	public void onDisable(){

		// remove the scheduled task
		this.healer.cancel();

		// save data
		this.manager.saveTotems();

		// print goodbye
		this.log("is disabled!");
	}

	public HealingTotemManager getManager(){
		return this.manager;
	}

	public void log(String message){
		this.log(Level.INFO, message);
	}

	public void log(Level level, String message){
		PluginDescriptionFile desc = this.getDescription();
		HealingTotemPlugin.log.log(level, desc.getName() + " v" + desc.getVersion() + ": " + message);
	}

	public void warn(String message){
		this.log(Level.WARNING, message);
	}

	public static void dumblog(String message){
		HealingTotemPlugin.log.log(Level.INFO, message);
	}
}
