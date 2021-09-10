package hunternif.mc.impl.atlas.mixin.forge;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class VolatileMixinPluginImpl
{
    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }
}
