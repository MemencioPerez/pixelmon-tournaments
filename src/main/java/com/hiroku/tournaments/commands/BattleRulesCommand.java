package com.hiroku.tournaments.commands;

import com.happyzleaf.tournaments.Text;
import com.happyzleaf.tournaments.User;
import com.hiroku.tournaments.api.Tournament;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextFormatting;

import static com.hiroku.tournaments.util.CommandUtils.getOptArgument;

public class BattleRulesCommand implements Command<CommandSource> {
	public LiteralArgumentBuilder<CommandSource> create() {
		return Commands.literal("rules")
//				.description(Text.of("Sets the Pixelmon battle rules. The battle rule argument should be the exported battle rules but instead of new lines, use commas"))
				.requires(source -> User.hasPermission(source, "tournaments.command.admin.battlerules"))
				.executes(this)
				.then(
						Commands.argument("<clear | battle rules export text", StringArgumentType.greedyString())
								.executes(this)
				);
	}

	@Override
	public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
		if (Tournament.instance() == null) {
			context.getSource().sendFeedback(Text.of(TextFormatting.RED, "No tournament to set battle rules for. Try /tournament create"), true);
			return 0;
		}

		String arg = getOptArgument(context, "<clear | battle rules export text", String.class).orElse("clear");
		if (arg.equalsIgnoreCase("clear")) {
			Tournament.instance().getRuleSet().br = new BattleRules();
			context.getSource().sendFeedback(Text.of(TextFormatting.GREEN, "Cleared the battle rules."), true);
			return 1;
		} else {
			String lineSep = arg.replaceAll(",", "\n");
			Tournament.instance().getRuleSet().br.importText(lineSep);
			context.getSource().sendFeedback(Text.of(TextFormatting.GREEN, "Imported battle rules text. Use /tournament to check them."), true);
			return 0;
		}
	}
}
