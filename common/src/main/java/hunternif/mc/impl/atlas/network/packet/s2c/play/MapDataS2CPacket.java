package hunternif.mc.impl.atlas.network.packet.s2c.play;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class MapDataS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "map", "data");

	public MapDataS2CPacket(int atlasID, NbtCompound data) {
		this.writeVarInt(atlasID);
		this.writeNbt(data);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketByteBuf buf, NetworkManager.PacketContext context) {
		int atlasID = buf.readVarInt();
		NbtCompound data = buf.readNbt();

		if (data == null) return;

		context.queue(() -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			assert player != null;
			AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, player.getEntityWorld());
			atlasData.updateFromNbt(data);

			if (AntiqueAtlasMod.CONFIG.doSaveBrowsingPos && MinecraftClient.getInstance().currentScreen instanceof GuiAtlas) {
				((GuiAtlas) MinecraftClient.getInstance().currentScreen).loadSavedBrowsingPosition();
			}
		});
	}
}
