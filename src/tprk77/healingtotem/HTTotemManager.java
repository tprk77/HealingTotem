package tprk77.healingtotem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import tprk77.healingtotem.totem.Totem;
import tprk77.healingtotem.totem.TotemType;
import tprk77.util.BlockHashable;
import tprk77.util.structure.BlockOffset;
import tprk77.util.structure.Rotator;
import tprk77.util.structure.StructureType;

/**
 *
 * @author tim
 */
public class HTTotemManager {

	private final HTPlugin plugin;

	private final String TOTEM_TYPES_FILENAME = "totemtypes.yml";
	private final String TOTEM_FILENAME = "totems.yml";
	private final String TOTEM_SQLITE_FILENAME = "totems.sqlite";

	private List<TotemType> totemtypes;
	private List<Totem> totems;

	HashMap<BlockHashable, Set<Totem>> blockhash;

	public HTTotemManager(HTPlugin plugin){
		this.plugin = plugin;
		this.totemtypes = new ArrayList<TotemType>();
		this.totems = new ArrayList<Totem>();
		this.blockhash = new HashMap<BlockHashable, Set<Totem>>();
	}

	public List<Totem> getTotems(){
		return new ArrayList<Totem>(this.totems);
	}

	public List<TotemType> getTotemTypes(){
		return new ArrayList<TotemType>(this.totemtypes);
	}

	public void addTotem(Totem totem){
		this.totems.add(totem);

		// add to block hash
		for(Block block : totem.getBlocks()){
			BlockHashable bh = new BlockHashable(block);
			Set<Totem> existing = this.blockhash.get(bh);
			if(existing == null){
				this.blockhash.put(bh, new HashSet<Totem>(Arrays.asList(totem)));
			}else{
				existing.add(totem);
			}
		}
	}

	public void removeTotem(Totem totem){
		this.totems.remove(totem);

		// remove from block hash
		for(Block block : totem.getBlocks()){
			BlockHashable bh = new BlockHashable(block);
			Set<Totem> existing = this.blockhash.get(bh);
			existing.remove(totem);
			if(existing.isEmpty()){
				this.blockhash.remove(bh);
			}
		}
	}

	public Set<Totem> getTotemsFromBlock(Block block){
		BlockHashable bh = new BlockHashable(block);
		return this.blockhash.get(bh);
	}

