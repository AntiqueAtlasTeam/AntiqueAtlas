package hunternif.mc.atlas;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.core.TileGroup;
import net.minecraft.nbt.NBTTagCompound;

public class TestTileGroup {

	TileGroup tg = new TileGroup("", 16, 16);
	
	@Before
	public void init() {
		tg.setTile(16, 16, new Tile(0));
		tg.setTile(17, 16, new Tile(1));
		tg.setTile(18, 16, new Tile(2));
		tg.setTile(16, 17, new Tile(3));
		tg.setTile(31, 31, new Tile(4));
	}
	
	@Test
	public void testBounds() {
		tg.setTile(15,15, new Tile(1));
		tg.setTile(32,32, new Tile(1));
		assertEquals(null, tg.getTile(15, 15));
		assertEquals(new Tile(0), tg.getTile(16, 16));
		assertEquals(new Tile(4), tg.getTile(31, 31));
		assertEquals(null, tg.getTile(32, 32));
	}
	
	@Test
	public void testNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		tg.writeToNBT(tag);
		TileGroup tg2 = new TileGroup("",0,0);
		tg2.readFromNBT(tag);
		assertEquals(tg, tg2);
	}
}
