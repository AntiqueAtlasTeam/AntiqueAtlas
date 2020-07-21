package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class OpenAtlasS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "atlas", "open");

	public OpenAtlasS2CPacket(Hand hand) {
		this.writeEnumConstant(hand);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketContext context, PacketByteBuf buf) {
		Hand hand = buf.readEnumConstant(Hand.class);
		MinecraftClient.getInstance().openScreen(new GuiAtlas().prepareToOpen(context.getPlayer().getStackInHand(hand)));
	}
}
