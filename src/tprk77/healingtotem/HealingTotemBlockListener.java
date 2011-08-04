package tprk77.healingtotem;

import java.util.Set;
import tprk77.healingtotem.totem.Totem;
import tprk77.healingtotem.totem.TotemType;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author tim
 */
public class HealingTotemBlockListener extends BlockListener {

	private final HealingTotemPlugin plugin;

	enum SubstructurePolicy {ALLOWED, REPLACE, NOT_ALLOWED};
	private SubstructurePolicy substructurepolicy;

	public HealingTotemBlockListener(HealingTotemPlugin plugin){
		this.plugin = plugin;
		this.substructurepolicy = SubstructurePolicy.NOT_ALLOWED;
	}

	public void registerEvents(){
		PluginManager pm = this.plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, this, Event.Priority.Low, this.plugin);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this, Event.Priority.Low, this.plugin);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event){

		Block blockplaced = event.getBlockPlaced();

		totemcreation:
		for(TotemType totemtype : this.plugin.getManager().getTotemTypes()){
			Totem totem = new Totem(totemtype, blockplaced);
			if(totem.verifyStructure()){

				/*
				 * Actually implementing REPLACE the right way is going to be really
				 * hard. It will require ranking totems by size, and probably always
				 * giving priority to the biggest one. But since the shapes are random,
				 * probably no policy will work 100% as expected by the user.
				 *
				 * For example lets say we have T1:
				 *
				 *  8888
				 *  8  8
				 *  8888
				 *
				 * But then we also have T2:
				 *
				 *  888
				 *    8
				 *
				 * And then we make:
				 *
				 *  8888
				 *  8  888
				 *  8888 8
				 *
				 * It's getting a little to complex. KISS.
				 */

				if(this.substructurepolicy == SubstructurePolicy.NOT_ALLOWED){
					for(Block block : totem.getBlocks()){
						if(this.plugin.getManager().getTotemsFromBlock(block) != null){
							break totemcreation;
						}
					}
				}else if(this.substructurepolicy == SubstructurePolicy.REPLACE){
					// TODO this REPLACE code doesn't work / isn't finished
					for(Block block : totem.getBlocks()){
						Set<Totem> subtotems = this.plugin.getManager().getTotemsFromBlock(block);
						if(subtotems != null){
							for(Totem subtotem : subtotems){
								this.plugin.getManager().removeTotem(subtotem);
							}
						}
					}
				}

				this.plugin.getManager().addTotem(totem);
				this.plugin.getManager().saveTotems();
				break totemcreation;
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event){

		Block brokenblock = event.getBlock();
		Set<Totem> totems = this.plugin.getManager().getTotemsFromBlock(brokenblock);

		if(totems == null) return;

		for(Totem totem : totems){

			// TODO add REPLACE code?

			this.plugin.getManager().removeTotem(totem);
			this.plugin.getManager().saveTotems();
		}
	}
}
