package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.event.OptionalMarkerEvent;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

/**
 * When the server wants to create an automatic marker in a player's atlas,
 * but it can be disabled by the player's client.
 */
public class OptionalMarkerPacket extends AbstractClientMessage<OptionalMarkerPacket> {
    private int atlasID;
    private int dimension;
    private String typeName;
    private String label;
    private int x, z;
    private boolean visibleAhead;

    public OptionalMarkerPacket() {}

    /** Use this constructor when creating a <b>local</b> marker. */
    public OptionalMarkerPacket(int atlasID, int dimension, MarkerType type, String label, int x, int z, boolean visibleAhead) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.typeName = type.getRegistryName().toString();
        this.label = label;
        this.x = x;
        this.z = z;
        this.visibleAhead = visibleAhead;
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        atlasID = buffer.readVarInt();
        dimension = buffer.readVarInt();
        typeName = ByteBufUtils.readUTF8String(buffer);
        label = ByteBufUtils.readUTF8String(buffer);
        x = buffer.readInt();
        z = buffer.readInt();
        visibleAhead = buffer.readBoolean();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        buffer.writeVarInt(atlasID);
        buffer.writeVarInt(dimension);
        ByteBufUtils.writeUTF8String(buffer, typeName);
        ByteBufUtils.writeUTF8String(buffer, label);
        buffer.writeInt(x);
        buffer.writeInt(z);
        buffer.writeBoolean(visibleAhead);
    }

    @Override
    protected void process(EntityPlayer player, Side side) {
        MarkerType type = MarkerRegistry.find(typeName);
        if (type == null) return;
        if (!MinecraftForge.EVENT_BUS.post(new OptionalMarkerEvent(
                atlasID, dimension, type, label, x, z, visibleAhead
        ))) {
            AtlasAPI.markers.putMarker(player.getEntityWorld(), visibleAhead, atlasID, type, label, x, z);
        }
    }
}
