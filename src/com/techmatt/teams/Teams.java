package com.techmatt.teams;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Teams extends JavaPlugin {
	
	private EventListener eventlistener;
	private CommandExe commandExecutor;
	public String theTeamNames = null;
	
	public boolean dropLoot = true;
	public boolean rejoin = true;
	public boolean colourNames = true;
	public boolean lockGear = false;
	public boolean allowInteract = false;
	//public boolean savePlayers = false; //TODO:coming soon

	public Map<String, Team> avlbleTeams = new HashMap<String, Team>();
	public Map<String, Team> teams = new HashMap<String, Team>();
	public Map<Player, PlayerState> players = new HashMap<Player, PlayerState>();
	public Map<String, String> offlinePlayers = new HashMap<String, String>();
	
	public void onEnable() {
		avlbleTeams.put("blue", new Team(this, "Blue", Color.BLUE, ChatColor.BLUE));
		avlbleTeams.put("red", new Team(this, "Red", Color.RED, ChatColor.RED));
		avlbleTeams.put("yellow", new Team(this, "Yellow", Color.YELLOW, ChatColor.YELLOW));
		avlbleTeams.put("green", new Team(this, "Green", Color.LIME, ChatColor.GREEN));
		avlbleTeams.put("purple", new Team(this, "Purple", Color.PURPLE, ChatColor.DARK_PURPLE));
		avlbleTeams.put("orange", new Team(this, "Orange", Color.ORANGE, ChatColor.GOLD));
		avlbleTeams.put("black", new Team(this, "Black", Color.BLACK, ChatColor.BLACK));
		
		for (Map.Entry<String, Team> name : avlbleTeams.entrySet()) {
			theTeamNames += " " + (String)name.getKey() + ",";
		}
		
		commandExecutor = new CommandExe(this);
		eventlistener = new EventListener(this);
		getServer().getPluginManager().registerEvents(eventlistener, this);
		getCommand("team").setExecutor(commandExecutor);//TODO:new class for each main commandExc?
		//getCommand("event").setExecutor(commandExecutor);
		getCommand("tc").setExecutor(commandExecutor);
		getCommand("spectate").setExecutor(commandExecutor);
		getCommand("ready").setExecutor(commandExecutor);
		
		load();
		getLogger().info("Team Work v" + getDescription().getVersion() + " plugin has successfully loaded");
	}
	
	public void onDisable() {
		getLogger().info("Team Work v" + getDescription().getVersion() + " has been disabled. :(");
	}
	
	public void load() { //TODO:fix all this mess
		saveDefaultConfig();
		lockGear = getConfig().getBoolean("lockGear");
		dropLoot = getConfig().getBoolean("dropLoot");
		rejoin = getConfig().getBoolean("rejoin");
		colourNames = getConfig().getBoolean("colourNames");
		
		for (String key : getConfig().getConfigurationSection("teams").getKeys(false)) {
			teams.put(key, avlbleTeams.get(key));
			if (getConfig().contains("teams." + key + ".spawn")) {
				String[] spawn = getConfig().getString("teams." + key + ".spawn").split(",");
				teams.get(key).setSpawn(new Location(
						getServer().getWorld(spawn[0]),
						Double.parseDouble(spawn[1]),
						Double.parseDouble(spawn[2]),
						Double.parseDouble(spawn[3])
				));
			}
			teams.get(key).setScore(getConfig().getInt("teams." + key + ".score"));
			teams.get(key).setGamemode(GameMode.getByValue(getConfig().getInt("teams." + key + ".gamemode")));
			//teams.get(key).setLocked(getConfig().getInt("teams." + key + ".locked"));
			//teams.get(key).setFriendlyFire(getConfig().getBoolean("teams." + key + ".friendlyFire"));
			teams.get(key).setFreeze(getConfig().getBoolean("teams." + key + ".freeze"));
			teams.get(key).setBlockProtect(getConfig().getBoolean("teams." + key + ".blockProtect"));
		}
	}
	
	public void save() { //TODO: working on
		//save main settings
		getConfig().set("lockedHat", Boolean.valueOf(lockGear));
		getConfig().set("dropLoot", Boolean.valueOf(dropLoot));
		getConfig().set("rejoin", Boolean.valueOf(rejoin));
		getConfig().set("colourNames", Boolean.valueOf(colourNames));
		
		//Loop through team settings
		for (Map.Entry<String, Team> team : teams.entrySet()) {
			String n = team.getKey();
			Team t = team.getValue();
			
			//save team settings
			getConfig().set("teams." + n + ".score", Integer.valueOf(t.getScore()));
			getConfig().set("teams." + n + ".gamemode", Integer.valueOf(t.getGamemode().getValue()));
			//getConfig().set("teams." + n + ".locked", Boolean.valueOf(t.locked())); //TODO: add when players are added
			//getConfig().set("teams." + n + ".friendlyFire", Boolean.valueOf(t.isFriendlyFire()));
			getConfig().set("teams." + n + ".freeze", Boolean.valueOf(t.isFrozen()));
			getConfig().set("teams." + n + ".blockProtect", Boolean.valueOf(t.isBlockProtected()));
			
			//save team spawn location
			if (t.getSpawn() != null) {
				getConfig().set("teams." + n + ".spawn",
								t.getSpawn().getWorld().getName() + "," +
								t.getSpawn().getX() + "," +
								t.getSpawn().getY() + "," +
								t.getSpawn().getZ()
				);
			}
			
			//save team equipment
			//getConfig().set("teams." + n + ".helmet", t.getHelmet().getTypeId());
			//getConfig().set("teams." + n + ".chestplate", t.getChestplate().getItemMeta());
			//getConfig().set("teams." + n + ".leggings", t.getLeggings().getItemMeta());
			//getConfig().set("teams." + n + ".boots", t.getBoots().getItemMeta());
			/*
			int[] itemIDs = new int[t.getEquipment().length];
			for(int i=0; i <= t.getEquipment().length; i++) {
				itemIDs[i] = t.getEquipment()[i].hashCode();
				getLogger().info(t.getEquipment()[i].hashCode()+" @");
			}
			getConfig().set("teams." + n + ".equipment", itemIDs);*/
		}
		
		//Loop through event settings
		/*for (Map.Entry<String, Team> team : teams.entrySet()) {
			
		}*/
		
		saveConfig();
	}
		
	public void globalMsg(String msg) {
		for (PlayerState plySte : players.values())
				plySte.getPlayer().sendMessage(msg);
	}
}