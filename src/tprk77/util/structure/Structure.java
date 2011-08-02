package tprk77.util.structure;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.Block;

/**
 *
 * @author tim
 */
public class Structure {
	
	private final StructureType structuretype;
	private final Block rootblock;
	private final Set<Block> blocks;
	
	public Structure(StructureType structuretype, Block block){
		this.structuretype = structuretype;
		this.rootblock = this.structuretype.searchAtBlock(block);
		if(this.rootblock == null){
			throw new IllegalArgumentException("this is not a valid structure");
		}
		this.blocks = this.structuretype.getBlocks(rootblock);
	}
	
	public StructureType getStructureType(){
		return this.structuretype;
	}
	
	public Block getRootBlock(){
		return this.rootblock;
	}
	
	public Set<Block> getBlocks(){
		// defend against outisde adding/removing
		return new HashSet<Block>(this.blocks);
	}
	
	public boolean containsBlock(Block block){
		return this.blocks.contains(block);
	}
	
	public boolean verifyStructure(){
		return this.structuretype.verifyStructure(this.rootblock);
	}
}
