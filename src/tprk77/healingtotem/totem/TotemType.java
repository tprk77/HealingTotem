package tprk77.healingtotem.totem;

import java.util.List;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import tprk77.util.structure.Rotator;
import tprk77.util.structure.StructureType;

/**
 * An immutable type representing totem types.
 *
 * @author tim
 */
public final class TotemType {

	private final String name;
	private final int power;
	private final double range;

	private final StructureType structuretype;
	private final List<StructureType> rotatedstructuretypes;
	private final Rotator rotator;

	private final boolean affectsplayers;
	private final boolean affectsmobs;
	private final boolean affectstamedwolves;
	private final boolean affectsangrywolves;

	public TotemType(String name, int power, double range, StructureType structuretype){
		this(name, power, range, structuretype, Rotator.NONE, true, true, true, true);
	}

	public TotemType(String name, int power, double range, StructureType structuretype, Rotator rotator){
		this(name, power, range, structuretype, rotator, true, true, true, true);
	}

	public TotemType(String name, int power, double range, StructureType structuretype, Rotator rotator,
					boolean affectsplayers, boolean affectsmobs, boolean affectstamedwolves, boolean affectsangrywolves){
		this.name = name;
		this.power = power;
		this.range = range;

		this.structuretype = structuretype;
		this.rotatedstructuretypes = structuretype.makeRotatedStructureTypes(rotator);
		this.rotator = rotator;

		this.affectsplayers = affectsplayers;
		this.affectsmobs = affectsmobs;
		this.affectstamedwolves = affectstamedwolves;
		this.affectsangrywolves = affectsangrywolves;
	}

	public String getName(){
		return this.name;
	}

	public int getPower(){
		return this.power;
	}

	public int getEffectivePower(LivingEntity entity){
		if(entity instanceof Player){
			return this.affectsplayers ? this.power : 0;
		}else if(entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast){
			return this.affectsmobs ? -this.power : 0;
		}else if(entity instanceof Wolf){
			if(((Wolf)entity).isTamed()){
				return this.affectstamedwolves ? this.power : 0;
			}else if(((Wolf)entity).isAngry()){
				return this.affectsangrywolves ? -this.power : 0;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

	public double getRange(){
		return this.range;
	}

	public StructureType getStructureType(){
		return this.structuretype;
	}

	public List<StructureType> getAllStructureTypes(){
		return this.rotatedstructuretypes;
	}

	public Rotator getRotator(){
		return this.rotator;
	}

	public boolean affectsPlayers(){
		return this.affectsplayers;
	}

	public boolean affectsMobs(){
		return this.affectsmobs;
	}

	public boolean affectsTamedWolves(){
		return this.affectstamedwolves;
	}

	public boolean affectsAngryWolves(){
		return this.affectsangrywolves;
	}

	@Override
	public String toString(){
		return "totemtype { name: " + this.name + ", power: " + this.power +
						", range: " + this.range +
						", affects players: " + this.affectsplayers +
						", affects mobs: " + this.affectsmobs +
						", affects tamed wolves: " + this.affectstamedwolves +
						", affects angry wolves: " + this.affectsangrywolves + "}";
	}
}
