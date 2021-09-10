package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Used to sync atlas data from server to client.
 *
 * @author Hunternif
 * @author Haven King
 */
public class MapDataS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "map", "data");

    int atlasID;
    CompoundTag data;

    public MapDataS2CPacket(int atlasID, CompoundTag data) {
        this.atlasID = atlasID;
        this.data = data;
    }

    public MapDataS2CPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        data = buf.readCompoundTag();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.MAP_DATA;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeCompoundTag(data);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (data == null) return;

	    context.queue(() -> {
            AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, context.getPlayer().getEntityWorld());
            atlasData.fromTag(data);

            if (AntiqueAtlasMod.CONFIG.doSaveBrowsingPos && MinecraftClient.getInstance().currentScreen instanceof GuiAtlas) {
                ((GuiAtlas) MinecraftClient.getInstance().currentScreen).loadSavedBrowsingPosition();
            }
        });
    }
}
