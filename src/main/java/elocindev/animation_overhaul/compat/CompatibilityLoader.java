package elocindev.animation_overhaul.compat;

import elocindev.animation_overhaul.util.PlatformUtility;

public class CompatibilityLoader {
    
    public static boolean BETTER_COMBAT = false;
    public static boolean SPELL_ENGINE = false;

    public static void refresh() {
        if (PlatformUtility.isModLoaded("bettercombat")) BETTER_COMBAT = true;
        if (PlatformUtility.isModLoaded("spell_engine")) SPELL_ENGINE = true;
    }
}
