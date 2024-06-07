package elocindev.animation_overhaul.config.utils;

import java.nio.file.Path;

import elocindev.animation_overhaul.AnimationOverhaul;
import elocindev.animation_overhaul.config.AnimationsConfig;
import elocindev.animation_overhaul.config.PlayerConfig;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;

public class ConfigLoader {
    public static boolean checkOutdated(Object[] configs) {
        for (Object config : configs) {
            if (config instanceof IAnimConfig aconfig && aconfig.isOutdated()) {
                
    
                updateConfig(aconfig.getFolder(), aconfig.getFileName());
    
                return true;
            }
        }

        return false;
    }

    public static void reloadConfigs() {
        registerConfigs();

        AnimationOverhaul.ANIM_CONFIG = AnimationsConfig.INSTANCE;
        AnimationOverhaul.LOCAL_PLAYER_CONFIG = PlayerConfig.INSTANCE;
    
        if (AnimationsConfig.INSTANCE.isOutdated()) {
            AnimationOverhaul.LOGGER.warn(String.format("The 'animations' Animation Overhaul config is outdated! It has been updated to the latest version and renamed to animation-old.json5"));

            registerConfigs();
        }
        if (PlayerConfig.INSTANCE.isOutdated()) {
            AnimationOverhaul.LOGGER.warn(String.format("The 'local_player' Animation Overhaul config is outdated! It has been updated to the latest version and renamed to local_player-old.json5"));

            registerConfigs();
        }
    }

    public static void registerConfigs() {
        NecConfigAPI.registerConfig(AnimationsConfig.class);
        NecConfigAPI.registerConfig(PlayerConfig.class);
    }

    public static void updateConfig(String FOLDER, String FILE_NAME) {
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
}
