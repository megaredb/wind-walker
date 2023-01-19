package io.github.megared.wind_walker.mixin.common;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> variant, World world) {
		super(variant, world);
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;isClimbing()Z", at = @At("HEAD"), cancellable = true)
	public void isClimbing(CallbackInfoReturnable<Boolean> cir) {
		cir.cancel();
		if (this.isSpectator()) {
			cir.setReturnValue(false);
		} else {
			LivingEntity entity = (LivingEntity) (Object) this;
			BlockPos blockpos = this.getBlockPos();
			BlockState blockstate = this.getBlockStateAtPos();
			cir.setReturnValue(isLivingOnLadder(blockstate, entity.world, blockpos, entity));
		}
	}

	public boolean isLivingOnLadder(
			@NotNull BlockState state,
			@NotNull World world,
			@NotNull BlockPos pos,
			@NotNull LivingEntity entity
	) {
		boolean isSpectator = (entity instanceof PlayerEntity);
		if (isSpectator) return false;
		if (!fullBoundingBoxLadders.get()) {
			return isLadder(state, world, pos, entity);
		} else {
			Box bb = entity.getBoundingBox();
			int mX = MathHelper.floor(bb.minX);
			int mY = Mth.floor(bb.minY);
			int mZ = Mth.floor(bb.minZ);
			for (int y2 = mY; y2 < bb.maxY; y2++) {
				for (int x2 = mX; x2 < bb.maxX; x2++) {
					for (int z2 = mZ; z2 < bb.maxZ; z2++) {
						BlockPos tmp = new BlockPos(x2, y2, z2);
						state = world.getBlockState(tmp);
						if (isLadder(state, world, tmp, entity)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	private boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
		Block block = state.getBlock();
		if (block instanceof FenceBlock) {
			int count = 0;
			if (state.getValue(CrossCollisionBlock.NORTH)) count++;
			if (state.getValue(CrossCollisionBlock.SOUTH)) count++;
			if (state.getValue(CrossCollisionBlock.EAST)) count++;
			if (state.getValue(CrossCollisionBlock.WEST)) count++;
			return count <= 0;
		} else if (block instanceof RotatedPillarBlock) {
			return state.getValue(RotatedPillarBlock.AXIS).isVertical();
		} else if (block instanceof EndRodBlock) {
			Direction direction = state.getValue(DirectionalBlock.FACING);
			return direction == Direction.UP || direction == Direction.DOWN;
		} else {
			return block.isLadder(state, world, pos, entity);
		}
	}
}
