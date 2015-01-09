package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.bidirectional.GlobalMarkersPacket;
import hunternif.mc.atlas.network.bidirectional.MarkersPacket;
import hunternif.mc.atlas.network.client.MapDataPacket;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.network.client.TilesPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * A wrapper class for the SimpleNetworkWrapper
 *
 */
public class PacketDispatcher
{
	/** This isn't a strict limitation, there is quite a bit of margin left. */
	public static final int MAX_SIZE_BYTES = 32000;

	private static byte packetId = 0;

	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(AntiqueAtlasMod.CHANNEL);

	/** No need to allow instantiation */
	private PacketDispatcher() {}

	/**
	 *  Registers all packets and handlers - call this during {@link FMLPreInitializationEvent}
	 */
	public static void registerPackets() {
		// Bi-directional messages
		registerBiMessage(MarkersPacket.Handler.class, MarkersPacket.class);
		registerBiMessage(GlobalMarkersPacket.Handler.class, GlobalMarkersPacket.class);
		
		// Messages sent to CLIENT
		registerMessage(MapDataPacket.Handler.class, MapDataPacket.class, Side.CLIENT);
		registerMessage(TileNameIDPacket.Handler.class, TileNameIDPacket.class, Side.CLIENT);
		registerMessage(TilesPacket.Handler.class, TilesPacket.class, Side.CLIENT);
		
		// Messages sent to SERVER
	}

	/**
	 * Registers a message and message handler on the designated side;
	 * used for standard IMessage + IMessageHandler implementations
	 */
	private static final <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass, Side side) {
		PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
	}

	/**
	 * Registers a message and message handler on both sides; used mainly
	 * for standard IMessage + IMessageHandler implementations
	 */
	private static final <REQ extends IMessage, REPLY extends IMessage> void registerBiMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass) {
		// use same packetId for registration to both sides to avoid waste
		PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId, Side.CLIENT);
		PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, Side.SERVER);
	}

	/**
	 * Send this message to everyone.
	 * See {@link SimpleNetworkWrapper#sendToAll(IMessage)}
	 */
	public static void sendToAll(IMessage message) {
		PacketDispatcher.dispatcher.sendToAll(message);
	}
	
	/**
	 * Send this message to the specified player.
	 * See {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
	 */
	public static final void sendTo(IMessage message, EntityPlayerMP player) {
		PacketDispatcher.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone within a certain range of a point.
	 * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in the same dimension.
	 */
	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Sends a message to everyone within a certain range of the player provided.
	 */
	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PacketDispatcher.sendToAllAround(message, player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, range);
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
	 */
	public static final void sendToDimension(IMessage message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server.
	 * See {@link SimpleNetworkWrapper#sendToServer(IMessage)}
	 */
	public static final void sendToServer(IMessage message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
}
