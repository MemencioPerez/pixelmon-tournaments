package com.hiroku.tournaments.listeners;

import com.pixelmonmod.pixelmon.api.events.PokedexEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DexListener {
    @SubscribeEvent
    public void onDex(PokedexEvent event) {
        if (event.getPokemon() == null || event.getPlayer() == null) {
            return;
        }
        if (event.getPokemon().hasFlag("rental")) {
            event.setCanceled(true);
        }
    }
}
