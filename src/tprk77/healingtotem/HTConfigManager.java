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

	private final String CONFIG_FILENAME = "config.yml";

	private boolean playeraffected;
	private boolean mobaffected;
	private boolean tamewolfaffected;
	private boolean angrywolfaffected;

	private int playerstackedheal;
	private int playerstackeddamage;

	private int mobstackedheal;
	private int mobstackeddamage;

	private int tamewolfstackedheal;
	private int tamewolfstackeddamage;

	private int angrywolfstackedheal;
	private int angrywolfstackeddamage;

	public HTConfigManager(HTPlugin plugin){
		this.plugin = plugin;
	}

	public void loadConfigOrDefault(){

		File configfile = new File(this.plugin.getDataFolder(), this.CONFIG_FILENAME);
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

		File configfile = new File(this.plugin.getDataFolder(), this.CONFIG_FILENAME);
		Configuration conf = new Configuration(configfile);
		conf.load();

		ConfigurationNode node;

		node = conf.getNode("player");
		if(node != null){
			this.playeraffected = node.getBoolean("affected", true);
			this.playerstackedheal = node.getInt("stackedheal", 4);
			this.playerstackeddamage = node.getInt("stackeddamage", 4);
		}else{
			this.playeraffected = true;
			this.playerstackedheal = 4;
			this.playerstackeddamage = 4;
		}

		node = conf.getNode("mob");
		if(node != null){
			this.mobaffected = node.getBoolean("affected", true);
			this.mobstackedheal = node.getInt("stackedheal", 4);
			this.mobstackeddamage = node.getInt("stackeddamage", 4);
		}else{
			this.mobaffected = true;
			this.mobstackedheal = 4;
			this.mobstackeddamage = 4;
		}

		node = conf.getNode("tamewolf");
		if(node != null){
			this.tamewolfaffected = node.getBoolean("affected", true);
			this.tamewolfstackedheal = node.getInt("stackedheal", 4);
			this.tamewolfstackeddamage = node.getInt("stackeddamage", 4);
		}else{
			this.tamewolfaffected = true;
			this.tamewolfstackedheal = 4;
			this.tamewolfstackeddamage = 4;
		}

		node = conf.getNode("angrywolf");
		if(node != null){
			this.angrywolfaffected = node.getBoolean("affected", true);
			this.angrywolfstackedheal = node.getInt("stackedheal", 4);
			this.angrywolfstackeddamage = node.getInt("stackeddamage", 4);
		}else{
			this.angrywolfaffected = true;
			this.angrywolfstackedheal = 4;
			this.angrywolfstackeddamage = 4;
		}
	}

	private void saveDefaultConfig(){

		File configfile = new File(this.plugin.getDataFolder(), this.CONFIG_FILENAME);
		Configuration conf = new Configuration(configfile);

		HashMap<String, Object> yamlmap = new HashMap<String, Object>();

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("affected", true);
		yamlmap.put("stackedheal", 4);
		yamlmap.put("stackeddamage", 4);
		conf.setProperty("player", yamlmap);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("affected", true);
		yamlmap.put("stackedheal", 4);
		yamlmap.put("stackeddamage", 4);
		conf.setProperty("mob", yamlmap);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("affected", true);
		yamlmap.put("stackedheal", 4);
		yamlmap.put("stackeddamage", 4);
		conf.setProperty("tamewolf", yamlmap);

		yamlmap = new HashMap<String, Object>();
		yamlmap.put("affected", true);
		yamlmap.put("stackedheal", 4);
		yamlmap.put("stackeddamage", 4);
		conf.setProperty("angrywolf", yamlmap);

		conf.save();
	}

	public boolean isPlayerAffected(){
		return this.playeraffected;
	}

	public boolean isMobAffected(){
		return this.mobaffected;
	}

	public boolean isTameWolfAffected(){
		return this.tamewolfaffected;
	}

	public boolean isAngryWolfAffected(){
		return this.angrywolfaffected;
	}

	public int getPlayerStackedHeal(){
		return this.playerstackedheal;
	}

	public int getMobStackedHeal(){
		return this.mobstackedheal;
	}

	public int getTameWolfStackedHeal(){
		return this.tamewolfstackedheal;
	}

	public int getAngryWolfStackedHeal(){
		return this.angrywolfstackedheal;
	}

	public int getPlayerStackedDamage(){
		return this.playerstackeddamage;
	}

	public int getMobStackedDamage(){
		return this.playerstackeddamage;
	}

	public int getTameWolfStackedDamage(){
		return this.playerstackeddamage;
	}

	public int getAngryWolfStackedDamage(){
		return this.playerstackeddamage;
	}
}
