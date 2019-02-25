package hunternif.mc.atlas.client;

import hunternif.mc.atlas.client.SubTile.Part;
import hunternif.mc.atlas.util.ArrayIterator;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;

/**
 * The 4 subtiles in a corner between 4 tiles, each subtile belonging to a
 * different tile. When the tiles are positioned as follows:
 * <pre>
 *  a b
 *  c d
 * </pre>
 * then the subtiles 0-1-2-3 belong to tiles a-b-c-d respectively.
 * @author Hunternif
 */
public class SubTileQuartet implements Iterable<SubTile> {
	/*
	 * 0 1
	 * 2 3
	 */
	private final SubTile[] array;
	private int variationNumber;
	
	public SubTileQuartet() {
		this(new SubTile(Part.BOTTOM_RIGHT), new SubTile(Part.BOTTOM_LEFT),
				   new SubTile(Part.TOP_RIGHT), new SubTile(Part.TOP_LEFT));
	}
	public SubTileQuartet(SubTile a, SubTile b, SubTile c, SubTile d) {
		array = new SubTile[]{a, b, c, d};
	}
	
	public SubTile get(int i) {
		return array[i];
	}

	/** Set the coordinates for the top left subtile, and the rest of them
	 * have their coordinates updated respectively. */
	public void setCoords(int x, int y) {
		array[0].x = x;
		array[0].y = y;
		array[1].x = x + 1;
		array[1].y = y;
		array[2].x = x;
		array[2].y = y + 1;
		array[3].x = x + 1;
		array[3].y = y + 1;
	}

	@Override
	public Iterator<SubTile> iterator() {
		return new ArrayIterator<>(array);
	}

	public void setChunkCoords(int chunkX, int chunkY, int step) {
		chunkX *= 2;
		chunkY *= 2;

		array[0].variationNumber = (int) MathHelper.hashCode(chunkX, chunkY, 0) & 0x7FFFFFFF;
		array[1].variationNumber = (int) MathHelper.hashCode(chunkX + step, chunkY, 0) & 0x7FFFFFFF;
		array[2].variationNumber = (int) MathHelper.hashCode(chunkX, chunkY + step, 0) & 0x7FFFFFFF;
		array[3].variationNumber = (int) MathHelper.hashCode(chunkX + step, chunkY + step, 0) & 0x7FFFFFFF;
	}
}
