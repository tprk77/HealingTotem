package tprk77.healingtotem;

import java.util.List;
import java.util.Set;
import tprk77.healingtotem.totem.Totem;
import tprk77.healingtotem.totem.TotemType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author tim
 */
public class HTBlockListener extends BlockListener {

	private final HTPlugin plugin;

	enum SubstructurePolicy {ALLOWED, REPLACE, NOT_ALLOWED};
	private final SubstructurePolicy substructurepolicy;

	public HTBlockListener(HTPlugin plugin){
		this.plugin = plugin;
		this.substructurepolicy = SubstructurePolicy.NOT_ALLOWED;
	}

	public void registerEvents(){
		PluginManager pm = this.plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, this, Event.Priority.Low, this.plugin);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this, Event.Priority.High, this.plugin);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.isCancelled()) return;

		Block blockplaced = event.getBlockPlaced();
		List<TotemType> totemtypes = this.plugin.getTotemManager().getTotemTypes();

		totembuild:
		for(TotemType totemtype : totemtypes){

			Totem totem = new Totem(totemtype, blockplaced);
			if(!totem.verifyStructure()) continue totembuild;

			// check permissions!
			Player player = event.getPlayer();
			if(!player.hasPermission("healingtotem.build")){
				event.setCancelled(true);
				return;
			}

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

//			if(this.substructurepolicy == SubstructurePolicy.NOT_ALLOWED){
				for(Block block : totem.getBlocks()){
					if(this.plugin.getTotemManager().getTotemsFromBlock(block) != null){
						break totembuild;
					}
				}
//			}else if(this.substructurepolicy == SubstructurePolicy.REPLACE){
//				// TODO this REPLACE code doesn't work / isn't finished
//				for(Block block : totem.getBlocks()){
//					Set<Totem> subtotems = this.plugin.getTotemManager().getTotemsFromBlock(block);
//					if(subtotems != null){
//						for(Totem subtotem : subtotems){
//							this.plugin.getTotemManager().removeTotem(subtotem);
//						}
//					}
//				}
//			}

			this.plugin.getTotemManager().addTotem(totem);
			this.plugin.getTotemManager().saveTotems();
			break totembuild;
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()) return;

		Block brokenblock = event.getBlock();
		Set<Totem> totems = this.plugin.getTotemManager().getTotemsFromBlock(brokenblock);

		if(totems == null) return;

		// check permissions!
		Player player = event.getPlayer();
		if(!player.hasPermission("healingtotem.break")){
			event.setCancelled(true);
			return;
		}

		for(Totem totem : totems){

			// TODO add REPLACE code?

			this.plugin.getTotemManager().removeTotem(totem);
			this.plugin.getTotemManager().saveTotems();
		}
	}
}
