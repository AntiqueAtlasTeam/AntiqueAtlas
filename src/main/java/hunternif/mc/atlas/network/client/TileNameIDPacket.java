package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 *
 * @author Hunternif
 */
public class TileNameIDPacket {
    private final Map<ResourceLocation, Integer> nameToIdMap;

    public TileNameIDPacket() {
        nameToIdMap = new HashMap<>();
    }

    public TileNameIDPacket(Map<ResourceLocation, Integer> nameToIdMap) {
        this.nameToIdMap = nameToIdMap;
    }

    public TileNameIDPacket put(ResourceLocation name, int biomeID) {
        nameToIdMap.put(name, biomeID);
        return this;
    }

    public static TileNameIDPacket read(PacketBuffer buffer) {
        int size = buffer.readVarInt();
        Map<ResourceLocation, Integer> nameToIdMap_ = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String name = buffer.readString(512);
            // Reading negative value to save on traffic, because custom biome
            // IDs are always negative.
            int biomeID = -buffer.readVarInt();
            nameToIdMap_.put(new ResourceLocation(name), biomeID);
        }
        return new TileNameIDPacket(nameToIdMap_);
    }

    public static void write(TileNameIDPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.nameToIdMap.size());
        for (Entry<ResourceLocation, Integer> entry : msg.nameToIdMap.entrySet()) {
            buffer.writeString(entry.getKey().toString());
            // Writing negative value to save on traffic, because custom biome
            // IDs are always negative.
            buffer.writeVarInt(-entry.getValue());
        }
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            for (Entry<ResourceLocation, Integer> entry : nameToIdMap.entrySet()) {
                ResourceLocation tileName = entry.getKey();
                int id = entry.getValue();
                // Remove old texture mapping
                int oldID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
                if (oldID != ExtTileIdMap.NOT_FOUND && oldID != id) {
                    BiomeTextureMap.instance().setTexture(Registry.BIOME.getByValue(oldID), null);
                }
                ExtTileIdMap.instance().setPseudoBiomeID(tileName, id);
                // Register new texture mapping
                TextureSet texture = ExtTileTextureMap.instance().getTexture(tileName);
                BiomeTextureMap.instance().setTexture(id, texture);
            }
            //TileIdRegisteredEvent.EVENT.invoker().onTileIDsReceived(nameToIdMap);
        });
        ctx.get().setPacketHandled(true);
    }
}
