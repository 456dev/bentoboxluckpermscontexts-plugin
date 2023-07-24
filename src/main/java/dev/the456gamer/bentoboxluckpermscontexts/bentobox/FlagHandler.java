package dev.the456gamer.bentoboxluckpermscontexts.bentobox;

import dev.the456gamer.bentoboxluckpermscontexts.BentoBoxLuckpermsContextsPlugin;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.flags.Flag.Mode;
import world.bentobox.bentobox.api.flags.Flag.Type;

public class FlagHandler {

  BentoBoxLuckpermsContextsPlugin plugin;

  public FlagHandler() {
    plugin = BentoBoxLuckpermsContextsPlugin.getPluginInstance();
    registerFlags();

  }

  public void registerFlags() {
    System.out.println("start register");
    plugin.config().getFlags().forEach(flag -> {
      System.out.println(flag.getId());
      if (flag.isEnabled()) {
        Flag new_flag = makeFlag(flag.getId(), flag.getDefaultRank(), flag.getName(),
            flag.getDescription(),
            flag.getIconItem(), flag.getLevel());
        boolean success = plugin.getBentoBoxApi().getFlagsManager().registerFlag(new_flag);
        if (!success) {
          plugin.getComponentLogger()
              .warn("register flag failed: id=%s. is it already registered?`".formatted(
                  new_flag.getID()));
          return;
        }

        // list = empty
        // acts as allow all

        List<String> gamemodeList = flag.getAllowedGamemodes();
        boolean invert = flag.getInvertAllowlist();
        gamemodeList = gamemodeList.stream().distinct().filter(s -> !s.isBlank())
            .collect(Collectors.toList());
        if (gamemodeList.size() != 0) {
          List<String> finalGamemodeList = gamemodeList;
          plugin.getBentoBoxApi().getAddonsManager().getGameModeAddons()
              .forEach(gameModeAddon -> {
                String addonName = gameModeAddon.getDescription().getName();
                boolean presentInList = finalGamemodeList.contains(addonName);
                boolean shouldAdd = presentInList != invert;
                System.out.println(
                    "presentinlist=%s shouldadd=%s: %s".formatted(presentInList, shouldAdd,
                        gameModeAddon.getDescription().getName()));
                if (shouldAdd) {
                  new_flag.addGameModeAddon(gameModeAddon);
                }
              });
        }
      }
    });
  }

  @NotNull
  private Flag makeFlag(String id, int defaultRank, String name, String desc, Material icon,
      Mode mode) {
    Flag flag = new Flag.Builder(id, icon).mode(mode)
        .type(Type.PROTECTION).defaultRank(
            defaultRank).build();
    plugin.getBentoBoxApi().getLocalesManager()
        .setTranslation(Locale.US, flag.getNameReference(), name);
    plugin.getBentoBoxApi().getLocalesManager()
        .setTranslation(Locale.US, flag.getDescriptionReference(), desc);
    return flag;
  }
}
