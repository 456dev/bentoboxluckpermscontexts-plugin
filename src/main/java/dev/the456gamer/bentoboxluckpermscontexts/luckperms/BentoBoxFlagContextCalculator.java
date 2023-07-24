package dev.the456gamer.bentoboxluckpermscontexts.luckperms;

import dev.the456gamer.bentoboxluckpermscontexts.BentoBoxLuckpermsContextsPlugin;
import java.util.Optional;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import world.bentobox.bentobox.database.objects.Island;

public class BentoBoxFlagContextCalculator implements ContextCalculator<Player> {


  public enum Result {
    ALLOW("ALLOW"),
    DENY("DENY"),
    UNSET("UNSET");

    public String getKey() {
      return key;
    }

    final String key;

    Result(String key) {
      this.key = key;
    }
  }

  public static final String CONTEXT_FLAG_PREFIX = "bentoboxflag:";
  BentoBoxLuckpermsContextsPlugin plugin;

  public BentoBoxFlagContextCalculator() {
    this.plugin = BentoBoxLuckpermsContextsPlugin.getPluginInstance();
  }

  @Override
  public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
    Optional<Island> islandOptional = plugin.getBentoBoxApi().getIslands()
        .getIslandAt(target.getLocation());
    boolean isInGameWorld = plugin.getBentoBoxApi().getIWM().inWorld(target.getLocation());
    ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
    plugin.getBentoBoxApi().getFlagsManager().getFlags().forEach(flag -> {
      if (!isInGameWorld) {
        builder.add(CONTEXT_FLAG_PREFIX.concat(flag.getID()), Result.UNSET.getKey());
      } else {
        islandOptional.ifPresentOrElse(island -> {

          int flagValue = island.getFlag(flag);
          boolean allowed = switch (flag.getType()) {
            case PROTECTION -> island.getRank(target.getUniqueId()) >= flagValue;
            case SETTING, WORLD_SETTING -> flagValue != 0;
          };
          builder.add(CONTEXT_FLAG_PREFIX.concat(flag.getID()),
              allowed ? Result.ALLOW.getKey() : Result.DENY.getKey());
        }, () -> {
          boolean allowed = plugin.getBentoBoxApi().getIWM().getWorldSettings(target.getWorld())
              .getWorldFlags()
              .get(flag.getID());
          builder.add(CONTEXT_FLAG_PREFIX.concat(flag.getID()),
              allowed ? Result.ALLOW.getKey() : Result.DENY.getKey());
        });
      }
    });
    consumer.accept(builder.build());
  }

  @Override
  public @NonNull ContextSet estimatePotentialContexts() {
    ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
    plugin.getBentoBoxApi().getFlagsManager().getFlags().forEach(flag -> {
      for (Result result : Result.values()) {
        builder.add(CONTEXT_FLAG_PREFIX.concat(flag.getID()), result.getKey());
      }
    });
    return builder.build();
  }
}
