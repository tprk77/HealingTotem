package tprk77.healingtotem.totem;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import tprk77.util.structure.Structure;

/**
 * A Totem Pole...
 *
 * @author tim
 */
public class Totem extends Structure {

	private final TotemType totemtype;

	public Totem(TotemType totemtype, Block block){
		super(totemtype.getAllStructureTypes(), block);
		this.totemtype = totemtype;
	}

	public TotemType getTotemType(){
		return this.totemtype;
	}

	public boolean inRange(LivingEntity livingentity){
		try{
			double range = this.totemtype.getRange();
			return this.getRootBlock().getLocation().distanceSquared(
							livingentity.getLocation()) < (range * range);
		}catch(IllegalArgumentException ex){
			return false;
		}
	}

	public int getEffectivePower(LivingEntity livingentity){

		if(this.isPowered()){
			return 0;
		}

		if(livingentity instanceof Player){
			return this.totemtype.getPower();
		}else if(livingentity instanceof Monster){
			return -this.totemtype.getPower();
		}else if(livingentity instanceof Wolf){
			if(((Wolf)livingentity).isTamed()){
				return this.totemtype.getPower();
			}else if(((Wolf)livingentity).isAngry()){
				return -this.totemtype.getPower();
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

	private boolean isPowered(){
		for(Block block : this.blocks){
			if(block.isBlockPowered() || block.isBlockIndirectlyPowered()){
				return true;
			}
		}
		return false;
	}
}
