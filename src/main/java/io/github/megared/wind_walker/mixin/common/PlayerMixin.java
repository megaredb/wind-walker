package io.github.megared.wind_walker.mixin.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
	public void onCheckFallFlying(CallbackInfoReturnable<Boolean> cir) {
		PlayerMixin player = this;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability != null && parkourability.getClingToCliff().isCling()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "travel", at = @At("TAIL"))
	public void onTravel(Vec3d movementInput,  CallbackInfo ci) {
	}
}
