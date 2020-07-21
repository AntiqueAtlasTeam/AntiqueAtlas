package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class MapDataS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "map", "data");

	public MapDataS2CPacket(int atlasID, CompoundTag data) {
		this.writeVarInt(atlasID);
		this.writeCompoundTag(data);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketContext context, PacketByteBuf buf) {
		int atlasID = buf.readVarInt();
		CompoundTag data = buf.readCompoundTag();

		if (data == null) return;

		context.getTaskQueue().execute(() -> {
			AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, context.getPlayer().getEntityWorld());
			atlasData.fromTag(data);

			if (AntiqueAtlasMod.CONFIG.doSaveBrowsingPos && MinecraftClient.getInstance().currentScreen instanceof GuiAtlas) {
				((GuiAtlas) MinecraftClient.getInstance().currentScreen).loadSavedBrowsingPosition();
			}
		});
	}
}
