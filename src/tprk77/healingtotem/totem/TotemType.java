package tprk77.healingtotem.totem;

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
	
	public TotemType(String name, int power, double range, StructureType structuretype){
		this.name = name;
		this.power = power;
		this.range = range;
		this.structuretype = structuretype;
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
	
	@Override
	public String toString(){
		return "totemtype { name: " + this.name + ", power: " + this.power +
						", range: " + this.range + ", structure: " + this.structuretype + "}";
	}
}
