package hunternif.mc.atlas.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.relauncher.Side;

public class PlayerTracker implements IPlayerTracker {
	private static final String ID_PLAYER_INFO = "antiqueAtlasPlayerInfo";
	
	private Map<EntityPlayer, PlayerInfo> clientPlayerInfoMap = new ConcurrentHashMap<EntityPlayer, PlayerInfo>();
	private Map<EntityPlayer, PlayerInfo> serverPlayerInfoMap = new ConcurrentHashMap<EntityPlayer, PlayerInfo>();
	private Map<EntityPlayer, PlayerInfo> getPlayerInfoMap(Side side) {
		return side.isClient() ? clientPlayerInfoMap : serverPlayerInfoMap;
	}
	
	public PlayerInfo getPlayerInfo(EntityPlayer player) {
		Map<EntityPlayer, PlayerInfo> playerInfoMap = getPlayerInfoMap(getSide(player));
		PlayerInfo info = playerInfoMap.get(player);
		if (info == null) {
			info = new PlayerInfo(player);
			playerInfoMap.put(player, info);
		} else {
			if (info.getPlayer() != player) {
				// This happens on the client when player changes dimension
				info.setPlayer(player);
			}
		}
		return info;
	}
	
	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote) {
			event.entity.registerExtendedProperties(ID_PLAYER_INFO, getPlayerInfo((EntityPlayer)event.entity));
		}
	}
	
	private static Side getSide(Entity entity) {
		return entity.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
	}
	
	@ForgeSubscribe
	public void onPlayerUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			getPlayerInfo((EntityPlayer)event.entity).atlas.updateMap();
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		getPlayerInfo(player).atlas.syncOnClinet();
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	/**
	 * Upon respawn the EntityPlayer is constructed anew, however, with a wrong
	 * entityID at the moment of EntityConstructing event dispatch. That entityID
	 * is changed later on, but the ExtendedProperties have already been written
	 * and cannot be removed. So let us manually copy the required values into
	 * the existing ExtendedProperties.
	 */
	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		PlayerInfo newInfo = (PlayerInfo)player.getExtendedProperties(ID_PLAYER_INFO);
		Map<EntityPlayer, PlayerInfo> playerInfoMap = getPlayerInfoMap(getSide(player));
		PlayerInfo oldInfo = playerInfoMap.get(player);
		newInfo.copyFrom(oldInfo);
		playerInfoMap.put(player, newInfo);
	}
}
