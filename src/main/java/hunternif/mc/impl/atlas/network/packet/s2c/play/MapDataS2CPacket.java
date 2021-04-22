package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 * @author Haven King
 */
public class MapDataS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "map", "data");

	int atlasID; 
	CompoundNBT data;

	public MapDataS2CPacket(int atlasID, CompoundNBT data) {
		this.atlasID = atlasID;
		this.data = data;
	}

	public static void encode(final MapDataS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeCompoundTag(msg.data);
	}

	public static MapDataS2CPacket decode(final PacketBuffer packetBuffer) {
		return new MapDataS2CPacket(
				packetBuffer.readVarInt(),
				packetBuffer.readCompoundTag());
	}

	@Override
	public boolean shouldRun() {
		return this.data != null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(ClientPlayerEntity player) {
		AtlasData atlasData = AntiqueAtlasMod.tileData.getData(this.atlasID, player.getEntityWorld());
		atlasData.read(this.data);

		if (AntiqueAtlasConfig.doSaveBrowsingPos.get() && Minecraft.getInstance().currentScreen instanceof GuiAtlas) {
			((GuiAtlas) Minecraft.getInstance().currentScreen).loadSavedBrowsingPosition();
		}
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
