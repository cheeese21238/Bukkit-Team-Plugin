package com.techmatt.teams;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
	/*//TODO:Scorboard take over
	@EventHandler
	public void friendlyFire(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			if (event.getDamager() instanceof Player) {
				Player player2 = (Player)event.getDamager();
				if (tm.players.containsKey(player) &&
						tm.players.get(player).getTeam().isFriendlyFire() &&
						tm.players.get(player).getTeam().equals(tm.players.get(player2).getTeam()))
					event.setCancelled(true);
			}
		}
	}*/
	
	@EventHandler
	public void dropLoot(PlayerDeathEvent event) {
		if (tm.dropLoot && tm.players.containsKey(event.getEntity()))
			event.getDrops().clear();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void teamSpawn(PlayerRespawnEvent event) {
		if (tm.players.containsKey(event.getPlayer())) {
			PlayerState plySte = tm.players.get(event.getPlayer());
			if (plySte.getTeam().isSpectateAfterDeath())
				plySte.spectate(true);
			plySte.setUpPlayer();
		}
	}
	
	/*@EventHandler //TODO:lockGear
	public void gearCheck(InventoryCloseEvent event) {
		if (tm.lockGear && tm.players.containsKey(event.getPlayer()))
			tm.players.get(event.getPlayer()).pickUpHat();
	}*/
	
	/*@EventHandler
	public void rejoinTeam(PlayerJoinEvent event) {
		if (tm.rejoin && tm.offlinePlayers.containsKey(event.getPlayer().getName())){
			if(tm.teams.containsKey(tm.offlinePlayers.get(event.getPlayer().getName())))
				tm.teams.get(tm.offlinePlayers.get(event.getPlayer().getName())).addPlayer(event.getPlayer());
		}
	}*/

	@EventHandler
	public void leaveTeam(PlayerQuitEvent event) {
		if (tm.players.containsKey(event.getPlayer())){
			//tm.offlinePlayers.put(event.getPlayer().getName(), tm.players.get(event.getPlayer()).getTeam().getKeyName());
			tm.players.get(event.getPlayer()).getTeam().removePlayer(event.getPlayer(), false);
		}
	}
	
	@EventHandler
	public void restrict(BlockBreakEvent event){
		if(tm.players.containsKey(event.getPlayer())){
			PlayerState p = tm.players.get(event.getPlayer());
			if (p.getTeam().isBlockProtected() || p.getTeam().isFrozen() || (tm.allowInteract && p.isSpectator())){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void spectate(PlayerInteractEvent event){
		if(tm.allowInteract && tm.players.containsKey(event.getPlayer()) && tm.players.get(event.getPlayer()).isSpectator()){
			event.setCancelled(true);
		}
	}
	  
	
	
	/*
	@EventHandler
	public void freezePlayer(PlayerMoveEvent event){
		if(tm.freeze && tm.players.containsKey(event.getPlayer())){
			event.setCancelled(true);
		}
	}*/
	
	/*@EventHandler
	public void magicArrows(ProjectileHitEvent event){	
		if(event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation()).getTypeId() == 102){
			event.getEntity().teleport(arg0, arg1)
		}
	}*/
}