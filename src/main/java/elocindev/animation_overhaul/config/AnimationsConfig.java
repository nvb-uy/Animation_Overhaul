package elocindev.animation_overhaul.config;

import java.nio.file.Path;

import elocindev.animation_overhaul.registry.AOAnimationInstance;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.necronomicon.config.Comment;
import elocindev.necronomicon.config.NecConfig;

public class AnimationsConfig {
    public static final String FOLDER = "animation_overhaul";
    public static final String FILE_NAME = "animations.json5";
    public static final int CURRENT_CONFIG_VERSION = 1;

    @NecConfig
    public static AnimationsConfig INSTANCE;

    public static String getFile() {
        Path folder = Path.of(NecConfigAPI.getFile(FOLDER));

        if (!folder.toFile().exists())
            folder.toFile().mkdirs();
          

        return folder.toString()+"/"+FILE_NAME;
    }

    public static void updateConfig() {
        Path folder = Path.of(NecConfigAPI.getFile(FOLDER));

        if (!folder.toFile().exists())
            folder.toFile().mkdirs();

        String oldFile = folder.toString()+"/"+FILE_NAME;
        String newFile = folder.toString()+"/"+FILE_NAME+".old";

        Path oldPath = Path.of(oldFile);
        Path newPath = Path.of(newFile);

        if (oldPath.toFile().exists()) {
            oldPath.toFile().renameTo(newPath.toFile());
        }
    }

    public boolean isOutdated() {
        if (this.CONFIG_VERSION != CURRENT_CONFIG_VERSION) {
            return true;
        }

        return false;
    }

    public AOAnimationInstance.States enabled_animations = AOAnimationInstance.getNewStates();

    @Comment("Don't touch this!")
    public int CONFIG_VERSION = 1;
}