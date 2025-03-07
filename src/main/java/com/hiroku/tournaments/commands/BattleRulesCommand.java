package com.hiroku.tournaments.commands;

import com.happyzleaf.tournaments.User;
import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.Tournament;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextFormatting;

public class BattleRulesCommand implements Command<CommandSource> {
    public LiteralArgumentBuilder<CommandSource> create() {
        return Commands.literal("battlerules")
//				.description(Text.of("Sets the Pixelmon battle rules. The battle rule argument should be the exported battle rules but instead of new lines, use commas"))
                .requires(source -> User.hasPermission(source, "tournaments.command.admin.battlerules"))
                .then(
                        Commands.literal("clear")
                                .executes(this)
                )
                .then(
                        Commands.argument("args", StringArgumentType.greedyString())
                                .executes(this::load)
                );
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        // Clear
        if (Tournament.instance() == null) {
            context.getSource().sendFeedback(Text.of(TextFormatting.RED, "No tournament to set battle rules for. Try /tournament create"), false);
            return 0;
        }

        Tournament.instance().getRuleSet().battleRules = new BattleRules();
        context.getSource().sendFeedback(Text.of(TextFormatting.GREEN, "Cleared the battle rules."), false);

        return 1;
    }

    private int load(CommandContext<CommandSource> context) {
        if (Tournament.instance() == null) {
            context.getSource().sendFeedback(Text.of(TextFormatting.RED, "No tournament to set battle rules for. Try /tournament create"), false);
            return 0;
        }

        String args = context.getArgument("args", String.class).replaceAll(",\\s?", ", ").replaceAll(":\\s?", ": ");

        try {
            Tournament.instance().getRuleSet().battleRules.importText(args);
            context.getSource().sendFeedback(Text.of(TextFormatting.GREEN, "Imported battle rules text. Use /tournament to check them."), false);
        } catch (Exception e) {
            context.getSource().sendFeedback(Text.of(TextFormatting.RED, "Invalid battle rules text. Check your battle rules text and try again. " + e.getMessage()), false);
        }

        return 1;
    }
}
