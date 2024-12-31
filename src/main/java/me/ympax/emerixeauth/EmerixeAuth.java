package me.ympax.emerixeauth;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.ympax.emerixeauth.commands.LoginCommand;
import me.ympax.emerixeauth.commands.RegisterCommand;
import me.ympax.emerixeauth.commands.ResetPasswordCommand;
import me.ympax.emerixeauth.handler.CustomMovementHandler;
import me.ympax.emerixeauth.listeners.PlayerListener;
import me.ympax.neverlessspigot.NeverLessSpigot;

public class EmerixeAuth extends JavaPlugin {
  private static EmerixeAuth instance;
  private ArrayList<AuthPlayer> authPlayers = new ArrayList<>();

  @Override
  public void onEnable() {
    instance = this;

    registerCommands();
    registerListeners();

    NeverLessSpigot.getInstance().registerMovementHandler(new CustomMovementHandler());
  }

  @Override
  public void onDisable() {
    instance = null;

    this.authPlayers.forEach(authPlayer -> {
      if (authPlayer.getAuthState().getId() == AuthState.Registering.getId()) {
        authPlayer.getPlayerConfig().getConfigFile().delete();
      } else {
        authPlayer.getPlayerConfig().save();
      }
    });
  }

  public static EmerixeAuth getInstance() {
    return instance;
  }

  public void registerCommand(final Command cmd , final String fallbackPrefix) {
    this.getServer().getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
  }

  private void registerCommands() {
    Arrays.asList(new LoginCommand(), new RegisterCommand(), new ResetPasswordCommand()).forEach(command -> this.registerCommand(command, this.getName()));
  }

  public void registerListener(final Listener listener) {
    this.getServer().getPluginManager().registerEvents(listener, this);
  }

  private void registerListeners() {
    Arrays.asList(new PlayerListener()).forEach(this::registerListener);
  }

  public ArrayList<AuthPlayer> getAuthPlayers() {
    return authPlayers;
  }

  public void addAuthPlayer(AuthPlayer authPlayer) {
    authPlayers.add(authPlayer);
  }

  public void removeAuthPlayer(AuthPlayer authPlayer) {
    authPlayers.remove(authPlayer);
  }

  public boolean containAuthPlayer(AuthPlayer authPlayer) {
    return authPlayers.contains(authPlayer);
  }

  public boolean containAuthPlayer(CraftPlayer craftPlayer) {
    return authPlayers.stream().filter(s -> s.getPlayer() == craftPlayer).findAny().isPresent();
  }

  public AuthPlayer getAuthPlayer(CraftPlayer player) {
    if (!containAuthPlayer(player)) return null;

    return authPlayers.stream().filter(s -> s.getPlayer() == player).findAny().get();
  }
}