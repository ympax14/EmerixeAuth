package me.ympax.emerixeauth.listeners;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.ympax.emerixeauth.AuthPlayer;
import me.ympax.emerixeauth.AuthState;
import me.ympax.emerixeauth.EmerixeAuth;

public class PlayerListener implements Listener {
    final String registerMessage = ChatColor.YELLOW + "Veuillez vous enregistrer avec la commande /register [mdp] [mdp].";
    final String logInMessage = ChatColor.YELLOW + "Veuillez vous connecter avec la commande /login [mdp].";

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();
        AuthPlayer authPlayer = new AuthPlayer(player);

        if (authPlayer.getAuthState() == AuthState.LoggingIn) {
            player.sendMessage(logInMessage);
        } else if (authPlayer.getAuthState() == AuthState.Registering) {
            player.sendMessage(registerMessage);
        }

        EmerixeAuth.getInstance().getAuthPlayers().add(authPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();

        AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(player);

        if (authPlayer.getAuthState() == AuthState.Registering) {
            authPlayer.getPlayerConfig().getConfigFile().delete();
        } else {
            authPlayer.getPlayerConfig().save();
        }

        EmerixeAuth.getInstance().getAuthPlayers().remove(authPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();
        AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(player);

        String command = event.getMessage().split(" ")[0];
        
        if (authPlayer.getAuthState() != AuthState.Logged
        || (authPlayer.getAuthState() == AuthState.LoggingIn && command != "/login")
        || (authPlayer.getAuthState() == AuthState.Registering && command != "/register")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Toutes les commandes sont désactivés pour le moment.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            /*CraftPlayer player = (CraftPlayer) event.getEntity();
            AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(player);

            if (authPlayer.getAuthState() != AuthState.Logged) */
            event.setCancelled(true);
        }
    }
}
