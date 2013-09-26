package com.techmatt.teams;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
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
		this.getServer().getPluginManager().registerEvents(eventlistener, this);
		this.getCommand("team").setExecutor(commandExecutor);
		this.getCommand("tc").setExecutor(commandExecutor);
		this.getCommand("spectate").setExecutor(commandExecutor);
		this.getCommand("ready").setExecutor(commandExecutor);
		
		sr = Bukkit.getScoreboardManager();
		board = sr.getNewScoreboard();
		
		this.getLogger().info("Team Work v" + getDescription().getVersion() + " plugin has successfully loaded");
	}
	
	public void onDisable() {
		this.getLogger().info("Team Work v" + getDescription().getVersion() + " has been disabled. :(");
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

	public Boolean load(String event) {
		try {
			this.getConfig().load(new File(getDataFolder(), event + ".yml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.getLogger().log(Level.SEVERE, "Failed to Load " + event + " Config File");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			this.getLogger().log(Level.SEVERE,"Failed to Load " + event + " Config File");
			return false;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			this.getLogger().log(Level.SEVERE,"Failed to Load " + event + " Config File");
			return false;
		}
		
		this.lockGear = this.getConfig().getBoolean("lockGear");
		this.dropLoot = this.getConfig().getBoolean("dropLoot");
		this.rejoin = this.getConfig().getBoolean("rejoin");
		this.colourTeamLeather = this.getConfig().getBoolean("colourTeamLeather");

		if(this.getConfig().contains("teams")) {
			for (String key : this.getConfig().getConfigurationSection("teams").getKeys(false)) { //TODO: del teams
				TeamManager team; 
				if(this.teams.containsKey(key)) {
					team = this.teams.get(key);
				} else {
					team = this.newTeam(key);
					this.teams.put(key, team);
				}
				String prefix = "teams." + key + ".";
				
				if (this.getConfig().contains(prefix + "spawn")) {
					String[] spawn = this.getConfig().getString(prefix + "spawn").split(",");
					team.setSpawn(new Location(
							getServer().getWorld(spawn[0]),
							Double.parseDouble(spawn[1]),
							Double.parseDouble(spawn[2]),
							Double.parseDouble(spawn[3])
					));
				}
				
				team.setScore(							this.getConfig().getInt(prefix + "score"));
				team.setGamemode(GameMode.getByValue(	this.getConfig().getInt(prefix + "gamemode")));
				//team.setLocked(						this.getConfig().getInt(prefix + "locked"));
				team.setFriendlyInvisibles(				this.getConfig().getBoolean(prefix + "friendlyInvisibles"));
				team.setFriendlyFire(					this.getConfig().getBoolean(prefix + "friendlyFire"));
				team.setFreeze(							this.getConfig().getBoolean(prefix + "freeze"));
				team.setBlockProtect(					this.getConfig().getBoolean(prefix + "blockProtect"));
				team.setSpectateAfterDeath(				this.getConfig().getBoolean(prefix + "spctAftrDeath"));
				
				if(this.getConfig().contains(prefix + "equipment")) {
					List<?> invent = this.getConfig().getList(prefix + "equipment.invent");
					ItemStack[] newInvent = new ItemStack[invent.size()];
					for (int i = 0; i < invent.size(); i++) {
						newInvent[i] = this.buildIteamStackFromString(invent.get(i).toString());
					}
					
					team.setEquipment(
							this.buildIteamStackFromString(this.getConfig().getString(prefix + "equipment.helmet")),
							this.buildIteamStackFromString(this.getConfig().getString(prefix + "equipment.chestplate")),
							this.buildIteamStackFromString(this.getConfig().getString(prefix + "equipment.leggings")),
							this.buildIteamStackFromString(this.getConfig().getString(prefix + "equipment.boots")),
							newInvent
					);
				}
			}
		}
		
		return true;
	}
	
	public Boolean save(String event) {
		//save main settings		
		this.getConfig().set("lockGear", this.lockGear);
		this.getConfig().set("dropLoot", this.dropLoot);
		this.getConfig().set("rejoin", this.rejoin);
		this.getConfig().set("colourTeamLeather", this.colourTeamLeather);
		
		//Loop through team settings
		for (Map.Entry<String, TeamManager> teamList : teams.entrySet()) {
			String key = teamList.getKey();
			TeamManager team = teamList.getValue();
			String prefix = "teams." + key + ".";
			
			//save team settings
			this.getConfig().set(prefix + "score", 				team.getScore());
			this.getConfig().set(prefix + "gamemode", 			team.getGamemode().getValue());
			//this.getConfig().set("teams." + n + ".locked", 	team.locked())); //TODO: add when players are added
			this.getConfig().set(prefix + "friendlyInvisibles", 	team.isFriendlyInvisibles());
			this.getConfig().set(prefix + "friendlyFire", 		team.isFriendlyFire());
			this.getConfig().set(prefix + "freeze", 				team.isFrozen());
			this.getConfig().set(prefix + "blockProtect", 		team.isBlockProtected());
			this.getConfig().set(prefix + "spctAftrDeath", 		team.isSpectateAfterDeath());
			
			//save team spawn location
			if (team.getSpawn() != null) {
				this.getConfig().set(prefix + "spawn",
								team.getSpawn().getWorld().getName() + "," +
								team.getSpawn().getX() + "," +
								team.getSpawn().getY() + "," +
								team.getSpawn().getZ()
				);
			}
			
			//save team equipment
			if(team.getHelmet() != null)
				this.getConfig().set(prefix + "equipment.helmet", 	this.convterItemStackToString(team.getHelmet()));	
			if(team.getChestplate() != null)
				this.getConfig().set(prefix + "equipment.chestplate",this.convterItemStackToString(team.getChestplate()));
			if(team.getLeggings() != null)
				this.getConfig().set(prefix + "equipment.leggings", 	this.convterItemStackToString(team.getLeggings()));
			if(team.getBoots() != null)
				this.getConfig().set(prefix + "equipment.boots",		this.convterItemStackToString(team.getBoots()));
			
			if(team.getEquipment() != null) {
				List<String> itemIDs = new ArrayList<String>();
				for(int i = 0; i < team.getEquipment().length; i++) {
					if(team.getEquipment()[i] != null)		
						itemIDs.add(this.convterItemStackToString(team.getEquipment()[i]));
				}
				this.getConfig().set(prefix + "equipment.invent", itemIDs);
			}
		}
		
		try {
			this.getConfig().save(new File(getDataFolder(), event + ".yml"));
		} catch (IOException e) {
			e.printStackTrace();
			this.getLogger().log(Level.SEVERE, "Failed to Save " + event + " Config File");
			return false;
		}
		
		return true;
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