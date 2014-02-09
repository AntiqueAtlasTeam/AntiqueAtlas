package hunternif.mc.atlas.ext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.IPlayerTracker;

public class ExtBiomeDataHandler implements IPlayerTracker {
	private static final String DATA_KEY = "aAtlasExtTiles";
	
	private ExtBiomeData data;
	
	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote) {
			data = (ExtBiomeData) event.world.loadItemData(ExtBiomeData.class, DATA_KEY);
			if (data == null) {
				data = new ExtBiomeData(DATA_KEY);
				data.markDirty();
				event.world.setItemData(DATA_KEY, data);
			}
		}
	}
	
	public ExtBiomeData getData() {
		if (data == null) { // This will happen on the client
			data = new ExtBiomeData(DATA_KEY);
		}
		return data;
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		ExtTileIdMap.instance().syncOnPlayer(player);
		data.syncOnPlayer(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {}

}
