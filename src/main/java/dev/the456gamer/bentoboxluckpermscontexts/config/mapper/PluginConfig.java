package dev.the456gamer.bentoboxluckpermscontexts.config.mapper;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class PluginConfig {

  @Comment("Custom Flags for permissions")
  private final List<BentoBoxFlag> flags = new ArrayList<>();




  public List<BentoBoxFlag> getFlags() {
    return flags;
  }
}
