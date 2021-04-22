package hunternif.mc.impl.atlas.client.texture;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents an icon texture for the atlas gui, such as the arrows and center on player.
 */
@OnlyIn(Dist.CLIENT)
public class IconTexture extends ATexture {
    public IconTexture(ResourceLocation texture) {
        super(texture);
    }

    @Override
    public int width() {
        return 16;
    }

    @Override
    public int height() {
        return 16;
    }
}