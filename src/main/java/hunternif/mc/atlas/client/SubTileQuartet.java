package hunternif.mc.atlas.client;

import hunternif.mc.atlas.client.SubTile.Part;
import hunternif.mc.atlas.util.ArrayIterator;

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
	private SubTile[] array = {new SubTile(Part.BOTTOM_RIGHT), new SubTile(Part.BOTTOM_LEFT),
							   new SubTile(Part.TOP_RIGHT), new SubTile(Part.TOP_LEFT)};
	
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
		return new ArrayIterator<SubTile>(array);
	}
}
