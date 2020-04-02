package virtuoel.pehkui.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.PehkuiConfig;
import virtuoel.pehkui.api.ScaleData;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin
{
	@ModifyArg(method = "getEyeHeight", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F"))
	private EntityDimensions onGetEyeHeightDimensionsProxy(EntityDimensions dimensions)
	{
		return dimensions.scaled(1.0F / pehkui_scaleData.getScale());
	}
	
	@ModifyArg(method = "handleFallDamage", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;computeFallDamage(FF)I"))
	private float onHandleFallDamageFallDistanceProxy(float fallDistance)
	{
		final double scale = pehkui_scaleData.getScale();
		
		if (scale != 1.0D)
		{
			if (Optional.ofNullable(PehkuiConfig.DATA.get("scaledFallDistance"))
				.filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
				.filter(JsonPrimitive::isBoolean).map(JsonPrimitive::getAsBoolean)
				.orElse(true))
			{
				return (float) (fallDistance / scale);
			}
		}
		
		return fallDistance;
	}
	
	@Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
	private void onGetEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> info)
	{
		if (pose != EntityPose.SLEEPING)
		{
			final float scale = pehkui_scaleData.getScale();
			
			if (scale != 1.0F)
			{
				info.setReturnValue(info.getReturnValueF() * scale);
			}
		}
	}
	
	@ModifyArg(method = "dropXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private Entity dropXpSpawnEntityProxy(Entity entity)
	{
		final float scale = pehkui_scaleData.getScale();
		
		if (scale != 1.0F)
		{
			final ScaleData data = ScaleData.of(entity);
			data.setScale(scale);
			data.setTargetScale(scale);
			data.markForSync();
		}
		
		return entity;
	}
	
	@ModifyArg(method = "onKilledBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private Entity onKilledBySpawnEntityProxy(Entity entity)
	{
		final float scale = pehkui_scaleData.getScale();
		
		if (scale != 1.0F)
		{
			final ScaleData data = ScaleData.of(entity);
			data.setScale(scale);
			data.setTargetScale(scale);
			data.markForSync();
		}
		
		return entity;
	}
}
