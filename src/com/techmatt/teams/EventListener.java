package com.techmatt.teams;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;


public class EventListener implements Listener {
	
	private Teams tm;
	
	public EventListener(Teams teams) {
		tm = teams;
	}
	
	@EventHandler
	public void teamChat(AsyncPlayerChatEvent event) {
		if (tm.players.containsKey(event.getPlayer())) {
			PlayerState plySte = tm.players.get(event.getPlayer());
			if (plySte.isTeamChat()) {
				event.getRecipients().clear();
				for (PlayerState players : plySte.getTeam().getPlayers()) {
					if (players.getPlayer().hasPermission("com.techmatt.teams.chatsnoop"))
					event.getRecipients().add(players.getPlayer());
				}
				event.setMessage(plySte.getTeam().getChatColour() + "(TC) " + ChatColor.RESET + event.getMessage());
			}
		}
	}
	
	@EventHandler
	public void dropLoot(PlayerDeathEvent event) {
		if (!tm.dropLoot && tm.players.containsKey(event.getEntity()))
			event.getDrops().clear();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void teamSpawn(PlayerRespawnEvent event) {
		if (tm.players.containsKey(event.getPlayer())) {
			PlayerState plySte = tm.players.get(event.getPlayer());
			if (plySte.getTeam().isSpectateAfterDeath())
				plySte.setSpectateMode(true);
			plySte.setUpPlayer();
		}
	}
	
	/*@EventHandler //TODO:lockGear
	public void gearCheck(InventoryCloseEvent event) {
		if (tm.lockGear && tm.players.containsKey(event.getPlayer()))
			tm.players.get(event.getPlayer()).pickUpHat();
	}*/
	
	@EventHandler
	public void rejoinTeam(PlayerJoinEvent event) {
		event.getPlayer().setScoreboard(tm.board);
		if (tm.rejoin && tm.offlinePlayers.containsKey(event.getPlayer().getName())) {
			PlayerState plySte = tm.offlinePlayers.get(event.getPlayer().getName());
			if(plySte.getLastOnline() + (5 * 60000) > System.currentTimeMillis()) { //if player had left longer than 5mins don't rejoin
				String playerTeamKey = plySte.getTeam().getKeyName();
				if(tm.teams.containsKey(playerTeamKey))
					tm.teams.get(playerTeamKey).addPlayer(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void leaveTeam(PlayerQuitEvent event) { //TODO: lose stuff
		if (tm.players.containsKey(event.getPlayer())){
			PlayerState plySte = tm.players.get(event.getPlayer());
			plySte.updateLastOnline();
			tm.offlinePlayers.put(event.getPlayer().getName(), plySte);
			tm.players.get(event.getPlayer()).remove(false);
		}
	}
	
	@EventHandler
	public void restrict(BlockBreakEvent event){
		if(tm.players.containsKey(event.getPlayer())){
			PlayerState p = tm.players.get(event.getPlayer());
			if (p.getTeam().isBlockProtected() || p.getTeam().isFrozen() || p.isSpectator()){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void spectate(PlayerInteractEvent event){
		if(tm.players.containsKey(event.getPlayer()) && tm.players.get(event.getPlayer()).isSpectator()){
			event.setCancelled(true);
		}
	}
		
	
	@EventHandler
	public void freezePlayer(PlayerMoveEvent event){
		if(tm.players.containsKey(event.getPlayer()) && tm.players.get(event.getPlayer()).getTeam().isFrozen()){
			event.getPlayer().setVelocity(new Vector(0,0,0));
		}
	}

}