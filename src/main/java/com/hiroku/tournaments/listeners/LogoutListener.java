package com.hiroku.tournaments.listeners;

import com.hiroku.tournaments.api.Tournament;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LogoutListener {
    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (Tournament.instance() != null)
            if (Tournament.instance().getTeam(event.getEntity().getUniqueID()) != null && !Tournament.instance().getTeam(event.getEntity().getUniqueID()).alive)
                Tournament.instance().removeTeams(true, Tournament.instance().getTeam(event.getEntity().getUniqueID()));
    }
}
