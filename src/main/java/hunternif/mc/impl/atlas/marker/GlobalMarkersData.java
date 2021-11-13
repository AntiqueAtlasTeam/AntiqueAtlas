package hunternif.mc.impl.atlas.marker;

import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.error.Mark;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/** Holds global markers, i.e. ones that appear in all atlases. */
public class GlobalMarkersData extends MarkersData {

	public GlobalMarkersData() {
	}

	@Override
	public Marker createAndSaveMarker(ResourceLocation type, ResourceKey<Level> world, int x, int y, boolean visibleAhead, Component label) {
		return super.createAndSaveMarker(type, world, x, y, visibleAhead, label).setGlobal(true);
	}

	public static GlobalMarkersData readNbt(CompoundTag compound) {
		GlobalMarkersData data = new GlobalMarkersData();
		doReadNbt(compound, data);
		return data;
	}

	@Override
	public Marker loadMarker(Marker marker) {
		return super.loadMarker(marker).setGlobal(true);
	}

	/** Send all data to the player in several packets. */
    void syncOnPlayer(ServerPlayer player) {
		syncOnPlayer(-1, player);
	}
}
