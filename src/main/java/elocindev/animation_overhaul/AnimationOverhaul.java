package elocindev.animation_overhaul;

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
//$$ { public AnimationOverhaul() {}
//#endif

    public static String MODID = "animation_overhaul";
    
    //#if FABRIC==1
    @Override
    public void onInitialize() {}
    //#endif
}