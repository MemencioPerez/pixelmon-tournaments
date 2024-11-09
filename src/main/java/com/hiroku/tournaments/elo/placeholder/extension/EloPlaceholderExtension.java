package com.hiroku.tournaments.elo.placeholder.extension;

import com.envyful.papi.api.manager.extensions.AbstractExtension;
import com.google.common.collect.Lists;
import com.hiroku.tournaments.elo.EloStorage;
import com.hiroku.tournaments.elo.EloTypes;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class EloPlaceholderExtension extends AbstractExtension<ServerPlayerEntity> {

    private static final String NAME = "name";
    private static final int PRIORITY = 1;
    private static final List<String> DESCRIPTION = Lists.newArrayList("Gets the player's tournaments elo");
    private static final List<String> EXAMPLES = Lists.newArrayList("%tournaments_elo_avg%");

    public EloPlaceholderExtension() {
        super(NAME, PRIORITY, DESCRIPTION, EXAMPLES);
    }


    @Override
    public boolean matches(ServerPlayerEntity player, String s) {
        String eloType = s.replace("elo_", "");

        switch (eloType) {
            case "avg" :
            case "single":
            case "double1v1":
            case "double2v2":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String parse(ServerPlayerEntity player, String s) {
        String eloType = s.replace("elo_", "");

        switch (eloType) {
            case "avg" :
                return String.valueOf(EloStorage.getAverageElo(player.getUniqueID()));
            case "single":
            case "double1v1":
            case "double2v2":
                return String.valueOf(EloStorage.getElo(player.getUniqueID(), Enum.valueOf(EloTypes.class, eloType)));
            default:
                return "Unknown elo type.";
        }
    }
}
