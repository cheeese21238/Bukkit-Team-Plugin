package com.techmatt.teams;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerState {
	
	private Player player;
	private TeamManager team;
	private long lastOnline = System.currentTimeMillis();
	private boolean ready = false;
	private boolean teamChat = false;
	private boolean spectator = false;
	
	private GameMode prevGamemode;
	private ItemStack[] oldInventContent;
	private ItemStack[] oldInventArmor;
	private Location oldLocation;

	public PlayerState(Player player, TeamManager team) {
		this.player = player;
		this.team = team;
		this.prevGamemode = player.getGameMode();
		this.oldLocation = player.getLocation();
		this.oldInventContent = player.getInventory().getContents();
		this.oldInventArmor = player.getInventory().getArmorContents();
	}

	public Player getPlayer() {
		return this.player;
	}

	public TeamManager getTeam() {
		return this.team;
	}
	
	public Long getLastOnline() {
		return lastOnline;
	}

	public boolean isReady() {
		return this.ready;
	}

	public boolean isTeamChat() {
		return this.teamChat;
	}
	
	public boolean isSpectator() {
		return this.spectator;
	}
	
	public void setTeam(TeamManager team) {
		this.team = team;
	}
	
	public void setReady(boolean value) {
		this.ready = value;
	}
	
	public void setChat(boolean value) {
		this.teamChat = value;
	}
	
	public void updateLastOnline() {
		this.lastOnline = System.currentTimeMillis();
	}
	
	public void prevData(GameMode gamemode, ItemStack[] inventContent, ItemStack[] inventArmor, Location location) {
		this.prevGamemode = gamemode;
		this.oldInventContent = inventContent;
		this.oldInventArmor = inventArmor;
		this.oldLocation = location;
	}
	
	public void setSpectateMode(boolean value) {
		this.spectator = value;
		this.player.setAllowFlight(value);
		this.player.setCanPickupItems(value);
		if(value)
			this.player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 0, 100000));
		else
			this.player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}
	
	public void teleport() {
		if(!this.spectator)
			this.getPlayer().teleport(this.getTeam().getSpawn());
	}
	
	public void teleport(Location i) {
		if(!this.spectator)
			this.getPlayer().teleport(i);
	}
	
	public void setUpPlayer() {
		this.player.setGameMode(this.team.getGamemode());
		
		this.player.getInventory().clear();
		this.player.getInventory().setHelmet(team.getHelmet());
		this.player.getInventory().setChestplate(team.getChestplate());
		this.player.getInventory().setLeggings(team.getLeggings());
		this.player.getInventory().setBoots(team.getBoots());
		this.player.getInventory().setContents(team.getEquipment());
		
		if(this.team.isFrozen())
			this.player.setWalkSpeed(0);
		
		if (this.team.getSpawn() != null && this.team.getSpawn().getWorld() == this.player.getWorld())
			this.player.teleport(this.team.getSpawn());
	}
	
	public void remove(Boolean msg) {
		this.team.removePlayer(this, msg);
		this.player.getInventory().setContents(oldInventContent);
		this.player.getInventory().setArmorContents(oldInventArmor);
		this.player.teleport(oldLocation);
		this.player.setGameMode(prevGamemode);		
		if(this.team.isFrozen())
			this.player.setWalkSpeed((float)0.2);
	}
}