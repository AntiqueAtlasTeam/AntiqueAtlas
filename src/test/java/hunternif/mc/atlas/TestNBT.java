package hunternif.mc.atlas;


import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.AtlasDataHandler;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.core.TileGroup;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class TestNBT {
	
	class DummyAtlasData extends AtlasData{
		public DummyAtlasData(String s){
			super(s);
		}
	}
	
	/**Sample atlas data*/
	AtlasData ad = new DummyAtlasData("Test");
	/**Dimension 0 in {@link #ad}*/
	DimensionData dd;
	/**Equal to (but not a reference to) the tile group at (16,16) in {@link #dd}*/
	TileGroup tg = new TileGroup(16, 16);
	
	/**Creates a pre-packet-rework AtlasData NBT tag*/
	public void writeToNBTv2(AtlasData atlasdata, NBTTagCompound compound) {
		compound.setInteger(AtlasData.TAG_VERSION, 2);
		NBTTagList dimensionMapList = new NBTTagList();
		int dimension = 0;
		int key;
		Iterator<Integer> dimensionEntryKey = atlasdata.getVisitedDimensions().iterator();
		for (dimension = 0; dimension<atlasdata.getVisitedDimensions().size(); dimension++) {
			NBTTagCompound dimTag = new NBTTagCompound();
			key = dimensionEntryKey.next().intValue();
			dimTag.setInteger(AtlasData.TAG_DIMENSION_ID, key);
			DimensionData dimData = atlasdata.getDimensionData(key);
			Map<ShortVec2, Tile> seenChunks = dimData.getSeenChunks();
			int[] intArray = new int[seenChunks.size()*3];
			int i = 0;
			for (Entry<ShortVec2, Tile> entry : seenChunks.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue().biomeID;
			}
			dimTag.setIntArray(AtlasData.TAG_VISITED_CHUNKS, intArray);
			dimTag.setInteger(AtlasData.TAG_BROWSING_X, dimData.getBrowsingX());
			dimTag.setInteger(AtlasData.TAG_BROWSING_Y, dimData.getBrowsingY());
			dimTag.setInteger(AtlasData.TAG_BROWSING_ZOOM, (int)Math.round(dimData.getBrowsingZoom() * BrowsingPositionPacket.ZOOM_SCALE_FACTOR));
			dimensionMapList.appendTag(dimTag);
		}
		compound.setTag(AtlasData.TAG_DIMENSION_MAP_LIST, dimensionMapList);
	}
	
	@Before
	public void init() {
		ad.setTile(0, 16, 16, new Tile(0));
		ad.setTile(0, 17, 16, new Tile(1));
		ad.setTile(0, 18, 16, new Tile(2));
		ad.setTile(0, 16, 17, new Tile(3));
		ad.setTile(0, 31, 31, new Tile(4));
		dd = ad.getDimensionData(0);
		tg.setTile(16, 16, new Tile(0));
		tg.setTile(17, 16, new Tile(1));
		tg.setTile(18, 16, new Tile(2));
		tg.setTile(16, 17, new Tile(3));
		tg.setTile(31, 31, new Tile(4));
	}
	
	@Test
	public void testTileGroupBounds() {
		Log.setModID(AntiqueAtlasMod.ID);
		tg.setTile(15,15, new Tile(1));
		tg.setTile(32,32, new Tile(1));
		assertEquals(null, tg.getTile(15, 15));
		assertEquals(new Tile(0), tg.getTile(16, 16));
		assertEquals(new Tile(4), tg.getTile(31, 31));
		assertEquals(null, tg.getTile(32, 32));
	}
	
	@Test
	public void testNBT(){
		NBTTagCompound tagTG = new NBTTagCompound();
		tg.writeToNBT(tagTG);
		TileGroup tg2 = new TileGroup(0,0);
		tg2.readFromNBT(tagTG);
		assertEquals(tg, tg2);
		
		NBTTagList tagDD = dd.writeToNBT();
		DimensionData dd2 = new DimensionData(ad, 0);
		dd2.readFromNBT(tagDD);
 		
		NBTTagCompound tagAD = new NBTTagCompound();
		ad.writeToNBT(tagAD);
		AtlasData ad2 = new DummyAtlasData("test");
		ad2.readFromNBT(tagAD);
		assertEquals(ad, ad2);
	}
}
