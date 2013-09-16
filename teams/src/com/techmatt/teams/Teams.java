package com.techmatt.teams;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Teams extends JavaPlugin {
	
	private EventListener eventlistener;
	private CommandExe commandExecutor;
	public String theTeamNames = "Blue, Red, Yellow, Green, Purple, Orange";
	
	private ScoreboardManager sr;
	public Scoreboard board;
	
	public boolean dropLoot = false;
	public boolean rejoin = true;
	public boolean lockGear = false;
	public boolean colourTeamLeather = true;
	public boolean showScoreBoard = false; //TODO: working on...

	public Map<String, TeamManager> teams = new HashMap<String, TeamManager>();
	public Map<Player, PlayerState> players = new HashMap<Player, PlayerState>();
	public Map<String, PlayerState> offlinePlayers = new HashMap<String, PlayerState>();
	
	public void onEnable() {
		//this.board = this.sr.getNewScoreboard();	
		commandExecutor = new CommandExe(this);
		eventlistener = new EventListener(this);
		getServer().getPluginManager().registerEvents(eventlistener, this);
		getCommand("team").setExecutor(commandExecutor);
		getCommand("tc").setExecutor(commandExecutor);
		getCommand("spectate").setExecutor(commandExecutor);
		getCommand("ready").setExecutor(commandExecutor);
		
		sr = Bukkit.getScoreboardManager();
		board = sr.getNewScoreboard();
		
		//load("default");	
		getLogger().info("Team Work v" + getDescription().getVersion() + " plugin has successfully loaded");
	}
	
	public TeamManager newTeam(String color) {
		switch(color) {
			case "blue":
				return new TeamManager(this, this.board, "Blue", Color.BLUE, ChatColor.BLUE);
			case "red":
				return new TeamManager(this, this.board, "Red", Color.RED, ChatColor.RED);
			case "yellow":
				return new TeamManager(this, this.board, "Yellow", Color.YELLOW, ChatColor.YELLOW);
			case "green":
				return new TeamManager(this, this.board, "Green", Color.LIME, ChatColor.GREEN);
			case "purple":
				return new TeamManager(this, this.board, "Purple", Color.PURPLE, ChatColor.DARK_PURPLE);
			case "orange":
				return new TeamManager(this, this.board, "Orange", Color.ORANGE, ChatColor.GOLD);
		}
		return null;
	}
	
	public void onDisable() {
		getLogger().info("Team Work v" + getDescription().getVersion() + " has been disabled. :(");
	}
	
	public void load(String event) {
		//saveDefaultConfig();
		event += ".";
		
		this.lockGear = getConfig().getBoolean(event + "lockGear");
		this.dropLoot = getConfig().getBoolean(event + "dropLoot");
		this.rejoin = getConfig().getBoolean(event + "rejoin");
		this.colourTeamLeather = getConfig().getBoolean(event + "colourTeamLeather");

		if(getConfig().contains(event + "teams")) {
			for (String key : getConfig().getConfigurationSection(event + "teams").getKeys(false)) {
				TeamManager team; 
				if(this.teams.containsKey(key)) {
					team = this.teams.get(key);
				} else {
					team = this.newTeam(key);
					this.teams.put(key, team);
				}
				String prefix = event + "teams." + key + ".";
				
				if (getConfig().contains(prefix + "spawn")) {
					String[] spawn = getConfig().getString(prefix + "spawn").split(",");
					team.setSpawn(new Location(
							getServer().getWorld(spawn[0]),
							Double.parseDouble(spawn[1]),
							Double.parseDouble(spawn[2]),
							Double.parseDouble(spawn[3])
					));
				}
				
				team.setScore(							getConfig().getInt(prefix + "score"));
				team.setGamemode(GameMode.getByValue(	getConfig().getInt(prefix + "gamemode")));
				//team.setLocked(						getConfig().getInt(prefix + "locked"));
				team.setFriendlyInvisibles(				getConfig().getBoolean(prefix + "friendlyInvisibles"));
				team.setFriendlyFire(					getConfig().getBoolean(prefix + "friendlyFire"));
				team.setFreeze(							getConfig().getBoolean(prefix + "freeze"));
				team.setBlockProtect(					getConfig().getBoolean(prefix + "blockProtect"));
				team.setSpectateAfterDeath(				getConfig().getBoolean(prefix + "spctAftrDeath"));
				
				if(getConfig().contains(prefix + "equipment")) {
					List<?> invent = getConfig().getList(prefix + "equipment.invent");
					ItemStack[] newInvent = new ItemStack[invent.size()];
					for (int i = 0; i < invent.size(); i++) {
						newInvent[i] = this.buildIteamStackFromString(invent.get(i).toString());
					}
					
					team.setEquipment(
							this.buildIteamStackFromString(getConfig().getString(prefix + "equipment.helmet")),
							this.buildIteamStackFromString(getConfig().getString(prefix + "equipment.chestplate")),
							this.buildIteamStackFromString(getConfig().getString(prefix + "equipment.leggings")),
							this.buildIteamStackFromString(getConfig().getString(prefix + "equipment.boots")),
							newInvent
					);
				}
			}
		}
	}
	
	public void save(String event) {
		//save main settings
		event += ".";
		
		getConfig().set(event + "lockGear", this.lockGear);
		getConfig().set(event + "dropLoot", this.dropLoot);
		getConfig().set(event + "rejoin", this.rejoin);
		getConfig().set(event + "colourTeamLeather", this.colourTeamLeather);
		
		//Loop through team settings
		for (Map.Entry<String, TeamManager> teamList : teams.entrySet()) {
			String key = teamList.getKey();
			TeamManager team = teamList.getValue();
			String prefix = event + "teams." + key + ".";
			
			//save team settings
			getConfig().set(prefix + "score", 				team.getScore());
			getConfig().set(prefix + "gamemode", 			team.getGamemode().getValue());
			//getConfig().set("teams." + n + ".locked", 	team.locked())); //TODO: add when players are added
			getConfig().set(prefix + "friendlyInvisibles", 	team.isFriendlyInvisibles());
			getConfig().set(prefix + "friendlyFire", 		team.isFriendlyFire());
			getConfig().set(prefix + "freeze", 				team.isFrozen());
			getConfig().set(prefix + "blockProtect", 		team.isBlockProtected());
			getConfig().set(prefix + "spctAftrDeath", 		team.isSpectateAfterDeath());
			
			//save team spawn location
			if (team.getSpawn() != null) {
				getConfig().set(prefix + "spawn",
								team.getSpawn().getWorld().getName() + "," +
								team.getSpawn().getX() + "," +
								team.getSpawn().getY() + "," +
								team.getSpawn().getZ()
				);
			}
			
			//save team equipment
			if(team.getHelmet() != null)
				getConfig().set(prefix + "equipment.helmet", 	this.convterItemStackToString(team.getHelmet()));	
			if(team.getChestplate() != null)
				getConfig().set(prefix + "equipment.chestplate",this.convterItemStackToString(team.getChestplate()));
			if(team.getLeggings() != null)
				getConfig().set(prefix + "equipment.leggings", 	this.convterItemStackToString(team.getLeggings()));
			if(team.getBoots() != null)
				getConfig().set(prefix + "equipment.boots",		this.convterItemStackToString(team.getBoots()));
			
			if(team.getEquipment() != null) {
				List<String> itemIDs = new ArrayList<String>();
				for(int i = 0; i < team.getEquipment().length; i++) {
					if(team.getEquipment()[i] != null)		
						itemIDs.add(this.convterItemStackToString(team.getEquipment()[i]));
				}
				getConfig().set(prefix + "equipment.invent", itemIDs);
			}
		}
		
		saveConfig();
	}
	
	private ItemStack buildIteamStackFromString(String item) {
		if(item == null)
			return null;
		
		String[] itemParts = item.split(", ");
		Map<Enchantment, Integer> itemEnchantments = new HashMap<Enchantment, Integer>();
		for(int i = 3; i < itemParts.length; i++) {
			String[] e = itemParts[i].split("=");
			itemEnchantments.put(Enchantment.getByName(e[0]), Integer.parseInt(e[1]));
		}
		ItemStack itemStck = new ItemStack(Material.getMaterial(Integer.parseInt(itemParts[0])), Integer.parseInt(itemParts[1]), Short.parseShort(itemParts[2]));
		itemStck.addEnchantments(itemEnchantments);
		
		return itemStck;
	}
	
	private String convterItemStackToString(ItemStack item) {
		String itemString = item.getTypeId() + ", " + item.getAmount() + ", " + item.getDurability();
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		Iterator<Entry<Enchantment,Integer>> iter = enchantments.entrySet().iterator();
		while(iter.hasNext()){
		    Entry<Enchantment, Integer> entry = iter.next();
		    itemString +=  ", " + entry.getKey().getName() + "=" + entry.getValue();
		}		
		
		return itemString;
	}
		
	public void globalMsg(String msg) {
		for (PlayerState plySte : players.values())
				plySte.getPlayer().sendMessage(msg);
	}
}