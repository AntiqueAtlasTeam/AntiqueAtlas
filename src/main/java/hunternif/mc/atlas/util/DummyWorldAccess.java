package hunternif.mc.atlas.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IWorldAccess;

public class DummyWorldAccess implements IWorldAccess {

	@Override
	public void markBlockForUpdate(BlockPos pos) {}

	@Override
	public void notifyLightSet(BlockPos pos) {}

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
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord,
			double yCoord, double zCoord, double xOffset, double yOffset,
			double zOffset, int ... p_180442_15_) {}

	@Override
	public void onEntityAdded(Entity entity) {}

	@Override
	public void onEntityRemoved(Entity entity) {}

	@Override
	public void playRecord(String p_72702_1_, BlockPos pos) {}

	@Override
	public void broadcastSound(int p_82746_1_, BlockPos pos, int p_82746_5_) {}

	@Override
	public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_) {}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}

}
