package com.hiroku.tournaments.rules.player;

import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.hiroku.tournaments.obj.Team;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Rule demanding that players not change their team between battles. Team Change defaults to being
 * allowed. If it is allowed, players will be able to change their Pokémon prior to each battle.
 * 
 * @author MemencioPerez
 */
public class TeamChange extends PlayerRule
{
	/** Whether team change is allowed. If true, Pokémon can be changed prior to each battle. */
	public final boolean teamChangeAllowed;
	/** A mapping from a player's UUID to a list of the UUIDs of the Pokémon on a player's team. It is updated at the start of the tournament. */
	public HashMap<UUID, ArrayList<UUID>> playerPokemonUUIDs = new HashMap<>();

	public TeamChange(String arg) throws Exception
	{
		super(arg);

		teamChangeAllowed = Boolean.parseBoolean(arg);
	}

	@Override
	public boolean passes(Player player, PlayerPartyStorage storage)
	{
		if (teamChangeAllowed)
			return true;

		ArrayList<UUID> pokemonUUIDs = playerPokemonUUIDs.get(player.getUniqueId());
		if (pokemonUUIDs != null)
			for (Pokemon pokemon : storage.getTeam())
				if (pokemonUUIDs.size() != storage.getTeam().size() || !pokemonUUIDs.contains(pokemon.getUUID()))
					return false;
		return true;
	}

	@Override
	public void onTournamentStart(Tournament tournament) {
		for (Team team : tournament.teams)
		{
			for (User user : team.users)
			{
				try
				{
					PlayerPartyStorage storage =  Pixelmon.storageManager.getParty(user.getUniqueId());
					ArrayList<UUID> playerPokemonUUIDs = new ArrayList<>();
					for (Pokemon pokemon : storage.getTeam())
						playerPokemonUUIDs.add(pokemon.getUUID());
					this.playerPokemonUUIDs.put(user.getUniqueId(), playerPokemonUUIDs);
				}
				catch (NoSuchElementException nsee)
				{
					nsee.printStackTrace();
				}
			}
		}
	}

	@Override
	public Text getDisplayText()
	{
		return Text.of(TextColors.GOLD, "Changing Pokémon between battles: ", TextColors.DARK_AQUA, teamChangeAllowed ? "Allowed." : "Not allowed.");
	}

	@Override
	public boolean duplicateAllowed(RuleBase other)
	{
		return false;
	}

	@Override
	public boolean visibleToAll()
	{
		return true;
	}

	@Override
	public Text getBrokenRuleText(Player player)
	{
		return Text.of(TextColors.DARK_AQUA, player.getName(), TextColors.RED, " has changed or removed at least one of their Pokémon! ", TextStyles.ITALIC, "Disqualified!");
	}

	@Override
	public String getSerializationString()
	{
		return "teamchange:" + teamChangeAllowed;
	}
}
