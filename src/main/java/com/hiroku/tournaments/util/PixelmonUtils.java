package com.hiroku.tournaments.util;

import com.pixelmonmod.pixelmon.battles.api.rules.BattleProperty;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.PropertyValue;

import java.lang.reflect.Field;
import java.util.Map;

public class PixelmonUtils {
    private static Field properties_f = null;

    public static Map<BattleProperty<?>, PropertyValue<?>> getBRProperties(BattleRules br) {
        try {
            if (properties_f == null) {
                properties_f = BattleRules.class.getDeclaredField("properties");
                properties_f.setAccessible(true);
            }

            //noinspection unchecked
            return (Map<BattleProperty<?>, PropertyValue<?>>) properties_f.get(br);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
