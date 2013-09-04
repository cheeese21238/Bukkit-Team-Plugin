package com.techmatt.teams;

/*import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;*/

public class Event {
	/*private Teams tm;
	
	private String name;
	private String keyName;
	private Color colour;
	private ChatColor chatColour;
	private List<PlayerState> players = new ArrayList<PlayerState>();
	private int score;
	
	private Location spawn;
	private int gamemode;
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack[] equipment = new ItemStack[0];
	
	private boolean locked = false;
	private boolean friendlyFire = true;
	private boolean freeze = false;
	private boolean breakBlock = false;

	public Event(Teams teams, String name, Color colour, ChatColor chatColour) {
		this.tm = teams;
		this.name = (chatColour + name + ChatColor.RESET);
		this.keyName = name.toLowerCase();
		this.colour = colour;
		this.chatColour = chatColour;
	}*/
/*
	public void delTeam() {
		while (players.size() > 0) {
			players.get(0).getPlayer().sendMessage(ChatColor.RED + "Your Team (" + name + ChatColor.RED + ") has been deleted, you are now are all alone :'(");
			removePlayer(players.get(0).getPlayer(), false);
		}
	}

	public String getName() {
		return this.name;
	}
	
	public String getKeyName() {
		return this.keyName;
	}

	public List<PlayerState> getPlayers() {
		return this.players;
	}
	
	public Color getColour() {
		return this.colour;
	}
	
	public ChatColor getChatColour() {
		return this.chatColour;
	}

	public ItemStack getHelmet() {
		return this.helmet;
	}
	
	public ItemStack getChestplate() {
		return this.chestplate;
	}
	
	public ItemStack getLeggings() {
		return this.leggings;
	}
	
	public ItemStack getBoots() {
		return this.boots;
	}

	public ItemStack[] getEquipment() {
		return this.equipment;
	}

	public int getScore() {
		return this.score;
	}
	
	public Location getSpawn() {
		return this.spawn;
	}
	
	public int getGamemode() {
		return gamemode;
	}
	
	public boolean isBlockProtected() {
		return this.breakBlock;
	}
	
	public boolean isFrozen() {
		return this.freeze;
	}
	
	public boolean isFriendlyFire() {
		return this.friendlyFire;
	}

	public boolean isLocked() {
		return this.locked;
	}
	
	public void addPlayer(Player player) { //TODO: add effects e.g. freeze
		if (tm.players.containsKey(player)) {
			swapPlayer(tm.players.get(player), true);
			return;
		}
		player.sendMessage("You are now on the " + this.name + " team");
		teamMsg(player.getDisplayName() + " has now joined your team");

		PlayerState plySte = new PlayerState(player, this);
		tm.players.put(player, plySte);
		this.players.add(plySte);
	}
	
	public void setScore(int score) {
		this.score = score; //todo: add logic
		tm.globalMsg(name + " now has " + score + " points");
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}
	
	public void setGamemode(int gamemode) {
		this.gamemode = gamemode;
	}

	public void setBlockProtect(boolean value) {
		this.breakBlock = value;
	}
	
	public void setFriendlyFire(boolean value) {
		this.friendlyFire = value;
	}
	
	public void setFreeze(boolean value) {
		this.freeze = value;
		if(value){
			for (PlayerState plySte : players) {
				plySte.getPlayer().setWalkSpeed(0);
				plySte.getPlayer().sendMessage("You have been frozen");
			}
		} else {
			for (PlayerState plySte : players)
				plySte.getPlayer().setWalkSpeed(1);
		}
	}
	
	public void setColouredEquipment(ItemStack[] equipment) { 

	}
	
	public void setEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack[] equipment) {
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		if (equipment == null)
			this.equipment = new ItemStack[0];
		else
			this.equipment = equipment;
	}

	public void rotateTeams() {//TODO:
	}

	public void removeTeam(Player player, boolean msg) {
		this.players.remove(tm.players.get(player));
		tm.players.get(player).delPlayer();
		tm.players.remove(player);
		if (msg){
			player.sendMessage("You have now left " + name + " team");
			teamMsg(player.getDisplayName() + " has now left your team");
		}
	}

	public void restTeams() {
		score = 0;
		tpTeamTo(spawn);
		for (PlayerState plySte : players)
			equip(plySte.getPlayer());
	}

	public void equip(Player p) {
		p.getInventory().clear();
		p.getInventory().setHelmet(this.helmet);
		p.getInventory().setChestplate(this.chestplate);
		p.getInventory().setLeggings(this.leggings);
		p.getInventory().setBoots(this.boots);
		p.getInventory().setContents(this.equipment);
	}
	
	public void eventMsg(String msg) {
		for (PlayerState plySte : this.getPlayers())
			plySte.getPlayer().sendMessage(msg);
	}*/
}