package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
	CompoundTag data;

	public MapDataS2CPacket(int atlasID, CompoundTag data) {
		this.atlasID = atlasID;
		this.data = data;
	}

	public static void encode(final MapDataS2CPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeNbt(msg.data);
	}

	public static MapDataS2CPacket decode(final FriendlyByteBuf packetBuffer) {
		return new MapDataS2CPacket(
				packetBuffer.readVarInt(),
				packetBuffer.readNbt());
	}

	@Override
	public boolean shouldRun() {
		return this.data != null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean handle(LocalPlayer player) {
		AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, player.getCommandSenderWorld());
		atlasData.readNbt(data);

		if (AntiqueAtlasMod.CONFIG.doSaveBrowsingPos && Minecraft.getInstance().screen instanceof GuiAtlas) {
			((GuiAtlas) Minecraft.getInstance().screen).loadSavedBrowsingPosition();
		}
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
