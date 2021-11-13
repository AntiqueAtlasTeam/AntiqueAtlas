package hunternif.mc.api.client;

import hunternif.mc.api.TileAPI;
import hunternif.mc.impl.atlas.client.TileRenderIterator;
import hunternif.mc.impl.atlas.util.Rect;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ClientTileAPI extends TileAPI {
    TileRenderIterator getTiles(Level world, int atlasID, Rect scope, int step);
}
