package com.techmatt.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Team {
	private Teams tm;

	private String name;
	private String keyName;
	private Color colour;
	private ChatColor chatColour;
	private int score;
	private Location spawn;
	private GameMode gamemode;
	//private int maxSize = 0; //TODO:ADD in sometime
	
	private List<PlayerState> players = new ArrayList<PlayerState>();
	
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack[] equipment = new ItemStack[0];
	
	private boolean locked = false;
	//private boolean friendlyFire = true;
	private boolean freeze = false;
	private boolean breakBlock = false;
	private boolean spctAftrDeath = false;

	public Team(Teams teams, String name, Color colour, ChatColor chatColour) {
		this.tm = teams;
		this.name = (chatColour + name + ChatColor.RESET);
		this.keyName = name.toLowerCase();
		this.colour = colour;
		this.chatColour = chatColour;
		this.gamemode = GameMode.SURVIVAL;
		
		//ScoreBoard 1.5 Stuff TODO:Add better support
		tm.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard teams add " + keyName + " " + name);
		tm.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard teams option " + keyName + " color " + chatColour.toString());//TODO:Colour not right?
		tm.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard teams option " + keyName + " seeFriendlyInvisibles true");
		tm.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard teams option " + keyName + " friendlyfire true");
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
	
	public GameMode getGamemode() {
		return gamemode;
	}
	
	public boolean isBlockProtected() {
		return this.breakBlock;
	}
	
	public boolean isFrozen() {
		return this.freeze;
	}
	
	/*public boolean isFriendlyFire() {
		return this.friendlyFire;
	}*/

	public boolean isLocked() {
		return this.locked;
	}
	
	public boolean isSpectateAfterDeath() {
		return this.spctAftrDeath;
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}
	
	public void setScore(int score) {
		this.score = score; //todo: add logic
		tm.globalMsg(name + " now has " + score + " points");
	}
	
	public void setGamemode(GameMode gamemode) {
		this.gamemode = gamemode;
		for (PlayerState plySte : players)
			plySte.getPlayer().setGameMode(gamemode);
	}

	public void setBlockProtect(boolean value) {
		this.breakBlock = value;
	}
	
	/*public void setFriendlyFire(boolean value) {
		this.friendlyFire = value;
	}*/
	
	public void setFreeze(boolean value) {//TODO: fix jump bug vol = 0 ?
		this.freeze = value;
		if(value){
			for (PlayerState plySte : players) {
				plySte.getPlayer().setWalkSpeed(0);
				plySte.getPlayer().sendMessage("You have been frozen");
			}
		} else {
			for (PlayerState plySte : players)
				plySte.getPlayer().setWalkSpeed((float)0.2);
		}
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public void setSpectateAfterDeath(boolean value) {
		this.spctAftrDeath = value;
	}
	
	public void setColouredEquipment(ItemStack[] equipment) { 
		helmet = new ItemStack(Material.LEATHER_HELMET, 1);
	    LeatherArmorMeta helmetMet = (LeatherArmorMeta)helmet.getItemMeta();
	    helmetMet.setColor(colour);
	    helmet.setItemMeta(helmetMet);
	    
		chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
	    LeatherArmorMeta chestplateMet = (LeatherArmorMeta)chestplate.getItemMeta();
	    chestplateMet.setColor(colour);
	    chestplate.setItemMeta(chestplateMet);
	    
		leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
	    LeatherArmorMeta leggingsMet = (LeatherArmorMeta)leggings.getItemMeta();
	    leggingsMet.setColor(colour);
	    this.leggings.setItemMeta(leggingsMet);
	    
		boots = new ItemStack(Material.LEATHER_BOOTS, 1);
	    LeatherArmorMeta bootsMet = (LeatherArmorMeta)boots.getItemMeta();
	    bootsMet.setColor(colour);
	    boots.setItemMeta(bootsMet);	

		if (equipment == null)
			this.equipment = new ItemStack[0];
		else
			this.equipment = equipment;
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
	
	public void addPlayer(Player player) {
		if (tm.players.containsKey(player)) {
			if(tm.players.get(player).getTeam() == this)
				player.sendMessage("You are already in this team :O");
			else
				swapPlayer(tm.players.get(player), true);
			return;
		}
		teamMsg(player.getDisplayName() + " has now joined your team");
		
		PlayerState plySte = new PlayerState(player, this);
		tm.players.put(player, plySte);
		this.players.add(plySte);
		plySte.setUpPlayer();
		
		player.sendMessage("You are now on the " + this.name + " team");
	}

	public void swapPlayer(PlayerState plySte, boolean msg) {
		Team oldTeam = plySte.getTeam();
		oldTeam.getPlayers().remove(oldTeam.getPlayers().indexOf(plySte));
		
		if (msg)
			teamMsg(plySte.getPlayer().getDisplayName() + " has now joined your team");
		
		this.players.add(plySte);
		plySte.setTeam(this);
		plySte.setUpPlayer();

		if (msg)
			oldTeam.teamMsg(plySte.getPlayer().getDisplayName() + " has swaped from your team to " + this.name + " team! :O");
		
		plySte.getPlayer().sendMessage("You have now on " + this.name + " team, say hello to your new team mates!");
	}
	
	public void removePlayer(Player player, boolean msg) {
		this.players.remove(tm.players.get(player));
		tm.players.get(player).delPlayer();
		tm.players.remove(player);
		if (msg){
			player.sendMessage("You have now left " + name + " team");
			teamMsg(player.getDisplayName() + " has now left your team");
		}
	}
	
	public void tpTeamTo(Location i) {//TODO:move else where add to player
		if (spawn == null) return;
		for (PlayerState plySte : players) {
			if(!plySte.isSpectator())
				plySte.getPlayer().teleport(i);
		}
		spawn.getWorld().playEffect(spawn, Effect.SMOKE, 4);
	}
	
	public void restTeam() {
		score = 0;
		for (PlayerState plySte : players){
			plySte.spectate(false);
			plySte.setUpPlayer();
		}
		tpTeamTo(spawn);
	}
	
	public void teamMsg(String msg) {
		for (PlayerState plySte : this.getPlayers())
			plySte.getPlayer().sendMessage(msg);
	}
	
	public void delTeam() {
		while (players.size() > 0) {
			players.get(0).getPlayer().sendMessage(ChatColor.RED + "Your Team (" + name + ChatColor.RED + ") has been deleted, you are now are all alone :'(");
			removePlayer(players.get(0).getPlayer(), false);
		}
		tm.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard teams remove " + keyName);//TODO:Add better support
	}
}