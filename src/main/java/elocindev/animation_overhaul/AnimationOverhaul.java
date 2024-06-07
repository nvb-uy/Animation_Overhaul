package elocindev.animation_overhaul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elocindev.animation_overhaul.compat.CompatibilityLoader;
import elocindev.animation_overhaul.config.AnimationsConfig;
import elocindev.animation_overhaul.config.PlayerConfig;
import elocindev.animation_overhaul.config.utils.ConfigLoader;

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

    public static AnimationsConfig ANIM_CONFIG;
    public static PlayerConfig LOCAL_PLAYER_CONFIG;


    public static String MODID = "animation_overhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger("animation_overhaul");

    //#if FABRIC==1
    @Override
    public void onInitialize() {
    //#else
    //$$ public AnimationOverhaul() {
    //#endif
        CompatibilityLoader.refresh();
        ConfigLoader.reloadConfigs();

        LOGGER.info("Animation Overhaul's Config initialized");
    }
}