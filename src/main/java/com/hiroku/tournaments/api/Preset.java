package com.hiroku.tournaments.api;

import java.util.ArrayList;

import com.hiroku.tournaments.api.reward.RewardTypeRegistrar;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.hiroku.tournaments.util.GsonUtils;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.hiroku.tournaments.api.reward.RewardBase;
import com.hiroku.tournaments.api.rule.RuleSet;
import com.hiroku.tournaments.obj.Zone;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;

/**
 * A preset of rules, rewards, and zones.
 * 
 * @author Hiroku
 */
public class Preset
{
	/** The {@link RuleSet} for a preset. */
	public RuleSet ruleSet;
	/** The {@link RewardBase}s for a preset. */
	public ArrayList<RewardBase> rewards;
	/** The {@link Zone}s that should be used for tournaments of this preset. If empty, all will be used. */
	public ArrayList<Zone> zones;
	/** The BattleRules for the preset. */
	public BattleRules battleRules;
	
	public Preset(RuleSet ruleSet, ArrayList<RewardBase> rewards, ArrayList<Zone> zones)
	{
		this.ruleSet = ruleSet;
		this.rewards = rewards;
		this.zones = zones;
		this.battleRules = ruleSet.br;
	}

	public Text getDisplayText()
	{
		Text.Builder builder = Text.builder().append(ruleSet.getDisplayText());
		builder.append(Text.of("\n\n", TextColors.GOLD, TextStyles.UNDERLINE, "Rewards:"));
		for (RewardBase reward : rewards)
			if (reward.getDisplayText() != null)
				builder.append(Text.of("\n", reward.getDisplayText()));
		if (!zones.isEmpty())
			builder.append(Text.of("\n", TextColors.GOLD, "Zones: ", TextColors.DARK_AQUA, zones.size()));
		for (String battleRule : battleRules.exportText().split("\n"))
			builder.append(Text.of("\n", TextColors.DARK_AQUA, battleRule));
		return builder.build();
	}

	public Preset deepCopy()
	{
		try
		{
			RuleSet ruleSet = new RuleSet(this.ruleSet.rules.stream().map(RuleBase::getSerializationString).toArray(String[]::new));
			ruleSet.br = new BattleRules(this.ruleSet.br.exportText());
			ArrayList<RewardBase> rewards = new ArrayList<>();
			for (RewardBase rewardBase : this.rewards)
			{
				String serializedReward = rewardBase.getSerializationString();
				String key = serializedReward.split(":")[0];
				String arg = serializedReward.substring(serializedReward.indexOf(":") + 1);
				rewards.add(RewardTypeRegistrar.parse(key, arg));
			}
			ArrayList<Zone> zones = new ArrayList<>();
			for (Zone zone : this.zones)
			{
				zones.add(GsonUtils.uglyGson.fromJson(GsonUtils.uglyGson.toJson(zone), Zone.class));
			}
			return new Preset(ruleSet, rewards, zones);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}