package com.hiroku.tournaments.rules.player;

import com.happyzleaf.tournaments.User;
import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.hiroku.tournaments.obj.Team;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

/**
 * Rule demanding that players not change their team between battles. Team Change defaults to being
 * allowed. If it is allowed, players will be able to change their Pokémon prior to each battle.
 *
 * @author MemencioPerez
 */
public class TeamChange extends PlayerRule {
	/**
	 * Whether team change is allowed. If true, Pokémon can be changed prior to each battle.
	 */
	public final boolean teamChangeAllowed;
	/**
	 * A mapping from a player's UUID to a list of the UUIDs of the Pokémon on a player's team. It is updated at the start of the tournament.
	 */
	public HashMap<UUID, ArrayList<UUID>> playerPokemonUUIDs = new HashMap<>();

	public TeamChange(String arg) throws Exception {
		super(arg);

		teamChangeAllowed = Boolean.parseBoolean(arg);
	}

	@Override
	public boolean passes(PlayerEntity player, PlayerPartyStorage storage) {
		if (teamChangeAllowed)
			return true;

		ArrayList<UUID> pokemonUUIDs = playerPokemonUUIDs.get(player.getUniqueID());
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
				UUID playerUUID = user.getPlayer().getUniqueID();
				PlayerPartyStorage storage = StorageProxy.getParty(playerUUID);
				ArrayList<UUID> playerPokemonUUIDs = new ArrayList<>();
				for (Pokemon pokemon : storage.getTeam())
					playerPokemonUUIDs.add(pokemon.getUUID());
				this.playerPokemonUUIDs.put(playerUUID, playerPokemonUUIDs);
			}
		}
	}

	@Override
	public Text getDisplayText() {
		return Text.of(TextFormatting.GOLD, "Changing Pokémon between battles: ", TextFormatting.DARK_AQUA, teamChangeAllowed ? "Allowed." : "Not allowed.");
	}

	@Override
	public boolean duplicateAllowed(RuleBase other) {
		return false;
	}

	@Override
	public boolean visibleToAll() {
		return true;
	}

	@Override
	public Text getBrokenRuleText(PlayerEntity player) {
		return Text.of(TextFormatting.DARK_AQUA, player.getName(), TextFormatting.RED, " has changed at least one of their Pokémon! ", TextFormatting.ITALIC, "Disqualified!");
	}

	@Override
	public String getSerializationString() {
		return "teamchange:" + teamChangeAllowed;
	}
}
