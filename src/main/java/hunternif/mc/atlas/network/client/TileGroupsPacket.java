package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.TileGroup;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;


public class TileGroupsPacket extends AbstractClientMessage<TileGroupsPacket> {

	public int atlasID;
	public RegistryKey<DimensionType> dimension;
	public ArrayList<TileGroup> tileGroups;

	public static final int TILE_GROUPS_PER_PACKET = 100;

	public TileGroupsPacket() {}

	public TileGroupsPacket(ArrayList<TileGroup> tileGroups, int atlasID, RegistryKey<DimensionType> dimension) {
		this.tileGroups = tileGroups;
		this.atlasID = atlasID;
		this.dimension = dimension;
	}

	@Override
	protected void read(PacketByteBuf buffer) {
		atlasID = buffer.readVarInt();
		dimension = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, buffer.readIdentifier());
		int length = buffer.readVarInt();
		tileGroups = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			TileGroup newbie = new TileGroup(0, 0);

			CompoundTag tag = buffer.readCompoundTag();
			if (tag != null) {
				newbie.readFromNBT(tag);
			}

			tileGroups.add(newbie);
		}
	}

	@Override
	protected void write(PacketByteBuf buffer) {
		buffer.writeVarInt(atlasID);
		buffer.writeIdentifier(dimension.getValue());
		buffer.writeVarInt(tileGroups.size());
		for (TileGroup t : tileGroups) {
			CompoundTag me = new CompoundTag();
			t.writeToNBT(me);
			buffer.writeCompoundTag(me);
		}
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.world);
		DimensionData dimData = atlasData.getDimensionData(dimension);
		for (TileGroup t : tileGroups) {
			dimData.putTileGroup(t);
		}
	}
}
