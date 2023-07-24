package dev.the456gamer.bentoboxluckpermscontexts.config.mapper;

import static world.bentobox.bentobox.api.flags.Flag.Mode.ADVANCED;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Matches;
import org.spongepowered.configurate.objectmapping.meta.Required;
import world.bentobox.bentobox.api.flags.Flag.Mode;
import world.bentobox.bentobox.managers.RanksManager;

@ConfigSerializable
public class BentoBoxFlag {

  boolean enabled = true;

  @Comment("ID of the flag\nthis is what player-settings are stored under,\nso other options can change while keeping their choice\nto prevent conflicts, the is prefixed with LPCONTEXT_")
  @Required
  @Matches(value = "^[A-Z_]+$", failureMessage = "%s needs to be A-Z and _ Only (matching regex) %s")
  String id;

  @Comment("Localized name")
  @Required
  String name;

  @Comment("Description in the settings menu")
  @Required
  String description;

  @Comment("an int representing the default rank needed or higher\nOwner=1000\nSub-owner=900\nmember=500\ntrusted=400\ncoop=200\nvisitor=0")
  int defaultRank = RanksManager.MEMBER_RANK;

  @Comment("what group this flag should go in\nValid values: BASIC,ADVANCED,EXPERT")
  Mode level = ADVANCED;


  @Comment("What gamemodes to allow this flag in\n(or not allow if invert-allowlist=true)\nif the final list of valid gamemodes is empty, this acts as if all are enabled")
  List<String> allowedGamemodes = new ArrayList<>();

  @Comment("Whether to make allowed-gamemodes block what gamemodes this flag is available in")
  Boolean invertAllowlist = false;

  public Material getIconItem() {
    return iconItem;
  }

  @Comment("minecraft:give format item for the icon")
  Material iconItem = Material.BOOK;

  public boolean isEnabled() {
    return enabled;
  }

  public String getRawId() {
    return id;
  }

  public String getId() {
    return "LPCONTEXT_".concat(getRawId());
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getDefaultRank() {
    return defaultRank;
  }

  public Mode getLevel() {
    return level;
  }

  public List<String> getAllowedGamemodes() {
    return allowedGamemodes;
  }

  public Boolean getInvertAllowlist() {
    return invertAllowlist;
  }
}
