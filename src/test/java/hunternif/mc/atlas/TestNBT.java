package hunternif.mc.atlas;


import hunternif.mc.atlas.core.*;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.dimension.DimensionType;
import org.junit.Before;

import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;

// TODO FABRIC: These tests won't run without Knot's access transformers, for now...
public class TestNBT {
	/**Dimension 0 in {@link #ad}*/
	DimensionData dd = new DimensionData(null, DimensionType.OVERWORLD);
	/**Equal to (but not a reference to) the tile group at (16,16) in {@link #dd}*/
	TileGroup tg = new TileGroup(16, 16);
	
	/**Creates a pre-packet-rework AtlasData NBT tag*/
	public void writeToNBTv2(AtlasData atlasdata, CompoundNBT compound) {
		compound.putInt(AtlasData.TAG_VERSION, 2);
		ListNBT dimensionMapList = new ListNBT();
		DimensionType key;
		for (DimensionType dimensionType : atlasdata.getVisitedDimensions()) {
			CompoundNBT dimTag = new CompoundNBT();
			key = dimensionType;
			dimTag.putInt(AtlasData.TAG_DIMENSION_ID, dimensionType.getId() + 1);
			DimensionData dimData = atlasdata.getDimensionData(key);
			Map<ShortVec2, TileKind> seenChunks = dimData.getSeenChunks();
			int[] intArray = new int[seenChunks.size()*3];
			int i = 0;
			for (Entry<ShortVec2, TileKind> entry : seenChunks.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue().getId();
			}
			dimTag.putIntArray(AtlasData.TAG_VISITED_CHUNKS, intArray);
			dimTag.putInt(AtlasData.TAG_BROWSING_X, dimData.getBrowsingX());
			dimTag.putInt(AtlasData.TAG_BROWSING_Y, dimData.getBrowsingY());
			dimTag.putInt(AtlasData.TAG_BROWSING_ZOOM, (int)Math.round(dimData.getBrowsingZoom() * BrowsingPositionPacket.ZOOM_SCALE_FACTOR));
			dimensionMapList.add(dimTag);
		}
		compound.put(AtlasData.TAG_DIMENSION_MAP_LIST, dimensionMapList);
	}
	
	@Before
	public void init() {
		tg.setTile(16, 16, TileKindFactory.get(0));
		tg.setTile(17, 16, TileKindFactory.get(1));
		tg.setTile(18, 16, TileKindFactory.get(2));
		tg.setTile(16, 17, TileKindFactory.get(3));
		tg.setTile(31, 31, TileKindFactory.get(4));
	}
	
	// @Test
	public void testTileGroupBounds() {
		tg.setTile(15,15, TileKindFactory.get(1));
		tg.setTile(32,32, TileKindFactory.get(1));
		assertEquals(null, tg.getTile(15, 15));
		assertEquals(TileKindFactory.get(0), tg.getTile(16, 16));
		assertEquals(TileKindFactory.get(4), tg.getTile(31, 31));
		assertEquals(null, tg.getTile(32, 32));
	}
	
	// @Test
	public void testNBT(){
		CompoundNBT tagTG = new CompoundNBT();
		tg.writeToNBT(tagTG);
		TileGroup tg2 = new TileGroup(0,0);
		tg2.readFromNBT(tagTG);
		assertEquals(tg, tg2);
		
		ListNBT tagDD = dd.writeToNBT();
		DimensionData dd2 = new DimensionData(null, DimensionType.OVERWORLD);
		dd2.readFromNBT(tagDD);
	}
}
