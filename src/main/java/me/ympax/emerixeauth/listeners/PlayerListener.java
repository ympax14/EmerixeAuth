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

        if (authPlayer.getAuthState().getId() == AuthState.LoggingIn.getId()) {
            player.sendMessage(logInMessage);
        } else if (authPlayer.getAuthState().getId() == AuthState.Registering.getId()) {
            player.sendMessage(registerMessage);
        }

        event.setJoinMessage(null);

        EmerixeAuth.getInstance().getAuthPlayers().add(authPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();

        AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(player);

        if (authPlayer.getAuthState().getId() == AuthState.Registering.getId()) {
            authPlayer.getPlayerConfig().getConfigFile().delete();
        } else {
            authPlayer.getPlayerConfig().save();
        }

        event.setQuitMessage(null);

        EmerixeAuth.getInstance().getAuthPlayers().remove(authPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();
        AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(player);

        String command = event.getMessage().split(" ")[0];

        EmerixeAuth.getInstance().getLogger().info(authPlayer.getAuthState() + " " + command + " " + (authPlayer.getAuthState().getId() == AuthState.Registering.getId() && !command.equals("/register")));
        
        if (authPlayer.getAuthState().getId() == AuthState.Logged.getId()
        || (authPlayer.getAuthState().getId() == AuthState.LoggingIn.getId() && !command.equals("/login"))
        || (authPlayer.getAuthState().getId() == AuthState.Registering.getId() && !command.equals("/register"))) {
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
