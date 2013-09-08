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
		
		load();	
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
	
	public void load() {
		saveDefaultConfig();
		this.lockGear = getConfig().getBoolean("lockGear");
		this.dropLoot = getConfig().getBoolean("dropLoot");
		this.rejoin = getConfig().getBoolean("rejoin");
		this.colourTeamLeather = getConfig().getBoolean("colourTeamLeather");
		
		for (String key : getConfig().getConfigurationSection("teams").getKeys(false)) {
			TeamManager team = this.newTeam(key);
			this.teams.put(key, team);
			
			if (getConfig().contains("teams." + key + ".spawn")) {
				String[] spawn = getConfig().getString("teams." + key + ".spawn").split(",");
				team.setSpawn(new Location(
						getServer().getWorld(spawn[0]),
						Double.parseDouble(spawn[1]),
						Double.parseDouble(spawn[2]),
						Double.parseDouble(spawn[3])
				));
			}
			
			team.setScore(							getConfig().getInt("teams." + key + ".score"));
			team.setGamemode(GameMode.getByValue(	getConfig().getInt("teams." + key + ".gamemode")));
			//team.setLocked(						getConfig().getInt("teams." + key + ".locked"));
			team.setFriendlyInvisibles(				getConfig().getBoolean("teams." + key + ".friendlyInvisibles"));
			team.setFriendlyFire(					getConfig().getBoolean("teams." + key + ".friendlyFire"));
			team.setFreeze(							getConfig().getBoolean("teams." + key + ".freeze"));
			team.setBlockProtect(					getConfig().getBoolean("teams." + key + ".blockProtect"));
			team.setSpectateAfterDeath(				getConfig().getBoolean("teams." + key + ".spctAftrDeath"));
			
			List<?> invent = getConfig().getList("teams." + key + ".equipment.invent");
			ItemStack[] newInvent = new ItemStack[invent.size()];
			for (int i = 0; i < invent.size(); i++) {
				newInvent[i] = this.buildIteamStackFromString(invent.get(i).toString());
			}
			
			team.setEquipment(
					this.buildIteamStackFromString(getConfig().getString("teams." + key + ".equipment.helmet")),
					this.buildIteamStackFromString(getConfig().getString("teams." + key + ".equipment.chestplate")),
					this.buildIteamStackFromString(getConfig().getString("teams." + key + ".equipment.leggings")),
					this.buildIteamStackFromString(getConfig().getString("teams." + key + ".equipment.boots")),
					newInvent
			);
		}
	}
	
	public void save() {
		//save main settings
		getConfig().set("lockGear", this.lockGear);
		getConfig().set("dropLoot", this.dropLoot);
		getConfig().set("rejoin", this.rejoin);
		getConfig().set("colourTeamLeather", this.colourTeamLeather);
		
		//Loop through team settings
		for (Map.Entry<String, TeamManager> teamList : teams.entrySet()) {
			String key = teamList.getKey();
			TeamManager team = teamList.getValue();
			
			//save team settings
			getConfig().set("teams." + key + ".score", 				team.getScore());
			getConfig().set("teams." + key + ".gamemode", 			team.getGamemode().getValue());
			//getConfig().set("teams." + n + ".locked", 			t.locked())); //TODO: add when players are added
			getConfig().set("teams." + key + ".friendlyInvisibles", team.isFriendlyInvisibles());
			getConfig().set("teams." + key + ".friendlyFire", 		team.isFriendlyFire());
			getConfig().set("teams." + key + ".freeze", 			team.isFrozen());
			getConfig().set("teams." + key + ".blockProtect", 		team.isBlockProtected());
			getConfig().set("teams." + key + ".spctAftrDeath", 		team.isSpectateAfterDeath());
			
			//save team spawn location
			if (team.getSpawn() != null) {
				getConfig().set("teams." + key + ".spawn",
								team.getSpawn().getWorld().getName() + "," +
								team.getSpawn().getX() + "," +
								team.getSpawn().getY() + "," +
								team.getSpawn().getZ()
				);
			}
			
			//save team equipment
			if(team.getHelmet() != null)
				getConfig().set("teams." + key + ".equipment.helmet", this.convterItemStackToString(team.getHelmet()));	
			if(team.getChestplate() != null)
				getConfig().set("teams." + key + ".equipment.chestplate", this.convterItemStackToString(team.getChestplate()));
			if(team.getLeggings() != null)
				getConfig().set("teams." + key + ".equipment.leggings", this.convterItemStackToString(team.getLeggings()));
			if(team.getBoots() != null)
				getConfig().set("teams." + key + ".equipment.boots", this.convterItemStackToString(team.getBoots()));
			
			if(team.getEquipment() != null) {
				List<String> itemIDs = new ArrayList<String>();
				for(int i = 0; i < team.getEquipment().length; i++) {
					if(team.getEquipment()[i] != null)		
						itemIDs.add(this.convterItemStackToString(team.getEquipment()[i]));
				}
				getConfig().set("teams." + key + ".equipment.invent", itemIDs);
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