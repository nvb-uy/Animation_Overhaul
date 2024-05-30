package elocindev.animation_overhaul.registry;

public class AOAnimationInstance {
    public class States {
        public boolean idle = true;
        public boolean sneak_idle = true;
        public boolean sneak_walk = true;
        public boolean walk = true;
        public boolean run = true;
        public boolean turn_right = true;
        public boolean turn_left = true;
        public boolean falling = true;
        public boolean landing = true;
        public boolean swimming = true;
        public boolean swim_idle = true;
        public boolean crawl_idle = true;
        public boolean crawling = true;
        public boolean eating = true;
        public boolean climbing = true;
        public boolean climbing_idle = true;
        public boolean sprint_stop = true;
        public boolean fence_idle = true;
        public boolean fence_walk = true;
        public boolean edge_idle = true;
        public boolean elytra_fly = true;
        public boolean flint_and_steel = true;
        public boolean flint_and_steel_sneak = true;
        public boolean boat_idle = true;
        public boolean boat_left_paddle = true;
        public boolean boat_right_paddle = true;
        public boolean boat_forward = true;
        public boolean rolling = true;
        public boolean jump = true;
        public boolean fall = true;
        public boolean punch = true;
        public boolean punch_sneaking = true;
        public boolean sword_swing = true;
        public boolean sword_swing_sneak = true;
    }

    public static States getNewStates() {
        return new AOAnimationInstance().
            new States();
    }
    
    public AOAnimationInstance() {}
}
