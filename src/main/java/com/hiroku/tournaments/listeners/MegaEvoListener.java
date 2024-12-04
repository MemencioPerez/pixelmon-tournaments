package com.hiroku.tournaments.listeners;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.enums.TournamentStates;
import com.hiroku.tournaments.rules.player.DisallowedMechanic;
import com.pixelmonmod.api.pokemon.requirement.impl.HasSpecFlagRequirement;
import com.pixelmonmod.pixelmon.api.events.MegaEvolutionEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MegaEvoListener {
    @SubscribeEvent
    public void onMegaEvolve(MegaEvolutionEvent.Battle event) {
        if (Tournament.instance() != null && Tournament.instance().state == TournamentStates.ACTIVE) {
            PixelmonWrapper pw = event.getPixelmonWrapper();
            if (pw.pokemon.getOwnerPlayer() == null || pw.pokemon == null)
                return;
            DisallowedMechanic disallowedMechanicRule = Tournament.instance().getRuleSet().getRule(DisallowedMechanic.class);
            if (disallowedMechanicRule != null && disallowedMechanicRule.isMegaEvolutionDisallowed() || (new HasSpecFlagRequirement("rental")).isDataMatch(pw.pokemon) && (new HasSpecFlagRequirement("!canmegaevolve")).isDataMatch(pw.pokemon)) {
                event.setCanceled(true);
                pw.pokemon.getOwnerPlayer().sendMessage(Text.of(TextFormatting.RED + "Mega Evolution is blocked in the tournament!"), Util.DUMMY_UUID);
            }
        }
    }
}
