package hunternif.mc.impl.atlas.network.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileGroup;
import hunternif.mc.impl.atlas.core.WorldData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * Syncs tile groups to the client.
 * @author Hunternif
 * @author Haven King
 */
public class TileGroupsS2CPacket extends S2CPacket {
	public static final int TILE_GROUPS_PER_PACKET = 100;
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "s2c", "tile", "groups");

	int atlasID;
	RegistryKey<World> world;
	List<TileGroup> tileGroups;

	public TileGroupsS2CPacket(int atlasID, RegistryKey<World> world, List<TileGroup> tileGroups) {
		this.atlasID = atlasID;
		this.world = world;
		this.tileGroups = tileGroups;
	}

	public static void encode(final TileGroupsS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.world.getLocation());
		packetBuffer.writeVarInt(msg.tileGroups.size());

		for (TileGroup tileGroup : msg.tileGroups) {
			packetBuffer.writeCompoundTag(tileGroup.writeToNBT(new CompoundNBT()));
		}
	}

	public static TileGroupsS2CPacket decode(final PacketBuffer packetBuffer) {
		int atlasID = packetBuffer.readVarInt();
		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, packetBuffer.readResourceLocation());
		int length = packetBuffer.readVarInt();
		List<TileGroup> tileGroups = new ArrayList<>(length);

		for (int i = 0; i < length; ++i) {
			CompoundNBT tag = packetBuffer.readCompoundTag();

			if (tag != null) {
				tileGroups.add(new TileGroup().readFromNBT(tag));
			}
		}

		return new TileGroupsS2CPacket(atlasID, world, tileGroups);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean handle(ClientPlayerEntity player) {
		AtlasData atlasData = AntiqueAtlasMod.tileData.getData(this.atlasID, player.world);
		WorldData dimData = atlasData.getWorldData(this.world);
		for (TileGroup t : this.tileGroups) {
			dimData.putTileGroup(t);
		}
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
