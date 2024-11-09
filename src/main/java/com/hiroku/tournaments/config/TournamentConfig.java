package com.hiroku.tournaments.config;

import com.google.common.base.Charsets;
import com.hiroku.tournaments.util.GsonUtils;

import java.io.*;

/**
 * JSON object for storing all configurable elements for tournaments
 *
 * @author Hiroku
 */
public class TournamentConfig {
	public static final String PATH = "config/tournaments/tournaments.json";
	public static TournamentConfig INSTANCE;

	public final int timeBeforeMatch = 30;

	private int nextZoneID = 0;

	public final int eloFactor = 400;
	/**
	 * The number of people on the elo leaderboard by default when using /elo list.
	 */
	public final int defaultEloTopNumber = 5;

	// All tournament messages
	public final String prefix = "&l&dTournament &6» &r";
	public final String joinMessage = "{{team}} &2joined the tournament!";
	public final String leaveMessage = "{{team}} &cleft the tournament.";
	public final String forfeitMessage = "{{team}} &cforfeited!";
	public final String openMessage = "&6A new tournament has been opened! Use &3/tournament rules &6to check the rules, and &3/tournament join &6to join!";
	public final String startMessage = "&6The tournament has started!";
	public final String closeMessage = "&7The tournament was closed.";
	public final String noWinnerMessage = "&6The tournament ended with no winners! What a bummer.";
	public final String winnerMessage = "&6The tournament has ended! Congratulations {{winners}}&6!";
	public final String matchWinMessage = "{{winners}} &2defeated {{losers}}&2!";
	public final String matchDrawMessage = "{{match}}&e ended in a draw! Rematch in&3 {{time}}&e seconds.";
	public final String matchErrorMessage = "{{match}}&c errored! Restarting match in&3 {{time}}&c seconds.";
	public final String upcomingRoundMessage = "&6Upcoming Round:\n{{round}}";
	public final String byeMessage = "&2You've been given a bye for this round!";
	public final String insufficientPokemonMessage = "{{side}}&c had insufficient Pokémon! Disqualified!";
	public final String offlinePlayerMessage = "{{side}}&c was offline! Disqualified!";
	public String offlinePlayersMessage = "{{side}}&c had at least 1 too many players offline! Disqualified!";
	public final String ruleBreakMessage = "{{ruleerror}}&c Disqualified!";
	public final String battleRuleBreakMessage = "&3{{user}} &cbroke the &e{{clause}} &cclause! Disqualified!";
	public final String ignorePromptMessage = "&7Click here to ignore all tournament messages";
	public final String ignoreToggleOnMessage = "&7Tournament messages: &2on";
	public final String ignoreToggleOffMessage = "&7Tournament messages: &coff";


	public static void load() {
		INSTANCE = new TournamentConfig();

		File file = new File(PATH);
		if (!file.exists())
			INSTANCE.save();
		else {
			try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)) {
				INSTANCE = GsonUtils.prettyGson.fromJson(isr, TournamentConfig.class);
				isr.close();
				// Save for when new options have been added
				INSTANCE.save();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void save() {
		File file = new File(PATH);
		try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
			String json = GsonUtils.prettyGson.toJson(this);
			osw.write(json);
			osw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int getNextZoneID() {
		nextZoneID++;
		save();
		return nextZoneID;
	}
}
