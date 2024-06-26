package elocindev.animation_overhaul.config;

import java.nio.file.Path;

import elocindev.animation_overhaul.registry.AOAnimationInstance;
import elocindev.animation_overhaul.api.HandAnimationProperties;
import elocindev.animation_overhaul.config.utils.IAnimConfig;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.necronomicon.config.Comment;
import elocindev.necronomicon.config.NecConfig;

public class AnimationsConfig implements IAnimConfig {
    public static final String FOLDER = "animation_overhaul";
    public static final String FILE_NAME = "animations.json5";
    public static final int CURRENT_CONFIG_VERSION = 2;

    @NecConfig
    public static AnimationsConfig INSTANCE;

    public static String getFile() {
        Path folder = Path.of(NecConfigAPI.getFile(FOLDER));

        if (!folder.toFile().exists())
            folder.toFile().mkdirs();
          

        return folder.toString()+"/"+FILE_NAME;
    }

    @Override
    public boolean isOutdated() {
        if (INSTANCE.CONFIG_VERSION != CURRENT_CONFIG_VERSION) {
            return true;
        }

        return false;
    }

    public String getFolder() {
        return FOLDER;
    }

    public String getFileName() {
        return FILE_NAME;
    }

    public AOAnimationInstance.States enabled_animations = AOAnimationInstance.getNewStates();
    public HandAnimationProperties hands_behavior = new HandAnimationProperties(true, true);

    @Comment("Don't touch this!")
    public int CONFIG_VERSION = 2;
}