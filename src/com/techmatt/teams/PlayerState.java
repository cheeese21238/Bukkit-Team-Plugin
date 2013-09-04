package com.techmatt.teams;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerState {
	
	private Player player;
	private Team team;
	private boolean ready = false;
	private boolean teamChat = false;
	private boolean spectator = false;
	
	private GameMode prevGamemode;
	private ItemStack[] oldInventContent;
	private ItemStack[] oldInventArmor;
	private Location oldLocation;

	public PlayerState(Player player, Team team) {
		this.player = player;
		this.team = team;
		this.prevGamemode = player.getGameMode();
		this.oldLocation = player.getLocation();
		this.oldInventContent = player.getInventory().getContents();
		this.oldInventArmor = player.getInventory().getArmorContents();
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

	public boolean isReady() {
		return ready;
	}

	public boolean isTeamChat() {
		return teamChat;
	}
	
	public boolean isSpectator() {
		return spectator;
	}
	
	public void setTeam(Team team) {
		this.team = team;
		player.performCommand("scoreboard teams join " + team.getKeyName());//TODO:Add better support
	}

	public void setReady(boolean value) {
		this.ready = value;
	}
	
	public void setChat(boolean value) {
		this.teamChat = value;
	}
	
	public void prevData(GameMode gamemode, ItemStack[] inventContent, ItemStack[] inventArmor, Location location) {
		this.prevGamemode = gamemode;
		this.oldInventContent = inventContent;
		this.oldInventArmor = inventArmor;
		this.oldLocation = location;
	}
	
	public void spectate(boolean value) {
		spectator = value;
		player.setAllowFlight(value);
		player.setSneaking(value);
		player.setCanPickupItems(value);
		if(value)
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 0, 100000));
	}
	
	public void setUpPlayer() {
		player.setGameMode(team.getGamemode());
		
		/*player.getInventory().clear();
		player.getInventory().setHelmet(team.getHelmet());
		player.getInventory().setChestplate(team.getChestplate());
		player.getInventory().setLeggings(team.getLeggings());
		player.getInventory().setBoots(team.getBoots());
		player.getInventory().setContents(team.getEquipment());*/
		
		if(team.isFrozen())
			player.setWalkSpeed(0);
		
		if (team.getSpawn() != null && team.getSpawn().getWorld() == player.getWorld())
			player.teleport(team.getSpawn());
	}
	
	public void delPlayer() {
		player.getInventory().setContents(oldInventContent);
		player.getInventory().setArmorContents(oldInventArmor);
		player.teleport(oldLocation);
		player.setGameMode(prevGamemode);		
		if(team.isFrozen())
			player.setWalkSpeed((float)0.2);//TODO:to be here not?
	}
	
/*	

	public void resetPlayer() {
		gearUp();
		tpToSpawn();
	}

	public void colourName() {//TODO:Full scorbaord take over
		player.setCustomName(team.getChatColour() + player.getName() + ChatColor.RESET);
		/*String dName = player.getDisplayName();
		if (dName.charAt(0) == ChatColor.COLOR_CHAR)
			dName = dName.substring(2, dName.length() - 2);
		player.setPlayerListName(team.getChatColour() + dName);
		player.setDisplayName(team.getChatColour() + dName + ChatColor.RESET);
	}

	public void uncolourName() {//TODO:Full scorbaord take over
		//player.setCustomName(player.getName());
		/*String dName = player.getDisplayName().substring(2, player.getDisplayName().length() - 2);
		player.setPlayerListName(dName);
		player.setDisplayName(dName);
	}

	public void gearUp() {
		team.equip(player);
	}

	public void pickUpHat() { //TODO: fix durability bug Needed?
		if (team.getHelmet() != null)
			player.getInventory().setHelmet(team.getHelmet());
	}

	public void tpToSpawn() {
		if (team.getSpawn() != null && team.getSpawn().getWorld() == player.getWorld()) {
			player.teleport(team.getSpawn());
			player.sendMessage(ChatColor.GRAY + "Poof!");
			team.getSpawn().getWorld().playEffect(team.getSpawn(), Effect.SMOKE, 4);
		} else {
			player.sendMessage(ChatColor.RED + "No team spawn has been set / or set in this world");
		}
	}*/
}