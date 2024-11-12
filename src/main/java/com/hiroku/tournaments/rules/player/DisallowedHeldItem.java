package com.hiroku.tournaments.rules.player;

import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.items.ItemHeld;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;

public class DisallowedHeldItem extends PlayerRule
{
	public ArrayList<String> items = new ArrayList<String>();
	public boolean asWhitelist = false;

	public DisallowedHeldItem(String arg) throws Exception
	{
		super(arg);

		String[] splits = arg.split(",");
		for (String name : splits) 
		{
			if (name.equals("as_whitelist"))
			{
				asWhitelist = true;
				continue;
			}
			Item heldItem = ItemHeld.getByNameOrId(name);
			if (name.contains("pixelmon:") && heldItem != null && heldItem.getRegistryName() != null)
				items.add(heldItem.getRegistryName().toString());
			else
				throw new Exception("Invalid held item ID. These are case sensitive, and without spaces. e.g. pixelmon:choice_scarf. Use _ instead of space");
		}
	}

	@Override
	public boolean passes(Player player, PlayerPartyStorage storage)
	{
		for (Pokemon pokemon : storage.getTeam())
		{
			ItemStack heldItem = pokemon.getHeldItem();
			if (heldItem != ItemStack.EMPTY)
			{
				String itemName = heldItem.getItem().getRegistryName() != null ? heldItem.getItem().getRegistryName().toString() : null;

				if (itemName != null)
				{
					boolean itemInList = items.contains(itemName);
					if (itemInList != asWhitelist)
						return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public Text getBrokenRuleText(Player player) 
	{
		return Text.of(TextColors.DARK_AQUA, player.getName(), TextColors.RED, " has a Pokémon with a disallowed held item!");
	}
	
	@Override
	public Text getDisplayText() 
	{
		Text.Builder builder = Text.builder();
		Item heldItem = ItemHeld.getByNameOrId(items.get(0));
		if (heldItem != null)
			builder.append(Text.of(TextColors.DARK_AQUA, new ItemStack(heldItem).getDisplayName()));
		for (int i = 1; i < items.size(); i++)
		{
			heldItem = ItemHeld.getByNameOrId(items.get(i));
			if (heldItem != null)
				builder.append(Text.of(TextColors.GOLD, ", ", TextColors.DARK_AQUA, new ItemStack(heldItem).getDisplayName()));
		}
		return Text.of(TextColors.GOLD, asWhitelist ? "Allowed" : "Disallowed"," held item(s): ", builder.build());
	}
	
	@Override
	public boolean duplicateAllowed(RuleBase other) 
	{
		// Transfers this rule's ability list into the rule that's about to replace it.
		DisallowedHeldItem disallowed = (DisallowedHeldItem) other;
		for (String ability : this.items)
			if (!disallowed.items.contains(ability))
				disallowed.items.add(ability);
		
		return false;
	}
	
	@Override
	public String getSerializationString() 
	{
		String serialize = items.get(0);
		for (int i = 1; i < items.size(); i++)
			serialize += "," + items.get(i);
		return "disallowedhelditems:" + serialize;
	}
	
	@Override
	public boolean visibleToAll()
	{
		return true;
	}
}
