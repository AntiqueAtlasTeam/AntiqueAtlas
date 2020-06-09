package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileGroupsPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.Rect;
import hunternif.mc.atlas.util.ShortVec2;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

/** All tiles seen in dimension. Thread-safe (probably) */
public class DimensionData implements ITileStorage {
	public final AtlasData parent;
	public final RegistryKey<DimensionType> dimension;

	private int browsingX, browsingY;
	private double browsingZoom = 0.5;

	/**
	 * a map of chunks the player has seen. This map is thread-safe. CAREFUL!
	 * Don't modify chunk coordinates that are already put in the map!
	 * 
	 * Key is a ShortVec2 representing the gilegroup's position in units of TileGroup.CHUNK_STEP
	 */
	private final Map<ShortVec2, TileGroup> tileGroups = new ConcurrentHashMap<ShortVec2, TileGroup>(2, 0.75f, 2);

	/**
	 * Maps threads to the temporary key for thread-safe access to the tile map.
	 */
	private final Map<Thread, ShortVec2> thread2KeyMap = new ConcurrentHashMap<Thread, ShortVec2>(2, 0.75f, 2);

	/** Limits of explored area, in chunks. */
	private final Rect scope = new Rect();

	public DimensionData(AtlasData parent, RegistryKey<DimensionType> dimension) {
		this.parent = parent;
		this.dimension = dimension;
	}

	/**
	 * This function has to create a new map on each call since the packet rework
	 */
	public Map<ShortVec2, TileKind> getSeenChunks() {
		Map<ShortVec2, TileKind> chunks = new ConcurrentHashMap<ShortVec2, TileKind>(2, 0.75f, 2);
		TileKind t = null;
		for (Map.Entry<ShortVec2, TileGroup> entry: tileGroups.entrySet()){
			int basex = entry.getValue().getScope().minX;
			int basey = entry.getValue().getScope().minY;
			for (int x = basex; x < basex+TileGroup.CHUNK_STEP; x++){
				for (int y = basey; y < basey+TileGroup.CHUNK_STEP; y++){
					t = entry.getValue().getTile(x, y);
					if (t!=null){
						chunks.put(new ShortVec2(x, y), t);
					}
				}
			}
		}
		return chunks;
	}

	/** Set world coordinates that are in the center of the GUI. */
	public void setBrowsingPosition(int x, int y, double zoom) {
		this.browsingX = x;
		this.browsingY = y;
		this.browsingZoom = zoom;
		if (browsingZoom <= 0) {
			Log.warn("Setting map zoom to invalid value of %f", zoom);
			browsingZoom = AntiqueAtlasMod.CONFIG.userInterface.minScale;
		}
		parent.markDirty();
	}

	public int getBrowsingX() {
		return browsingX;
	}

	public int getBrowsingY() {
		return browsingY;
	}

	public double getBrowsingZoom() {
		return browsingZoom;
	}

	/** Temporary key for thread-safe access to the tile map. */
	private ShortVec2 getKey() {
		return thread2KeyMap.computeIfAbsent(Thread.currentThread(), k -> new ShortVec2(0, 0));
	}

	@Override
	public void setTile(int x, int y, TileKind tile) {
		ShortVec2 groupPos = getKey().set((int)Math.floor(x / (float) TileGroup.CHUNK_STEP),
				(int)Math.floor(y / (float) TileGroup.CHUNK_STEP));
		TileGroup tg = tileGroups.get(groupPos);
		if (tg == null) {
			tg = new TileGroup(groupPos.x * TileGroup.CHUNK_STEP, groupPos.y * TileGroup.CHUNK_STEP);
			tileGroups.put(groupPos, tg);
		}
		tg.setTile(x, y, tile);
		scope.extendTo(x, y);
		parent.markDirty();
	}

	/**Puts a tileGroup into this dimensionData, overwriting any previous stuff.*/
	public void putTileGroup(TileGroup t){
		ShortVec2 key = new ShortVec2(Math.floorDiv(t.scope.minX,TileGroup.CHUNK_STEP), Math.floorDiv(t.scope.minY,TileGroup.CHUNK_STEP));
		tileGroups.put(key, t);
		extendToTileGroup(t);
	}
	
