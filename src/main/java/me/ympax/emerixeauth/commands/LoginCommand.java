package me.ympax.emerixeauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.ympax.emerixeauth.AuthPlayer;
import me.ympax.emerixeauth.AuthState;
import me.ympax.emerixeauth.EmerixeAuth;
import me.ympax.emerixeauth.utils.SecurityUtils;

public class LoginCommand extends Command {
    public LoginCommand() {
        super("login");
        this.setUsage(ChatColor.RED + "/login [mdp]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CraftPlayer craftPlayer = (CraftPlayer) player;
            AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(craftPlayer);

            if (authPlayer.getAuthState() == AuthState.Logged) {
                player.sendMessage("Vous êtes déjà connecté(e).");
            } else if (authPlayer.getAuthState() == AuthState.LoggingIn) {
                if (args.length != 1) {
                    player.sendMessage(this.usageMessage);
                    return false;
                }

                String password = args[0];

                try {
                    if (SecurityUtils.verifyPassword(password, authPlayer.getPassword())) {
                        authPlayer.setAuthState(AuthState.Logged);
                        player.sendMessage(ChatColor.GREEN + "Vous êtes désormais connecté(e).");
                    } else {
                        player.sendMessage(ChatColor.RED + "Le mot de passe est incorrect.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(ChatColor.RED + "Vous devez d'abord vous enregistrer. Utilisez /register [mdp] [mdp] pour vous enregistrer.");
            }
        }

        return true;
    }
}