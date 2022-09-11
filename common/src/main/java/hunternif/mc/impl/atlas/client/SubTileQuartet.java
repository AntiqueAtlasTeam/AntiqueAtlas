package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.util.ArrayIterator;

import java.util.Iterator;

/**
 * The 4 subtiles in a corner between 4 tiles, each subtile belonging to a
 * different tile. When the tiles are positioned as follows:
 * <pre>
 *  a b
 *  c d
 * </pre>
 * then the subtiles 0-1-2-3 belong to tiles a-b-c-d respectively.
 *
 * @author Hunternif
 */
public class SubTileQuartet implements Iterable<SubTile> {
    /*
     * 0 1
     * 2 3
     */
    private final SubTile[] array;

    public SubTileQuartet(SubTile a, SubTile b, SubTile c, SubTile d) {
        array = new SubTile[]{a, b, c, d};
    }

    public SubTile get(int i) {
        return array[i];
    }

    /**
     * Set the coordinates for the top left subtile, and the rest of them
     * have their coordinates updated respectively.
     */
    public void setCoords(int x, int y) {
        array[0].x = x;
        array[1].x = x + 1;
        array[2].x = x;
        array[3].x = x + 1;

        array[0].y = y;
        array[1].y = y;
        array[2].y = y + 1;
        array[3].y = y + 1;
    }

    @Override
    public Iterator<SubTile> iterator() {
        return new ArrayIterator<>(array);
    }

    /**
     * As SubTileQuartets aren't aligned with chunk boundaries, we'll just
     * delegating the coords down to the four SubTiles in this quartet
     */
    public void setChunkCoords(int chunkX, int chunkY, int step) {
        array[0].setChunkCoords(chunkX, chunkY, step);
        array[1].setChunkCoords(chunkX + step, chunkY, step);
        array[2].setChunkCoords(chunkX, chunkY + step, step);
        array[3].setChunkCoords(chunkX + step, chunkY + step, step);
    }
}
