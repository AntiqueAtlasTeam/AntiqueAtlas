package hunternif.mc.atlas.registry;

import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import net.minecraft.util.ResourceLocation;

public interface IRegistryEntry
{
    void setRegistryName(ResourceLocation name);
    
    ResourceLocation getRegistryName();
    
    class Impl implements IRegistryEntry
    {
    	private ResourceLocation registryName = null;

        public final void setRegistryName(String name)
        {
            int index = name.lastIndexOf(':');
            String prefix = index == -1 ? "" : name.substring(0, index);
            name = index == -1 ? name : name.substring(index + 1);
            if(index == -1) {
            	ModContainer mc = Loader.instance().activeModContainer();
            	prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
            }
            this.registryName = new ResourceLocation(prefix, name);
        }

        //Helper functions
        public final void setRegistryName(ResourceLocation name){ setRegistryName(name.toString()); }
        public final void setRegistryName(String modID, String name){ setRegistryName(modID + ":" + name); }

        public final ResourceLocation getRegistryName()
        {
            return registryName != null ? registryName : null;
        }
    }
}