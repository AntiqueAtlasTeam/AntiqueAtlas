package hunternif.mc.impl.atlas.network.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public abstract class AntiqueAtlasPacket extends PacketBuffer {
	public AntiqueAtlasPacket() {
		super(Unpooled.buffer());
	}

	public abstract ResourceLocation getId();
}
