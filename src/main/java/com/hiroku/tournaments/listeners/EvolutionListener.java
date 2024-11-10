package com.hiroku.tournaments.listeners;

import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.enums.EnumTournamentState;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent.PreEvolve;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EvolutionListener
{
	@SubscribeEvent
	public void onEvolution(PreEvolve event)
	{
		if (Tournament.instance() != null && (Tournament.instance().state == EnumTournamentState.ACTIVE || Tournament.instance().state == EnumTournamentState.OPEN))
		{
			if (event.preEvo == null || event.player == null)
				return;
			if ((new PokemonSpec("rental")).matches(event.preEvo) && (new PokemonSpec("!canevolve")).matches(event.preEvo))
			{
				event.setCanceled(true);
				event.player.sendMessage(new TextComponentString(TextFormatting.RED + "Evolution cancelled; one of the Pokémon was rented for a tournament!"));
			}
		}
	}
}