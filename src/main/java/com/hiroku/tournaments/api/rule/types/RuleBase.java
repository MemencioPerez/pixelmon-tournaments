package com.hiroku.tournaments.api.rule.types;

import com.hiroku.tournaments.api.Mode;
import com.hiroku.tournaments.api.rule.RuleTypeRegistrar;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Abstract representation of all rules. Implementing the required methods will ensure
 * validity to the {@link RuleTypeRegistrar}. Implementation is done through one
 * of: {{@link GeneralRule}, {@link TeamRule}, {@link PlayerRule}, {@link DeciderRule}}.
 *
 * @author Hiroku
 */
public abstract class RuleBase extends Mode {
	/**
	 * Construction of the implementation's {@link RuleBase} potentially based off a parameter on the right of the colon (eg. levelmax:50).
	 *
	 * @param arg - The optional argument on the right side of the colon (in the above example, 50 as a string).
     */
	RuleBase(String arg) {
	}

	/**
	 * Gets whether a duplicate rule type is acceptable. The potential duplicate is passed in for situations where identical rule
	 * type might not be enough to decide if the duplicate is acceptable. Think rules against a list of Pokémon, where duplicates
	 * would be acceptable if the disallowed Pokémon isn't the same.
	 *
	 * @param other - The potential duplicate.
	 * @return - true if the given duplicate is acceptable
	 */
	public abstract boolean duplicateAllowed(RuleBase other);

	/**
	 * Gets the string form of this rule for serializing. This is effectively how the mode would be added. eg. 'levelmax:50'.
	 */
	public abstract String getSerializationString();

	@Override
	public boolean canShow(PlayerEntity player) {
		return super.canShow(player) || (getDisplayText() != null && PixelmonCommandUtils.hasPermission(player.getCommandSource(), "tournaments.command.rules.modify"));
	}
}
