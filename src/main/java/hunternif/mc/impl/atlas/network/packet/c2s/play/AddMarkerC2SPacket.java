package hunternif.mc.impl.atlas.network.packet.c2s.play;

import java.util.function.Supplier;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 * @author Hunternif
 * @author Haven King
 */
public class AddMarkerC2SPacket extends C2SPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "add");

	int atlasID; 
	ResourceLocation markerType; 
	int x, z; 
	boolean visibleBeforeDiscovery;
	ITextComponent label;


	public AddMarkerC2SPacket(int atlasID, ResourceLocation markerType, int x, int z, boolean visibleBeforeDiscovery, ITextComponent label) {
		this.atlasID = atlasID;
		this.markerType = markerType;
		this.x = x;
		this.z = z;
		this.visibleBeforeDiscovery = visibleBeforeDiscovery;
		this.label = label;
	}

	public static void encode(final AddMarkerC2SPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.markerType);
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.z);
		packetBuffer.writeBoolean(msg.visibleBeforeDiscovery);
		packetBuffer.writeTextComponent(msg.label);
	}

	public static AddMarkerC2SPacket decode(final PacketBuffer packetBuffer) {
		return new AddMarkerC2SPacket(
				packetBuffer.readVarInt(), 
				packetBuffer.readResourceLocation(),
				packetBuffer.readVarInt(),
				packetBuffer.readVarInt(),
				packetBuffer.readBoolean(),
				packetBuffer.readTextComponent());
	}

	public static void handle(final AddMarkerC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			ServerPlayerEntity player = (ServerPlayerEntity) context.getSender();
			if (!AtlasAPI.getPlayerAtlases(player).contains(msg.atlasID)) {
				AntiqueAtlasMod.LOG.warn(
						"Player {} attempted to put marker into someone else's Atlas #{}}",
						player.getName(), msg.atlasID);
				return;
			}

			AtlasAPI.getMarkerAPI().putMarker(player.world, msg.visibleBeforeDiscovery, msg.atlasID, msg.markerType, msg.label, msg.x,msg.z);
		});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

//	public static void apply(PacketContext context, PacketBuffer buf) {
//		int atlasID = buf.readVarInt();
//		ResourceLocation markerType = buf.readResourceLocation();
//		int x = buf.readVarInt();
//		int z = buf.readVarInt();
//		boolean visibleBeforeDiscovery = buf.readBoolean();
//		ITextComponent label = buf.readTextComponent();
//
//		context.getTaskQueue().execute(() -> {
//			ServerPlayerEntity playerEntity = (ServerPlayerEntity) context.getPlayer();
//			if (!AtlasAPI.getPlayerAtlases(playerEntity).contains(atlasID)) {
//				AntiqueAtlasMod.LOG.warn(
//						"Player {} attempted to put marker into someone else's Atlas #{}}",
//						playerEntity.getName(), atlasID);
//				return;
//			}
//
//			if (playerEntity.getServer() != null) {
//				MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, playerEntity.getEntityWorld());
//				Marker marker = markersData.createAndSaveMarker(
//						MarkerType.REGISTRY.get(markerType),
//						context.getPlayer().getEntityWorld().getRegistryKey(),
//						x,
//						z,
//						visibleBeforeDiscovery,
//						label);
//
//				new MarkersS2CPacket(atlasID, context.getPlayer().getEntityWorld().getRegistryKey(), Collections.singleton(marker)).send(playerEntity.server);
//			}
//		});
//	}
}
