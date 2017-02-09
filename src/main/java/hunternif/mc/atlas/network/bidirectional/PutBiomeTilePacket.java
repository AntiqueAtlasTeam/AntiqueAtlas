package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.util.Log;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Puts biome tile into one atlas. When sent to server, forwards it to every
 * client that has this atlas' data synced.
 * @author Hunternif
 */
public class PutBiomeTilePacket extends AbstractMessage<PutBiomeTilePacket> {
	private int atlasID, dimension, x, z, biomeID;
	
	public PutBiomeTilePacket() {}
	
	public PutBiomeTilePacket(int atlasID, int dimension, int x, int z, int biomeID) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.x = x;
		this.z = z;
		this.biomeID = biomeID;
	}
	
	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = buffer.readVarInt();
		x = buffer.readVarInt();
		z = buffer.readVarInt();
		biomeID = buffer.readVarInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeVarInt(dimension);
		buffer.writeVarInt(x);
		buffer.writeVarInt(z);
		buffer.writeVarInt(biomeID);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			// Make sure it's this player's atlas :^)
			if (!player.inventory.hasItemStack(new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID))) {
				Log.warn("Player %s attempted to modify someone else's Atlas #%d",
						player.getGameProfile().getName(), atlasID);
				return;
			}
			AtlasAPI.tiles.putBiomeTile(player.getEntityWorld(), atlasID, biomeID, x, z);
		} else {
			AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.getEntityWorld());
			data.setTile(dimension, x, z, new Tile(biomeID));
		}
	}

}
