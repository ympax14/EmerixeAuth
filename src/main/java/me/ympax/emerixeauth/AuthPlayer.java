package me.ympax.emerixeauth;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.ympax.emerixeauth.config.PlayerConfig;

public class AuthPlayer {
    private final CraftPlayer player;
    private final PlayerConfig playerConfig;
    private AuthState authState = AuthState.Registering;

    public AuthPlayer(CraftPlayer p) {
        player = p;

        if (player.isPremium() == "true") {
            if ((new File(EmerixeAuth.getInstance().getDataFolder() + "/players/" + player.getName() + ".yml")).exists()) {
                playerConfig = new PlayerConfig(player.getName(), player.isPremium());
                authState = AuthState.LoggingIn;
            } else {
                playerConfig = new PlayerConfig(player.getUniqueId().toString(), player.isPremium());

                if (playerConfig.gotCreated()) {
                    Bukkit.getScheduler().runTaskLater(EmerixeAuth.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            player.openInventory(loginMethodInventory());
                        }
                    }, 1L);

                    authState = AuthState.Logged;
                } else authState = playerConfig.getString("premium") == "true" ? AuthState.Logged : AuthState.LoggingIn;
            }
        } else {
            String UUID = getUUIDFromUsername(player.getName());

            if ((new File(EmerixeAuth.getInstance().getDataFolder() + "/players/" + UUID + ".yml")).exists()) {
                player.kickPlayer("Un joueur premium est déjà enregistré avec le même pseudo. Veuillez changer de pseudo.");
                playerConfig = null;
                return;
            } else {
                playerConfig = new PlayerConfig(player.getName(), player.isPremium());
                authState = playerConfig.gotCreated() ? AuthState.Registering : AuthState.LoggingIn;
            }
        }
    }

    private Inventory loginMethodInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9*8);
        ItemStack blueGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLUE.getData());
        ItemStack redGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData());

        ItemStack lockerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta lockerHeadMeta = (SkullMeta) lockerHead.getItemMeta();
        GameProfile lockerProfile = new GameProfile(UUID.randomUUID(), null);

        lockerProfile.getProperties().put("textures", new Property("textures","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZiMTkzMmM0MmNkN2FmNjIxYjhlNTJmZGY0OWE0YTdmYTZmNDgwOTViYjYwOGUwNTgwNTVhZjM4YjNmMWZjNCJ9fX0="));

        try {
            Field profileField = lockerHeadMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(lockerHeadMeta, lockerProfile);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }

        lockerHeadMeta.setDisplayName(ChatColor.BLUE + "Microsoft");
        lockerHead.setItemMeta(lockerHeadMeta);

        ItemStack seriousHead = new ItemStack(Material.SKULL, 1, (short) 3);
        SkullMeta seriousHeadMeta = (SkullMeta) lockerHead.getItemMeta();
        GameProfile seriousProfile = new GameProfile(UUID.randomUUID(), null);

        seriousProfile.getProperties().put("textures", new Property("textures","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTA0ZDUzN2U5NDIyOGM1ODk4M2YyNjljMTVhZGUxMjRmMGZkYmU2MGQ3OGIyMGE3MDRmZTBkMTFjZjY3NiJ9fX0="));

        try {
            Field profileField = seriousHeadMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(seriousHeadMeta, seriousProfile);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }

        seriousHeadMeta.setDisplayName(ChatColor.RED + "Crack");
        seriousHead.setItemMeta(seriousHeadMeta);

        for (int i = 0; i < 3; i++) {
            inventory.setItem(i + 1, blueGlassPane);
            inventory.setItem(i + 5, redGlassPane);

            inventory.setItem(i + 19, blueGlassPane);
            inventory.setItem(i + 23, redGlassPane);
        }

        inventory.setItem(10, blueGlassPane);
        inventory.setItem(11, lockerHead);
        inventory.setItem(12, blueGlassPane);

        inventory.setItem(14, redGlassPane);
        inventory.setItem(15, seriousHead);
        inventory.setItem(16, redGlassPane);

        return inventory;
    }

    public CraftPlayer getPlayer() {
        return player;
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public AuthState getAuthState() {
        return authState;
    }

    public void setAuthState(AuthState state) {
        authState = state;
    }

    public String getPassword() {
        return playerConfig.getString("password");
    }

    public void setPassword(String pwd) {
        playerConfig.setString("password", pwd);
    }

    public String getRecoveryKey() {
        return playerConfig.getString("recoverykey");
    }

    private String getUUIDFromUsername(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (response.length() > 0) {
                String jsonResponse = response.toString();
                int idStartIndex = jsonResponse.indexOf("\"id\":\"") + 6;
                int idEndIndex = jsonResponse.indexOf("\"", idStartIndex);

                if (idStartIndex != -1 && idEndIndex != -1) {
                    return jsonResponse.substring(idStartIndex, idEndIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
