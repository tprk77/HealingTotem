package tprk77.healingtotem;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import tprk77.healingtotem.totem.Totem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author tim
 */
public class HTHealerRunnable implements Runnable {

	private final HTPlugin plugin;

	private final int period = 20;

	private int taskID;
	private List<LivingEntityProcessor> processors;

	private abstract class LivingEntityProcessor {

		private final PluginManager eventcaller;
		private final int stackedheal;
		private final int stackeddamage;

		public LivingEntityProcessor(PluginManager eventcaller, int stackedheal, int stackeddamage){
			this.eventcaller = eventcaller;
			this.stackedheal = stackedheal;
			this.stackeddamage = stackeddamage;
		}

		public abstract boolean process(LivingEntity entity, List<Totem> totems);

		protected int sumTotemEffectivePower(LivingEntity entity, List<Totem> totems){
			int power = 0;
			for(Totem totem : totems){
				if(totem.inRange(entity)){
					power += totem.getEffectivePower(entity);
				}
			}
			return power;
		}

		protected void applyHeal(LivingEntity entity, int power){
			if(power > this.stackedheal){
				power = this.stackedheal;
			}else if(power < -this.stackeddamage){
				power = -this.stackeddamage;
			}
			if(power > 0){
				EntityRegainHealthEvent regen = new EntityRegainHealthEvent(
								entity, power, EntityRegainHealthEvent.RegainReason.CUSTOM);
				this.eventcaller.callEvent(regen);
				if(!regen.isCancelled()){
					int newhealth = entity.getHealth() + regen.getAmount();
					if(newhealth < 0){
						newhealth = 0;
					}else if(newhealth > 20){
						newhealth = 20;
					}
					entity.setHealth(newhealth);
				}
			}else if(power < 0){
				EntityDamageEvent damage = new EntityDamageEvent(
								entity, EntityDamageEvent.DamageCause.CUSTOM, -power);
				this.eventcaller.callEvent(damage);
				if(!damage.isCancelled()){
					entity.damage(damage.getDamage());
				}
			}
		}
	}

	HTHealerRunnable(HTPlugin plugin){
		this.plugin = plugin;
		this.processors = new ArrayList<LivingEntityProcessor>();

		if(this.plugin.getConfigManager().isPlayerAffected()){
			LivingEntityProcessor processor = new LivingEntityProcessor(
							this.plugin.getServer().getPluginManager(),
							this.plugin.getConfigManager().getPlayerStackedHeal(),
							this.plugin.getConfigManager().getPlayerStackedDamage()){
				@Override
				public boolean process(LivingEntity entity, List<Totem> totems){
					if(!(entity instanceof Player)) return false;
					Player player = (Player) entity;
					boolean canbehealed = player.hasPermission("healingtotem.heal");
					boolean canbedamaged = player.hasPermission("healingtotem.damage");
					if(!canbehealed && !canbedamaged) return false;
					int power = this.sumTotemEffectivePower(entity, totems);
					if(power > 0 && !canbehealed) power = 0;
					if(power < 0 && !canbedamaged) power = 0;
					this.applyHeal(entity, power);
					return true;
				}
			};
			this.processors.add(processor);
		}

		if(this.plugin.getConfigManager().isMobAffected()){
			LivingEntityProcessor processor = new LivingEntityProcessor(
							this.plugin.getServer().getPluginManager(),
							this.plugin.getConfigManager().getMobStackedHeal(),
							this.plugin.getConfigManager().getMobStackedDamage()){
				@Override
				public boolean process(LivingEntity entity, List<Totem> totems){
					if(!(entity instanceof Monster)) return false;
					int power = this.sumTotemEffectivePower(entity, totems);
					this.applyHeal(entity, power);
					return true;
				}
			};
			this.processors.add(processor);
		}

		if(this.plugin.getConfigManager().isTameWolfAffected()){
			LivingEntityProcessor processor = new LivingEntityProcessor(
							this.plugin.getServer().getPluginManager(),
							this.plugin.getConfigManager().getTameWolfStackedHeal(),
							this.plugin.getConfigManager().getTameWolfStackedDamage()){
				@Override
				public boolean process(LivingEntity entity, List<Totem> totems){
					if(!(entity instanceof Wolf) || !((Wolf) entity).isTamed()) return false;
					int power = this.sumTotemEffectivePower(entity, totems);
					this.applyHeal(entity, power);
					return true;
				}
			};
			this.processors.add(processor);
		}

		if(this.plugin.getConfigManager().isAngryWolfAffected()){
			LivingEntityProcessor processor = new LivingEntityProcessor(
							this.plugin.getServer().getPluginManager(),
							this.plugin.getConfigManager().getAngryWolfStackedHeal(),
							this.plugin.getConfigManager().getAngryWolfStackedDamage()){
				@Override
				public boolean process(LivingEntity entity, List<Totem> totems){
					if(!(entity instanceof Wolf) || !((Wolf) entity).isAngry()) return false;
					int power = this.sumTotemEffectivePower(entity, totems);
					this.applyHeal(entity, power);
					return true;
				}
			};
			this.processors.add(processor);
		}
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

		List<Totem> totems = this.plugin.getTotemManager().getTotems();
		List<World> worlds = this.plugin.getServer().getWorlds();

		for(World world : worlds){
			List<LivingEntity> livingentities = world.getLivingEntities();
			for(LivingEntity livingentity : livingentities){
				for(LivingEntityProcessor processor : this.processors){
					if(processor.process(livingentity, totems)) break;
				}
			}
		}
	}
}
