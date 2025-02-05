package com.hiroku.tournaments.listeners;

import com.hiroku.tournaments.api.Tournament;
import com.hiroku.tournaments.obj.Team;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class LogoutListener
{
	@Listener
	public void onLogout(ClientConnectionEvent.Disconnect event)
	{
		if (Tournament.instance() != null)
		{
			Team team = Tournament.instance().getTeam(event.getTargetEntity().getUniqueId());
			if (team != null && team.alive && team.inMatch)
				Tournament.instance().forfeitTeams(true, team);
		}
	}
}
