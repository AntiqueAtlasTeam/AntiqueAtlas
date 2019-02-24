/*
    Copyright (C) <2014> <coolAlias>

    This file is part of coolAlias' Zelda Sword Skills Minecraft Mod; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package hunternif.mc.atlas.network;

import com.google.common.base.Throwables;
import hunternif.mc.atlas.AntiqueAtlasMod;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;
import java.io.IOException;

/**
 *
 * A wrapper much like the vanilla packet class, allowing use of PacketBuffer's many
 * useful methods as well as implementing a final version of IMessageHandler which
 * calls {@link #process(PlayerEntity, EnvType)} on each received message, letting the
 * message handle itself rather than having to add an extra class in each one.
 *
 */
public abstract class AbstractMessage<T extends AbstractMessage<T>>
{
	/**
	 * Some PacketBuffer methods throw IOException - default handling propagates the exception.
	 * If an IOException is expected but should not be fatal, handle it within this method.
	 */
	protected abstract void read(PacketByteBuf buffer) throws IOException;

	/**
	 * Some PacketBuffer methods throw IOException - default handling propagates the exception.
	 * If an IOException is expected but should not be fatal, handle it within this method.
	 */
	protected abstract void write(PacketByteBuf buffer) throws IOException;

	/**
	 * Called on whichever side the message is received;
	 * for bidirectional packets, be sure to check side
	 */
	protected abstract void process(PlayerEntity player, EnvType side);

	/**
	 * If message is sent to the wrong side, an exception will be thrown during handling
	 * @return True if the message is allowed to be handled on the given side
	 */
	boolean isValidOnSide(EnvType side) {
		return true;
	}

	/**
	 * Whether this message requires the main thread to be processed (i.e. it
	 * requires that the world, player, and other objects are in a valid state).
	 */
	boolean requiresMainThread() {
		return true;
	}

	public void fromBytes(ByteBuf buffer) {
		try {
			read(new PacketByteBuf(buffer));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void toBytes(ByteBuf buffer) {
		try {
			write(new PacketByteBuf(buffer));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO FABRIC
	/*
	@Override
	public final IMessage onMessage(T msg, MessageContext ctx) {
		if (!msg.isValidOnSide(ctx.side)) {
			throw new RuntimeException("Invalid side " + ctx.side.name() + " for " + msg.getClass().getSimpleName());
		} else if (msg.requiresMainThread()) {
			checkThreadAndEnqueue(msg, ctx);
		} else {
			msg.process(AntiqueAtlasMod.proxy.getPlayerEntity(ctx), ctx.side);
		}
		return null;
	} */

	/**
	 * Ensures that the message is being handled on the main thread
	 */
	/*
	private static <T extends AbstractMessage<T>> void checkThreadAndEnqueue(final AbstractMessage<T> msg, final MessageContext ctx) {
		XX_1_13_2_none_acv_XX thread = AntiqueAtlasMod.proxy.getThreadFromContext(ctx);
		if (!thread.av()) {
			thread.a(() -> msg.process(AntiqueAtlasMod.proxy.getPlayerEntity(ctx), ctx.side));
		}
	} */

	/**
	 * Messages that can only be sent from the server to the client should use this class
	 */
	public static abstract class AbstractClientMessage<T extends AbstractClientMessage<T>> extends AbstractMessage<T> {
		@Override
		protected final boolean isValidOnSide(EnvType side) {
			return side == EnvType.CLIENT;
		}
	}

	/**
	 * Messages that can only be sent from the client to the server should use this class
	 */
	public static abstract class AbstractServerMessage<T extends AbstractServerMessage<T>> extends AbstractMessage<T> {
		@Override
		protected final boolean isValidOnSide(EnvType side) {
			return side == EnvType.SERVER;
		}
	}
}
