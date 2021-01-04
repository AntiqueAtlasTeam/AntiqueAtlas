package hunternif.mc.impl.atlas.client;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * A quarter of a tile, containing the following information:
 * <ul>
 * <li><b>tile</b>, containing the texture file and the variation number</li>
 * <li><b>offset</b> from the top left corner to the appropriate sub-tile part
 * 		of the texture</li>
 * <li><b>x, y</b> coordinates of the subtile on the grid, measured in subtiles,
 * 		starting from (0,0) in the top left corner</li>
 * <li><b>shape</b> of the subtile</li>
 * <li>which <b>part</b> of the whole tile this subtile constitutes</li>
 * </ul>
 *
 * @author Hunternif
 */
public class SubTile {
    public Identifier tile;
    /**
     * coordinates of the subtile on the grid, measured in subtiles,
     * starting from (0,0) in the top left corner.
     */
    public int x, y;

    /**
     * The variationnumber of that tile, which is set in the TileRenderIterator
     */
    public int variationNumber;
    public Shape shape;
    public Part part;

    public SubTile(Part part) {
        this.part = part;
    }

    public static int generateVariationNumber(int chunkX, int chunkY, int step) {
        return (int) (MathHelper.hashCode(chunkX, chunkY, chunkX * chunkY) & 0x7FFFFFFF);
    }

    /**
     * Texture offset from to the respective subtile section, in subtiles.
     */
    public int getTextureU() {
        switch (shape) {
            case SINGLE_OBJECT:
                return part.u;
            case CONCAVE:
                return 2 + part.u;
            case VERTICAL:
            case CONVEX:
                return part.u * 3;
            case HORIZONTAL:
            case FULL:
                return 2 - part.u;
            default:
                return 0;
        }
    }

    /**
     * Texture offset from to the respective subtile section, in subtiles.
     */
    public int getTextureV() {
        switch (shape) {
            case SINGLE_OBJECT:
            case CONCAVE:
                return part.v;
            case CONVEX:
            case HORIZONTAL:
                return 2 + part.v * 3;
            case FULL:
            case VERTICAL:
                return 4 - part.v;
            default:
                return 0;
        }
    }

    public void setChunkCoords(int chunkX, int chunkY, int step) {
        variationNumber = generateVariationNumber(chunkX, chunkY, step);
    }

    public enum Shape {
        CONVEX, CONCAVE, HORIZONTAL, VERTICAL, FULL, SINGLE_OBJECT
    }

    public enum Part {
        TOP_LEFT(0, 0), TOP_RIGHT(1, 0), BOTTOM_LEFT(0, 1), BOTTOM_RIGHT(1, 1);
        /**
         * Texture offset from a whole-tile-section to the respective part, in subtiles.
         */
        final int u, v;

        Part(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
