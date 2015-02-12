package hunternif.mc.atlas.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;

public class DummyWorldAccess implements IWorldAccess {

	@Override
	public void markBlockForUpdate(int p_147586_1_, int p_147586_2_,
			int p_147586_3_) {}

	@Override
	public void markBlockForRenderUpdate(int p_147588_1_, int p_147588_2_,
			int p_147588_3_) {}

	@Override
	public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_,
			int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {}

	@Override
	public void playSound(String p_72704_1_, double p_72704_2_,
			double p_72704_4_, double p_72704_6_, float p_72704_8_,
			float p_72704_9_) {}

	@Override
	public void playSoundToNearExcept(EntityPlayer p_85102_1_,
			String p_85102_2_, double p_85102_3_, double p_85102_5_,
			double p_85102_7_, float p_85102_9_, float p_85102_10_) {}

	@Override
	public void spawnParticle(String p_72708_1_, double p_72708_2_,
			double p_72708_4_, double p_72708_6_, double p_72708_8_,
			double p_72708_10_, double p_72708_12_) {}

	@Override
	public void onEntityCreate(Entity p_72703_1_) {}

	@Override
	public void onEntityDestroy(Entity p_72709_1_) {}

	@Override
	public void playRecord(String p_72702_1_, int p_72702_2_, int p_72702_3_,
			int p_72702_4_) {}

	@Override
	public void broadcastSound(int p_82746_1_, int p_82746_2_, int p_82746_3_,
			int p_82746_4_, int p_82746_5_) {}

	@Override
	public void playAuxSFX(EntityPlayer p_72706_1_, int p_72706_2_,
			int p_72706_3_, int p_72706_4_, int p_72706_5_, int p_72706_6_) {}

	@Override
	public void destroyBlockPartially(int p_147587_1_, int p_147587_2_,
			int p_147587_3_, int p_147587_4_, int p_147587_5_) {}

	@Override
	public void onStaticEntitiesChanged() {}

}
