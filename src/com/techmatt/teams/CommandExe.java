package com.techmatt.teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandExe implements CommandExecutor {
	private Teams tm;
	private int countdown;
	private int timmer;
	private String permission = "com.techmatt.teams";

	public CommandExe(Teams teams) {
		tm = teams;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] parameters) {
			
		//-----Utils-------------------------------------------------------------------
		String newline = "\n--------------------------";
		String msgTeamLock = ChatColor.RED + "Teams have been locked, either ask for a invite or wait for a team unlock notification :(";
		String msgNotInTeam = ChatColor.RED + "You need to be on a team to use this command";
		String msgPermission = ChatColor.RED + "You don't have permission to use this command";
		
		//-----Spectate----------------------------------------------------------------------
		if (cmd.getName().equals("spectate") && sender instanceof Player) {
			if (tm.players.containsKey(sender)){
				PlayerState plySte = tm.players.get(sender);
				if(!plySte.isSpectator()){
					plySte.setSpectateMode(true);
					sender.sendMessage("Spectator Mode Enabled");
				} else{
					if(!plySte.getTeam().isSpectateAfterDeath()){
						plySte.setSpectateMode(false);
						sender.sendMessage("Spectator Mode Disabled");
					} else
						sender.sendMessage("Can't leave spector mode when you are out of the game, will have to wait till the game is finished");
				}
			} else
				sender.sendMessage(msgNotInTeam);
				
			return true;
		}
		
		//-----Team Chat----------------------------------------------------------------------
		if (cmd.getName().equals("tc") && sender instanceof Player) {
			if (tm.players.containsKey(sender)){
				PlayerState plySte = tm.players.get(sender);
				if(plySte.isTeamChat()) {
					plySte.setChat(false);
					sender.sendMessage(ChatColor.GRAY + "Team Chat Disabled");
				} else {
					plySte.setChat(true);
					sender.sendMessage(ChatColor.GRAY + "Team Chat Enabled");
				}
			} else
				sender.sendMessage(msgNotInTeam);
				
			return true;
		}
		
		//-----Ready Up----------------------------------------------------------------------
		if (cmd.getName().equals("ready") && sender instanceof Player) { //TODO: move to team
			if (tm.players.containsKey(sender)) {
				PlayerState plySte = tm.players.get(sender);
				if(plySte.isReady())
					plySte.setReady(false);
				else
					plySte.setReady(true);
				return true;
			} else
				sender.sendMessage(msgNotInTeam);
				
			return true;
		}
		
		//-----Ready Up----------------------------------------------------------------------
		if (cmd.getName().equals("join") && sender instanceof Player) {
			if (tm.players.containsKey(sender)) {
				PlayerState plySte = tm.players.get(sender);
				if(plySte.isReady())
					plySte.setReady(false);
				else
					plySte.setReady(true);
				return true;
			} else
				sender.sendMessage(msgNotInTeam);
				
			return true;
		}
		
		//-----Team Stuff----------------------------------------------------------------------
		if (cmd.getName().equals("team")) {

			if(parameters.length <= 0)
				return false;
			//-----Lower Case Fix-------------------------------------------------------------------
			String[] args = new String[parameters.length];
			for (int i = 0; i < args.length; i++)
				args[i] = parameters[i].toLowerCase();
			String action = args[0];
						
			/////////////////////////
			///User Based Commands///
			/////////////////////////
			
			//-----Join Team-------------------------------------------------------------------
			if (action.equals("join") && sender instanceof Player) {
				if (parameters.length == 1 && sender instanceof Player) {
					if (tm.teams.size() != 0) {
						int i = tm.players.size() + 1; //set number to be as the highest as possible so it does not skip a team
						TeamManager avlbleTeam = null;
						for (TeamManager team : tm.teams.values()) { //get team with least amount of players
							if (!team.isLocked() && team.getPlayers().size() < i) {
								i = team.getPlayers().size();
								avlbleTeam = team;
							}
						}
						if(avlbleTeam == null)
							sender.sendMessage(msgTeamLock);
						else
							avlbleTeam.addPlayer((Player)sender); //add player to that team
					} else
						sender.sendMessage(ChatColor.RED + "There have been no teams created for you to join! :O");
					
					return true;
				}
				
				//-----Choose to join Team-------------------------------------------------------------------
				if (tm.teams.containsKey(args[1]) && sender instanceof Player) {
					if (sender.hasPermission(permission + ".pickTeams")) {
						if (!tm.teams.get(args[1]).isLocked())
							tm.teams.get(args[1]).addPlayer((Player)sender);
						else
							sender.sendMessage(msgTeamLock);
					} else
						sender.sendMessage("You can't pick your teams today, sorry");

					return true;
				}

				//-----Force Player on Team-------------------------------------------------------------------
				if (args.length > 2 && 
						sender.hasPermission(permission + ".setPlayersTeam") &&
						tm.getServer().getPlayer(args[1]) != null &&
						tm.teams.containsKey(args[2])) {
					tm.teams.get(args[2]).addPlayer(tm.getServer().getPlayer(args[1])); //TODO: don't work
					return true;
				}	
				
				return true;
			}
			
			//-----Leave Team-------------------------------------------------------------------
			if (action.equals("leave") && (sender instanceof Player)) {
				if (tm.players.containsKey(sender))
					tm.players.get(sender).remove(true);
				else
					sender.sendMessage(ChatColor.RED + "You are currently not in a team");
				
				return true;
			}
			
			//-----TP to Team Spawn-------------------------------------------------------------------
			if (action.equals("spawn")) { //TODO: error //TODO: not spawning in right place
				if (sender.hasPermission(permission + ".spawn")){
					if(sender instanceof Player){
						if (tm.players.containsKey(sender)){					
							tm.players.get(sender).teleport();
						} else
							sender.sendMessage(ChatColor.RED + "You are currently not in a team");
					} else
						sender.sendMessage(ChatColor.RED + "Player commmand only");
				} else 
					sender.sendMessage(msgPermission);

				return true;
			}
			
			//-----List Team Info-------------------------------------------------------------------
			if (action.equals("list")) {
				if (tm.teams.size() == 0)
					sender.sendMessage(ChatColor.RED + "No teams found");
				else if(tm.players.containsKey(sender)) {
					String msg = "";
					TeamManager playerTeam = tm.players.get(sender).getTeam();
					for (TeamManager team : tm.teams.values()) {
						msg += "\n\n" + team.getName() + " - Score: " + team.getScore() + newline;
						if(playerTeam == team) {
							for (PlayerState plySte : team.getPlayers())
								msg += "\n" + plySte.getPlayer().getDisplayName() + playersHealth(plySte.getPlayer()) + (plySte.isReady() ? " - Ready!" : "") + (plySte.isSpectator() ? " - Spectator!" : "");
						} else {
							for (PlayerState plySte : team.getPlayers())
								msg += "\n" + plySte.getPlayer().getDisplayName() + (plySte.isReady() ? " - Ready!" : "") + (plySte.isSpectator() ? " - Spectator!" : "");
						}
					}
					sender.sendMessage(msg);
				} else {
					String msg = "";
					for (TeamManager team : tm.teams.values()) {
						msg += "\n\n" + team.getName() + " - Score: " + team.getScore() + newline;
						for (PlayerState plySte : team.getPlayers())
							msg += "\n" + plySte.getPlayer().getDisplayName() + playersHealth(plySte.getPlayer()) + (plySte.isReady() ? " - Ready!" : "") + (plySte.isSpectator() ? " - Spectator!" : "");
					}
					sender.sendMessage(msg);
				}
				
				return true;
			}
			
			//////////////////////////////
			///Moderator Based Commands///
			//////////////////////////////
			
			//TODO: rotate teams 
			
			//-----Scoring System-------------------------------------------------------------------
			if (action.equals("score")) { //TODO: Add scoreboard 1.5 //TODO: notice twice
				if (args.length > 3 && args[1].equals("set")) { //Setting Score e.g. /team score set blue 3
					if (sender.hasPermission(permission + ".manage")) {
						if (tm.teams.containsKey(args[2])) {
							if (args[3].matches("[+-]?(\\d){1,4}")) {
								TeamManager team = tm.teams.get(args[2]);
								team.setScore(Integer.parseInt(args[3]));
								tm.globalMsg(team.getName() + ChatColor.GRAY + " team now have " + team.getScore() + " points!");
							} else
								sender.sendMessage(ChatColor.RED + "Needs to be a whole number");
						} else
							sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
					} else
						sender.sendMessage(msgPermission);
				} 
				
				else if (args.length > 2) { //Adding to score /team score red 5
					if (sender.hasPermission(permission + ".manage")) {
						if (tm.teams.containsKey(args[1])) {
							if (args[2].matches("[+-]?(\\d){1,4}")) {
								TeamManager team = tm.teams.get(args[1]);
								team.setScore(team.getScore() + Integer.parseInt(args[2]));
								tm.globalMsg(team.getName() + ChatColor.GRAY + " team now have " + team.getScore() + " points!");
							} else
								sender.sendMessage(ChatColor.RED + "Needs to be a whole number");
						} else
							sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
					} else
						sender.sendMessage(msgPermission);
				}
				
				else { //View ScoreBoard /team score
					List<TeamManager> teams = new ArrayList<TeamManager>();
					teams.addAll(tm.teams.values());	
					Collections.sort(teams, new Comparator<TeamManager>() {
						public int compare(TeamManager t1, TeamManager t2) {
							return new Integer(t1.getScore()).compareTo(new Integer(t2.getScore())) * -1;
						}
					});
					
					for (TeamManager team : teams) {
						sender.sendMessage(team.getName() + " - Score: " + team.getScore());
					}
				}
				return true;
			}
			
			//-----Gather Team Players-------------------------------------------------------------------
			if (action.equals("gather") || action.equals("tpt")) { //TODO: rewrite for admins and nonadmins
				if (sender.hasPermission(permission + ".gather")) {
					if (sender instanceof Player) {
						Player player = (Player)sender;
						if (args.length > 1 && args[1] == "all")
							tm.teams.get(args[1]).tpTeam(player.getLocation());
						else if (args.length > 1 && tm.teams.containsKey(args[1]))
							tm.teams.get(args[1]).tpTeam(player.getLocation());
						else if (tm.players.containsKey(sender))
							tm.players.get(player).getTeam().tpTeam(player.getLocation());
						else
							sender.sendMessage(msgNotInTeam + " or specify a team");
					} else
						sender.sendMessage(ChatColor.RED + "Player commmand only");
				} else
					sender.sendMessage(msgPermission);

				return true;
			}
			
			//-----Set Team Spawn-------------------------------------------------------------------
			if (action.equals("setspawn")) {
				if (sender.hasPermission(permission + ".setspawn")) {
					if (sender instanceof Player) {
						if (args.length > 1) {
							if (tm.teams.containsKey(args[1])) {
								tm.teams.get(args[1]).setSpawn(((Player)sender).getLocation());
								sender.sendMessage(tm.teams.get(args[1]).getName() + ChatColor.GRAY + " spawn has been successfully set");
							} else
								sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
						} else
							sender.sendMessage(ChatColor.RED + "Please enter the team you want to set the spawn for");
					} else
						sender.sendMessage(ChatColor.RED + "Player commmand only");
				} else
					sender.sendMessage(msgPermission);

				return true;
			}
			
			//-----Kick Player from Team-------------------------------------------------------------------
			if (action.equals("kick")) { //TODO: need a msg for admin
				if (sender.hasPermission(permission + ".kick")) {
					if (args.length > 1) {
						Player player = tm.getServer().getPlayer(args[1]);
						if (tm.players.containsKey(player)) {
							tm.players.get(player).remove(true);
							player.sendMessage(ChatColor.RED + "You have been kicked from your team");
						} else
							sender.sendMessage(ChatColor.RED + args[1] + " does not exist or is not in a team");
					} else
						sender.sendMessage(ChatColor.RED + "Please enter players name you would like to kick");
				} else
					sender.sendMessage(msgPermission);

				return true;
			}
			
			if(sender.hasPermission(permission + ".manage") || sender.hasPermission(permission + ".control")) {
				
				//-----CountDown-------------------------------------------------------------------
				if (action.equals("countdown") || action.equals("cd")) {		
					tm.globalMsg(ChatColor.GRAY + "" + ChatColor.ITALIC + "Be ready to go in...");	
					if (args.length > 1 && args[1].matches("^[1-9][0-9]?$"))
						this.countdown(Integer.parseInt(args[1]));
					else 
						this.countdown(5);
					
					return true;
				}
										
				//-----Prepare Players for a start-------------------------------------------------------------------
				if (action.equals("start")) {
					for (TeamManager team : tm.teams.values()){
						team.restTeam();
						team.setFreeze(true);
					}
					
					tm.globalMsg(ChatColor.GRAY + "" + ChatColor.ITALIC + "Game starts in...");//TODO:improve
					this.countdown(5);
					
					for (PlayerState plySte : tm.players.values())
						plySte.setReady(false);
					
					for (TeamManager team : tm.teams.values()){
						team.setFreeze(false);
					}
					
					//TODO: add clear player maybe?
						
					return true;
				}
				
				//==========================//
				//-----Toggles YAY :D-------------------------------------------------------------------
				//==========================//
				
				if (action.equals("droploot")) {
					if (tm.dropLoot) {
						tm.dropLoot = false;
						sender.sendMessage(ChatColor.GRAY + "Drop Loot disabled");
					} else {
						tm.dropLoot = true;
						sender.sendMessage(ChatColor.GRAY + "Drop Loot enabled");
					}
					
					return true;
				}
	
				if (action.equals("rejoin")) {
					if (tm.rejoin) {
						tm.rejoin = false;
						sender.sendMessage(ChatColor.GRAY + "Allow player rejoin disabled");
					} else {
						tm.rejoin = true;
						sender.sendMessage(ChatColor.GRAY + "Allow player rejoin enabled");
					}
					
					return true;
				}
				
				if (action.equals("lockgear")) {
					if (tm.lockGear) {
						tm.lockGear = false;
						sender.sendMessage(ChatColor.GRAY + "Lock Gear disabled");
					} else {
						tm.lockGear = true;
						sender.sendMessage(ChatColor.GRAY + "Lock Gear enabled");
					}
					
					return true;
				}
				
				if (action.equals("colourTeamLeather")) {
					if (tm.colourTeamLeather) {
						tm.colourTeamLeather = false;
						sender.sendMessage(ChatColor.GRAY + "Colour Team Leather disabled");
					} else {
						tm.colourTeamLeather = true;
						sender.sendMessage(ChatColor.GRAY + "Colour Team Leather enabled");
					}
					
					return true;
				}
				
				if (action.equals("gamemode")) {
					if (args.length > 2 && tm.teams.containsKey(args[1])) {
						int gm = Integer.parseInt(args[2]);
						if(gm >= 0 && gm <= 2){
							tm.teams.get(args[1]).setGamemode(GameMode.getByValue(gm));
							sender.sendMessage(ChatColor.GRAY + "Gamemode " + gm + " has been set for " + tm.teams.get(args[1]).getName() + " team");
						}
					} else
						sender.sendMessage(ChatColor.RED + "That team does not exist");
					
					return true;
				}
				
				if (action.equals("locked")) {
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						if (tm.teams.get(args[1]).isLocked()) {
							tm.teams.get(args[1]).setLocked(false);
							sender.sendMessage(ChatColor.GRAY + "Team Lock disabled");
						} else {
							tm.teams.get(args[1]).setLocked(true);
							sender.sendMessage(ChatColor.GRAY + "Team Lock enabled");
						}
					} else
						sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
					
					return true;
				}
						
				if (action.equals("freeze")) {
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						if (tm.teams.get(args[1]).isFrozen()) {
							tm.teams.get(args[1]).setFreeze(false);
							sender.sendMessage(ChatColor.GRAY + "Freeze disabled");
						} else {
							tm.teams.get(args[1]).setFreeze(true);
							sender.sendMessage(ChatColor.GRAY + "Freeze enabled");
						}
					} else
						sender.sendMessage(ChatColor.RED + "That team does not exist");
					
					return true;
				}
				
				if (action.equals("breakblock")) {
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						if (tm.teams.get(args[1]).isBlockProtected()) {
							tm.teams.get(args[1]).setBlockProtect(false);
							sender.sendMessage(ChatColor.GRAY + "Breakblock disabled");
						} else {
							tm.teams.get(args[1]).setBlockProtect(true);
							sender.sendMessage(ChatColor.GRAY + "Breakblock enabled");
						}
					} else
						sender.sendMessage(ChatColor.RED + "That team does not exist");
					
					return true;
				}
				
				if (action.equals("spectateaftrdeath")) { //TODO: still visbale
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						if (tm.teams.get(args[1]).isSpectateAfterDeath()) {
							tm.teams.get(args[1]).setSpectateAfterDeath(false);
							sender.sendMessage(ChatColor.GRAY + "Spectate After Death disabled");
						} else {
							tm.teams.get(args[1]).setSpectateAfterDeath(true);
							sender.sendMessage(ChatColor.GRAY + "Spectate After Death enabled");
						}
					} else
						sender.sendMessage(ChatColor.RED + "That team does not exist");
					
					return true;
				}
				
				if (action.equals("friendlyfire")) {
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						if (tm.teams.get(args[1]).isFriendlyFire()) {
							tm.teams.get(args[1]).setFriendlyFire(false);
							sender.sendMessage(ChatColor.GRAY + "Friendly Fire disabled");
						} else {
							tm.teams.get(args[1]).setFriendlyFire(true);
							sender.sendMessage(ChatColor.GRAY + "Friendly Fire enabled");
						}
					} else
						sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
					
					return true;
				}
				
				if (action.equals("friendlyinvisibles")) {
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						if (tm.teams.get(args[1]).isFriendlyInvisibles()) {
							tm.teams.get(args[1]).setFriendlyInvisibles(false);
							sender.sendMessage(ChatColor.GRAY + "Friendly Invisibles disabled");
						} else {
							tm.teams.get(args[1]).setFriendlyInvisibles(true);
							sender.sendMessage(ChatColor.GRAY + "Friendly Invisibles enabled");
						}
					} else
						sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
					
					return true;
				}
				
				
				//-----Set Teams Equipment-------------------------------------------------------------------
				if (action.equals("setgear")) {
					if (sender instanceof Player) {
						if (args.length > 1 && tm.teams.containsKey(args[1])) {
							Player player = (Player)sender;
							tm.teams.get(args[1]).setEquipment(
								player.getInventory().getHelmet(), 
								player.getInventory().getChestplate(), 
								player.getInventory().getLeggings(), 
								player.getInventory().getBoots(), 
								player.getInventory().getContents());
							sender.sendMessage(tm.teams.get(args[1]).getName() + ChatColor.GRAY + " gear has been set");
						} else
							sender.sendMessage(ChatColor.RED + "Invaild team name");
					} else
						sender.sendMessage(ChatColor.RED + "Player commmand only");
	
					return true;
				}
	
				//-----Winning!-------------------------------------------------------------------
				if (action.equals("win") || (action.equals("end"))) {//TODO:some day
						if (args.length > 1) {
							if (tm.teams.containsKey(args[1]))
								winner(tm.teams.get(args[1]));
							else
								sender.sendMessage(ChatColor.RED + args[1] + " team does not exist");
						}
						/*else {//TODO: add logic - score / lives
							int i = 0; winingTeam = null; Object winingTeams = new ArrayList();
							for (Team team : tm.teams.values()) {
								if (team.getPlayers().size() > i) {
									i = team.getPlayers().size();
									winingTeam = team;
									(List)winingTeams).clear();
								} else if (team.getPlayers().size() == i) {
									(List)winingTeams).add(team.getName());
								}
							}
							if (List)winingTeams).size() == 0) {
								winer(winingTeam);
							} else {
								msg = null;
								for (String txt : (List)winingTeams)
									msg = msg + txt + ",";
								tm.teamMsg(msg + " have all drawed first!", null);
							}
						}*/
	
					return true;
				}
				
				//-----Even Teams-------------------------------------------------------------------
				if (action.equals("even")) {
					tm.globalMsg("Prepare for evening of teams...");
					tm.getServer().getScheduler().runTaskLater(tm, new Runnable() {
						public void run() {
							int targetSize = Math.round(tm.players.size() / tm.teams.size());
							List<PlayerState> swapedPlayers = new ArrayList<PlayerState>();
							
							for (TeamManager team : tm.teams.values()) {
								for(int i = targetSize; i < team.getPlayers().size(); i++) {
									swapedPlayers.add(team.getPlayers().get(i));
								}
							}
							int i = 0;
							for (TeamManager team : tm.teams.values()) {
								for (int ii = team.getPlayers().size(); ii < targetSize; ii++)
									team.swapPlayer(swapedPlayers.get(i + targetSize * i), false);
								i++;
							}
							tm.globalMsg(ChatColor.GRAY + "Teams have now be evened!");
						}
					}, 200L);
	
					return true;
				}
				
				//-----Shuffle Teams-------------------------------------------------------------------
				if (action.equals("shuffle")) {
					tm.globalMsg("Prepare for shuffling of teams...");
					tm.getServer().getScheduler().runTaskLater(tm, new Runnable() {
						public void run() {
							int targetSize = Math.round(tm.players.size() / tm.teams.size());
							List<PlayerState> shuffledPlayers = new ArrayList<PlayerState>();
							Collections.shuffle(shuffledPlayers);
							int i = 0;
							for (TeamManager team : tm.teams.values()) {
								for (int ii = 0; ii < targetSize; ii++)
									team.swapPlayer(shuffledPlayers.get(i + targetSize * i), false);
								i++;
							}
							tm.globalMsg(ChatColor.GRAY + "Teams have now be shuffled, say hi to yor new team mates!");
						}
					}, 200L);
						
					return true;
				}
				
				//-----Clear Teams-------------------------------------------------------------------
				if (action.equals("clear")) {
					for (TeamManager team : tm.teams.values()) {
						while(team.getPlayers().size() > 0)
							team.getPlayers().get(0).remove(false);
					}
					tm.globalMsg("All Teams have been cleared, you are now are all alone :'(");
					sender.sendMessage(ChatColor.GRAY + "Teams have been successfully cleared");
	
					return true;
				}
			}
				
			//////////////////////////
			///Admin Based Commands///
			//////////////////////////
			
			if(sender.hasPermission(permission + ".control")) {
				
				//-----Create Team-------------------------------------------------------------------
				if (action.equals("create")) {
					if (args.length > 1 && !tm.teams.containsKey(args[1])) {
						TeamManager newTeam = tm.newTeam(args[1]);
						if(newTeam != null) {
							tm.teams.put(args[1], newTeam);
							sender.sendMessage(newTeam.getName() + " team has be created, go " + tm.teams.get(args[1]).getName() + " team!");
						} else
							sender.sendMessage(ChatColor.RED + "Invaild team name, choose between" + tm.theTeamNames);
					} else
						sender.sendMessage(ChatColor.RED + "There is allready a " + args[1] + " team");
					
					return true;
				}
	
				//-----Delete Team-------------------------------------------------------------------
				if (action.equals("del")) {
					if (args.length > 1 && tm.teams.containsKey(args[1])) {
						tm.teams.get(args[1]).delTeam();
					} else
						sender.sendMessage(ChatColor.RED + "Invaild team name");
	
					return true;
				}
	
				if (action.equals("debug")) {			
					tm.getLogger().info("WIP");
	
					return true;
				}
				
				if (action.equals("load")) {
					String profile = "default";
					if (args.length > 1)
						profile = args[1];
					else
						profile = "default";
					
					if(tm.load(profile))
						sender.sendMessage("Team Plugin " + profile + " Setting have been Loaded!");
					else
						sender.sendMessage(ChatColor.RED + "Team Plugin " + profile + " Setting FAILED to Loaded!");
					
					return true;
				}
				
				if (action.equals("save")) {
					String profile = "default";
					if (args.length > 1)
						profile = args[1];
					else
						profile = "default";
					
					if(tm.save(profile))
						sender.sendMessage("Team Plugin " + profile + " Setting have been Saved!");
					else
						sender.sendMessage(ChatColor.RED + "Team Plugin " + profile + " Setting FAILED to Saved!");
					
					return true;
				}
			}

			if (action.equals("info")) {
				sender.sendMessage(ChatColor.GRAY + "Team Work v" + tm.getDescription().getVersion() + " By Matthew Kemp aka techmatt. Plugin for Bukkit 1.4.7 Type '/team help' for command info. :D");
				return true;
			}
		}
		
		return false;
	}
	
	private void countdown(int time) {
		timmer = time;		
		countdown = tm.getServer().getScheduler().scheduleSyncRepeatingTask(tm, new Runnable() {
			public void run() {
				if (timmer != 0) {
					tm.globalMsg(ChatColor.GRAY + "" + timmer);
					timmer -= 1;
				} else {
					tm.globalMsg(ChatColor.GREEN + "GO! GO! GO!!!!!");
					tm.getServer().getScheduler().cancelTask(countdown);
				}
			}
		}, 20L, 20L);
	}

	private String playersHealth(Player player) {
		ChatColor[] colour = { ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN };
		String healthBar = "[";
		for (int i = 1; i <= 20; i++) {
			if (player.getHealth() >= i)
				healthBar = healthBar + colour[(int)(i / 5.2D)] + "-";
			else
				healthBar = healthBar + " ";
		}
		
		return " - " + healthBar + ChatColor.RESET + "]";
	}

	private void winner(TeamManager team) {
		tm.globalMsg(team.getName() + " WINS!!! YAY go " + team.getName() + " team you rock! :D");
		for (PlayerState plySte : team.getPlayers()) {
			Location loc = plySte.getPlayer().getLocation();
			loc.setY(loc.getY() + 4.0D);
			
			String[] duckNames = new String[7];
			duckNames[0] = "Party Duck!";
			duckNames[1] = "Bubberduck";
			duckNames[2] = "Lazy Duck";
			duckNames[3] = "Silly Duck";
			duckNames[4] = "Happy Duck";
			duckNames[5] = "Emo Duck";
			duckNames[6] = "Evil Duck";
			
			for (int i = 0; i < 7; i++) {
				this.randomFirework(loc);
				loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
				LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
				entity.setCustomName(duckNames[i]);
				entity.setCustomNameVisible(true);
			}
			
			plySte.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000, 2));
			plySte.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 2));
			plySte.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000, 2));
			plySte.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000, 2));
		}
		
		for (PlayerState plySte : tm.players.values()) {
			if (!plySte.getTeam().equals(team))
				plySte.getPlayer().getInventory().clear();
		}
	}
	
	public void randomFirework(Location l) {           
        //Spawn the Firework, get the FireworkMeta.
        Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        //Our random generator
            Random r = new Random();   
 
            //Get the type
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;       
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
       
        //Get our random colours   
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = Color.fromRGB(r1i);
        Color c2 = Color.fromRGB(r2i);
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
       
        //Then apply the effect to the meta
        fwm.addEffect(effect);
       
        //Generate some random power and set it
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);
       
        //Then apply this to our rocket
        fw.setFireworkMeta(fwm); 
	}
}