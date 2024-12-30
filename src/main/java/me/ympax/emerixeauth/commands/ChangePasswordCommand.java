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

public class ChangePasswordCommand extends Command {
    public ChangePasswordCommand() {
        super("changepassword");
        this.setUsage(ChatColor.RED + "/changepassword [RecoveryKey] [mdp] [mdp]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CraftPlayer craftPlayer = (CraftPlayer) player;
            AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(craftPlayer);

            if (authPlayer.getAuthState() == AuthState.Logged || authPlayer.getAuthState() == AuthState.LoggingIn) {
                if (args.length != 3) {
                    player.sendMessage(this.usageMessage);
                    return false;
                }

                String recoveryKey = args[0];
                String firstPwd = args[1];
                String secondPwd = args[2];

                try {
                    if (SecurityUtils.verifyPassword(recoveryKey, authPlayer.getRecoveryKey())) {
                        if (!firstPwd.equals(secondPwd)) {
                            player.sendMessage(ChatColor.RED + "Les mots de passe ne correspondent pas.");
                            return false;
                        }

                        authPlayer.setPassword(SecurityUtils.hashPassword(firstPwd));

                        player.sendMessage(ChatColor.GREEN + "Votre mot de passe a bien été changé.");
                    } else {
                        player.sendMessage(ChatColor.RED + "La clé de récupération est incorrecte.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage("Vous devez d'abord vous enregistrer. Utilisez /register [mdp] [mdp] pour vous enregistrer.");
            }
        }

        return true;
    }
}