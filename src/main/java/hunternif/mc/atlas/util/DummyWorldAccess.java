package hunternif.mc.atlas.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class DummyWorldAccess implements IWorldEventListener {

	@Override
	public void notifyLightSet(BlockPos pos) {}

	@Override
	public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_,
			int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {}

	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord,
			double yCoord, double zCoord, double xOffset, double yOffset,
			double zOffset, int ... p_180442_15_) {}

	@Override
	public void spawnParticle(int p_190570_1_, boolean p_190570_2_, boolean p_190570_3_, double p_190570_4_, double p_190570_6_, double p_190570_8_, double p_190570_10_, double p_190570_12_, double p_190570_14_, int... p_190570_16_) {

	}

	@Override
	public void onEntityAdded(Entity entity) {}

	@Override
	public void onEntityRemoved(Entity entity) {}

	@Override
	public void broadcastSound(int p_82746_1_, BlockPos pos, int p_82746_5_) {}


	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}

	@Override
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {}

	@Override
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
			double y, double z, float volume, float pitch) {}

	@Override
	public void playRecord(SoundEvent soundIn, BlockPos pos) {}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}

}
