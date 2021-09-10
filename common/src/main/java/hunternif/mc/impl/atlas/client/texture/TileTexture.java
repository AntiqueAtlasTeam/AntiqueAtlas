package hunternif.mc.impl.atlas.client.texture;

import hunternif.mc.impl.atlas.client.SubTile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * A specialized class for textures used as tiles in the atlas map.
 * It has a special method to draw SubTile instances give a size of the map.
 * By default, TileTextures DO NOT bind the texture. This is on purpose to allow
 * the performance optimization shown in the SetTileRenderer.
 */
@Environment(EnvType.CLIENT)
public class TileTexture extends ATexture {
    public TileTexture(Identifier texture) {
        super(texture, false);
    }

    @Override
    public int width() {
        return 32;
    }

    @Override
    public int height() {
        return 48;
    }

    public void drawSubTile(MatrixStack matrices, SubTile subtile, int tileHalfSize) {
        draw(matrices, subtile.x * tileHalfSize, subtile.y * tileHalfSize, tileHalfSize, tileHalfSize, subtile.getTextureU() * 8, subtile.getTextureV() * 8, 8, 8);
    }
}
