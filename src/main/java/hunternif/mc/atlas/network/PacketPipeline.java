package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.ZipUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Packet pipeline class. Directs all registered packet data to be handled by the packets themselves.
 * @author sirgingalot
 * some code from: cpw
 */
@ChannelHandler.Sharable
public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, ModPacket> {

	private EnumMap<Side, FMLEmbeddedChannel>		  channels;
	private LinkedList<Class<? extends ModPacket>> packets		   = new LinkedList<Class<? extends ModPacket>>();
	private boolean									 isPostInitialized = false;

	/**
	 * Register your packet with the pipeline. Discriminators are automatically set.
	 *
	 * @param clazz the class to register
	 *
	 * @return whether registration was successful. Failure may occur if 256 packets
	 * have been registered or if the registry already contains this packet.
	 */
	public boolean registerPacket(Class<? extends ModPacket> clazz) {
		if (this.packets.size() > 256) {
			AntiqueAtlasMod.logger.error("Too many packets registered");
			return false;
		}

		if (this.packets.contains(clazz)) {
			AntiqueAtlasMod.logger.warn("Packet already registered: " + clazz.getName());
			return false;
		}

		if (this.isPostInitialized) {
			AntiqueAtlasMod.logger.error("Attempted to register packet in post-init");
			return false;
		}

		this.packets.add(clazz);
		return true;
	}

	// In line encoding of the packet, including discriminator setting
	@Override
	protected void encode(ChannelHandlerContext ctx, ModPacket msg, List<Object> out) throws Exception {
		ByteBuf buffer = Unpooled.buffer();
		Class<? extends ModPacket> clazz = msg.getClass();
		if (!this.packets.contains(msg.getClass())) {
			throw new NullPointerException("No Packet Registered for: " + msg.getClass().getCanonicalName());
		}

		byte discriminator = (byte) this.packets.indexOf(clazz);
		buffer.writeByte(discriminator);
		
		// Compressing:
		if (msg.isCompressed()) {
			ByteBuf tempBuf = Unpooled.buffer();
			msg.encodeInto(ctx, tempBuf);
			byte[] data = new byte[tempBuf.readableBytes()];
			tempBuf.readBytes(data);
			byte[] zipData = ZipUtil.compressByteArray(data);
			buffer.writeBytes(zipData);
		} else {
			msg.encodeInto(ctx, buffer);
		}
		
		FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(proxyPacket);
	}

	// In line decoding and handling of the packet
	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
		ByteBuf payload = msg.payload();
		byte discriminator = payload.readByte();
		Class<? extends ModPacket> clazz = this.packets.get(discriminator);
		if (clazz == null) {
			throw new NullPointerException("No packet registered for discriminator: " + discriminator);
		}

		ModPacket pkt = clazz.newInstance();
		if (pkt.isCompressed()) {
			ByteBuf tempBuf = payload.slice();
			byte[] zipData = new byte[tempBuf.readableBytes()];
			tempBuf.readBytes(zipData);
			tempBuf = Unpooled.wrappedBuffer(ZipUtil.decompressByteArray(zipData));
			pkt.decodeInto(ctx, tempBuf);
		} else {
			pkt.decodeInto(ctx, payload.slice());
		}

		EntityPlayer player;
		switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				player = this.getClientPlayer();
				pkt.handleClientSide(player);
				break;

			case SERVER:
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				player = ((NetHandlerPlayServer) netHandler).playerEntity;
				pkt.handleServerSide(player);
				break;

			default:
		}

		out.add(pkt);
	}

	/** Method to call from FMLInitializationEvent. */
	public void initialize() {
		this.channels = NetworkRegistry.INSTANCE.newChannel(AntiqueAtlasMod.CHANNEL, this);
	}

	/**
	 * Method to call from FMLPostInitializationEvent. Ensures that packet
	 * discriminators are common between server and client by using logical sorting.
	 */
	public void postInitialize() {
		if (this.isPostInitialized) {
			return;
		}

		this.isPostInitialized = true;
		Collections.sort(this.packets, new Comparator<Class<? extends ModPacket>>() {
			@Override
			public int compare(Class<? extends ModPacket> clazz1, Class<? extends ModPacket> clazz2) {
				int com = String.CASE_INSENSITIVE_ORDER.compare(clazz1.getCanonicalName(), clazz2.getCanonicalName());
				if (com == 0) {
					com = clazz1.getCanonicalName().compareTo(clazz2.getCanonicalName());
				}

				return com;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	/**
	 * Send this message to everyone.
	 * <p/>
	 * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param packet The message to send
	 */
	public void sendToAll(ModPacket packet) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Send this message to the specified player.
	 * <p/>
	 * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param packet The message to send
	 * @param player  The player to send it to
	 */
	public void sendTo(ModPacket packet, EntityPlayer player) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Send this message to everyone within a certain range of a point.
	 * <p/>
	 * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param packet The message to send
	 * @param point   The {@link cpw.mods.fml.common.network.NetworkRegistry.TargetPoint} around which to send
	 */
	public void sendToAllAround(ModPacket packet, NetworkRegistry.TargetPoint point) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 * <p/>
	 * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param packet	 The message to send
	 * @param dimensionId The dimension id to target
	 */
	public void sendToDimension(ModPacket packet, int dimensionId) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Send this message to the server.
	 * <p/>
	 * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
	 *
	 * @param packet The message to send
	 */
	public void sendToServer(ModPacket packet) {
		this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		this.channels.get(Side.CLIENT).writeAndFlush(packet);
	}
	
	/**
	 * Send this message to every player in the world.
	 * @param packet
	 * @param world
	 */
	public void sendToWorld(ModPacket packet, World world) {
		if (world.isRemote) return;
		for (Object playerObj : world.playerEntities) {
			sendTo(packet, (EntityPlayer) playerObj);
		}
	}
}