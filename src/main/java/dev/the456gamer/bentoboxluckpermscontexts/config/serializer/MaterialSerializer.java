package dev.the456gamer.bentoboxluckpermscontexts.config.serializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

public class MaterialSerializer extends ScalarSerializer<Material> {

  public MaterialSerializer() {
    super(Material.class);
  }

  @Override
  public Material deserialize(Type type, Object obj) throws SerializationException {
    if (obj instanceof String string) {
      Material matched = Material.matchMaterial(string);
      if (matched == null) {
        throw new SerializationException(string + "is not valid for " + type);
      }
      return matched;
    }

    throw new SerializationException(obj + "is not valid for " + type);
  }

  @Override
  protected Object serialize(Material item, Predicate<Class<?>> typeSupported) {
    return item.key().asString();
  }
}
