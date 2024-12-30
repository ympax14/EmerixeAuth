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

public class RegisterCommand extends Command {
    public RegisterCommand() {
        super("register");
        this.setUsage(ChatColor.YELLOW + "/register [mdp] [mdp]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CraftPlayer craftPlayer = (CraftPlayer) player;
            AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(craftPlayer);

            if (authPlayer.getAuthState() == AuthState.Logged) {
                player.sendMessage(ChatColor.RED + "Vous êtes déjà connecté(e).");
            } else if (authPlayer.getAuthState() == AuthState.LoggingIn) {
                player.sendMessage(ChatColor.RED + "Vous êtes déjà enregistré(e).");
            } else {
                if (args.length != 2) {
                    player.sendMessage(this.usageMessage);
                    return false;
                }

                String firstPwd = args[0];
                String secondPwd = args[1];

                if (!firstPwd.equals(secondPwd)) {
                    player.sendMessage(ChatColor.RED + "Les mots de passe ne correspondent pas.");
                    return false;
                } else {
                    String recoveryKey = SecurityUtils.generateRecoveryKey();
                    try {
                        String hashedPassword = SecurityUtils.hashPassword(firstPwd);
                        String hashedRecoveryKey = SecurityUtils.hashPassword(recoveryKey);

                        authPlayer.getPlayerConfig().setString("password", hashedPassword);
                        authPlayer.getPlayerConfig().setString("recoverykey", hashedRecoveryKey);
                        authPlayer.getPlayerConfig().setString("premium", "false");
                        authPlayer.getPlayerConfig().save();

                        player.sendMessage(ChatColor.GREEN + "Enregistré(e) avec succès.\nVeuillez votre clé de récupération en cas de perte de compte: " + ChatColor.YELLOW + recoveryKey
                        + "\n" + ChatColor.RED + ChatColor.BOLD + "/!\\ Attention ! Ne partagez pas votre clé de récupération !\nCette clé permet de réinitialiser votre mot de passe !");
                        authPlayer.setAuthState(AuthState.Logged);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    
                }
            }
        }

        return true;
    }
}