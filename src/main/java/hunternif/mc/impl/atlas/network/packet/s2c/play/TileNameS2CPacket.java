package hunternif.mc.impl.atlas.network.packet.s2c.play;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.Sets;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.BiomeTextureMap;
import hunternif.mc.impl.atlas.client.TextureSet;
import hunternif.mc.impl.atlas.ext.ExtTileTextureMap;
import hunternif.mc.impl.atlas.forge.hook.AntiqueAtlasHooks;
//import hunternif.mc.impl.atlas.ext.TileIdRegisteredCallback;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 * @author Haven King
 */
public class TileNameS2CPacket extends S2CPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "update");

	Collection<ResourceLocation> tileIds;

	public TileNameS2CPacket(Collection<ResourceLocation> tileIds) {
		this.tileIds = tileIds;
	}

	public TileNameS2CPacket(ResourceLocation id) {
		this.tileIds = Sets.newHashSet(id);
	}

	public static void encode(final TileNameS2CPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.tileIds.size());

		for (ResourceLocation id : msg.tileIds) {
			packetBuffer.writeResourceLocation(id);
		}
	}

	public static TileNameS2CPacket decode(final PacketBuffer packetBuffer) {
		int size = packetBuffer.readVarInt();
		Collection<ResourceLocation> tileIds = new HashSet<>();
		for (int i = 0; i < size; ++i) {
			tileIds.add(packetBuffer.readResourceLocation());
		}

		return new TileNameS2CPacket(tileIds);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean handle(ClientPlayerEntity player) {
		for (ResourceLocation id: this.tileIds) {
			TextureSet texture = ExtTileTextureMap.instance().getTexture(id);
			BiomeTextureMap.instance().setTexture(id, texture);
		}

		//FIXME TileIdRegisteredCallback.EVENT.invoker().onTileIDsReceived(tileIds);
		AntiqueAtlasHooks.onTileIdRegistered(this.tileIds);
		return true;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	//	@OnlyIn(Dist.CLIENT)
	//	public static void apply(PacketContext context, PacketByteBuf buf) {
	//		int size = buf.readVarInt();
	//		Collection<ResourceLocation> tileIds = new HashSet<>();
	//		for (int i = 0; i < size; ++i) {
	//			tileIds.add(buf.readResourceLocation());
	//		}
	//
	//		context.getTaskQueue().execute(() -> {
	//			for (ResourceLocation id: tileIds) {
	//				TextureSet texture = ExtTileTextureMap.instance().getTexture(id);
	//				BiomeTextureMap.instance().setTexture(id, texture);
	//			}
	//
	//			 TileIdRegisteredCallback.EVENT.invoker().onTileIDsReceived(tileIds);
	//		});
	//	}
}
