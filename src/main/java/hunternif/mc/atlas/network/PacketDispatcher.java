package hunternif.mc.atlas.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.bidirectional.PutTilePacket;
import hunternif.mc.atlas.network.client.*;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.network.server.RegisterTileIdPacket;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.*;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.impl.network.PacketRegistryImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;
import java.util.*;


/**
 * 
 * A wrapper class for the SimpleNetworkWrapper
 *
 */
public class PacketDispatcher {
	private static final BiMap<Identifier, Class<?>> packets = HashBiMap.create();
	private static final Set<Identifier> clientPackets = new HashSet<>();
	private static final Set<Identifier> serverPackets = new HashSet<>();

	/** No need to allow instantiation */
	private PacketDispatcher() {}

	/**
	 *  Registers all packets and handlers
	 */
	public static void registerPackets() {
		// Bi-directional messages
		registerMessage(new Identifier("antiqueatlas", "delete_marker"), DeleteMarkerPacket.class);
		registerMessage(new Identifier("antiqueatlas", "put_tile"), PutTilePacket.class);

		// Messages sent to CLIENT
		registerMessage(new Identifier("antiqueatlas", "map_data"), MapDataPacket.class);
		registerMessage(new Identifier("antiqueatlas", "tile_groups"), TileGroupsPacket.class);
		registerMessage(new Identifier("antiqueatlas", "dimension_update"), DimensionUpdatePacket.class);
		registerMessage(new Identifier("antiqueatlas", "tile_name_id"), TileNameIDPacket.class);
		registerMessage(new Identifier("antiqueatlas", "tiles"), TilesPacket.class);
		registerMessage(new Identifier("antiqueatlas", "markers"), MarkersPacket.class);
		registerMessage(new Identifier("antiqueatlas", "delete_custom_global_tile"), DeleteCustomGlobalTilePacket.class);

		// Messages sent to SERVER
		registerMessage(new Identifier("antiqueatlas", "add_marker"), AddMarkerPacket.class);
		registerMessage(new Identifier("antiqueatlas", "register_tile_id"), RegisterTileIdPacket.class);
		registerMessage(new Identifier("antiqueatlas", "browsing_position"), BrowsingPositionPacket.class);

		AntiqueAtlasMod.proxy.registerPackets(clientPackets, serverPackets, (id) -> ((context, buffer) -> {
			Class<?> c = packets.get(id);
			try {
				AbstractMessage<?> message = (AbstractMessage<?>) c.getDeclaredConstructor().newInstance();
				message.read(buffer);
				if (message.requiresMainThread()) {
					context.getTaskQueue().execute(() -> message.process(context.getPlayer(), context.getPacketEnvironment()));
				} else {
					message.process(context.getPlayer(), context.getPacketEnvironment());
				}
			} catch (Exception e) {
				AntiqueAtlasMod.logger.warn("Error receiving packet " + id + "!", e);
			}
		}));
	}

	private static PacketByteBuf toByteBuf(AbstractMessage<?> message) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		try {
			message.write(buf);
		} catch (IOException e) {
			AntiqueAtlasMod.logger.warn("Error sending packet", e);
		}
		return buf;
	}

	/**
	 * Registers an {@link AbstractMessage} to the appropriate side(s)
	 */
	private static <T extends AbstractMessage<T>> void registerMessage(Identifier id, Class<T> clazz) {
		packets.put(id, clazz);
		if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
			clientPackets.add(id);
		} else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			serverPackets.add(id);
		} else {
			clientPackets.add(id);
			serverPackets.add(id);
		}
	}

	/**
	 * Send this message to everyone.
	 */
	public static void sendToAll(AbstractMessage<?> message) {
		PlayerStream.all(AntiqueAtlasMod.proxy.getServer()).forEach(p -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,
				packets.inverse().get(message.getClass()),
				toByteBuf(message)
		));
	}

	/**
	 * Send this message to the specified player.
	 */
	public static void sendTo(AbstractMessage<?> message, ServerPlayerEntity player) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
				packets.inverse().get(message.getClass()),
				toByteBuf(message)
		);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in the same dimension.
	 */
	private static void sendToAllAround(AbstractMessage<?> message, DimensionType dimension, double x, double y, double z, double range) {
		PlayerStream.around(
				AntiqueAtlasMod.proxy.getServer().getWorld(dimension),
				new Vec3d(x, y, z),
				range
		).forEach(p -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,
				packets.inverse().get(message.getClass()),
				toByteBuf(message)
		));
	}

	/**
	 * Sends a message to everyone within a certain range of the player provided.
	 */
	public static void sendToAllAround(AbstractMessage<?> message, PlayerEntity player, double range) {
		PacketDispatcher.sendToAllAround(message, player.getEntityWorld().getDimension().getType(), player.x, player.y, player.z, range);
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 */
	public static void sendToDimension(AbstractMessage<?> message, DimensionType type) {
		PlayerStream.world(AntiqueAtlasMod.proxy.getServer().getWorld(type)).forEach(p -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,
				packets.inverse().get(message.getClass()),
				toByteBuf(message)
		));
	}

	/**
	 * Send this message to the server.
	 */
	@Environment(EnvType.CLIENT)
	public static void sendToServer(AbstractMessage<?> message) {
		ClientSidePacketRegistry.INSTANCE.sendToServer(
				packets.inverse().get(message.getClass()),
				toByteBuf(message)
		);
	}
}
