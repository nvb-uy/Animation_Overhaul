package elocindev.animation_overhaul.config;

import java.nio.file.Path;

import elocindev.animation_overhaul.config.utils.IAnimConfig;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.necronomicon.config.Comment;
import elocindev.necronomicon.config.NecConfig;

public class PlayerConfig implements IAnimConfig {
    public static final String FOLDER = "animation_overhaul";
    public static final String FILE_NAME = "local_player.json5";
    public static final int CURRENT_CONFIG_VERSION = 1;

    @NecConfig
    public static PlayerConfig INSTANCE;

    public static String getFile() {
        Path folder = Path.of(NecConfigAPI.getFile(FOLDER));

        if (!folder.toFile().exists())
            folder.toFile().mkdirs();
          

        return folder.toString()+"/"+FILE_NAME;
    }

    @Override
    public boolean isOutdated() {
        if (this.CONFIG_VERSION != CURRENT_CONFIG_VERSION) {
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

    @Comment("Enables a leaning when moving, and squashing when falling from high distances.")
    @Comment("This may lead into some mod incompatibilities with other mods, as it's replacing the rendering of the player!")
    public boolean enable_leaning_and_squash = false;

    @Comment("Don't touch this!")
    public int CONFIG_VERSION = 1;
}