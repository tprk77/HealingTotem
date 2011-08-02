package tprk77.healingtotem.totem;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import tprk77.util.structure.Structure;

/**
 * A Totem Pole...
 * 
 * @author tim
 */
public class Totem extends Structure {
	
	private final TotemType totemtype;

	public Totem(TotemType totemtype, Block block){
		super(totemtype.getStructureType(), block);
		this.totemtype = totemtype;
	}
	
	public TotemType getTotemType(){
		return totemtype;
	}

	public boolean inRange(Player player){
		try{
			double range = this.totemtype.getRange();
			return (range * range) > this.getRootBlock().getLocation().distanceSquared(player.getLocation());
		}catch(IllegalArgumentException ex){
			return false;
		}
	}

	public void affectHealth(Player player){
		int power = this.totemtype.getPower();
		if(power > 0){
			int hp = player.getHealth();
			// BTW, 20 is full health, 0 is dead
			if(hp < 20){
				player.setHealth(hp + power);
			}
		}else if(power < 0){
			player.damage(-power);
		}
	}
}
