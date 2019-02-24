package hunternif.mc.atlas.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DummyBlockView implements BlockView {
	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos var1) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos var1) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos var1) {
		return Fluids.EMPTY.getDefaultState();
	}
}