	@Override
	public TileKind removeTile(int x, int y) {
		//TODO
		// since scope is not modified, I assume this was never really used
		// Tile oldTile = tileGroups.remove(getKey().set(x, y));
		// if (oldTile != null) parent.markDirty();
		// return oldTile;
		return getTile(x,y);
	}

	@Override
	public TileKind getTile(int x, int y) {
		ShortVec2 groupPos = getKey().set((int)Math.floor(x / (float) TileGroup.CHUNK_STEP),
				(int)Math.floor(y / (float) TileGroup.CHUNK_STEP));
		TileGroup tg = tileGroups.get(groupPos);
		if (tg == null) {
			return null;
		}
		return tg.getTile(x, y);
	}

	@Override
	public boolean hasTileAt(int x, int y) {
		return getTile(x,y)!=null;
	}

	@Override
	public Rect getScope() {
		return scope;
	}
	
	@Override
	public DimensionData clone() {
		//TODO
		DimensionData data = new DimensionData(parent, dimension);
		data.tileGroups.putAll(tileGroups);
		data.scope.set(scope);
		return data;
	}

	public void addData(DimensionData other){
		for (Entry<ShortVec2, TileGroup> e:other.tileGroups.entrySet()){
			TileGroup group = e.getValue();
			Rect s = group.getScope();
			for (int x = s.minX; x <= s.maxX; x++){
				for (int y = s.minY; y <= s.maxY; y++){
					TileKind tile = group.getTile(x, y);
					if (tile != null) setTile(x, y, tile);
				}
			}
		}
	}
	
	public ListTag writeToNBT() {
		ListTag tileGroupList = new ListTag();
		for (Entry<ShortVec2, TileGroup> entry : tileGroups.entrySet()) {
			CompoundTag newbie = new CompoundTag();
			entry.getValue().writeToNBT(newbie);
			tileGroupList.add(newbie);
		}
		return tileGroupList;
	}
	
	private void extendToTileGroup(TileGroup tg){
		for (int x = tg.scope.minX; x <= tg.scope.maxX; x++){
			for (int y = tg.scope.minY; y <= tg.scope.maxY; y++){
				if (tg.hasTileAt(x, y)){
					scope.extendTo(x, y);
				}
			}
		}
	}
	
	public void readFromNBT(ListTag me){
		if (me == null){
			return;
		}
		for (int d = 0; d < me.size(); d++) {
			CompoundTag tgTag = me.getCompound(d);
			TileGroup tg = new TileGroup(0, 0);
			tg.readFromNBT(tgTag);
			putTileGroup(tg);
		}
	}
	
	public void syncOnPlayer(int atlasID, PlayerEntity player){
		Log.info("Sending dimension #%s", dimension.toString());
		ArrayList<TileGroup> tgs = new ArrayList<TileGroup>(TileGroupsPacket.TILE_GROUPS_PER_PACKET);
		int count = 0;
		int total = 0;
		for (Entry<ShortVec2, TileGroup> t:tileGroups.entrySet()){
			tgs.add(t.getValue());
			count++;
			total++;
			if (count >= TileGroupsPacket.TILE_GROUPS_PER_PACKET){
				TileGroupsPacket p = new TileGroupsPacket(tgs, atlasID, dimension);
				PacketDispatcher.sendTo(p, (ServerPlayerEntity) player);
				tgs.clear();
				count = 0;
			}
		}
		if (count > 0){
			TileGroupsPacket p = new TileGroupsPacket(tgs, atlasID, dimension);
			PacketDispatcher.sendTo(p, (ServerPlayerEntity) player);
		}
		Log.info("Sent dimension #%s (%d tiles)", dimension.toString(), total);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DimensionData)) return false;
		DimensionData other = (DimensionData)obj;
		if (other.tileGroups.size() != tileGroups.size()) return false;
		for (ShortVec2 entry : tileGroups.keySet()) {
			if (!this.tileGroups.get(entry).equals(other.tileGroups.get(entry))) return false;
		}
		return true;
	}
}
