package tprk77.healingtotem;

import java.util.List;
import tprk77.healingtotem.totem.Totem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author tim
 */
public class HealingTotemHealer implements Runnable {

	private final HealingTotemPlugin plugin;
	private int taskID;
	private int period;

	HealingTotemHealer(HealingTotemPlugin plugin){
		this.plugin = plugin;
		this.period = 20;
	}

	public void schedule(){
		BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
		this.taskID = scheduler.scheduleSyncRepeatingTask(this.plugin, this, 0, this.period);
		if(this.taskID == -1){
			this.plugin.warn("failed to schedule!");
		}
	}

	public void cancel(){
		BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
		scheduler.cancelTask(this.taskID);
	}

	@Override
	public void run(){
		Player[] players = this.plugin.getServer().getOnlinePlayers();
		List<Totem> totems = this.plugin.getManager().getTotems();

		for(Totem totem : totems){
			for(Player player : players){
				if(totem.inRange(player)){
					totem.affectHealth(player);
				}
			}
		}
	}
}
