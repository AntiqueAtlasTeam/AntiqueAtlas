package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public
class AtlasCreateS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "atlas", "create");

    @Override
    public
    Identifier getId() {
        return ID;
    }

    public static void apply(PacketContext context, PacketByteBuf buf) {
        PlayerEntity player = context.getPlayer();
        player.getEntityWorld().playSound(player, player.getBlockPos(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.AMBIENT, 1F, 1F);
    }
}
