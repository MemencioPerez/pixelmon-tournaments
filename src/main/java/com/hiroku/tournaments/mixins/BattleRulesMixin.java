package com.hiroku.tournaments.mixins;

import com.pixelmonmod.pixelmon.battles.api.rules.BattleProperty;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.PropertyValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = BattleRules.class, remap = false)
public class BattleRulesMixin {

    @Shadow
    private Map<BattleProperty<?>, PropertyValue<?>> properties;

    /**
     * @author MemencioPerez
     * @reason Fix method return value by appending a newline character after each exported BattleProperty
     */
    @Overwrite
    public String exportText() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<BattleProperty<?>, PropertyValue<?>> entry : this.properties.entrySet()) {
            if (builder.length() != 0) {
                builder.append("\n");
            }

            builder.append(entry.getKey().getId()).append(": ").append(entry.getValue().get().toString());
        }
        return builder.toString();
    }
}
