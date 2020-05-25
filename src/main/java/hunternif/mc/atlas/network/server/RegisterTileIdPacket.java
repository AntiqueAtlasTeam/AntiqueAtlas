package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;


/**
 * Sent from client to server to register a new
 * (unique tile name)-(pseudo-biome ID) pair.
 *
 * @author Hunternif
 */
public class RegisterTileIdPacket {
    private ResourceLocation name;

    public RegisterTileIdPacket() {
    }

    public RegisterTileIdPacket(ResourceLocation uniqueTileName) {
        this.name = uniqueTileName;
    }

    public static RegisterTileIdPacket read(PacketBuffer buffer) {
        return new RegisterTileIdPacket(new ResourceLocation(buffer.readString(512)));
    }

    public static void write(RegisterTileIdPacket msg, PacketBuffer buffer) {
        buffer.writeString(msg.name.toString());
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            // Register the new tile id:
            int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(name);
            // Send it to all clients:
            TileNameIDPacket packet = new TileNameIDPacket();
            packet.put(name, biomeID);
            //PacketDispatcher.sendToAll(((ServerWorld) player.getEntityWorld()).getServer(), packet);
            PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
        });
        ctx.get().setPacketHandled(true);
    }

}
