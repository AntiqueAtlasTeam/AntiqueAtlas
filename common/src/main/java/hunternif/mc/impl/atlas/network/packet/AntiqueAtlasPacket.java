package hunternif.mc.impl.atlas.network.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class AntiqueAtlasPacket extends PacketByteBuf {
	public AntiqueAtlasPacket() {
		super(Unpooled.buffer());
	}

	public abstract Identifier getId();
}
