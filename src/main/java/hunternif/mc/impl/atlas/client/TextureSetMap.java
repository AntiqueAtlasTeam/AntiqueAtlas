package hunternif.mc.impl.atlas.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Maps texture sets to their names.
 *
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class TextureSetMap {
    private static final TextureSetMap INSTANCE = new TextureSetMap();

    public static TextureSetMap instance() {
        return INSTANCE;
    }

    private final Map<Identifier, TextureSet> map = new HashMap<>();

    public void register(TextureSet set) {
        map.put(set.name, set);
    }

    public TextureSet getByName(Identifier name) {
        return map.get(name);
    }

    static public boolean isRegistered(Identifier name) {
        return INSTANCE.map.containsKey(name);
    }
}
