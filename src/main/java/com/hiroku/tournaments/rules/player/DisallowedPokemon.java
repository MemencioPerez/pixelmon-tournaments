package com.hiroku.tournaments.rules.player;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.hiroku.tournaments.api.tiers.Tier;
import com.hiroku.tournaments.util.PixelmonUtils;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.util.helpers.CollectionHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class DisallowedPokemon extends PlayerRule {
	public final List<String> pokemons = new ArrayList<>();
	public final List<Tier> tiers = new ArrayList<>();

	public DisallowedPokemon(String arg) throws Exception {
		super(arg);

		String[] splits = arg.split(",");
		for (String name : splits) {
			String str = name.replace("_", " ");
			if (PixelmonUtils.getFormData(str).isPresent() || PixelmonSpecies.has(str))
				pokemons.add(str);
			else if (Tier.parse(name) != null)
				tiers.add(Tier.parse(name));
			else
				throw new Exception("Invalid Pokémon or tier. These are case sensitive, and without spaces. e.g. Pikachu. Use _ instead of space");
		}
	}

	@Override
	public boolean passes(PlayerEntity player, PlayerPartyStorage storage) {
		for (Pokemon pokemon : storage.getTeam())
			if (pokemons.contains(PixelmonUtils.getFormName(pokemon)))
				return false;
			else if (CollectionHelper.find(tiers, tier -> tier.condition.test(pokemon)) != null)
				return false;
		return true;
	}

	@Override
	public Text getDisplayText() {
		Text.Builder builder = Text.builder();

		if (!pokemons.isEmpty())
			builder.append(Text.of(TextFormatting.DARK_AQUA, pokemons.get(0)));
		if (!tiers.isEmpty())
			if (pokemons.isEmpty())
				builder.append(Text.of(TextFormatting.DARK_AQUA, tiers.get(0).key));
			else
				builder.append(Text.of(TextFormatting.GOLD, ", ", TextFormatting.DARK_AQUA, tiers.get(0).key));
		for (int i = 1; i < pokemons.size(); i++)
			builder.append(Text.of(TextFormatting.GOLD, ", ", TextFormatting.DARK_AQUA, pokemons.get(i)));
		for (int i = 1; i < tiers.size(); i++)
			builder.append(Text.of(TextFormatting.GOLD, ", ", TextFormatting.DARK_AQUA, tiers.get(i)));
		return Text.of(TextFormatting.GOLD, "Disallowed Pokémon/tier(s): ", builder.build());
	}

	@Override
	public Text getBrokenRuleText(PlayerEntity player) {
		return Text.of(TextFormatting.DARK_AQUA, player.getName(), TextFormatting.RED, " has a disallowed Pokémon!");
	}

	@Override
	public boolean duplicateAllowed(RuleBase other) {
		// Transfers this rule's Pokémon list into the rule that's about to replace it.
		DisallowedPokemon disallowed = (DisallowedPokemon) other;
		for (String pokemon : this.pokemons)
			if (!disallowed.pokemons.contains(pokemon))
				disallowed.pokemons.add(pokemon);
		for (Tier tier : this.tiers)
			if (!disallowed.tiers.contains(tier))
				disallowed.tiers.add(tier);

		return false;
	}

	@Override
	public String getSerializationString() {
		StringBuilder serialize = new StringBuilder(pokemons.get(0));
		for (int i = 1; i < pokemons.size(); i++)
			serialize.append(",").append(pokemons.get(i));
		return "disallowedpokemon:" + serialize;
	}

	@Override
	public boolean visibleToAll() {
		return true;
	}
}
