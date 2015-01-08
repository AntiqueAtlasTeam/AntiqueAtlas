package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * Base message handler class using SimpleNetworkWrapper / IMessage framework;
 * its sole purpose is to call the appropriate handling method for the current
 * side and to pass a valid player to that method.
 * 
 */
public abstract class AbstractMessageHandler<T extends IMessage> implements IMessageHandler <T, IMessage>
{
	/**
	 * Handle a message received on the client side
	 * @return a message to send back to the Server, or null if no reply is necessary
	 */
	@SideOnly(Side.CLIENT)
	public abstract IMessage handleClientMessage(EntityPlayer player, T msg, MessageContext ctx);

	/**
	 * Handle a message received on the server side
	 * @return a message to send back to the Client, or null if no reply is necessary
	 */
	public abstract IMessage handleServerMessage(EntityPlayer player, T msg, MessageContext ctx);

	@Override
	public IMessage onMessage(T msg, MessageContext ctx) {
		EntityPlayer player = AntiqueAtlasMod.proxy.getPlayerEntity(ctx);
		if (player == null) {
			AntiqueAtlasMod.logger.error("Unable to process " + msg.getClass().getSimpleName() + " on " + ctx.side.name() + ": player was NULL");
			return null;
		}
		if (ctx.side.isClient()) {
			return handleClientMessage(player, msg, ctx);
		} else {
			return handleServerMessage(player, msg, ctx);
		}
	}
}
