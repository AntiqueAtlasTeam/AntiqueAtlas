package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.CustomPacket.ProtocolException;
import hunternif.mc.atlas.util.ZipUtil;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class CustomPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try {
			EntityPlayer entityPlayer = (EntityPlayer)player;
			ByteArrayDataInput in = ByteStreams.newDataInput(packet.data);
			// Assuming your packetId is between 0 (inclusive) and 256 (exclusive).
			int packetId = in.readUnsignedByte();
			CustomPacket customPacket = CustomPacket.constructPacket(packetId);
			if (customPacket.isCompressed()) {
				byte[] unzipped = ZipUtil.decompressByteArray(packet.data, 1);
				in = ByteStreams.newDataInput(unzipped);
			}
			customPacket.read(in);
			customPacket.execute(entityPlayer, entityPlayer.worldObj.isRemote ? Side.CLIENT : Side.SERVER);
		} catch (ProtocolException e) {
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer("Protocol Exception!");
				AntiqueAtlasMod.logger.log(Level.WARNING, String.format("Player %s caused a Protocl Exception and was kicked.", ((EntityPlayer)player).username), e);
			} else {
				// Can't tolerate protocol exceptions on the client:
				throw new RuntimeException(e);
			}
		} catch (InstantiationException e) {
			throw new RuntimeException("Unexpected InstantiationException during Packet construction!", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unexpected IllegalAccessException during Packet construction!", e);
		}
	}
}
