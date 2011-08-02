package tprk77.util.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * An immutable type representing a type of structure. This is basically used
 * as a pattern to search for. It has some protected methods used by Structure.
 * 
 * @author tim
 */
public final class StructureType {

	public static final class Prototype {

		private Map<BlockOffset, Material> protopattern;
		private Map<Material, ArrayList<BlockOffset>> protoreversepattern;
		
		public Prototype(){
			this.protopattern = new HashMap<BlockOffset, Material>();
			this.protoreversepattern = new EnumMap<Material, ArrayList<BlockOffset>>(Material.class);
		}

		public void addBlock(int x, int y, int z, Material material){
			
			BlockOffset offset = new BlockOffset(x, y, z);
			
			// check for a bad material
			if(material == null){
				throw new IllegalArgumentException("material can't be null");
			}
			
			// check for previous definition
			if(this.protopattern.containsKey(offset)){
				throw new IllegalArgumentException("block was already defined");
			}
			
			// add to the pattern
			this.protopattern.put(offset, material);

			// also add to the reverse pattern
			ArrayList<BlockOffset> offsetlist;
			if(this.protoreversepattern.containsKey(material)){
				offsetlist = this.protoreversepattern.get(material);
				offsetlist.add(offset);
			}else{
				offsetlist = new ArrayList<BlockOffset>(Arrays.asList(offset));
			}
			this.protoreversepattern.put(material, offsetlist);
		}
	}
	
	/*
	 * This object is supposed to be immutable. These maps should never be
	 * modified by any method. Also, methods should never return a reference to
	 * these maps from a method. This implies that this type needs to be able
	 * to verify structures and whatever, so it doesn't have to expose its
	 * data to the outside world.
	 */
	private Map<BlockOffset, Material> pattern;
	private Map<Material, ArrayList<BlockOffset>> reversepattern;

	public StructureType(Prototype proto){
		this.pattern = new HashMap<BlockOffset, Material>(proto.protopattern);
		this.reversepattern = new EnumMap<Material, ArrayList<BlockOffset>>(proto.protoreversepattern);
	}

	public int getBlockCount(){
		return this.pattern.size();
	}

	public Set<Material> getMaterials(){
		return this.reversepattern.keySet();
	}
	
	public Map<BlockOffset, Material> getPattern(){
		// members of the hash are immutable, so this should be ok
		return new HashMap<BlockOffset, Material>(this.pattern);
	}

	/**
	 * This function should only be called by Structure objects!
	 * 
	 * Search around the given block for the block pattern. For the search to be
	 * successful v must point to a block which is part of the pattern. If the
	 * block is not part of the pattern, or the pattern is incomplete,
	 * then this function will return null. If the pattern is found, a
	 * Block corresponding to the pattern origin will be returned.
	 * 
	 * @param v The Block to search around.
	 * @return If the search is successful, then a Block corresponding to the
	 * pattern origin (offset <0, 0, 0>). If the search fails, then null.
	 */
	protected Block searchAtBlock(Block block){

		// check if the block isn't a meterial from the set
		if(!this.pattern.containsValue(block.getType())){
			return null;
		}
		
		// get all the matching offsets
		ArrayList<BlockOffset> offsets = this.reversepattern.get(block.getType());
		
		// for each offset find the possible world origin and test for a structure
		for(BlockOffset offset : offsets){
			
			// find the origin block
			Block rootblock = block.getRelative(
							-offset.getX(), -offset.getY(), -offset.getZ());
			
			// see if it is a structure
			if(this.verifyStructure(rootblock)){
				return rootblock;
			}
		}

		// for all possible searches, we got nothing
		return null;
	}
	
	/**
	 * This function should only be called by Structure objects!
	 * 
	 * Verify that a structure exists at this root block.
	 * 
	 * @param rootblock The origin of the structure.
	 * @return If a structure was actually there.
	 */
	protected boolean verifyStructure(Block rootblock){
		
		// check each block from the pattern
		for(BlockOffset offset : this.pattern.keySet()){
			
			// use the offset to get the block
			Block block = rootblock.getRelative(
							offset.getX(), offset.getY(), offset.getZ());
			
			// check the material
			Material material = this.pattern.get(offset);
			if(block.getType() != material){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This function should only be called by Structure objects!
	 * 
	 * Get a list of structure blocks given a valid root block. The root block
	 * MUST be part of a valid structure!
	 * 
	 * @param rootblock The origin of the structure.
	 * @return A set of blocks.
	 */
	protected Set<Block> getBlocks(Block rootblock){
		
		// make a new set
		Set<Block> blocks = new HashSet<Block>();
		
		// for each offset add a block
		for(BlockOffset offset : this.pattern.keySet()){
			Block block = rootblock.getRelative(
							offset.getX(), offset.getY(), offset.getZ());
			blocks.add(block);
		}
		
		return blocks;
	}
	
	@Override
	public String toString(){
		String s = "";
		for(BlockOffset b:this.pattern.keySet()){
			Material m = this.pattern.get(b);
			s = s + "{x: " + b.getX() + ", y: " + b.getY() + ", z: " + b.getZ() + ", type: " + m.toString() + "}, ";
		}
		return "[" + s + "]";
	}
}
