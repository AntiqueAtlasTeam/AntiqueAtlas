package hunternif.mc.impl.atlas;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AntiqueAtlasMod.ID)
public class AntiqueAtlasModForge
{
    public AntiqueAtlasModForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(AntiqueAtlasMod.ID,
                FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initializeClient);

        MinecraftForge.EVENT_BUS.register(this);
        AntiqueAtlasMod.init();
    }

    /**
     * Despite what the events name might suggest, this event can be used for more than registering
     * particle renders as it's called in the {@link net.minecraft.client.MinecraftClient} constructor.
     * Thus here we use it to do all client stuff
     *
     * @param event
     */
    public void initializeClient(ParticleFactoryRegisterEvent event)
    {
        AntiqueAtlasModClient.init();
    }
}
