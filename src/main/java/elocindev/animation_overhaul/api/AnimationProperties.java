package elocindev.animation_overhaul.api;

public class AnimationProperties {
    public boolean  enabled = true;
    public float    speed   = 1.0f;
    public int      fade    = 5;

    public AnimationProperties() {}

    public AnimationProperties(boolean enabled, float speed, int fade) {
        this.enabled = enabled;
        this.speed = speed;
        this.fade = fade;
    }

    public AnimationProperties setEnabled(boolean enabled) {
        this.enabled = enabled;

        return this;
    }

    public AnimationProperties setSpeed(float speed) {
        this.speed = speed;

        return this;
    }

    public AnimationProperties setFade(int fade) {
        this.fade = fade;

        return this;
    }
}