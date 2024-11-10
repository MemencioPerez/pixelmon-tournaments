package com.hiroku.tournaments.rules.player;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.rule.types.PlayerRule;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import com.pixelmonmod.api.pokemon.requirement.impl.CanMegaEvolveRequirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class DisallowedMechanic extends PlayerRule {
    ArrayList<String> mechanics = new ArrayList<>();

    public DisallowedMechanic(String arg) throws Exception {
        super(arg);

        String[] splits = arg.split(",");
        for (String name : splits) {
            String str = name.replace("_", " ");
            if (str.contains("Mega Evolution") || str.contains("Dynamax") || str.contains("Gigantamax")) {
                mechanics.add(str);
            } else {
                throw new Exception("Invalid mechanic. These are case sensitive, and without spaces. e.g. Mega_Evolution. Use _ instead of space");
            }
        }
    }

    @Override
    public boolean passes(PlayerEntity player, PlayerPartyStorage storage) {
        for (Pokemon pokemon : storage.getTeam()) {
            if (mechanics.contains("Mega Evolution")) {
                return !new CanMegaEvolveRequirement().isDataMatch(pokemon);
            }
            if (mechanics.contains("Dynamax")) {
                return !(pokemon.getDynamaxLevel() > 0);
            }
            if (mechanics.contains("Gigantamax")) {
                return !pokemon.canGigantamax();
            }
        }
        return true;
    }

    @Override
    public Text getBrokenRuleText(PlayerEntity player) {
        return Text.of(TextFormatting.DARK_AQUA, player.getName(), TextFormatting.RED, " has a Pokémon with a disallowed mechanic!");
    }

    @Override
    public Text getDisplayText() {
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextFormatting.DARK_AQUA, mechanics.get(0)));
        for (int i = 1; i < mechanics.size(); i++)
            builder.append(Text.of(TextFormatting.GOLD, ", ", TextFormatting.DARK_AQUA, mechanics.get(i)));
        return Text.of(TextFormatting.GOLD, "Disallowed mechanic(s): ", builder.build());
    }

    @Override
    public boolean duplicateAllowed(RuleBase other) {
        // Transfers this rule's ability list into the rule that's about to replace it.
        DisallowedMechanic disallowed = (DisallowedMechanic) other;
        for (String ability : this.mechanics)
            if (!disallowed.mechanics.contains(ability))
                disallowed.mechanics.add(ability);

        return false;
    }

    @Override
    public String getSerializationString() {
        StringBuilder serialize = new StringBuilder(mechanics.get(0));
        for (int i = 1; i < mechanics.size(); i++)
            serialize.append(",").append(mechanics.get(i));
        return "disallowedmechanics:" + serialize;
    }

    @Override
    public boolean visibleToAll() {
        return true;
    }
}
