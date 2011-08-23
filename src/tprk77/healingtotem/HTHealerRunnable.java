package tprk77.healingtotem;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import tprk77.healingtotem.totem.Totem;

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
		private final int maxhealth;

		public LivingEntityProcessor(PluginManager eventcaller, int stackedheal, int stackeddamage, int maxhealth){
			this.eventcaller = eventcaller;
			this.stackedheal = stackedheal;
			this.stackeddamage = stackeddamage;
			this.maxhealth = maxhealth;
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

			/*
			 * Only let the events be cancelled. For now don't let the event modify
			 * the power. Just use the original power and disregard the event power
			 * (if it has been changed).
			 *
			 * Then again... Maybe I'll change my mind.
			 */

			if(power > this.stackedheal){
				power = this.stackedheal;
			}else if(power < -this.stackeddamage){
				power = -this.stackeddamage;
			}

			int health = entity.getHealth();

			if(power > 0){
				EntityRegainHealthEvent regen = new EntityRegainHealthEvent(
								entity, power, EntityRegainHealthEvent.RegainReason.CUSTOM);
				this.eventcaller.callEvent(regen);
				if(regen.isCancelled()){
					return;
				}
			}else if(power < 0){
				EntityDamageEvent damage = new EntityDamageEvent(
								entity, EntityDamageEvent.DamageCause.CUSTOM, -power);
				this.eventcaller.callEvent(damage);
				if(damage.isCancelled()){
					return;
				}
			}else{
				return;
			}

			int newhealth = health + power;
			if(newhealth > this.maxhealth){
				newhealth = this.maxhealth;
			}

			if(newhealth > health){
				entity.setHealth(newhealth);
			}else if(newhealth < health){
				entity.damage(-power);
			}
		}
	}

	HTHealerRunnable(HTPlugin plugin){
		this.plugin = plugin;
		this.processors = new ArrayList<LivingEntityProcessor>();

		LivingEntityProcessor processor;

		processor = new LivingEntityProcessor(
						this.plugin.getServer().getPluginManager(),
						this.plugin.getConfigManager().getPlayerStackedHeal(),
						this.plugin.getConfigManager().getPlayerStackedDamage(),
						20){
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

		processor = new LivingEntityProcessor(
						this.plugin.getServer().getPluginManager(),
						this.plugin.getConfigManager().getMobStackedHeal(),
						this.plugin.getConfigManager().getMobStackedDamage(),
						20){
			@Override
			public boolean process(LivingEntity entity, List<Totem> totems){
				if(!(entity instanceof Monster)) return false;
				int power = this.sumTotemEffectivePower(entity, totems);
				this.applyHeal(entity, power);
				return true;
			}
		};
		this.processors.add(processor);

		processor = new LivingEntityProcessor(
						this.plugin.getServer().getPluginManager(),
						this.plugin.getConfigManager().getTamedWolfStackedHeal(),
						this.plugin.getConfigManager().getTamedWolfStackedDamage(),
						20){
			@Override
			public boolean process(LivingEntity entity, List<Totem> totems){
				if(!(entity instanceof Wolf) || !((Wolf) entity).isTamed()) return false;
				int power = this.sumTotemEffectivePower(entity, totems);
				this.applyHeal(entity, power);
				return true;
			}
		};
		this.processors.add(processor);

		processor = new LivingEntityProcessor(
						this.plugin.getServer().getPluginManager(),
						this.plugin.getConfigManager().getAngryWolfStackedHeal(),
						this.plugin.getConfigManager().getAngryWolfStackedDamage(),
						8){
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
