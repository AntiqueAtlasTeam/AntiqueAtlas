package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.AtlasData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to sync atlas data from server to client.
 *
 * @author Hunternif
 */
public class MapDataPacket {
    private int atlasID;
    private CompoundNBT data;

    public MapDataPacket() {
    }

    public MapDataPacket(int atlasID, CompoundNBT data) {
        this.atlasID = atlasID;
        this.data = data;
    }

    public static MapDataPacket read(PacketBuffer buffer) {
        return new MapDataPacket(buffer.readVarInt(), buffer.readCompoundTag());
    }

    public static void write(MapDataPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeCompoundTag(msg.data);
    }


    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //ServerPlayerEntity player = ctx.get().getSender();
            if (data == null) return; // Atlas is empty
            AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, Minecraft.getInstance().player.getEntityWorld());
            atlasData.read(data);
            // GuiAtlas may already be opened at (0, 0) browsing position, force load saved position:
            if (SettingsConfig.doSaveBrowsingPos &&
                    Minecraft.getInstance().currentScreen instanceof GuiAtlas) {
                ((GuiAtlas) Minecraft.getInstance().currentScreen).loadSavedBrowsingPosition();
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
