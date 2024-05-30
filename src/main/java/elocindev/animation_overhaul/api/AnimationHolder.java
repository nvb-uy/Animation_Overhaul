package elocindev.animation_overhaul.api;

import org.jetbrains.annotations.Nullable;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;

public class AnimationHolder {
    public static AnimationHolder EMPTY = new AnimationHolder((KeyframeAnimation) null, false);

    private boolean isEnabled;
    @Nullable
    private KeyframeAnimation animation;
    
    public AnimationHolder(ResourceLocation animation_id, boolean isEnabled) {
        this(PlayerAnimationRegistry.getAnimation(animation_id), isEnabled);
    }

    public AnimationHolder(@Nullable KeyframeAnimation animation, boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.animation = animation;
    }

    public AnimationHolder() { this.isEnabled = false; this.animation = null; }

    public KeyframeAnimation getAnimation() {
        if (!isEnabled) return null;

        return animation;
    }

    public void setAnimation(KeyframeAnimation animation) {
        this.animation = animation;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean condition) {
        this.isEnabled = condition;
    }
}
