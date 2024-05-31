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
    private void animation_overhaul$init(ClientLevel level, GameProfile profile,
        //#if MC==11902
        //$$@org.jetbrains.annotations.Nullable net.minecraft.world.entity.player.ProfilePublicKey key,
        //#endif
        CallbackInfo info
    ) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayer) (Object) this).addAnimLayer(850, modAnimationContainer);

        var cfg = AnimationOverhaul.CONFIG.enabled_animations;

        anim_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "idle"), cfg.idle.enabled);
        anim_fall[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fall_first"), cfg.fall.enabled);
        anim_fall[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fall_second"), cfg.fall.enabled);
        anim_jump[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "jump_first"), cfg.jump.enabled);
        anim_jump[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "jump_second"), cfg.jump.enabled);
        anim_sneak_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sneak_idle"), cfg.sneak_idle.enabled);
        anim_sneak_walk = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sneak_walk"), cfg.sneak_walk.enabled);
        anim_walk = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "walking"), cfg.walk.enabled);
        anim_run = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "running"), cfg.run.enabled);
        anim_turn_right = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "turn_right"), cfg.turn_right.enabled);
        anim_turn_left = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "turn_left"), cfg.turn_left.enabled);
        anim_punch[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_right"), cfg.punch.enabled);
        anim_punch[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_left"), cfg.punch.enabled);
        anim_punch_sneaking[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_right_sneak"), cfg.punch_sneaking.enabled);
        anim_punch_sneaking[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "punch_left_sneak"), cfg.punch_sneaking.enabled);
        anim_sword_swing[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_first"), cfg.sword_swing.enabled);
        anim_sword_swing[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_second"), cfg.sword_swing.enabled);
        anim_sword_swing_sneak[0] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_sneak_first"), cfg.sword_swing_sneak.enabled);
        anim_sword_swing_sneak[1] = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sword_swing_sneak_second"), cfg.sword_swing_sneak.enabled);
        anim_falling = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "falling"), cfg.falling.enabled);
        anim_landing = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "landing"), cfg.landing.enabled);
        anim_swimming = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "swimming"), cfg.swimming.enabled);
        anim_swim_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "swim_idle"), cfg.swim_idle.enabled);
        anim_crawl_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "crawl_idle"), cfg.crawl_idle.enabled);
        anim_crawling = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "crawling"), cfg.crawling.enabled);
        anim_eating = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "eating"), cfg.eating.enabled);
        anim_climbing = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "climbing"), cfg.climbing.enabled);
        anim_climbing_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "climbing_idle"), cfg.climbing_idle.enabled);
        anim_sprint_stop = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "sprint_stop"), cfg.sprint_stop.enabled);
        anim_fence_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fence_idle"), cfg.fence_idle.enabled);
        anim_fence_walk = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "fence_walk"), cfg.fence_walk.enabled);
        anim_edge_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "edge_idle"), cfg.edge_idle.enabled);
        anim_elytra_fly = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "elytra_fly"), cfg.elytra_fly.enabled);
        anim_flint_and_steel = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "flint_and_steel"), cfg.flint_and_steel.enabled);
        anim_flint_and_steel_sneak = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "flint_and_steel_sneak"), cfg.flint_and_steel_sneak.enabled);
        anim_boat_idle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_idle"), cfg.boat_idle.enabled);
        anim_boat_forward = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_forward"), cfg.boat_forward.enabled);
        anim_boat_right_paddle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_right_paddle"), cfg.boat_right_paddle.enabled);
        anim_boat_left_paddle = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "boat_left_paddle"), cfg.boat_left_paddle.enabled);
        anim_rolling = new AnimationHolder(new ResourceLocation(AnimationOverhaul.MODID, "rolling"), cfg.rolling.enabled);
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
        
        AnimationHolder animationToPlay = AnimationHolder.EMPTY;

        boolean onGroundInWater = isUnderWater() && this.getBlockStateOn().getCollisionShape(this.level(), this.blockPosition()).isEmpty();
        
        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {
            if (isPassenger() && getVehicle() instanceof Boat) {
                animationToPlay = anim_boat_idle;
                
                boolean left_paddle = ((Boat) getVehicle()).getPaddleState(0);
                boolean right_paddle = ((Boat) getVehicle()).getPaddleState(1);

                if (left_paddle && right_paddle) {
                    animationToPlay = anim_boat_forward;
                } else if (left_paddle) {
                    animationToPlay = anim_boat_left_paddle;
                } else if (right_paddle) {
                    animationToPlay = anim_boat_right_paddle;
                }
            } else if (level.getBlockState(blockPosition()).getBlock() instanceof LadderBlock && !onGround() && !jumping) {
                animationToPlay = anim_climbing_idle;

                if (getDeltaMovement().y > 0) {
                    animationToPlay = anim_climbing;                    
                }
            } else if (isUsingItem() && getMainHandItem().getItem().isEdible()) {
                animationToPlay = anim_eating;
            } else if (isFallFlying()) {
                animationToPlay = anim_elytra_fly;
            } else if (onGround() || onGroundInWater) {
                animationToPlay = anim_idle;

                if (onFence) {
                    animationToPlay = anim_fence_idle;
                } else if (onEdge) {
                    animationToPlay = anim_edge_idle;
                }

                if (turnDelta != 0 && !onEdge) {
                    animationToPlay = anim_turn_right;
                    if (turnDelta < 0) {
                        animationToPlay = anim_turn_left;
                    }
                }

                if ((isInWaterOrBubble() || isInLava()) && !onGroundInWater) {
                    if (this.isSwimming() || this.isSprinting()) {
                        animationToPlay = anim_swimming;
                    }
                } else if (isVisuallyCrawling()) {
                    if (isWalking) {
                        animationToPlay = anim_crawling;
                    } else {
                        animationToPlay = anim_crawl_idle;
                    }
                } else if (isShiftKeyDown()) {
                    animationToPlay = anim_sneak_idle;

                    if (isWalking || turnDelta != 0) {
                        animationToPlay = anim_sneak_walk;
                    }
                } else {
                    if (isWalking) {
                        if (momentum > 1 && !isWalkingForwards) {
                            animationToPlay = anim_sprint_stop;                    
                        } else {
                            if (isSprinting() && !isUsingItem()) {
                                animationToPlay = anim_run;
                            } else {
                                animationToPlay = anim_walk;
                                if (onFence) {
                                    animationToPlay = anim_fence_walk;
                                }
                            }
                        }
                    }
                }
            } else {

                if (isInWaterOrBubble() || isInLava()) {
                    if (this.isSwimming() || this.isSprinting()) {
                        animationToPlay = anim_swimming;
                    } else {
                        animationToPlay = anim_swim_idle;
                    }
                } else {
                    if (this.fallDistance > 1) {
                        if (this.fallDistance > 3) {
                            animationToPlay = anim_falling;
                        } else {
                            animationToPlay = anim_fall[jump_index];
                        }
                    }
                }
            }
        }
        
        playAnimation(animationToPlay.getAnimation(), animationToPlay.getSpeed(), animationToPlay.getFade());

        if (this.isUsingItem()) {
            if (this.getUseItem() != null) {
                var action = getUseItem().getUseAnimation();
                if (action == UseAnim.BOW || action == UseAnim.CROSSBOW || action == UseAnim.SPYGLASS
                        || action == UseAnim.SPEAR || action == UseAnim.TOOT_HORN || action == UseAnim.BLOCK ||
                        action == UseAnim.DRINK) {
                    disableArmAnimations();
                } else if (getUseItem().getItem() instanceof FlintAndSteelItem) {
                    animationToPlay = anim_flint_and_steel;

                    if (isShiftKeyDown()) {
                        animationToPlay = anim_flint_and_steel_sneak;
                    }

                    playAnimation(animationToPlay.getAnimation(), animationToPlay.getSpeed(), animationToPlay.getFade());
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

        AnimationHolder anim = anim_jump[jump_index];

        if (anim != null)
            playAnimation(anim.getAnimation(), anim.getSpeed(), anim.getFade());
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
