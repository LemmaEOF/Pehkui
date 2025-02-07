package virtuoel.pehkui.mixin.compat115;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireballEntity;
import virtuoel.pehkui.util.MixinConstants;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(FireballEntity.class)
public abstract class FireballEntityMixin
{
	@ModifyArg(method = MixinConstants.EXPLOSIVE_PROJECTILE_ON_COLLISION, at = @At(value = "INVOKE", target = MixinConstants.CREATE_EXPLOSION, remap = false), remap = false)
	private float pehkui$onCollision$createExplosion(float power)
	{
		final float scale = ScaleUtils.getExplosionScale((Entity) (Object) this);
		
		return scale != 1.0F ? power * scale : power;
	}
}
