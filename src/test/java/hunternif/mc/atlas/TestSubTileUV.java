package hunternif.mc.atlas;

import hunternif.mc.atlas.client.SubTile;
import hunternif.mc.atlas.client.SubTile.Part;
import hunternif.mc.atlas.client.SubTile.Shape;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test that texture UV coordinates are returned correctly pased on subtile's
 * shape and part.
 * @author Hunternif
 */
public class TestSubTileUV {
	private SubTile s = new SubTile(null);
	
	@Test
	public void testSingleObject() {
		s.shape = Shape.SINGLE_OBJECT;
		
		s.part = Part.TOP_LEFT;
		assertEquals(0, s.getTextureU());
		assertEquals(0, s.getTextureV());
		
		s.part = Part.TOP_RIGHT;
		assertEquals(1, s.getTextureU());
		assertEquals(0, s.getTextureV());
		
		s.part = Part.BOTTOM_LEFT;
		assertEquals(0, s.getTextureU());
		assertEquals(1, s.getTextureV());
		
		s.part = Part.BOTTOM_RIGHT;
		assertEquals(1, s.getTextureU());
		assertEquals(1, s.getTextureV());
	}
	
	@Test
	public void testConcave() {
		s.shape = Shape.CONCAVE;
		
		s.part = Part.TOP_LEFT;
		assertEquals(2, s.getTextureU());
		assertEquals(0, s.getTextureV());
		
		s.part = Part.TOP_RIGHT;
		assertEquals(3, s.getTextureU());
		assertEquals(0, s.getTextureV());
		
		s.part = Part.BOTTOM_LEFT;
		assertEquals(2, s.getTextureU());
		assertEquals(1, s.getTextureV());
		
		s.part = Part.BOTTOM_RIGHT;
		assertEquals(3, s.getTextureU());
		assertEquals(1, s.getTextureV());
	}
	
	@Test
	public void testConvex() {
		s.shape = Shape.CONVEX;
		
		s.part = Part.TOP_LEFT;
		assertEquals(0, s.getTextureU());
		assertEquals(2, s.getTextureV());
		
		s.part = Part.TOP_RIGHT;
		assertEquals(3, s.getTextureU());
		assertEquals(2, s.getTextureV());
		
		s.part = Part.BOTTOM_LEFT;
		assertEquals(0, s.getTextureU());
		assertEquals(5, s.getTextureV());
		
		s.part = Part.BOTTOM_RIGHT;
		assertEquals(3, s.getTextureU());
		assertEquals(5, s.getTextureV());
	}
	
	@Test
	public void testHorizontal() {
		s.shape = Shape.HORIZONTAL;
		
		s.part = Part.TOP_LEFT;
		assertEquals(2, s.getTextureU());
		assertEquals(2, s.getTextureV());
		
		s.part = Part.TOP_RIGHT;
		assertEquals(1, s.getTextureU());
		assertEquals(2, s.getTextureV());
		
		s.part = Part.BOTTOM_LEFT;
		assertEquals(2, s.getTextureU());
		assertEquals(5, s.getTextureV());
		
		s.part = Part.BOTTOM_RIGHT;
		assertEquals(1, s.getTextureU());
		assertEquals(5, s.getTextureV());
	}
	
	@Test
	public void testVertical() {
		s.shape = Shape.VERTICAL;
		
		s.part = Part.TOP_LEFT;
		assertEquals(0, s.getTextureU());
		assertEquals(4, s.getTextureV());
		
		s.part = Part.TOP_RIGHT;
		assertEquals(3, s.getTextureU());
		assertEquals(4, s.getTextureV());
		
		s.part = Part.BOTTOM_LEFT;
		assertEquals(0, s.getTextureU());
		assertEquals(3, s.getTextureV());
		
		s.part = Part.BOTTOM_RIGHT;
		assertEquals(3, s.getTextureU());
		assertEquals(3, s.getTextureV());
	}
	
	@Test
	public void testFull() {
		s.shape = Shape.FULL;
		
		s.part = Part.TOP_LEFT;
		assertEquals(2, s.getTextureU());
		assertEquals(4, s.getTextureV());
		
		s.part = Part.TOP_RIGHT;
		assertEquals(1, s.getTextureU());
		assertEquals(4, s.getTextureV());
		
		s.part = Part.BOTTOM_LEFT;
		assertEquals(2, s.getTextureU());
		assertEquals(3, s.getTextureV());
		
		s.part = Part.BOTTOM_RIGHT;
		assertEquals(1, s.getTextureU());
		assertEquals(3, s.getTextureV());
	}
}
