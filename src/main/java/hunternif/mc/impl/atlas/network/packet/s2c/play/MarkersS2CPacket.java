package hunternif.mc.impl.atlas.network.packet.s2c.play;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Sends markers set via API from server to client.
 * Only one dimension per packet.
 * The markers in one packet are either all global or all local.
 * @author Hunternif
 * @author Haven King
 */
public class MarkersS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "marker", "info");

	private static final int GLOBAL = -1;

	int atlasID; 
	ResourceKey<Level> world; 
	ListMultimap<ResourceLocation, Marker.Precursor> markersByType;

	public MarkersS2CPacket(int atlasID, ResourceKey<Level> world, ListMultimap<ResourceLocation, Marker.Precursor> markersByType) {
		this.atlasID = atlasID;
		this.world = world;
		this.markersByType = markersByType;
	}

	public MarkersS2CPacket(int atlasID, ResourceKey<Level> world, Collection<Marker> markers) {
		this.atlasID = atlasID;
		this.world = world;
		ListMultimap<ResourceLocation, Marker.Precursor> markersByType = ArrayListMultimap.create();
		for (Marker marker : markers) {
			markersByType.put(marker.getType(), new Marker.Precursor(marker));
		}
		this.markersByType = markersByType;
	}

	public static void encode(final MarkersS2CPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.world.location());
		packetBuffer.writeVarInt(msg.markersByType.keySet().size());

		for (ResourceLocation type : msg.markersByType.keySet()) {
			packetBuffer.writeResourceLocation(type);
			List<Marker.Precursor> markerList = msg.markersByType.get(type);
			packetBuffer.writeVarInt(markerList.size());
			for (Marker.Precursor marker : markerList) {
				new Marker(type, msg.world, marker).write(packetBuffer);
			}
		}
	}

	public static MarkersS2CPacket decode(final FriendlyByteBuf packetBuffer) {
		int atlasID = packetBuffer.readVarInt();
		ResourceKey<Level> world = ResourceKey.create(Registry.DIMENSION_REGISTRY, packetBuffer.readResourceLocation());
		int typesLength = packetBuffer.readVarInt();

		ListMultimap<ResourceLocation, Marker.Precursor> markersByType = ArrayListMultimap.create();
		for (int i = 0; i < typesLength; ++i) {
			ResourceLocation type = packetBuffer.readResourceLocation();
			int markersLength = packetBuffer.readVarInt();
			for (int j = 0; j < markersLength; ++j) {
				markersByType.put(type, new Marker.Precursor(packetBuffer));
			}
		}

		return new MarkersS2CPacket(atlasID, world, markersByType);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(LocalPlayer player) {
		MarkersData markersData = atlasID == GLOBAL
				? AntiqueAtlasMod.globalMarkersData.getData()
						: AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getCommandSenderWorld());

		for (ResourceLocation type : markersByType.keys()) {
			MarkerType markerType = MarkerType.REGISTRY.get(type);
			for (Marker.Precursor precursor : markersByType.get(type)) {
				markersData.loadMarker(new Marker(MarkerType.REGISTRY.getKey(markerType), world, precursor));
			}
		}

		AntiqueAtlasModClient.getAtlasGUI().updateBookmarkerList();
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

}
