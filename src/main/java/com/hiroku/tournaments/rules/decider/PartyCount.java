package com.hiroku.tournaments.rules.decider;

import com.happyzleaf.tournaments.text.Text;
import com.happyzleaf.tournaments.User;
import com.hiroku.tournaments.api.Match;
import com.hiroku.tournaments.api.rule.types.DeciderRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.hiroku.tournaments.obj.Side;
import com.hiroku.tournaments.obj.Team;
import net.minecraft.util.text.TextFormatting;

/**
 * Decides the winner based on who has the most living party Pokémon. Matching party counts
 * will result in indecision. Applies to crashes.
 *
 * @author Hiroku
 */
public class PartyCount extends DeciderRule {
	int weight = 3;

	public PartyCount(String arg) throws Exception {
		super(arg);

		if (!arg.isEmpty())
			weight = Integer.parseInt(arg);
	}

	@Override
	public Side decideWinner(Match match) {
		int[] sideCounts = new int[]{0, 0};
		for (int i = 0; i < 2; i++) {
			for (Team team : match.sides[i].teams) {
				for (User user : team.users) {
					sideCounts[i] += user.getParty().findAll(pokemon -> !pokemon.isEgg() && pokemon.getHealth() > 0).size();
				}
			}
		}

		if (sideCounts[0] > sideCounts[1])
			return match.sides[0];
		else if (sideCounts[0] < sideCounts[1])
			return match.sides[1];
		else
			return null;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public boolean applyToDraws() {
		return false;
	}

	@Override
	public boolean applyToCrashes() {
		return true;
	}

	@Override
	public Text getDisplayText() {
		return Text.of(TextFormatting.GOLD, "Decide crashes: ", TextFormatting.DARK_AQUA, "Party count [" + getWeight() + "]");
	}

	@Override
	public boolean duplicateAllowed(RuleBase other) {
		return false;
	}

	@Override
	public String getSerializationString() {
		return "partycount:" + getWeight();
	}
}