	public TotemType getTotemType(String name){
		for(TotemType type : this.totemtypes){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

	public void loadTotemTypesOrDefault(){

		File totemtypesfile = new File(this.plugin.getDataFolder(), this.TOTEM_TYPES_FILENAME);
		if(!totemtypesfile.isFile()){
			try{
				totemtypesfile.getParentFile().mkdirs();
				totemtypesfile.createNewFile();
				this.saveDefaultTotemTypes();
			}catch(Exception ex){
				this.plugin.warn("could not create file " + totemtypesfile.getName());
			}
		}

		this.loadTotemTypes();
	}

	private void loadTotemTypes(){

		File totemtypesfile = new File(this.plugin.getDataFolder(), this.TOTEM_TYPES_FILENAME);
		Configuration conf = new Configuration(totemtypesfile);
		conf.load();

		List<ConfigurationNode> nodelist = conf.getNodeList("totemtypes", new ArrayList<ConfigurationNode>());

		for(ConfigurationNode node : nodelist){
			TotemType totemtype = this.yaml2totemtype(node);
			if(totemtype == null){
				this.plugin.warn("a totem type couldn't be loaded");
			}else{
				this.totemtypes.add(totemtype);
			}
		}

		/*
		 * Sort the TotemTypes by structure size. This way, larger totems will be
		 * found before smaller totems (and possibly subtotems).
		 */
		Collections.sort(this.totemtypes, new Comparator<TotemType>(){
			@Override
			public int compare(TotemType o1, TotemType o2){
				return o1.getStructureType().getBlockCount()
								- o2.getStructureType().getBlockCount();
			}
		});
		Collections.reverse(this.totemtypes);

		this.plugin.log("loaded " + this.totemtypes.size() + " totem types");
	}

	private void saveDefaultTotemTypes(){

		File totemtypesfile = new File(this.plugin.getDataFolder(), this.TOTEM_TYPES_FILENAME);
		Configuration conf = new Configuration(totemtypesfile);

		List<Object> yamllist = new ArrayList<Object>();

		TotemType totemtype;
		StructureType structuretype;
		StructureType.Prototype proto;

		proto = new StructureType.Prototype();
		proto.addBlock(0, 0, 0, Material.COBBLESTONE);
		proto.addBlock(0, 1, 0, Material.COBBLESTONE);
		proto.addBlock(0, 2, 0, Material.COBBLESTONE);
		proto.addBlock(0, 3, 0, Material.LAPIS_BLOCK);
		proto.addBlock(0, 4, 0, Material.LAPIS_BLOCK);
		structuretype = new StructureType(proto);
		totemtype = new TotemType("minor", 1, 15.0, structuretype, Rotator.NONE);
		yamllist.add(this.totemtype2yaml(totemtype));

		proto = new StructureType.Prototype();
		proto.addBlock(0, 0, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 1, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 2, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 3, 0, Material.GOLD_BLOCK);
		proto.addBlock(0, 4, 0, Material.GOLD_BLOCK);
		structuretype = new StructureType(proto);
		totemtype = new TotemType("normal", 1, 30.0, structuretype, Rotator.NONE);
		yamllist.add(this.totemtype2yaml(totemtype));

		proto = new StructureType.Prototype();
		proto.addBlock(0, 0, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 1, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 2, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 3, 0, Material.GOLD_BLOCK);
		proto.addBlock(0, 4, 0, Material.DIAMOND_BLOCK);
		structuretype = new StructureType(proto);
		totemtype = new TotemType("major", 2, 45.0, structuretype, Rotator.NONE);
		yamllist.add(this.totemtype2yaml(totemtype));

		proto = new StructureType.Prototype();
		proto.addBlock(0, 0, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 1, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 2, 0, Material.IRON_BLOCK);
		proto.addBlock(0, 3, 0, Material.DIAMOND_BLOCK);
		proto.addBlock(0, 4, 0, Material.DIAMOND_BLOCK);
		structuretype = new StructureType(proto);
		totemtype = new TotemType("super", 2, 75.0, structuretype, Rotator.NONE);
		yamllist.add(this.totemtype2yaml(totemtype));

		proto = new StructureType.Prototype();
		proto.addBlock(0, 0, 0, Material.COBBLESTONE);
		proto.addBlock(0, 1, 0, Material.COBBLESTONE);
		proto.addBlock(0, 2, 0, Material.COBBLESTONE);
		proto.addBlock(0, 3, 0, Material.JACK_O_LANTERN);
		proto.addBlock(0, 4, 0, Material.JACK_O_LANTERN);
		structuretype = new StructureType(proto);
		totemtype = new TotemType("evilminor", -1, 15.0, structuretype, Rotator.NONE);
		yamllist.add(this.totemtype2yaml(totemtype));

		proto = new StructureType.Prototype();
		proto.addBlock(0, 0, 0, Material.NETHERRACK);
		proto.addBlock(0, 1, 0, Material.NETHERRACK);
		proto.addBlock(0, 2, 0, Material.NETHERRACK);
		proto.addBlock(0, 3, 0, Material.GLOWSTONE);
		proto.addBlock(0, 4, 0, Material.GLOWSTONE);
		structuretype = new StructureType(proto);
		totemtype = new TotemType("evilnormal", -1, 30.0, structuretype, Rotator.NONE);
		yamllist.add(this.totemtype2yaml(totemtype));

		conf.setProperty("totemtypes", yamllist);
		conf.save();
	}

	public void loadTotems(){

		File totemsfile = new File(this.plugin.getDataFolder(), this.TOTEM_FILENAME);
		Configuration conf = new Configuration(totemsfile);
		conf.load();

		List<ConfigurationNode> nodelist = conf.getNodeList("totems", new ArrayList<ConfigurationNode>());

		for(ConfigurationNode node : nodelist){
			Totem totem = this.yaml2totem(node);
			if(totem == null){
				this.plugin.warn("a totem couldn't be loaded");
			}else{
				this.addTotem(totem);
			}
		}

		this.plugin.log("loaded " + this.totems.size() + " totems");
	}

	public void saveTotems(){

		File totemsfile = new File(this.plugin.getDataFolder(), this.TOTEM_FILENAME);
		Configuration conf = new Configuration(totemsfile);

		List<Object> yamllist = new ArrayList<Object>();

		for(Totem totem : this.totems){
			yamllist.add(this.totem2yaml(totem));
		}

		this.plugin.log("saved " + this.totems.size() + " totems");

		conf.setProperty("totems", yamllist);
		conf.save();
	}

	private Map<String, Object> totem2yaml(Totem totem){
		HashMap<String, Object> yamlmap = new HashMap<String, Object>();
		yamlmap.put("world", totem.getRootBlock().getWorld().getName());
		yamlmap.put("x", totem.getRootBlock().getX());
		yamlmap.put("y", totem.getRootBlock().getY());
		yamlmap.put("z", totem.getRootBlock().getZ());
		yamlmap.put("type", totem.getTotemType().getName());
		return yamlmap;
	}

	private Totem yaml2totem(ConfigurationNode node){
		String worldstr = node.getString("world", null);
		if(worldstr == null){
			this.plugin.warn("totem's world is not set");
			return null;
		}

		int x = node.getInt("x", Integer.MIN_VALUE);
		int y = node.getInt("y", Integer.MIN_VALUE);
		int z = node.getInt("z", Integer.MIN_VALUE);
		if(x == Integer.MIN_VALUE || y == Integer.MIN_VALUE || z == Integer.MIN_VALUE){
			this.plugin.warn("totem's x, y, or z is not set");
			return null;
		}

		String totemtypestr = node.getString("type", null);
		if(totemtypestr == null){
			this.plugin.warn("totem's type is not set");
			return null;
		}

		World world = this.plugin.getServer().getWorld(worldstr);
		if(world == null){
			this.plugin.warn("totem's world is not recognized");
			return null;
		}

		TotemType totemtype = this.getTotemType(totemtypestr);
		if(totemtype == null){
			this.plugin.warn("totem's type is not recognized");
			return null;
		}

		Block block = world.getBlockAt(x, y, z);
		Totem totem = new Totem(totemtype, block);

		if(!totem.verifyStructure()){
			this.plugin.warn("totem's structure was bad");
			return null;
		}

		return totem;
	}

	private List<Object> structuretype2yaml(StructureType structuretype){
		List<Object> yamllist = new ArrayList<Object>();
		for(BlockOffset offset : structuretype.getPattern().keySet()){
			Material material = structuretype.getPattern().get(offset);
			HashMap<String, Object> part = new HashMap<String, Object>();
			part.put("x", offset.getX());
			part.put("y", offset.getY());
			part.put("z", offset.getZ());
			part.put("material", material.toString());
			yamllist.add(part);
		}
		return yamllist;
	}

	private StructureType yaml2structuretype(List<ConfigurationNode> nodes){
		StructureType.Prototype prototype = new StructureType.Prototype();
		for(ConfigurationNode node : nodes){
			int x = node.getInt("x", Integer.MIN_VALUE);
			int y = node.getInt("y", Integer.MIN_VALUE);
			int z = node.getInt("z", Integer.MIN_VALUE);
			if(x == Integer.MIN_VALUE || y == Integer.MIN_VALUE || z == Integer.MIN_VALUE){
				this.plugin.warn("structure's x, y, or z is not set");
				return null;
			}

			String materialstr = node.getString("material", null);
			if(materialstr == null){
				this.plugin.warn("structure's material is not set");
				return null;
			}

			Material material = Material.matchMaterial(materialstr);
			if(material == null){
				this.plugin.warn("structure's material is not recognized");
				return null;
			}

			prototype.addBlock(x, y, z, material);
		}
		return new StructureType(prototype);
	}

	private Map<String, Object> totemtype2yaml(TotemType totemtype){
		HashMap<String, Object> yamlmap = new HashMap<String, Object>();
		yamlmap.put("name", totemtype.getName());
		yamlmap.put("power", totemtype.getPower());
		yamlmap.put("range", totemtype.getRange());
		yamlmap.put("rotator", totemtype.getRotator().toString());
		yamlmap.put("structure", this.structuretype2yaml(totemtype.getStructureType()));
		return yamlmap;
	}

	private TotemType yaml2totemtype(ConfigurationNode node){
		String name = node.getString("name", null);
		if(name == null){
			this.plugin.warn("totem type's name is not set");
			return null;
		}

		int power = node.getInt("power", Integer.MIN_VALUE);
		if(power  == Integer.MIN_VALUE){
			this.plugin.warn("totem type's name is not set");
			return null;
		}

		double range = node.getDouble("range", Double.NaN);
		if(Double.isNaN(range)){
			this.plugin.warn("totem type's range is not set");
			return null;
		}

		String rotatorstr = node.getString("rotator", null);
		if(rotatorstr == null){
			this.plugin.warn("totem type's rotator is not set");
			rotatorstr = ":(";
		}

		Rotator rotator = Rotator.matchRotator(rotatorstr);
		if(rotator == null){
			this.plugin.warn("totem type's rotator is not valid, using default");
			rotator = Rotator.getDefault();
		}

		List<ConfigurationNode> structuretypenodes = node.getNodeList("structure", new ArrayList<ConfigurationNode>());
		if(structuretypenodes.isEmpty()){
			this.plugin.warn("totem type's structure is not set");
			return null;
		}

		StructureType structuretype = this.yaml2structuretype(structuretypenodes);
		if(structuretype == null){
			this.plugin.warn("totem type's structure is not valid");
			return null;
		}

		if(structuretype.getBlockCount() < 3){
			this.plugin.warn("for technical reasons, the structure's block count must be at least 3");
			return null;
		}

		return new TotemType(name, power, range, structuretype, rotator);
	}
}
