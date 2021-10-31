package hunternif.mc.impl.atlas.network.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class AntiqueAtlasPacket extends FriendlyByteBuf {
	public AntiqueAtlasPacket() {
		super(Unpooled.buffer());
	}

	public abstract ResourceLocation getId();
}
