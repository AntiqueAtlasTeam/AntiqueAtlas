package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 */
public class MapDataPacket extends AbstractClientMessage<MapDataPacket> {
	private int atlasID;
	private CompoundTag data;

	public MapDataPacket() {}

	public MapDataPacket(int atlasID, CompoundTag data) {
		this.atlasID = atlasID;
		this.data = data;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		atlasID = buffer.readVarInt();
		data = buffer.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeVarInt(atlasID);
		buffer.writeCompoundTag(data);
	}


	@Override
	@Environment(EnvType.CLIENT)
	protected void process(PlayerEntity player, EnvType side) {
		if (data == null) return; // Atlas is empty
		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.getEntityWorld());
		atlasData.fromTag(data);
		// GuiAtlas may already be opened at (0, 0) browsing position, force load saved position:
		if (AntiqueAtlasMod.CONFIG.gameplay.doSaveBrowsingPos &&
				MinecraftClient.getInstance().currentScreen instanceof GuiAtlas) {
			((GuiAtlas)MinecraftClient.getInstance().currentScreen).loadSavedBrowsingPosition();
		}
	}
}
