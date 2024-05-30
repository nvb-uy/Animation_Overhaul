package elocindev.animation_overhaul.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.MathHelper;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import elocindev.animation_overhaul.AnimationOverhaul;
import elocindev.animation_overhaul.api.ILeanablePlayer;
import elocindev.animation_overhaul.util.PlatformUtility;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import elocindev.animation_overhaul.api.AnimationHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Player implements ILeanablePlayer {

    @Unique
    private final ModifierLayer<IAnimation> modAnimationContainer = new ModifierLayer<>();

    public AbstractClientPlayerEntityMixin(Level level, BlockPos pos, float yaw, GameProfile gameProfile
        //#if MC==11902
        //$$, @org.jetbrains.annotations.Nullable net.minecraft.world.entity.player.ProfilePublicKey key
        //#endif

    ) {
        super(level, pos, yaw, gameProfile
            //#if MC==11902
            //$$, key
            //#endif
        );
    }

    public AnimationHolder anim_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    public AnimationHolder anim_walk = AnimationHolder.EMPTY;
    public AnimationHolder anim_run = AnimationHolder.EMPTY;
    public AnimationHolder anim_turn_right = AnimationHolder.EMPTY;
    public AnimationHolder anim_turn_left = AnimationHolder.EMPTY;
    public AnimationHolder anim_falling = AnimationHolder.EMPTY;
    public AnimationHolder anim_landing = AnimationHolder.EMPTY;
    public AnimationHolder anim_swimming = AnimationHolder.EMPTY;
    public AnimationHolder anim_swim_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_crawl_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_crawling = AnimationHolder.EMPTY;
    public AnimationHolder anim_eating = AnimationHolder.EMPTY;
    public AnimationHolder anim_climbing = AnimationHolder.EMPTY;
    public AnimationHolder anim_climbing_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_sprint_stop = AnimationHolder.EMPTY;
    public AnimationHolder anim_fence_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_fence_walk = AnimationHolder.EMPTY;
    public AnimationHolder anim_edge_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_elytra_fly = AnimationHolder.EMPTY;
    public AnimationHolder anim_flint_and_steel = AnimationHolder.EMPTY;
    public AnimationHolder anim_flint_and_steel_sneak = AnimationHolder.EMPTY;
    public AnimationHolder anim_boat_idle = AnimationHolder.EMPTY;
    public AnimationHolder anim_boat_left_paddle = AnimationHolder.EMPTY;
    public AnimationHolder anim_boat_right_paddle = AnimationHolder.EMPTY;
    public AnimationHolder anim_boat_forward = AnimationHolder.EMPTY;
    public AnimationHolder anim_rolling = AnimationHolder.EMPTY;
    public AnimationHolder anim_jump[] = new AnimationHolder[2];
    public AnimationHolder anim_fall[] = new AnimationHolder[2];
    public AnimationHolder anim_punch[] = new AnimationHolder[2];
    public AnimationHolder anim_punch_sneaking[] = new AnimationHolder[2];
    public AnimationHolder anim_sword_swing[] = new AnimationHolder[2];
    public AnimationHolder anim_sword_swing_sneak[] = new AnimationHolder[2];

    public int punch_index = 0;
    public int jump_index = 0;

    public float leanAmount = 0;
    public float leanMultiplier = 1;
    public float realLeanMultiplier = 1;

    public float squash = 0;
    public float realSquash = 0;

    public float momentum = 0;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void animation_overhaul$init(ClientLevel level, GameProfile profile, CallbackInfo info) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayer) (Object) this).addAnimLayer(850, modAnimationContainer);

        var cfg = AnimationOverhaul.CONFIG.enabled_animations;

        anim_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "idle"), cfg.idle);
        anim_fall[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fall_first"), cfg.fall);
        anim_fall[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fall_second"), cfg.fall);
        anim_jump[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "jump_first"), cfg.jump);
        anim_jump[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "jump_second"), cfg.jump);
        anim_sneak_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sneak_idle"), cfg.sneak_idle);
        anim_sneak_walk = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sneak_walk"), cfg.sneak_walk);
        anim_walk = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "walking"), cfg.walk);
        anim_run = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "running"), cfg.run);
        anim_turn_right = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "turn_right"), cfg.turn_right);
        anim_turn_left = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "turn_left"), cfg.turn_left);
        anim_punch[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_right"), cfg.punch);
        anim_punch[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_left"), cfg.punch);
        anim_punch_sneaking[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_right_sneak"), cfg.punch_sneaking);
        anim_punch_sneaking[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_left_sneak"), cfg.punch_sneaking);
        anim_sword_swing[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_first"), cfg.sword_swing);
        anim_sword_swing[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_second"), cfg.sword_swing);
        anim_sword_swing_sneak[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_sneak_first"), cfg.sword_swing_sneak);
        anim_sword_swing_sneak[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_sneak_second"), cfg.sword_swing_sneak);
        anim_falling = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "falling"), cfg.falling);
        anim_landing = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "landing"), cfg.landing);
        anim_swimming = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "swimming"), cfg.swimming);
        anim_swim_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "swim_idle"), cfg.swim_idle);
        anim_crawl_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "crawl_idle"), cfg.crawl_idle);
        anim_crawling = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "crawling"), cfg.crawling);
        anim_eating = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "eating"), cfg.eating);
        anim_climbing = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "climbing"), cfg.climbing);
        anim_climbing_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "climbing_idle"), cfg.climbing_idle);
        anim_sprint_stop = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sprint_stop"), cfg.sprint_stop);
        anim_fence_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fence_idle"), cfg.fence_idle);
        anim_fence_walk = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fence_walk"), cfg.fence_walk);
        anim_edge_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "edge_idle"), cfg.edge_idle);
        anim_elytra_fly = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "elytra_fly"), cfg.elytra_fly);
        anim_flint_and_steel = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "flint_and_steel"), cfg.flint_and_steel);
        anim_flint_and_steel_sneak = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "flint_and_steel_sneak"), cfg.flint_and_steel_sneak);
        anim_boat_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_idle"), cfg.boat_idle);
        anim_boat_forward = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_forward"), cfg.boat_forward);
        anim_boat_right_paddle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_right_paddle"), cfg.boat_right_paddle);
        anim_boat_left_paddle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_left_paddle"), cfg.boat_left_paddle);
        anim_rolling = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "rolling"), cfg.rolling);
    }

    public float turnDelta = 0;

    public Vec3 lastPos = new Vec3(0, 0, 0);

    public boolean lastOnGround = false;

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        boolean onGround = this.onGround();

        float delta = 1.0f / 20.0f;
        Vec3 pos = position();

        if (!onGround() && lastOnGround && getDeltaMovement().y > 0) {
            playJumpAnimation();
        }

        Block standingBlock = level.getBlockState(blockPosition().below()).getBlock();
        boolean onFence = (standingBlock instanceof FenceBlock || standingBlock instanceof WallBlock
                || standingBlock instanceof IronBarsBlock) && onGround();

        boolean onEdge = level.getBlockState(blockPosition().below()).isAir() && onGround();

        if (turnDelta != 0) {
            leanAmount = MathHelper.lerp(delta * 4, leanAmount, yBodyRot - yBodyRotO);
        } else {
            leanAmount = MathHelper.lerp(delta * 4, leanAmount, 0);
        }
        leanMultiplier = MathHelper.lerp(delta * 8, leanMultiplier, realLeanMultiplier);

        squash = MathHelper.lerp(delta * 8, squash, realSquash);
        realSquash = MathHelper.lerp(delta * 8, realSquash, 0);

        Vec3f movementVector = new Vec3f((float) (pos.x - lastPos.x), 0, (float) (pos.z - lastPos.z));
        Vec3f lookVector = new Vec3f((float) Math.cos(Math.toRadians(yBodyRot + 90)), 0,
                (float) Math.sin(Math.toRadians(yBodyRot + 90)));

        float movementLength = (float) Math.sqrt(movementVector.getX() * movementVector.getX() +
                movementVector.getY() * movementVector.getY() +
                movementVector.getZ() * movementVector.getZ());

        boolean isWalking = movementLength > 0;

        float dotProduct = movementVector.getX() * lookVector.getX() +
            movementVector.getY() * lookVector.getY() +
            movementVector.getZ() * lookVector.getZ();

        boolean isWalkingForwards = isWalking && dotProduct > 0;

        float walk_sign = isWalking ? isWalkingForwards ? 1 : -1 : 0;

        float sprint_multiplier = ((isSprinting() && isWalkingForwards) ? 2 : 1);
        momentum = MathHelper.lerp(delta * 2 * sprint_multiplier, momentum, (walk_sign) * sprint_multiplier);

        if (realLeanMultiplier < 1) {
            realLeanMultiplier += 0.1f;
        } else {
            realLeanMultiplier = 1;
        }

        KeyframeAnimation anim = null;

        float anim_speed = 1.0f;
        int fade_time = 5;

        boolean onGroundInWater = isUnderWater()
                && this.getBlockStateOn().getCollisionShape(this.level(), this.blockPosition()).isEmpty();

        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {

            if (isPassenger() && getVehicle() instanceof Boat) {
                anim = anim_boat_idle.getAnimation();
                boolean left_paddle = ((Boat) getVehicle()).getPaddleState(0);
                boolean right_paddle = ((Boat) getVehicle()).getPaddleState(1);

                if (left_paddle && right_paddle) {
                    anim = anim_boat_forward.getAnimation();
                } else if (left_paddle) {
                    anim = anim_boat_left_paddle.getAnimation();
                } else if (right_paddle) {
                    anim = anim_boat_right_paddle.getAnimation();
                }
            } else if (level.getBlockState(blockPosition()).getBlock() instanceof LadderBlock && !onGround()
                    && !jumping) {
                anim = anim_climbing_idle.getAnimation();
                if (getDeltaMovement().y > 0) {
                    anim = anim_climbing.getAnimation();
                }
            } else if (isUsingItem() && getMainHandItem().getItem().isEdible()) {
                anim = anim_eating.getAnimation();
            } else if (isFallFlying()) {
                anim = anim_elytra_fly.getAnimation();
            } else if (onGround() || onGroundInWater) {
                anim = anim_idle.getAnimation();
                if (onFence) {
                    anim = anim_fence_idle.getAnimation();
                } else if (onEdge) {
                    anim = anim_edge_idle.getAnimation();
                }

                if (turnDelta != 0 && !onEdge) {
                    anim = anim_turn_right.getAnimation();
                    if (turnDelta < 0) {
                        anim = anim_turn_left.getAnimation();
                    }
                }

                if ((isInWaterOrBubble() || isInLava()) && !onGroundInWater) {
                    if (this.isSwimming() || this.isSprinting()) {
                        anim = anim_swimming.getAnimation();
                    }
                } else if (isVisuallyCrawling()) {
                    if (isWalking) {
                        if (currentAnimation == anim_crawl_idle.getAnimation()) {
                            fade_time = 0;
                        }
                        anim = anim_crawling.getAnimation();
                    } else {
                        if (currentAnimation == anim_crawling.getAnimation()) {
                            fade_time = 0;
                        }
                        anim = anim_crawl_idle.getAnimation();
                    }
                } else if (isShiftKeyDown()) {
                    anim = anim_sneak_idle.getAnimation();

                    if (isWalking || turnDelta != 0) {
                        anim = anim_sneak_walk.getAnimation();
                    }
                } else {
                    if (isWalking) {
                        if (momentum > 1 && !isWalkingForwards) {
                            anim = anim_sprint_stop.getAnimation();
                            fade_time = 2;
                        } else {
                            if (isSprinting() && !isUsingItem()) {
                                anim = anim_run.getAnimation();
                                anim_speed = 1.0f;
                            } else {
                                anim = anim_walk.getAnimation();
                                if (onFence) {
                                    anim = anim_fence_walk.getAnimation();
                                }
                            }
                        }
                    }
                }
            } else {

                if (isInWaterOrBubble() || isInLava()) {
                    if (this.isSwimming() || this.isSprinting()) {
                        anim = anim_swimming.getAnimation();
                    } else {
                        anim = anim_swim_idle.getAnimation();
                    }
                } else {
                    if (this.fallDistance > 1) {
                        if (this.fallDistance > 3) {
                            anim = anim_falling.getAnimation();
                        } else {
                            anim = anim_fall[jump_index].getAnimation();
                        }
                    }
                }
            }
        }
        
        playAnimation(anim, anim_speed, fade_time);

        if (this.isUsingItem()) {
            if (this.getUseItem() != null) {
                var action = getUseItem().getUseAnimation();
                if (action == UseAnim.BOW || action == UseAnim.CROSSBOW || action == UseAnim.SPYGLASS
                        || action == UseAnim.SPEAR || action == UseAnim.TOOT_HORN || action == UseAnim.BLOCK ||
                        action == UseAnim.DRINK) {
                    disableArmAnimations();
                } else if (getUseItem().getItem() instanceof FlintAndSteelItem) {
                    anim = anim_flint_and_steel.getAnimation();
                    if (isShiftKeyDown()) {
                        anim = anim_flint_and_steel_sneak.getAnimation();
                    }

                    playAnimation(anim, anim_speed, fade_time);
                }
            }
        } else {
            enableArmAnimations();
        }

        lastPos = new Vec3(pos.x, pos.y, pos.z);
        lastOnGround = onGround;
    }

    KeyframeAnimation currentAnimation = null;

    public void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {

        if (fallDistance > 0) {
            if (onGround) {
                if (fallDistance >= 3) {
                    playAnimation(anim_landing.getAnimation(), 1.0f, 0);
                }
                realSquash = -fallDistance / 10.0f;
            } else {
                realSquash = Math.min(fallDistance / 20.0f, 0.25f);
            }

        }

        super.checkFallDamage(heightDifference, onGround, state, landedPosition);
    }

    public void playAnimation(KeyframeAnimation anim) {
        playAnimation(anim, 1.0f, 10);
    }

    private boolean modified = false;
    private boolean armAnimationsEnabled = true;

    public void playAnimation(KeyframeAnimation anim, float speed, int fade) {
        if (currentAnimation == anim || anim == null)
            return;

        currentAnimation = anim;
        ModifierLayer<IAnimation> animationContainer = modAnimationContainer;

        var builder = anim.mutableCopy();
        builder.leftArm.setEnabled(armAnimationsEnabled);
        builder.rightArm.setEnabled(armAnimationsEnabled);
        anim = builder.build();

        if (modified) {
            animationContainer.removeModifier(0);
        }
        modified = true;
        animationContainer.addModifierBefore(new SpeedModifier(speed));
        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fade, Ease.LINEAR),
                new KeyframeAnimationPlayer(anim));
        animationContainer.setupAnim(1.0f / 20.0f);
    }

    public void disableArmAnimations() {
        if (currentAnimation != null && armAnimationsEnabled) {
            armAnimationsEnabled = false;
            ModifierLayer<IAnimation> animationContainer = modAnimationContainer;

            var builder = currentAnimation.mutableCopy();

            builder.leftArm.setEnabled(false);
            builder.rightArm.setEnabled(false);

            currentAnimation = builder.build();

            if (modified) {
                animationContainer.removeModifier(0);
            }

            modified = true;
            
            animationContainer.addModifierBefore(new SpeedModifier(getSpeed()));
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR),
                    new KeyframeAnimationPlayer(currentAnimation));
            animationContainer.setupAnim(1.0f / 20.0f);
            animationContainer.tick();
        }
    }

    public void enableArmAnimations() {
        if (currentAnimation != null && !armAnimationsEnabled) {
            armAnimationsEnabled = true;
            ModifierLayer<IAnimation> animationContainer = modAnimationContainer;

            var builder = currentAnimation.mutableCopy();
            builder.leftArm.setEnabled(true);
            builder.rightArm.setEnabled(true);
            currentAnimation = builder.build();

            if (modified) {
                animationContainer.removeModifier(0);
            }
            modified = true;
            animationContainer.addModifierBefore(new SpeedModifier(getSpeed()));
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR),
                    new KeyframeAnimationPlayer(currentAnimation));

        }
    }

    public void playJumpAnimation() {
        realLeanMultiplier = 0;
        realSquash = -0.1f;

        jump_index++;
        jump_index %= 2;

        playAnimation(anim_jump[jump_index].getAnimation(), 1.0f, 0);
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
    }

    private int getCurrentSwingDuration() {
        if (MobEffectUtil.hasDigSpeed(this)) {
            return 6 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        } else {
            return this.hasEffect(MobEffects.DIG_SLOWDOWN)
                    ? 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2
                    : 6;
        }
    }

    @Override
    public void swing(InteractionHand hand) {
        super.swing(hand);

        if (getUseItem().getItem() instanceof FlintAndSteelItem && isUsingItem()) {
            return;
        }

        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {
            punch_index++;
            punch_index %= 2;

            ItemStack stack = getMainHandItem();

            boolean sword_animations = false;

            if (PlatformUtility.isModLoaded("bettercombat")) {
                sword_animations = false;
            }

            if (stack != null) {
                if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof PickaxeItem ||
                        stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem ||
                        stack.getItem() instanceof ShovelItem || stack.getItem() instanceof FishingRodItem) {
                    sword_animations = true;
                }
            }

            if (isShiftKeyDown()) {
                if (sword_animations) {
                    playAnimation(anim_sword_swing_sneak[punch_index].getAnimation(), 1.0f, 0);
                } else {
                    playAnimation(anim_punch_sneaking[punch_index].getAnimation(), 1.0f, 0);
                }
            } else {
                if (sword_animations) {
                    playAnimation(anim_sword_swing[punch_index].getAnimation(), 1.0f, 0);
                } else {
                    playAnimation(anim_punch[punch_index].getAnimation(), 1.0f, 0);
                }
            }
        }
    }

    public float getLeanAmount() {
        return leanAmount * 0.01f;
    }

    public float getLeanMultiplier() {
        return leanMultiplier;
    }

    public float getSquash() {
        return squash;
    }
}
