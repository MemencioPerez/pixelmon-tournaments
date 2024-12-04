package com.hiroku.tournaments.listeners;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.enums.TournamentStates;
import com.pixelmonmod.api.pokemon.requirement.impl.HasSpecFlagRequirement;
import com.pixelmonmod.pixelmon.api.events.DynamaxEvent;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DynamaxListener {
    @SubscribeEvent
    public void onDynamax(DynamaxEvent.BattleEvolve event) {
        if (Tournament.instance() != null && Tournament.instance().state == TournamentStates.ACTIVE) {
            if (event.pw.pokemon.getOwnerPlayer() == null || event.pw.pokemon == null)
                return;
            if ((new HasSpecFlagRequirement("rental")).isDataMatch(event.pw.pokemon) && (new HasSpecFlagRequirement("!candynamax")).isDataMatch(event.pw.pokemon)) {
                event.setCanceled(true);
                event.pw.pokemon.getOwnerPlayer().sendMessage(Text.of(TextFormatting.RED, "Dynamax is blocked in the tournament!"), Util.DUMMY_UUID);
            }
        }
    }
}
