package elocindev.animation_overhaul.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Shadow
    private ModelPart cloak;

    @Inject(at = @At("TAIL"), method = "setupAnim", cancellable = true)
    public void setupAnim(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {        
        if (livingEntity instanceof Player && livingEntity.isSprinting()) {
            cloak.z = 1.35F;
            cloak.y = 0.80F;
        }
    }
}
