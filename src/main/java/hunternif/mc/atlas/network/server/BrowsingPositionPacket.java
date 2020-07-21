package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 * @author Hunternif
 */
public class BrowsingPositionPacket extends AbstractServerMessage<BrowsingPositionPacket> {
	public static final double ZOOM_SCALE_FACTOR = 1024;
	
	private int atlasID;
	private RegistryKey<DimensionType> dimension;
	private int x, y;
	private double zoom;
	
	public BrowsingPositionPacket() {}
	
	public BrowsingPositionPacket(int atlasID, RegistryKey<DimensionType> dimension, int x, int y, double zoom) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}
	
	@Override
	protected void read(PacketByteBuf buffer) throws IOException {
		atlasID = buffer.readVarInt();
		dimension = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, buffer.readIdentifier());
		x = buffer.readVarInt();
		y = buffer.readVarInt();
		zoom = (double)buffer.readVarInt() / ZOOM_SCALE_FACTOR;
	}

	@Override
	protected void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(atlasID);
		buffer.writeIdentifier(dimension.getValue());
		buffer.writeVarInt(x);
		buffer.writeVarInt(y);
		buffer.writeVarInt((int)Math.round(zoom * ZOOM_SCALE_FACTOR));
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		// Make sure it's this player's atlas :^)
		if (SettingsConfig.gameplay.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
			Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
					player.getCommandSource().getName(), atlasID);
			return;
		}

		AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.getEntityWorld())
			.getDimensionData(dimension).setBrowsingPosition(x, y, zoom);
	}

}
