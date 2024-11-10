package com.hiroku.tournaments.listeners;

import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.enums.EnumTournamentState;
import com.pixelmonmod.pixelmon.api.events.DynamaxEvent.BattleEvolve;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DynamaxListener
{
	@SubscribeEvent
	public void onDynamax(BattleEvolve event)
	{
		if (Tournament.instance() != null && Tournament.instance().state == EnumTournamentState.ACTIVE)
		{
			if (event.pw.pokemon.getOwnerPlayer() == null || event.pw.pokemon == null)
				return;
			if ((new PokemonSpec("rental")).matches(event.pw.pokemon) && (new PokemonSpec("!candynamax")).matches(event.pw.pokemon))
			{
				event.setCanceled(true);
				event.pw.pokemon.getOwnerPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Dynamax is blocked in the tournament!"));
			}
		}
	}
}