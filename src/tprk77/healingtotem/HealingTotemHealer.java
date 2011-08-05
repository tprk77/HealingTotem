package tprk77.healingtotem;

import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import tprk77.healingtotem.totem.Totem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author tim
 */
public class HealingTotemHealer implements Runnable {

	private final HealingTotemPlugin plugin;

	private final int period = 20;
	private final int max_healing = 4;
	private final int max_damage = 4;

	private int taskID;

	HealingTotemHealer(HealingTotemPlugin plugin){
		this.plugin = plugin;
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

		List<Totem> totems = this.plugin.getManager().getTotems();
		List<World> worlds = this.plugin.getServer().getWorlds();

		for(World world : worlds){

			List<LivingEntity> livingentities = world.getLivingEntities();

			for(LivingEntity livingentity : livingentities){

				// hopefully this will cut down on comparisons...
				if(!(livingentity instanceof Monster || livingentity instanceof Player)){
					continue;
				}

				int power = 0;

				for(Totem totem : totems){
					if(totem.inRange(livingentity)){
						power += totem.getEffectivePower(livingentity);
					}
				}

				if(power > this.max_healing){
					power = this.max_healing;
				}else if(power < -this.max_damage){
					power = -this.max_damage;
				}

				if(power > 0){
					int hp = livingentity.getHealth();
					if(hp < 20){
						livingentity.setHealth(hp + power);
					}
				}else if(power < 0){
					livingentity.damage(-power);
				}
			}
		}
	}
}
