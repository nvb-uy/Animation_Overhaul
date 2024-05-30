package elocindev.animation_overhaul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elocindev.animation_overhaul.config.AnimationOverhaulConfig;
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

    public static AnimationOverhaulConfig CONFIG;
    public static String MODID = "animation_overhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger("animation_overhaul");

    //#if FABRIC==1
    @Override
    public void onInitialize() {
    //#else
    //$$ public AnimationOverhaul() {
    //#endif
        NecConfigAPI.registerConfig(AnimationOverhaulConfig.class);
        CONFIG = AnimationOverhaulConfig.INSTANCE;
    }
}