package tprk77.healingtotem.totem;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import tprk77.util.structure.Structure;

/**
 * A Totem Pole...
 *
 * @author tim
 */
public class Totem extends Structure {

	private final TotemType totemtype;
	private final String owner;

	public Totem(TotemType totemtype, Block block){
		this(totemtype, block, null);
	}

	public Totem(TotemType totemtype, Block block, String owner){
		super(totemtype.getAllStructureTypes(), block);
		this.totemtype = totemtype;
		this.owner = owner;
	}

	public TotemType getTotemType(){
		return this.totemtype;
	}

	public String getOwner(){
		return this.owner;
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

		return this.totemtype.getEffectivePower(livingentity);
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
