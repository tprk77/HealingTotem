package tprk77.healingtotem;

import java.io.File;
import java.util.HashMap;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author tim
 */
public class HTConfigManager {

	private final HTPlugin plugin;

	private final String filename = "config.yml";

	private final int def_totemsperplayer = 100;
	private final boolean def_lightning = true;
	private final boolean def_quiet = false;
	private final int def_stackedheal = 4;
	private final int def_stackeddamage = 4;

	private int totemsperplayer;
	private boolean lightning;
	private boolean quiet;

	private int playerstackedheal;
	private int playerstackeddamage;

	private int mobstackedheal;
	private int mobstackeddamage;

	private int tamedwolfstackedheal;
	private int tamedwolfstackeddamage;

	private int angrywolfstackedheal;
	private int angrywolfstackeddamage;

	public HTConfigManager(HTPlugin plugin){
		this.plugin = plugin;
	}

	public void loadConfigOrDefault(){

		File configfile = new File(this.plugin.getDataFolder(), this.filename);
		if(!configfile.isFile()){
			try{
				configfile.getParentFile().mkdirs();
				configfile.createNewFile();
				this.saveDefaultConfig();
			}catch(Exception ex){
				this.plugin.warn("could not create file " + configfile.getName());
			}
		}

		this.loadConfig();
	}

	private void loadConfig(){

		File configfile = new File(this.plugin.getDataFolder(), this.filename);
		Configuration conf = new Configuration(configfile);
		conf.load();

		ConfigurationNode node;

		this.totemsperplayer = conf.getInt("totemsperplayer", this.totemsperplayer);
		this.lightning = conf.getBoolean("lightning", this.def_lightning);
		this.quiet = conf.getBoolean("quiet", this.def_quiet);

		node = conf.getNode("player");
		if(node != null){
			this.playerstackedheal = node.getInt("stackedheal", this.def_stackedheal);
			this.playerstackeddamage = node.getInt("stackeddamage", this.def_stackeddamage);
		}else{
			this.playerstackedheal = this.def_stackedheal;
			this.playerstackeddamage = this.def_stackeddamage;
		}

		node = conf.getNode("mob");
		if(node != null){
			this.mobstackedheal = node.getInt("stackedheal", this.def_stackedheal);
			this.mobstackeddamage = node.getInt("stackeddamage", this.def_stackeddamage);
		}else{
			this.mobstackedheal = this.def_stackedheal;
			this.mobstackeddamage = this.def_stackeddamage;
		}

		node = conf.getNode("tamedwolf");
		if(node != null){
			this.tamedwolfstackedheal = node.getInt("stackedheal", this.def_stackedheal);
			this.tamedwolfstackeddamage = node.getInt("stackeddamage", this.def_stackeddamage);
		}else{
			this.tamedwolfstackedheal = this.def_stackedheal;
			this.tamedwolfstackeddamage = this.def_stackeddamage;
		}

		node = conf.getNode("angrywolf");
		if(node != null){
			this.angrywolfstackedheal = node.getInt("stackedheal", this.def_stackedheal);
			this.angrywolfstackeddamage = node.getInt("stackeddamage", this.def_stackeddamage);
		}else{
			this.angrywolfstackedheal = this.def_stackedheal;
			this.angrywolfstackeddamage = this.def_stackeddamage;
		}
	}

	private void saveDefaultConfig(){

		File configfile = new File(this.plugin.getDataFolder(), this.filename);
		Configuration conf = new Configuration(configfile);

		HashMap<String, Object> yamlmap = new HashMap<String, Object>();

		conf.setProperty("totemsperplayer", this.def_totemsperplayer);
		conf.setProperty("lightning", this.def_lightning);
		conf.setProperty("quiet", this.def_quiet);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("stackedheal", this.def_stackedheal);
		yamlmap.put("stackeddamage", this.def_stackeddamage);
		conf.setProperty("player", yamlmap);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("stackedheal", this.def_stackedheal);
		yamlmap.put("stackeddamage", this.def_stackeddamage);
		conf.setProperty("mob", yamlmap);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("stackedheal", this.def_stackedheal);
		yamlmap.put("stackeddamage", this.def_stackeddamage);
		conf.setProperty("tamedwolf", yamlmap);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("stackedheal", this.def_stackedheal);
		yamlmap.put("stackeddamage", this.def_stackeddamage);
		conf.setProperty("angrywolf", yamlmap);

		conf.save();
	}

	public boolean isLightning(){
		return this.lightning;
	}

	public boolean isQuiet(){
		return this.quiet;
	}

	public int getTotemsPerPlayer(){
		return this.totemsperplayer;
	}

	public int getPlayerStackedHeal(){
		return this.playerstackedheal;
	}

	public int getMobStackedHeal(){
		return this.mobstackedheal;
	}

	public int getTamedWolfStackedHeal(){
		return this.tamedwolfstackedheal;
	}

	public int getAngryWolfStackedHeal(){
		return this.angrywolfstackedheal;
	}

	public int getPlayerStackedDamage(){
		return this.playerstackeddamage;
	}

	public int getMobStackedDamage(){
		return this.mobstackeddamage;
	}

	public int getTamedWolfStackedDamage(){
		return this.tamedwolfstackeddamage;
	}

	public int getAngryWolfStackedDamage(){
		return this.angrywolfstackeddamage;
	}
}
