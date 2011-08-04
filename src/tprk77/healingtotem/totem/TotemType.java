package tprk77.healingtotem.totem;

import java.util.List;
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

	public TotemType(String name, int power, double range, StructureType structuretype){
		this(name, power, range, structuretype, Rotator.NONE);
	}

	public TotemType(String name, int power, double range, StructureType structuretype, Rotator rotator){
		this.name = name;
		this.power = power;
		this.range = range;
		this.structuretype = structuretype;

		this.rotatedstructuretypes = structuretype.makeRotatedStructureTypes(rotator);
		this.rotator = rotator;
	}

	public String getName(){
		return this.name;
	}

	public int getPower(){
		return this.power;
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

	@Override
	public String toString(){
		return "totemtype { name: " + this.name + ", power: " + this.power +
						", range: " + this.range + ", number of structures: " +
						this.rotatedstructuretypes.size() + " }";
	}
}
