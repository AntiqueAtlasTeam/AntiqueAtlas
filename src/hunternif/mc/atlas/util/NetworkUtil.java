package hunternif.mc.atlas.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class NetworkUtil {
	/** Because some modded servers can handle several worlds at once. */
	public static void sendPacketToAllPlayersInWorld(World world, Packet packet) {
		for (Object playerObj : world.playerEntities) {
			EntityPlayer player = (EntityPlayer) playerObj;
			PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
		}
	}
}
