package elocindev.animation_overhaul.compat;

import elocindev.animation_overhaul.util.PlatformUtility;
import net.minecraft.client.player.AbstractClientPlayer;
import net.spell_engine.internals.casting.SpellCasterClient;

public class SpellEngineCompat {
    public static boolean shouldNotLetAnimate(AbstractClientPlayer player) {
        if (PlatformUtility.isModLoaded("spell_engine")) {
            if (player instanceof SpellCasterClient caster)

            if (caster.isCastingSpell() || caster.isBeaming()) return true;
        }

        return false;
    }
}
