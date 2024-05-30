package elocindev.animation_overhaul.config;

import elocindev.animation_overhaul.registry.AOAnimationInstance;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.necronomicon.config.NecConfig;

public class AnimationOverhaulConfig {
    @NecConfig
    public static AnimationOverhaulConfig INSTANCE;

    public static String getFile() {
        return NecConfigAPI.getFile("animation_overhaul.json5");
    }

    public AOAnimationInstance.States enabled_animations = AOAnimationInstance.getNewStates();
}