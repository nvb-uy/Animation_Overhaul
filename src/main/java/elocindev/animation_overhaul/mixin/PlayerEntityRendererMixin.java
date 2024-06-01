package elocindev.animation_overhaul.mixin;

import elocindev.animation_overhaul.compat.SpellEngineCompat;
import elocindev.animation_overhaul.api.ILeanablePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(value = PlayerRenderer.class, priority = 300)
public abstract class PlayerEntityRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerEntityRendererMixin(EntityRendererProvider.Context ctx,
            PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    
    @Shadow
    protected abstract void setModelProperties(AbstractClientPlayer player);

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    public void animation_overhaul$renderLeaning(AbstractClientPlayer player, float f, float g, PoseStack matrixStack,
        MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameRenderer == null) return;

        if (!mc.gameRenderer.getMainCamera().isDetached()
                && player == mc.player) {
            return;
        }
        
        if (SpellEngineCompat.shouldLetAnimate(player)) return;

        matrixStack.pushPose();

        float lean_x = (float) player.getDeltaMovement().z;
        float lean_z = -(float) player.getDeltaMovement().x;

        float turnLeanAmount = ((ILeanablePlayer) player).getLeanAmount();
        float leanMultiplier = ((ILeanablePlayer) player).getLeanMultiplier();
        float player_squash = ((ILeanablePlayer) player).getSquash();
        player_squash = Mth.clamp(player_squash, -1, 1) * 0.25f;

        float h_scale = 1;
        float v_scale = 1;

        h_scale = Mth.lerp(player_squash, 1, 0.5f);
        v_scale = Mth.lerp(player_squash, 1, 1.5f);

        float yaw = (float) Math.toRadians(player.yBodyRot + 90);
        lean_x += Math.cos(yaw) * turnLeanAmount;
        lean_z += Math.sin(yaw) * turnLeanAmount;

        lean_x *= leanMultiplier;
        lean_z *= leanMultiplier;

        if (player.isFallFlying()) {
            lean_x = 0;
            lean_z = 0;
            h_scale = 1.0f;
            v_scale = 1.0f;
        }

        //#if MC==12001
        Quaternionf quat = new Quaternionf();

        quat = new Matrix4f().rotate(lean_x, new Vector3f(1, 0, 0)).rotate(lean_z, new Vector3f(0, 0, 1))
                .getNormalizedRotation(quat);
        //#else
        //$$com.mojang.math.Quaternion quat = com.mojang.math.Quaternion.fromXYZ(lean_x, 0, lean_z);
        //$$quat.normalize();
        //#endif

        matrixStack.mulPose(quat);
        matrixStack.scale(h_scale, v_scale, h_scale);

        this.setModelProperties(player);

        super.render(player, f, g, matrixStack, vertexConsumerProvider, i);

        matrixStack.popPose();

        ci.cancel();
    }
}
