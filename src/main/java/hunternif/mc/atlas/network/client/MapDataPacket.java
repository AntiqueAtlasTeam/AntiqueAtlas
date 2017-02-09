package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Used to sync bulk atlas data from server to client.
 * @author Hunternif
 */
public class MapDataPacket extends AbstractClientMessage<MapDataPacket> {
	private int atlasID;
	private NBTTagCompound data;

	public MapDataPacket() {}

	public MapDataPacket(int atlasID, NBTTagCompound data) {
		this.atlasID = atlasID;
		this.data = data;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (data == null) return; // Atlas is empty
		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.getEntityWorld());
		atlasData.readFromNBT(data);
		// GuiAtlas may already be opened at (0, 0) browsing position, force load saved position:
		if (AntiqueAtlasMod.settings.doSaveBrowsingPos &&
				Minecraft.getMinecraft().currentScreen instanceof GuiAtlas) {
			((GuiAtlas)Minecraft.getMinecraft().currentScreen).loadSavedBrowsingPosition();
		}
	}
}
