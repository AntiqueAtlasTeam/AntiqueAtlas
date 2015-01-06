package hunternif.mc.atlas;

import static org.junit.Assert.*;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TileRenderIterator;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.Tile;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTileStitching {
	private DimensionData storage;
	private TileRenderIterator iter;
	private Tile tile1;
	private Tile tile2;
	private Tile tile3;
	private Tile tile4;
	
	@BeforeClass
	public static void setup() {
		BiomeTextureMap.instance().setTexture(-1);
	}
	
	@Before
	public void init() {
		storage = new DimensionData(0);
		iter = new TileRenderIterator(storage);
		tile1 = new Tile(-1);
		tile2 = new Tile(-1);
		tile3 = new Tile(-1);
		tile4 = new Tile(-1);
	}
	
	//TODO: test the stitching

}
