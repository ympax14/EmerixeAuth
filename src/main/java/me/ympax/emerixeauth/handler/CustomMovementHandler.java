package me.ympax.emerixeauth.handler;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.ympax.emerixeauth.AuthPlayer;
import me.ympax.emerixeauth.AuthState;
import me.ympax.emerixeauth.EmerixeAuth;
import me.ympax.neverlessspigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;

public class CustomMovementHandler implements MovementHandler {

    @Override
    public void updateLocation(Player player, Location to, Location from, PacketPlayInFlying paramPacketPlayInFlying) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(craftPlayer);

        if (authPlayer == null) return;

        if (authPlayer.getAuthState().getId() == AuthState.LoggingIn.getId() || authPlayer.getAuthState().getId() == AuthState.Registering.getId()) {
            if (to.getX() != from.getX() || to.getZ() != from.getZ() || to.getY() != from.getY()) {
                player.teleport(from);
                ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
            }
        }
    }

    @Override
    public void updateRotation(Player player, Location to, Location from, PacketPlayInFlying paramPacketPlayInFlying) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        AuthPlayer authPlayer = EmerixeAuth.getInstance().getAuthPlayer(craftPlayer);

        if (authPlayer == null) return;

        if (authPlayer.getAuthState().getId() == AuthState.LoggingIn.getId() || authPlayer.getAuthState().getId() == AuthState.Registering.getId()) {
            if (to.getPitch() != from.getPitch() || to.getYaw() != from.getYaw()) {
                player.teleport(from);
                ((CraftPlayer)player).getHandle().playerConnection.checkMovement = false;
            }
        }
    }
}
