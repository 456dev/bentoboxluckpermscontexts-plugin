package dev.the456gamer.bentoboxluckpermscontexts.luckperms;

import dev.the456gamer.bentoboxluckpermscontexts.BentoBoxLuckpermsContextsPlugin;
import java.util.Optional;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import world.bentobox.bentobox.api.addons.GameModeAddon;

public class BentoBoxGamemodeContextCalculator implements ContextCalculator<Player> {

  public static final String CONTEXT_GAMEMODE_KEY = "bentoboxgamemode";
  BentoBoxLuckpermsContextsPlugin plugin;

  public BentoBoxGamemodeContextCalculator() {
    this.plugin = BentoBoxLuckpermsContextsPlugin.getPluginInstance();
  }

  @Override
  public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
    World targetWorld = target.getWorld();
    Optional<GameModeAddon> addonOptional = plugin.getBentoBoxApi().getIWM().getAddon(targetWorld);
    addonOptional.ifPresentOrElse(gameModeAddon -> consumer.accept(CONTEXT_GAMEMODE_KEY,
            gameModeAddon.getDescription().getName()),
        () -> consumer.accept(CONTEXT_GAMEMODE_KEY, "none"));

  }

  @Override
  public @NonNull ContextSet estimatePotentialContexts() {
    ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
    builder.add(CONTEXT_GAMEMODE_KEY, "none");
    plugin.getBentoBoxApi().getAddonsManager().getGameModeAddons().forEach(
        gameModeAddon -> builder.add(CONTEXT_GAMEMODE_KEY,
            gameModeAddon.getDescription().getName()));
    return builder.build();
  }
}
