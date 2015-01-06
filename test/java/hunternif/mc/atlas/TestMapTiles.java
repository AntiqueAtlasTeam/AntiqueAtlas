package hunternif.mc.atlas;

import hunternif.mc.atlas.client.MapTileStitcher;
import hunternif.mc.atlas.core.BiomeTextureMap;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMapTiles {
	private static class BlindStitcher extends MapTileStitcher {
		@Override
		public boolean shouldStitch(int ... biomeIDs) {
			return true;
		}
	}
	
	private MapTileStitcher stitcher = new BlindStitcher();
	private Map<ShortVec2, Tile> tiles;
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
		tiles = new HashMap<ShortVec2, Tile>();
		tile1 = new Tile(-1);
		tile2 = new Tile(-1);
		tile3 = new Tile(-1);
		tile4 = new Tile(-1);
	}
	
	@Test
	public void testJoinRight() {
		ShortVec2 coords1 = new ShortVec2(0, 0);
		tiles.put(coords1, tile1);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords1), tile1);
		ShortVec2 coords2 = new ShortVec2(1, 0);
		tiles.put(coords2, tile2);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords2), tile2);
		Assert.assertEquals(Tile.HORIZONTAL, tile1.topRight);
		Assert.assertEquals(Tile.HORIZONTAL, tile1.bottomRight);
		Assert.assertEquals(Tile.HORIZONTAL, tile2.topLeft);
		Assert.assertEquals(Tile.HORIZONTAL, tile2.bottomLeft);
	}
	
	@Test
	public void testJoinLeft() {
		ShortVec2 coords2 = new ShortVec2(1, 0);
		tiles.put(coords2, tile2);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords2), tile2);
		ShortVec2 coords1 = new ShortVec2(0, 0);
		tiles.put(coords1, tile1);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords1), tile1);
		Assert.assertEquals(Tile.HORIZONTAL, tile1.topRight);
		Assert.assertEquals(Tile.HORIZONTAL, tile1.bottomRight);
		Assert.assertEquals(Tile.HORIZONTAL, tile2.topLeft);
		Assert.assertEquals(Tile.HORIZONTAL, tile2.bottomLeft);
	}
	
	@Test
	public void testJoinDown() {
		ShortVec2 coords1 = new ShortVec2(0, 0);
		tiles.put(coords1, tile1);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords1), tile1);
		ShortVec2 coords2 = new ShortVec2(0, 1);
		tiles.put(coords2, tile2);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords2), tile2);
		Assert.assertEquals(Tile.VERTICAL, tile1.bottomLeft);
		Assert.assertEquals(Tile.VERTICAL, tile1.bottomRight);
		Assert.assertEquals(Tile.VERTICAL, tile2.topLeft);
		Assert.assertEquals(Tile.VERTICAL, tile2.topRight);
	}
	
	@Test
	public void testJoinUp() {
		ShortVec2 coords2 = new ShortVec2(0, 1);
		tiles.put(coords2, tile2);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords2), tile2);
		ShortVec2 coords1 = new ShortVec2(0, 0);
		tiles.put(coords1, tile1);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords1), tile1);
		Assert.assertEquals(Tile.VERTICAL, tile1.bottomLeft);
		Assert.assertEquals(Tile.VERTICAL, tile1.bottomRight);
		Assert.assertEquals(Tile.VERTICAL, tile2.topLeft);
		Assert.assertEquals(Tile.VERTICAL, tile2.topRight);
	}
	
	@Test
	public void testJoinSquare() {
		testJoinDown();
		ShortVec2 coords3 = new ShortVec2(1, 0);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.HORIZONTAL, tile3.topLeft);
		Assert.assertEquals(Tile.HORIZONTAL, tile3.bottomLeft);
		Assert.assertEquals(Tile.HORIZONTAL, tile1.topRight);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomRight);
		
		ShortVec2 coords4 = new ShortVec2(1, 1);
		tiles.put(coords4, tile4);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords4), tile4);
		Assert.assertEquals(Tile.FULL, tile1.bottomRight);
		Assert.assertEquals(Tile.FULL, tile2.topRight);
		Assert.assertEquals(Tile.FULL, tile3.bottomLeft);
		Assert.assertEquals(Tile.FULL, tile4.topLeft);
	}
	
	@Test
	public void testCornerTR() {
		testJoinDown();
		ShortVec2 coords3 = new ShortVec2(-1, 0);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomLeft);
	}
	@Test
	public void testCornerTR2() {
		testJoinUp();
		ShortVec2 coords3 = new ShortVec2(-1, 0);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomLeft);
	}
	@Test
	public void testCornerTR3() {
		testJoinLeft();
		ShortVec2 coords3 = new ShortVec2(1, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.bottomLeft);
	}
	@Test
	public void testCornerTR4() {
		testJoinRight();
		ShortVec2 coords3 = new ShortVec2(1, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.bottomLeft);
	}
	
	@Test
	public void testCornerBR() {
		testJoinDown();
		ShortVec2 coords3 = new ShortVec2(-1, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.topLeft);
	}
	@Test
	public void testCornerBR2() {
		testJoinUp();
		ShortVec2 coords3 = new ShortVec2(-1, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.topLeft);
	}
	@Test
	public void testCornerBR3() {
		testJoinRight();
		ShortVec2 coords3 = new ShortVec2(1, -1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.topLeft);
	}
	@Test
	public void testCornerBR4() {
		testJoinLeft();
		ShortVec2 coords3 = new ShortVec2(1, -1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.topLeft);
	}
	
	@Test
	public void testCornerTL() {
		testJoinDown();
		ShortVec2 coords3 = new ShortVec2(1, 0);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomRight);
	}
	@Test
	public void testCornerTL2() {
		testJoinUp();
		ShortVec2 coords3 = new ShortVec2(1, 0);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomRight);
	}
	@Test
	public void testCornerTL3() {
		testJoinLeft();
		ShortVec2 coords3 = new ShortVec2(0, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomRight);
	}
	@Test
	public void testCornerTL4() {
		testJoinRight();
		ShortVec2 coords3 = new ShortVec2(0, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.bottomRight);
	}
	
	@Test
	public void testCornerBL() {
		testJoinDown();
		ShortVec2 coords3 = new ShortVec2(1, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.topRight);
	}
	@Test
	public void testCornerBL2() {
		testJoinUp();
		ShortVec2 coords3 = new ShortVec2(1, 1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile2.topRight);
	}
	@Test
	public void testCornerBL3() {
		testJoinLeft();
		ShortVec2 coords3 = new ShortVec2(0, -1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.topRight);
	}
	@Test
	public void testCornerBL4() {
		testJoinRight();
		ShortVec2 coords3 = new ShortVec2(0, -1);
		tiles.put(coords3, tile3);
		stitcher.stitchAdjacentTiles(tiles, new ShortVec2(coords3), tile3);
		Assert.assertEquals(Tile.CONCAVE, tile1.topRight);
	}
}
