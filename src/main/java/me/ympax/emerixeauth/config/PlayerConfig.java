package me.ympax.emerixeauth.config;

import java.io.File;
import java.io.IOException;
import me.ympax.emerixeauth.EmerixeAuth;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerConfig {
  private final FileConfiguration config;
  private final File configFile;
  private Boolean justCreated = false;

  public PlayerConfig(String name, String isPremium) {
    configFile = new File(EmerixeAuth.getInstance().getDataFolder() + "/players/" + name + ".yml");
    if (!configFile.exists()) {
      justCreated = true;
      if (configFile.getParentFile().mkdirs()) {
        try {
          configFile.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    config = YamlConfiguration.loadConfiguration(configFile);
    config.addDefault("password", null);
    config.addDefault("premium", isPremium);
    config.addDefault("recoverykey", null);
    save();
  }

  public boolean gotCreated() {
    return justCreated;
  }

  public File getConfigFile() {
    return this.configFile;
  }

  public FileConfiguration getConfig() {
    return this.config;
  }

  public void save() {
    try {
      this.config.save(this.configFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getString(String key) {
    return config.getString(key);
  }

  public void setString(String key, String value) {
    config.set(key, value);
  }
}