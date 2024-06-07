package elocindev.animation_overhaul.api;

public class HandAnimationProperties {
    public boolean  punch  = true;
    public boolean  mining = false;

    public HandAnimationProperties() {}

    public HandAnimationProperties(boolean punch, boolean mining) {
        this.punch = punch;
        this.mining = mining;
    }

    public HandAnimationProperties setPunch(boolean enabled) {
        this.punch = enabled;

        return this;
    }

    public HandAnimationProperties setMining(boolean enabled) {
        this.mining = enabled;

        return this;
    }
}