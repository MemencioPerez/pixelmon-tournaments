package com.hiroku.tournaments.elo.placeholder;

import com.envyful.papi.api.manager.AbstractPlaceholderManager;
import com.hiroku.tournaments.elo.placeholder.extension.EloPlaceholderExtension;
import net.minecraft.entity.player.ServerPlayerEntity;

public class EloPlaceholderManager extends AbstractPlaceholderManager<ServerPlayerEntity> {

    private static final String IDENTIFIER = "tournaments";
    private static final String[] AUTHORS = new String[] { "Hiroku", "happyzleaf", "MemencioPerez" };
    private static final String VERSION = "1.0.0";
    private static final String NAME = "tournaments";

    public EloPlaceholderManager() {
        super(IDENTIFIER, AUTHORS, VERSION, NAME, ServerPlayerEntity.class);

        this.registerPlaceholder(new EloPlaceholderExtension());
    }
}
