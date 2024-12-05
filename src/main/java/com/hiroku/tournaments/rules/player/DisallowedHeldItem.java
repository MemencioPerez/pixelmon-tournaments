package com.hiroku.tournaments.rules.player;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.items.HeldItem;
import com.pixelmonmod.pixelmon.items.heldItems.NoItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class DisallowedHeldItem extends PlayerRule {
	public final List<String> items = new ArrayList<>();
	public boolean asWhitelist = false;

	public DisallowedHeldItem(String arg) throws Exception {
		super(arg);

		String[] splits = arg.split(",");
		for (String name : splits) {
			if (name.equals("as_whitelist")) {
				asWhitelist = true;
				continue;
			}
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
			if (item instanceof HeldItem)
				items.add(name);
			else
				throw new Exception("Invalid held item name. These are case sensitive, and without spaces. e.g. pixelmon:choice_scarf. Use _ instead of space");
		}
	}

	@Override
	public boolean passes(PlayerEntity player, PlayerPartyStorage storage) {
		for (Pokemon pokemon : storage.getTeam()) {
			HeldItem heldItem = pokemon.getHeldItemAsItemHeld();
			ResourceLocation location = heldItem.getRegistryName();
			if (heldItem != NoItem.noItem && location != null) {
				boolean itemInList = items.contains(location.toString());
				if (itemInList != asWhitelist)
					return false;
			}
		}
		return true;
	}

	@Override
	public Text getBrokenRuleText(PlayerEntity player) {
		return Text.of(TextFormatting.DARK_AQUA, player.getName(), TextFormatting.RED, " has a Pokémon with a disallowed held item!");
	}

	@Override
	public Text getDisplayText() {
		Text.Builder builder = Text.builder();
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.get(0)));
		if (item != null)
			builder.append(Text.of(TextFormatting.DARK_AQUA, item.getDefaultInstance().getDisplayName()));
		for (int i = 1; i < items.size(); i++) {
			item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(items.get(i)));
			if (item != null)
				builder.append(Text.of(TextFormatting.GOLD, ", ", TextFormatting.DARK_AQUA, item.getDefaultInstance().getDisplayName()));
		}
		return Text.of(TextFormatting.GOLD, asWhitelist ? "Allowed" : "Disallowed", " held item(s): ", builder.build());
	}

	@Override
	public boolean duplicateAllowed(RuleBase other) {
		// Transfers this rule's ability list into the rule that's about to replace it.
		DisallowedHeldItem disallowed = (DisallowedHeldItem) other;
		for (String ability : this.items)
			if (!disallowed.items.contains(ability))
				disallowed.items.add(ability);

		return false;
	}

	@Override
	public String getSerializationString() {
		StringBuilder serialize = new StringBuilder(items.get(0));
		for (int i = 1; i < items.size(); i++)
			serialize.append(",").append(items.get(i));
		return "disallowedhelditems:" + serialize;
	}

	@Override
	public boolean visibleToAll() {
		return true;
	}
}
