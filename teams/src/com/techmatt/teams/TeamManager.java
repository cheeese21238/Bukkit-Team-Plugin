package com.techmatt.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Scoreboard;

public class TeamManager {
	private Teams tm;
	
	private Team team;
	//Settings Stored in Team
	// - Friendly Fire
	// - Friendly Invisibles
	
	private String name;
	private String keyName;
	private int score;
	
	private Color colour;
	private ChatColor chatColour;
	
	private Location spawn;
	private GameMode gamemode;
	//private boolean spectateMode = false; //TODO: add;
	
	//private int maxSize = 0; //TODO:ADD in sometime
	private boolean locked = false;
	private boolean freeze = false;
	private boolean breakBlock = false;
	private boolean spctAftrDeath = false;
	
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack[] equipment;
	
	private List<PlayerState> players = new ArrayList<PlayerState>();

	public TeamManager(Teams teams, Scoreboard board, String name, Color colour, ChatColor chatColour) {
		this.tm = teams;
		this.name = chatColour + name + ChatColor.RESET;
		this.keyName = name.toLowerCase();
		this.colour = colour;
		this.chatColour = chatColour;
		
		this.team = board.registerNewTeam(this.keyName);
		this.team.setDisplayName(name);
		this.team.setPrefix(chatColour.toString());
		this.team.setSuffix(ChatColor.RESET.toString());
		
		//Defaults
		this.gamemode = tm.getServer().getDefaultGameMode();
		this.setFriendlyInvisibles(true);
		this.setFriendlyFire(true);
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
		return this.gamemode;
	}
	
	public boolean isBlockProtected() {
		return this.breakBlock;
	}
	
	public boolean isFrozen() {
		return this.freeze;
	}

	public boolean isLocked() {
		return this.locked;
	}
	
	public boolean isSpectateAfterDeath() {
		return this.spctAftrDeath;
	}
	
	public boolean isFriendlyFire() {
		return this.team.allowFriendlyFire();
	}
	
	public boolean isFriendlyInvisibles() {
		return this.team.canSeeFriendlyInvisibles();
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}
	
	public void setScore(int score) {
		this.score = score; //TODO: add logic
		tm.globalMsg(this.name + " now has " + score + " points");
	}
	
	public void setGamemode(GameMode gamemode) {
		this.gamemode = gamemode;
		for (PlayerState plySte : this.players)
			plySte.getPlayer().setGameMode(gamemode);
	}

	public void setBlockProtect(boolean value) {
		this.breakBlock = value;
	}
	
	public void setFreeze(boolean value) {//TODO: fix jump bug vol = 0 ?
		this.freeze = value;
		if(value){
			for (PlayerState plySte : this.players) {
				plySte.getPlayer().setWalkSpeed(0);
				plySte.getPlayer().sendMessage("You have been frozen");
			}
		} else {
			for (PlayerState plySte : this.players)
				plySte.getPlayer().setWalkSpeed((float)0.2);
		}
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public void setSpectateAfterDeath(boolean value) {
		this.spctAftrDeath = value;
	}
	
	public void setFriendlyFire(boolean value) {
		this.team.setAllowFriendlyFire(value);
	}
	
	public void setFriendlyInvisibles(boolean value) {
		this.team.setCanSeeFriendlyInvisibles(value);
	}
	
	private void setColouredLeather() {
		if(tm.colourTeamLeather) {
			if(this.helmet != null && this.helmet.getType() == Material.LEATHER_HELMET) {
				LeatherArmorMeta helmetMet = (LeatherArmorMeta) helmet.getItemMeta();
				helmetMet.setColor(colour);
			    this.helmet.setItemMeta(helmetMet);
			}
			
			if(this.helmet != null && this.chestplate.getType() == Material.LEATHER_CHESTPLATE) {
				LeatherArmorMeta chestplateMet = (LeatherArmorMeta) chestplate.getItemMeta();
				chestplateMet.setColor(colour);
			    this.chestplate.setItemMeta(chestplateMet);
			}
			
			if(this.helmet != null && this.leggings.getType() == Material.LEATHER_LEGGINGS) {
				LeatherArmorMeta leggingsMet = (LeatherArmorMeta) leggings.getItemMeta();
				leggingsMet.setColor(colour);
			    this.leggings.setItemMeta(leggingsMet);
			}
			
			if(this.helmet != null && this.boots.getType() == Material.LEATHER_BOOTS) {
				LeatherArmorMeta bootsMet = (LeatherArmorMeta) boots.getItemMeta();
				bootsMet.setColor(colour);
			    this.boots.setItemMeta(bootsMet);
			}
		}
	}
	
	public void setEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack[] equipment) {			
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		
		this.setColouredLeather();
		
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
		this.teamMsg(player.getDisplayName() + " has now joined your team");
		
		this.team.addPlayer(player);
		PlayerState plySte = new PlayerState(player, this);
		tm.players.put(player, plySte);
		this.players.add(plySte);
		plySte.setUpPlayer();
		
		player.sendMessage("You are now on the " + this.name + " team");
	}

	public void swapPlayer(PlayerState plySte, boolean msg) {
		TeamManager oldTeam = plySte.getTeam();
		oldTeam.getPlayers().remove(oldTeam.getPlayers().indexOf(plySte));
		
		if (msg)
			this.teamMsg(plySte.getPlayer().getDisplayName() + " has now joined your team");
		
		this.players.add(plySte);
		this.team.addPlayer(plySte.getPlayer());
		plySte.setTeam(this);
		plySte.setUpPlayer();

		if (msg)
			oldTeam.teamMsg(plySte.getPlayer().getDisplayName() + " has swaped from your team to " + this.name + " team! :O");
		
		plySte.getPlayer().sendMessage("You have now on " + this.name + " team, say hello to your new team mates!");
	}
	
	public void removePlayer(PlayerState plySte) {
		this.players.remove(plySte);
		tm.players.remove(plySte);
		this.team.removePlayer(plySte.getPlayer());
	}
	
	public void tpTeam() {
		for (PlayerState plySte : this.players) {
			plySte.teleport(this.spawn);
		}
		this.spawn.getWorld().playEffect(this.spawn, Effect.SMOKE, 4);
	}
	
	public void tpTeam(Location i) {
		for (PlayerState plySte : this.players) {
			plySte.teleport(i);
		}
		i.getWorld().playEffect(i, Effect.SMOKE, 4);
	}
	
	public void restTeam() { // TODO: review function
		this.score = 0;
		for (PlayerState plySte : this.players){
			plySte.setSpectateMode(false);
			plySte.setUpPlayer();
		}
		this.tpTeam();
	}
	
	public void teamMsg(String msg) {
		for (PlayerState plySte : this.getPlayers())
			plySte.getPlayer().sendMessage(msg);
	}
	
	public void delTeam() {
		while (this.players.size() > 0) {
			this.players.get(0).getPlayer().sendMessage(ChatColor.RED + "Your Team (" + this.name + ChatColor.RED + ") has been deleted, you are now are all alone :'(");
			this.players.get(0).remove(false);
		}
		this.team.unregister();
	}
}