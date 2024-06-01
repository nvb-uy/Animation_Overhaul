package elocindev.animation_overhaul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elocindev.animation_overhaul.config.AnimationsConfig;
import elocindev.necronomicon.api.config.v1.NecConfigAPI;

//#if FABRIC==1

import net.fabricmc.api.ModInitializer;

//#else

//$$ import net.minecraftforge.fml.common.Mod;
//$$ @Mod("animation_overhaul")
//#endif
public class AnimationOverhaul
//#if FABRIC==1
    implements ModInitializer {
//#else
//$$ {
//#endif

    public static AnimationsConfig CONFIG;
    public static String MODID = "animation_overhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger("animation_overhaul");

    //#if FABRIC==1
    @Override
    public void onInitialize() {
    //#else
    //$$ public AnimationOverhaul() {
    //#endif
        NecConfigAPI.registerConfig(AnimationsConfig.class);
        CONFIG = AnimationsConfig.INSTANCE;

        if (CONFIG.isOutdated()) {
            LOGGER.warn("Your Animation Overhaul config is outdated! It has been updated to the latest version and your old config was renamed to animation_overhaul.json5.old");

            AnimationsConfig.updateConfig();

            NecConfigAPI.registerConfig(AnimationsConfig.class);
            CONFIG = AnimationsConfig.INSTANCE;
        }

        LOGGER.info("Animation Overhaul's Config initialized");
    }
}