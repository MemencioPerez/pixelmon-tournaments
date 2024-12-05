package com.hiroku.tournaments.rules.player;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.pixelmonmod.api.pokemon.requirement.impl.CanMegaEvolveRequirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;

public class DisallowedMechanic extends PlayerRule {
    private boolean isMegaEvolutionDisallowed = false;
    private boolean isDynamaxDisallowed = false;

    public DisallowedMechanic(String arg) throws Exception {
        super(arg);

        String[] splits = arg.split(",");
        for (String name : splits) {
            String str = name.replace("_", " ");
            if (str.contains("Mega Evolution")) {
                isMegaEvolutionDisallowed = true;
            } else if (str.contains("Dynamax")) {
                isDynamaxDisallowed = true;
            } else {
                throw new Exception("Invalid mechanic. These are case sensitive, and without spaces. e.g. Mega_Evolution. Use _ instead of space");
            }
        }
    }

    public boolean isMegaEvolutionDisallowed() {
        return isMegaEvolutionDisallowed;
    }

    public boolean isDynamaxDisallowed() {
        return isDynamaxDisallowed;
    }

    @Override
    public boolean passes(PlayerEntity player, PlayerPartyStorage storage) {
        if (isDynamaxDisallowed)
            player.sendMessage(Text.of(TextFormatting.RED, "Your Pokémon will be unable to Dynamax because this Tournament disallows the Dynamax mechanic"), Util.DUMMY_UUID);
        CanMegaEvolveRequirement canMegaEvolveRequirement = new CanMegaEvolveRequirement();
        for (Pokemon pokemon : storage.getTeam())
            if (isMegaEvolutionDisallowed && canMegaEvolveRequirement.isDataMatch(pokemon))
                player.sendMessage(Text.of(TextFormatting.RED, "Your ", pokemon.getLocalizedName(), " will be unable to Mega Evolve because this Tournament disallows the Mega Evolution mechanic"), Util.DUMMY_UUID);
        return true;
    }

    @Override
    public Text getBrokenRuleText(PlayerEntity player) {
        return Text.of(TextFormatting.DARK_AQUA, player.getName(), TextFormatting.RED, " has a Pokémon with a disallowed mechanic!");
    }

    @Override
    public Text getDisplayText() {
        Text.Builder builder = Text.builder();
        if (isMegaEvolutionDisallowed)
            builder.append(Text.of(TextFormatting.DARK_AQUA, "Mega Evolution"));
        if (isDynamaxDisallowed)
            builder.append(Text.of(TextFormatting.DARK_AQUA, "Dynamax"));
        return Text.of(TextFormatting.GOLD, "Disallowed mechanic(s): ", builder.build());
    }

    @Override
    public boolean duplicateAllowed(RuleBase other) {
        // Transfers this rule's ability list into the rule that's about to replace it.
        DisallowedMechanic disallowed = (DisallowedMechanic) other;
        this.isMegaEvolutionDisallowed = disallowed.isMegaEvolutionDisallowed();
        this.isDynamaxDisallowed = disallowed.isDynamaxDisallowed();

        return false;
    }

    @Override
    public String getSerializationString() {
        StringBuilder serialize = new StringBuilder();
        if (isMegaEvolutionDisallowed)
            serialize.append(Text.of(TextFormatting.DARK_AQUA, "Mega_Evolution"));
        if (isDynamaxDisallowed)
            serialize.append(Text.of(TextFormatting.DARK_AQUA, "Dynamax"));
        return "disallowedmechanics:" + serialize;
    }

    @Override
    public boolean visibleToAll() {
        return true;
    }
}
