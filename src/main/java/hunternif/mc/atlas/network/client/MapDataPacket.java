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
import cpw.mods.fml.relauncher.Side;

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
		atlasID = buffer.readVarIntFromBuffer();
		data = readNBT(buffer);
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarIntToBuffer(atlasID);
		writeNBT(buffer, data);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (data == null) return; // Atlas is empty
		AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getAtlasData(atlasID, player.worldObj);
		atlasData.readFromNBT(data);
		// GuiAtlas may already be opened at (0, 0) browsing position, force load saved position:
		if (AntiqueAtlasMod.settings.doSaveBrowsingPos &&
				Minecraft.getMinecraft().currentScreen instanceof GuiAtlas) {
			((GuiAtlas)Minecraft.getMinecraft().currentScreen).loadSavedBrowsingPosition();
		}
	}
}
