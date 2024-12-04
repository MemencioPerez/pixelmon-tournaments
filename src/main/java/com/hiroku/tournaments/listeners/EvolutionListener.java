package com.hiroku.tournaments.listeners;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.enums.TournamentStates;
import com.pixelmonmod.api.pokemon.requirement.impl.HasSpecFlagRequirement;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EvolutionListener {
    @SubscribeEvent
    public void onEvolution(EvolveEvent.Pre event) {
        if (Tournament.instance() != null && (Tournament.instance().state == TournamentStates.ACTIVE || Tournament.instance().state == TournamentStates.OPEN)) {
            if (event.getPokemon() == null || event.getPlayer() == null)
                return;
            if ((new HasSpecFlagRequirement("rental")).isDataMatch(event.getPokemon()) && (new HasSpecFlagRequirement("!canevolve")).isDataMatch(event.getPokemon())) {
                event.setCanceled(true);
                event.getPlayer().sendMessage(Text.of(TextFormatting.RED + "Evolution cancelled; one of the Pokémon was rented for a tournament!"), Util.DUMMY_UUID);
            }
        }
    }
}
