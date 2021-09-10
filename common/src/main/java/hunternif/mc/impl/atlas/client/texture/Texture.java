package hunternif.mc.impl.atlas.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

/**
 * A generic texture, which needs the size of the texture at construction time.
 */
@Environment(EnvType.CLIENT)
public class Texture extends ATexture {
    public final int width;
    public final int height;

    public Texture(Identifier texture, int width, int height, boolean autobind) {
        super(texture, autobind);
        this.width = width;
        this.height = height;
    }

    public Texture(Identifier texture, int width, int height) {
        this(texture, width, height, true);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
