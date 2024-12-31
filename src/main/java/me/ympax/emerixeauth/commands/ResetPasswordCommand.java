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

public class ResetPasswordCommand extends Command {
    public ResetPasswordCommand() {
        super("resetpassword");
        this.setUsage(ChatColor.RED + "/resetpassword [RecoveryKey]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CraftPlayer craftPlayer = (CraftPlayer) player;
            AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(craftPlayer);

            if (authPlayer.getAuthState().getId() == AuthState.Logged.getId() || authPlayer.getAuthState().getId() == AuthState.LoggingIn.getId()) {
                if (args.length != 1) {
                    player.sendMessage(this.usageMessage);
                    return false;
                }

                String recoveryKey = args[0];

                try {
                    if (SecurityUtils.verifyPassword(recoveryKey, authPlayer.getRecoveryKey())) {
                        String newPassword = SecurityUtils.generateRecoveryKey();
                        authPlayer.setPassword(SecurityUtils.hashPassword(newPassword));

                        player.sendMessage(ChatColor.GREEN + "Votre mot de passe a bien été réinitialisé.\nVoici votre nouveau mot de passe: " + ChatColor.YELLOW + newPassword);
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