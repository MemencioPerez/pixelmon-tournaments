package com.hiroku.tournaments.mixins;

import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.battles.api.rules.PropertyValue;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.property.ClausesProperty;
import com.pixelmonmod.pixelmon.battles.api.rules.value.ClausesValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;
import java.util.Set;

@Mixin(value = ClausesProperty.class, remap = false)
public class ClausesPropertyMixin {

    /**
     * @author MemencioPerez
     * @reason Fix method return value by adding a String parser
     */
    @Overwrite
    @SuppressWarnings("unchecked")
    public Optional<PropertyValue<Set<BattleClause>>> parse(Object o) {
        if (o instanceof ClausesValue) {
            return Optional.of((ClausesValue)o);
        } else if (o instanceof Set) {
            return Optional.of(new ClausesValue((Set<BattleClause>)o));
        } else if (o instanceof String) {
            String[] clauseIDs = ((String) o).replaceAll("[\\[\\]]", "").split(", ");
            Set<BattleClause> clauses = Sets.newHashSet();
            for (String clauseID : clauseIDs) {
                clauses.add(BattleClauseRegistry.getClause(clauseID));
            }
            return Optional.of(new ClausesValue(clauses));
        }
        return Optional.empty();
    }
}
