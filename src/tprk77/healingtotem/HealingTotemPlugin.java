package tprk77.healingtotem;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author tim
 */
public final class HealingTotemPlugin extends JavaPlugin {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	HealingTotemManager manager;
	HealingTotemBlockListener blocklistener;
	HealingTotemRunnable runnable;
	
	private int taskID;
	private int taskPeriod = 20; // 20 ticks = 1 second
	
	@Override
	public void onEnable(){
		
		// print hello
		this.log(Level.INFO, "is enabled!");
		
		// load the config (and data)
		this.manager = new HealingTotemManager(this);
		this.manager.loadTotemTypes();
		this.manager.loadTotems();
		
		// register events
		PluginManager pm = this.getServer().getPluginManager();
		this.blocklistener = new HealingTotemBlockListener(this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blocklistener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.blocklistener, Event.Priority.Low, this);
		
		// hook into the server thread
		BukkitScheduler scheduler = this.getServer().getScheduler();
		this.runnable = new HealingTotemRunnable(this);
		this.taskID = scheduler.scheduleSyncRepeatingTask(this, this.runnable, 0, this.taskPeriod);
		if(this.taskID == -1){
			this.log("failed to schedule!");
		}
	}
	
	@Override
	public void onDisable(){
		
		// remove the scheduled task
		BukkitScheduler scheduler = this.getServer().getScheduler();
		scheduler.cancelTask(this.taskID);
		
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
}
