package hunternif.mc.atlas.network;

import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.bidirectional.PutTilePacket;
import hunternif.mc.atlas.network.client.*;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.network.server.RegisterTileIdPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;


/**
 * A wrapper class for the SimpleNetworkWrapper
 */
public class PacketDispatcher {
//    private static final BiMap<ResourceLocation, Class<?>> packets = HashBiMap.create();
//    private static final BiMap<ResourceLocation, Class<?>> clientPackets = HashBiMap.create();
//    private static final BiMap<ResourceLocation, Class<?>> serverPackets = HashBiMap.create();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("antiqueatlas", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    /**
     * No need to allow instantiation
     */
    private PacketDispatcher() {
    }

    /**
     * Registers all packets and handlers
     */
//    public static void registerPackets() {
//        // Bi-directional messages
//        registerMessage(new ResourceLocation("antiqueatlas", "delete_marker"), DeleteMarkerPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "put_tile"), PutTilePacket.class);
//
//        // Messages sent to CLIENT
//        registerMessage(new ResourceLocation("antiqueatlas", "map_data"), MapDataPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "tile_groups"), TileGroupsPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "dimension_update"), DimensionUpdatePacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "tile_name_id"), TileNameIDPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "tiles"), TilesPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "markers"), MarkersPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "delete_custom_global_tile"), DeleteCustomGlobalTilePacket.class);
//
//        // Messages sent to SERVER
//        registerMessage(new ResourceLocation("antiqueatlas", "add_marker"), AddMarkerPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "register_tile_id"), RegisterTileIdPacket.class);
//        registerMessage(new ResourceLocation("antiqueatlas", "browsing_position"), BrowsingPositionPacket.class);

//        registerPackets(clientPackets, serverPackets, (id) -> ((context, buffer) -> {
//            Class<?> c = packets.get(id);
//            try {
//                AbstractMessage<?> message = (AbstractMessage<?>) c.getDeclaredConstructor().newInstance();
//                message.read(buffer);
//                if (message.requiresMainThread()) {
//                    context.getTaskQueue().execute(() -> message.process(context.getPlayer(), context.getPacketEnvironment()));
//                } else {
//                    message.process(context.getPlayer(), context.getPacketEnvironment());
//                }
//            } catch (Exception e) {
//                AntiqueAtlasMod.logger.warn("Error receiving packet " + id + "!", e);
//            }
//        }));
//    }

    public static void registerPackets() {
        registerPacketsClient();
        registerPacketsCommon();
        registerPacketsServer();
    }

    public static void registerPacketsClient() {
        INSTANCE.registerMessage(id++, DeleteCustomGlobalTilePacket.class, DeleteCustomGlobalTilePacket::write, DeleteCustomGlobalTilePacket::read, DeleteCustomGlobalTilePacket::process);
        INSTANCE.registerMessage(id++, DimensionUpdatePacket.class, DimensionUpdatePacket::write, DimensionUpdatePacket::read, DimensionUpdatePacket::process);
        INSTANCE.registerMessage(id++, MapDataPacket.class, MapDataPacket::write, MapDataPacket::read, MapDataPacket::process);
        INSTANCE.registerMessage(id++, MarkersPacket.class, MarkersPacket::write, MarkersPacket::read, MarkersPacket::process);
        INSTANCE.registerMessage(id++, TileGroupsPacket.class, TileGroupsPacket::write, TileGroupsPacket::read, TileGroupsPacket::process);
        INSTANCE.registerMessage(id++, TileNameIDPacket.class, TileNameIDPacket::write, TileNameIDPacket::read, TileNameIDPacket::process);
        INSTANCE.registerMessage(id++, TilesPacket.class, TilesPacket::write, TilesPacket::read, TilesPacket::process);
    }

    public static void registerPacketsCommon() {
        INSTANCE.registerMessage(id++, DeleteMarkerPacket.class, DeleteMarkerPacket::write, DeleteMarkerPacket::read, DeleteMarkerPacket::process);
        INSTANCE.registerMessage(id++, PutTilePacket.class, PutTilePacket::write, PutTilePacket::read, PutTilePacket::process);
    }

    public static void registerPacketsServer() {
        INSTANCE.registerMessage(id++, AddMarkerPacket.class, AddMarkerPacket::write, AddMarkerPacket::read, AddMarkerPacket::process);
        INSTANCE.registerMessage(id++, BrowsingPositionPacket.class, BrowsingPositionPacket::write, BrowsingPositionPacket::read, BrowsingPositionPacket::process);
        INSTANCE.registerMessage(id++, RegisterTileIdPacket.class, RegisterTileIdPacket::write, RegisterTileIdPacket::read, RegisterTileIdPacket::process);
    }

//    private static PacketBuffer toByteBuf(AbstractMessage<?> message) {
//        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
//        try {
//            message.write(buf);
//        } catch (IOException e) {
//            AntiqueAtlasMod.logger.warn("Error sending packet", e);
//        }
//        return buf;
//    }

    /**
     * Registers an {@link AbstractMessage} to the appropriate side(s)
     */
//    private static <T extends AbstractMessage<T>> void registerMessage(ResourceLocation id, Class<T> clazz) {
//        packets.put(id, clazz);
//        if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
//            clientPackets.put(id, clazz);
//        } else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
//            serverPackets.put(id, clazz);
//        } else {
//            clientPackets.put(id, clazz);
//            serverPackets.put(id, clazz);
//        }
//    }

    /**
     * Send this message to everyone.
     */
//    public static void sendToAll(MinecraftServer server, AbstractMessage<?> message) {
//        server.getPlayerList().getPlayers().forEach(p -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,
//                packets.inverse().get(message.getClass()),
//                toByteBuf(message)
//        ));
//        INSTANCE.send(PacketDistributor.ALL.noArg(), new MyMessage());
//    }
//
//    /**
//     * Send this message to the specified player.
//     */
//    public static void sendTo(AbstractMessage<?> message, ServerPlayerEntity player) {
//        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
//                packets.inverse().get(message.getClass()),
//                toByteBuf(message)
//        );
//    }
//
//    /**
//     * Sends a message to everyone within a certain range of the coordinates in the same dimension.
//     */
//    private static void sendToAllAround(AbstractMessage<?> message, World world, double x, double y, double z, double range) {
//        PlayerStream.around(
//                world,
//                new Vec3d(x, y, z),
//                range
//        ).forEach(p -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,
//                packets.inverse().get(message.getClass()),
//                toByteBuf(message)
//        ));
//    }
//
//    /**
//     * Sends a message to everyone within a certain range of the player provided.
//     */
//    public static void sendToAllAround(AbstractMessage<?> message, PlayerEntity player, double range) {
//        PacketDispatcher.sendToAllAround(message, player.getEntityWorld(), player.getX(), player.getY(), player.getZ(), range);
//    }
//
//    /**
//     * Send this message to everyone within the supplied dimension.
//     */
//    public static void sendToDimension(AbstractMessage<?> message, World world) {
//        PlayerStream.world(world).forEach(p -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(p,
//                packets.inverse().get(message.getClass()),
//                toByteBuf(message)
//        ));
//    }
//
//    /**
//     * Send this message to the server.
//     */
//    @OnlyIn(Dist.CLIENT)
//    public static void sendToServer(AbstractMessage<?> message) {
//        ClientSidePacketRegistry.INSTANCE.sendToServer(
//                packets.inverse().get(message.getClass()),
//                toByteBuf(message)
//        );
//    }
}
