package me.ympax.emerixeauth;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

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
                    //Montrer les deux possibilités de connection

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
