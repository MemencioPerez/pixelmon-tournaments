package com.hiroku.tournaments.mixins;

import com.pixelmonmod.pixelmon.api.util.IEncodeable;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BattleClause.class, remap = false)
public abstract class BattleClauseMixin implements Comparable<BattleClause>, IEncodeable {

    /**
     * @author MemencioPerez
     * @reason Fix method return value by returning {@link BattleClause#getID()} instead of {@link BattleClause#getLocalizedName()}
     */
    @Overwrite
    public String toString() {
        return ((BattleClause)(Object)this).getID(); // Return the ID instead of the localized name
    }
}