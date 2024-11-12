package com.hiroku.tournaments.rules.player;

import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class DisallowedMechanic extends PlayerRule
{
	public ArrayList<String> mechanics = new ArrayList<String>();

	public DisallowedMechanic(String arg) throws Exception
	{
		super(arg);
		
		String[] splits = arg.split(",");
		for (String name : splits) 
		{
			String str = name.replace("_", " ");
			if (str.equals("Mega Evolution") || str.equals("Dynamax"))
				mechanics.add(str);
			else
				throw new Exception("Invalid mechanic. These are case sensitive, and without spaces. e.g. Mega_Evolution. Use _ instead of space");
		}
	}
	
	@Override
	public boolean passes(Player player, PlayerPartyStorage storage)
	{
		return true;
	}
	
	@Override
	public Text getBrokenRuleText(Player player) 
	{
		return Text.of(TextColors.DARK_AQUA, player.getName(), TextColors.RED, " has a Pokémon with a disallowed mechanic!");
	}
	
	@Override
	public Text getDisplayText() 
	{
		Text.Builder builder = Text.builder();
		builder.append(Text.of(TextColors.DARK_AQUA, mechanics.get(0)));
		for (int i = 1; i < mechanics.size(); i++)
			builder.append(Text.of(TextColors.GOLD, ", ", TextColors.DARK_AQUA, mechanics.get(i)));
		return Text.of(TextColors.GOLD, "Disallowed mechanic(s): ", builder.build());
	}
	
	@Override
	public boolean duplicateAllowed(RuleBase other) 
	{
		// Transfers this rule's ability list into the rule that's about to replace it.
		DisallowedMechanic disallowed = (DisallowedMechanic) other;
		for (String ability : this.mechanics)
			if (!disallowed.mechanics.contains(ability))
				disallowed.mechanics.add(ability);
		
		return false;
	}
	
	@Override
	public String getSerializationString() 
	{
		String serialize = mechanics.get(0);
		for (int i = 1; i < mechanics.size(); i++)
			serialize += "," + mechanics.get(i);
		return "disallowedmechanics:" + serialize;
	}
	
	@Override
	public boolean visibleToAll()
	{
		return true;
	}
}
