package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.bidirectional.PutBiomeTilePacket;
import hunternif.mc.atlas.network.client.*;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.network.server.RegisterTileIdPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;


/**
 * 
 * A wrapper class for the SimpleNetworkWrapper
 *
 */
public class PacketDispatcher
{
	private static byte packetId = 0;

	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(AntiqueAtlasMod.CHANNEL);

	/** No need to allow instantiation */
	private PacketDispatcher() {}

	/**
	 *  Registers all packets and handlers - call this during {@link FMLPreInitializationEvent}
	 */
	public static void registerPackets() {
		// Bi-directional messages
		registerMessage(DeleteMarkerPacket.class);
		registerMessage(PutBiomeTilePacket.class);

		// Messages sent to CLIENT
		registerMessage(MapDataPacket.class);
		registerMessage(TileGroupsPacket.class);
		registerMessage(DimensionUpdatePacket.class);
		registerMessage(TileNameIDPacket.class);
		registerMessage(TilesPacket.class);
		registerMessage(MarkersPacket.class);
		registerMessage(DeleteCustomGlobalTilePacket.class);

		// Messages sent to SERVER
		registerMessage(AddMarkerPacket.class);
		registerMessage(RegisterTileIdPacket.class);
		registerMessage(BrowsingPositionPacket.class);
	}

	/**
	 * Registers an {@link AbstractMessage} to the appropriate side(s)
	 */
	private static <T extends AbstractMessage<T>> void registerMessage(Class<T> clazz) {
		if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
		} else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		} else {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		}
	}

	/**
	 * Send this message to everyone.
	 * See {@link SimpleNetworkWrapper#sendToAll(AbstractMessage<?>)}
	 */
	public static void sendToAll(AbstractMessage<?> message) {
		PacketDispatcher.dispatcher.sendToAll(message);
	}

	/**
	 * Send this message to the specified player.
	 * See {@link SimpleNetworkWrapper#sendTo(AbstractMessage<?>, ServerPlayerEntity)}
	 */
	public static void sendTo(AbstractMessage<?> message, ServerPlayerEntity player) {
		PacketDispatcher.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone within a certain range of a point.
	 * See {@link SimpleNetworkWrapper#sendToAllAround(AbstractMessage<?>, NetworkRegistry.TargetPoint)}
	 */
	private static void sendToAllAround(AbstractMessage<?> message, NetworkRegistry.TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in the same dimension.
	 */
	private static void sendToAllAround(AbstractMessage<?> message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Sends a message to everyone within a certain range of the player provided.
	 */
	public static void sendToAllAround(AbstractMessage<?> message, PlayerEntity player, double range) {
		PacketDispatcher.sendToAllAround(message, player.getEntityWorld().s.getType(), player.x, player.y, player.z, range);
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 * See {@link SimpleNetworkWrapper#sendToDimension(AbstractMessage<?>, int)}
	 */
	public static void sendToDimension(AbstractMessage<?> message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server.
	 * See {@link SimpleNetworkWrapper#sendToServer(AbstractMessage<?>)}
	 */
	public static void sendToServer(AbstractMessage<?> message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
}
