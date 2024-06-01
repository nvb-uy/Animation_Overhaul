package elocindev.animation_overhaul.registry;

import elocindev.animation_overhaul.api.AnimationProperties;

public class AOAnimationInstance {
    public class States {
        public AnimationProperties idle = new AnimationProperties();
        public AnimationProperties sneak_idle = new AnimationProperties();
        public AnimationProperties sneak_walk = new AnimationProperties();
        public AnimationProperties walk = new AnimationProperties();
        public AnimationProperties run = new AnimationProperties();
        public AnimationProperties turn_right = new AnimationProperties();
        public AnimationProperties turn_left = new AnimationProperties();
        public AnimationProperties falling = new AnimationProperties();
        public AnimationProperties slow_falling = new AnimationProperties().setSpeed(0.5f);
        public AnimationProperties fall = new AnimationProperties();
        public AnimationProperties landing = new AnimationProperties();
        public AnimationProperties swimming = new AnimationProperties();
        public AnimationProperties swim_idle = new AnimationProperties();
        public AnimationProperties crawl_idle = new AnimationProperties().setFade(0);
        public AnimationProperties crawling = new AnimationProperties().setFade(0);
        public AnimationProperties eating = new AnimationProperties();
        public AnimationProperties drinking = new AnimationProperties();
        public AnimationProperties climbing = new AnimationProperties();
        public AnimationProperties climbing_idle = new AnimationProperties();
        public AnimationProperties sprint_stop = new AnimationProperties().setFade(2);
        public AnimationProperties fence_idle = new AnimationProperties();
        public AnimationProperties fence_walk = new AnimationProperties();
        public AnimationProperties edge_idle = new AnimationProperties();
        public AnimationProperties elytra_fly = new AnimationProperties();
        public AnimationProperties flint_and_steel = new AnimationProperties();
        public AnimationProperties flint_and_steel_sneak = new AnimationProperties();
        public AnimationProperties boat_idle = new AnimationProperties();
        public AnimationProperties boat_left_paddle = new AnimationProperties();
        public AnimationProperties boat_right_paddle = new AnimationProperties();
        public AnimationProperties boat_forward = new AnimationProperties();
        public AnimationProperties rolling = new AnimationProperties();
        public AnimationProperties jump = new AnimationProperties();
        public AnimationProperties punch = new AnimationProperties();
        public AnimationProperties punch_sneaking = new AnimationProperties();
        public AnimationProperties sword_swing = new AnimationProperties();
        public AnimationProperties sword_swing_sneak = new AnimationProperties();
    }

    public static States getNewStates() {
        return new AOAnimationInstance().
            new States();
    }
    
    public AOAnimationInstance() {}
}
