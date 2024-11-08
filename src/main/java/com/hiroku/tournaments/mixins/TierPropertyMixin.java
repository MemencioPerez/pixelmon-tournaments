package com.hiroku.tournaments.mixins;

import com.pixelmonmod.pixelmon.battles.api.rules.PropertyValue;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleTierRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.tiers.Tier;
import com.pixelmonmod.pixelmon.battles.api.rules.property.TierProperty;
import com.pixelmonmod.pixelmon.battles.api.rules.value.TierValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(value = TierProperty.class, remap = false)
public class TierPropertyMixin {

    /**
     * @author MemencioPerez
     * @reason Fix method return value by adding a String parser
     */
    @Overwrite
    public Optional<PropertyValue<Tier>> parse(Object o) {
        if (o instanceof Tier) {
            return Optional.of(new TierValue((Tier)o));
        } else {
            return o instanceof String ? Optional.of(new TierValue(BattleTierRegistry.getTierWithID((String) o))) : Optional.empty();
        }
    }
}
