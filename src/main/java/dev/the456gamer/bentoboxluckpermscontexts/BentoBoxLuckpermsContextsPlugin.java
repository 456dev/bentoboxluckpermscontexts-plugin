package dev.the456gamer.bentoboxluckpermscontexts;

import static net.kyori.adventure.text.Component.text;

import dev.the456gamer.bentoboxluckpermscontexts.bentobox.FlagHandler;
import dev.the456gamer.bentoboxluckpermscontexts.config.mapper.PluginConfig;
import dev.the456gamer.bentoboxluckpermscontexts.config.serializer.MaterialSerializer;
import dev.the456gamer.bentoboxluckpermscontexts.luckperms.BentoBoxFlagContextCalculator;
import dev.the456gamer.bentoboxluckpermscontexts.luckperms.BentoBoxGamemodeContextCalculator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.BentoBoxReadyEvent;


public final class BentoBoxLuckpermsContextsPlugin extends JavaPlugin implements Listener {

  LuckPerms luckPermsApi;
  BentoBox bentoBoxApi;

  CommentedConfigurationNode configRootNode;
  PluginConfig config;

  FlagHandler flagHandler;

  public PluginConfig config() {
    return config;
  }

  @Override
  public @NotNull FileConfiguration getConfig() {
    throw new UnsupportedOperationException("Not using bukkit config");
  }

  public LuckPerms getLuckPermsApi() {
    return luckPermsApi;
  }

  public BentoBox getBentoBoxApi() {
    return bentoBoxApi;
  }

  @Override
  public void onEnable() {
    if (checkPlugin("luckperms", JavaPlugin.class) == null) {
      return;
    }
    RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServer().getServicesManager()
        .getRegistration(LuckPerms.class);
    if (provider == null) {
      getComponentLogger().error(text("No Luckperms :("));
      setEnabled(false);
      return;
    }
    luckPermsApi = provider.getProvider();

    BentoBox bentoBox = checkPlugin("bentobox", BentoBox.class);

    if (bentoBox == null) {
      return;
    }
    bentoBoxApi = bentoBox;

    setupConfig();
    getServer().getPluginManager().registerEvents(this, this);

    getLuckPermsApi().getContextManager().registerCalculator(new BentoBoxGamemodeContextCalculator());
    getLuckPermsApi().getContextManager().registerCalculator(new BentoBoxFlagContextCalculator());


  }

  @EventHandler
  public void onBentoBoxReadyEvent(BentoBoxReadyEvent readyEvent) {
      flagHandler = new FlagHandler();
  }

  private void setupConfig() {
    getDataFolder().mkdirs();
    File configFile = new File(getDataFolder(), "config.yml");
    if (!configFile.isFile() || configFile.length() == 0) {
      try (
          InputStream in = getResource("config.yml");
          OutputStream out = new BufferedOutputStream(
              new FileOutputStream(configFile))) {

        in.transferTo(out);
      } catch (IOException e) {
        throw new RuntimeException("unable to copy default config");
      }
    }
    loadConfig();
    saveConfig();
  }

  private <T extends Plugin> T checkPlugin(String pluginName, Class<T> expectedClass) {
    Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);

    if (plugin == null || !plugin.isEnabled()) {
      getComponentLogger().error(text("No %s :(".formatted(pluginName)));
      setEnabled(false);
      return null;
    }

    if (expectedClass.isInstance(plugin)) {
      return (T) plugin;
    }
    getComponentLogger().error(text(
        "Unexpected Class %s when checking for %s (expected %s)".formatted(
            plugin.getClass().getName(), pluginName, expectedClass.getName())));
    setEnabled(false);
    return null;

  }

  public static BentoBoxLuckpermsContextsPlugin getPluginInstance() {
    return BentoBoxLuckpermsContextsPlugin.getPlugin(BentoBoxLuckpermsContextsPlugin.class);
  }


  public void loadConfig() {
    YamlConfigurationLoader loader = getYamlConfigurationLoader();
    try {
      configRootNode = loader.load();
      config = configRootNode.get(PluginConfig.class);
    } catch (ConfigurateException e) {
      throw new RuntimeException(e);
    }
  }

  public void saveConfig() {
    YamlConfigurationLoader loader = getYamlConfigurationLoader();
    try {
      configRootNode.set(PluginConfig.class, config);
      loader.save(configRootNode);
    } catch (ConfigurateException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  private YamlConfigurationLoader getYamlConfigurationLoader() {
    return YamlConfigurationLoader.builder()
        .commentsEnabled(true)
        .nodeStyle(NodeStyle.BLOCK)
        .indent(4)
        .file(new File(getDataFolder(), "config.yml"))
        .defaultOptions(
            options -> options
                .header("""
                    BentoBox Luckperms Context Link
                    """)
                .implicitInitialization(false)
                .shouldCopyDefaults(true)
                .serializers(builder -> builder
                    .register(new MaterialSerializer())
                )
        )
        .build();
  }
}
