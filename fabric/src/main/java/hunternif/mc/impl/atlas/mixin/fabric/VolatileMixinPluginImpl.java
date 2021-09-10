package hunternif.mc.impl.atlas.mixin.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class VolatileMixinPluginImpl
{
    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
