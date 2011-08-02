package tprk77.healingtotem;

import tprk77.healingtotem.totem.Totem;
import tprk77.healingtotem.totem.TotemType;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 *
 * @author tim
 */
public class HealingTotemBlockListener extends BlockListener {
	
	private final HealingTotemPlugin plugin;
	
	public HealingTotemBlockListener(HealingTotemPlugin plugin){
		this.plugin = plugin;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event){
		
		Block blockplaced = event.getBlockPlaced();
		
		for(TotemType totemtype : this.plugin.getManager().getTotemTypes()){
			try{
				Totem totem = new Totem(totemtype, blockplaced);
				this.plugin.getManager().addTotem(totem);
				this.plugin.getManager().saveTotems();
				break;
			}catch(IllegalArgumentException ex){
				// do nothing
			}
		}
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event){
		
		Block brokenblock = event.getBlock();
		
		for(Totem totem : this.plugin.getManager().getTotems()){
			if(totem.containsBlock(brokenblock)){
				this.plugin.getManager().removeTotem(totem);
				this.plugin.getManager().saveTotems();
				break;
			}
		}
	}
}
