package tprk77.healingtotem;

import tprk77.healingtotem.totem.Totem;
import org.bukkit.entity.Player;

/**
 *
 * @author tim
 */
public class HealingTotemRunnable implements Runnable {

	private final HealingTotemPlugin plugin;

	HealingTotemRunnable(HealingTotemPlugin plugin){
		this.plugin = plugin;
	}

	@Override
	public void run(){

		Player[] players = this.plugin.getServer().getOnlinePlayers();

		for(Totem totem : this.plugin.getManager().getTotems()){
			for(Player player : players){
				if(totem.inRange(player)){
					totem.affectHealth(player);
				}
			}
		}
	}
}
