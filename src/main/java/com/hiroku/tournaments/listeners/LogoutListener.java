package com.hiroku.tournaments.listeners;

import com.hiroku.tournaments.api.Tournament;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;

public class LogoutListener
{
	@Listener
	public void onLogout(Disconnect event)
	{
		if (Tournament.instance() != null)
			if (Tournament.instance().getTeam(event.getTargetEntity().getUniqueId()) != null || !Tournament.instance().getTeam(event.getTargetEntity().getUniqueId()).alive)
				Tournament.instance().removeTeams(true, Tournament.instance().getTeam(event.getTargetEntity().getUniqueId()));
			else
				Tournament.instance().getTeam(event.getTargetEntity().getUniqueId()).refreshUser(event.getTargetEntity());
	}
}
