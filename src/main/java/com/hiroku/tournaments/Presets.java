package com.hiroku.tournaments;

import com.google.common.collect.ImmutableMap;
import com.hiroku.tournaments.api.Preset;
import com.hiroku.tournaments.api.rule.RuleSet;
import com.hiroku.tournaments.util.GsonUtils;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static manager of all the rule+reward+zone presets. These are located: ./config/tournaments/presets/*.json
 *
 * @author Hiroku
 */
public class Presets {
	/**
	 * Root directory for setting presets.
	 */
	public static final String PATH = "config/tournaments/presets/";

	/**
	 * The mapping from preset name to the {@link Preset}
	 */
	private static final HashMap<String, Preset> presets = new HashMap<>();

	/**
	 * Saves the preset with the given name. This will save into ./config/tournaments/presets/{name}.json.
	 */
	public static void savePreset(String name) {
		File file = new File(PATH + name + ".json");
		try {
			if (!file.exists()) {
				if (file.createNewFile()) {
					Preset preset = getPreset(name);
					if (preset != null) {
						OutputStreamWriter osw = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8);
						String json = GsonUtils.prettyGson.toJson(preset);
						osw.write(json);
						osw.flush();
					}
				} else {
					Tournaments.log("Unknown error while saving preset: " + name);
				}
			} else {
				Tournaments.log("A preset file with this name already exists: " + name);
			}

		} catch (IOException ioe) {
			Tournaments.log("Error saving preset: " + name);
			ioe.printStackTrace();
		}
	}

	/**
	 * Loads all of the presets saved under ./config/tournaments/presets/* into memory.
	 */
	public static void load() {
		presets.clear();

		Tournaments.log("Loading default presets...");

		Preset standard = new Preset(
				new RuleSet("legendaries:false", "partycount:3", "healthtotal:2", "battletype:single", "levelmax:50"),
				new ArrayList<>(),
				new ArrayList<>()
		);

		List<BattleClause> standardBattleClauses = new ArrayList<>();
		standardBattleClauses.add(BattleClauseRegistry.getClause("bag"));
		standardBattleClauses.add(BattleClauseRegistry.getClause("ohko"));
		standardBattleClauses.add(BattleClauseRegistry.getClause("batonpass"));
		standard.ruleSet.br.setNewClauses(standardBattleClauses);

		presets.put("Standard", standard);
		Tournaments.log("Loaded default preset: Standard");
		// Add more probably maybe ok unlikely

		Tournaments.log("Loading presets from config/tournaments/presets/ ...");

		File dir = new File(PATH);
		dir.mkdirs();
		String[] fileNames = dir.list();
		if (fileNames != null) {
			for (String fileName : fileNames) {
				if (fileName.toLowerCase().endsWith(".json")) {
					try {
						String name = fileName.replaceAll(".json", "");
						InputStreamReader isr = new InputStreamReader(Files.newInputStream((new File(dir, fileName)).toPath()), StandardCharsets.UTF_8);
						Preset preset = GsonUtils.prettyGson.fromJson(isr, Preset.class);
						isr.close();
						if (getMatchingKey(name) != null) {
							Tournaments.log("Duplicate presets for name: " + name);
						} else {
							Tournaments.log("Loaded preset: " + fileName);
							presets.put(name, preset);
						}
					} catch (Exception e) {
						Tournaments.log("Problem loading preset: " + fileName);
						e.printStackTrace();
					}
				}
			}
		}
		if (presets.isEmpty())
			Tournaments.log("No presets to load.");
		else
			Tournaments.log("Successfully loaded " + presets.size() + " preset" + (presets.size() == 1 ? "." : "s."));
	}

	/**
	 * Gets the case-precise preset name for the case insensitive argument. This is intended for other functions in this class.
	 * The idea is that preset names should maintain their precise case, e.g. "OU-Normal" instead of the case dropped "ou-normal".
	 * This function's purpose is to simply get the precise case form of the argument, if one exists in the preset map.
	 */
	public static String getMatchingKey(String name) {
		for (String existingName : presets.keySet())
			if (existingName.equalsIgnoreCase(name))
				return existingName;
		return null;
	}

	/**
	 * Returns the pre-defined {@link Preset} saved under the given name, case insensitive.
	 */
	public static Preset getPreset(String name) {
		String key = getMatchingKey(name);
		if (key == null)
			return null;
		return presets.get(key);
	}

	/**
	 * Gets all the current presets. (Immutable. To change a preset, you must use Presets.setPreset(String, Preset) ).
	 * This is to ensure edits are saved. Nothing personal, I just don't trust you.
	 */
	public static Map<String, Preset> getPresets() {
		return ImmutableMap.copyOf(presets);
	}

	/**
	 * Renames a preset from the given old name to a given new name.
	 */
	public static void renamePreset(String oldName, String newName) {
		String key = getMatchingKey(oldName);
		if (key != null) {
			Preset preset = presets.get(key);
			presets.remove(key);
			presets.put(newName, preset);
			Tournaments.log("Renamed preset " + key + " to " + newName);
			savePreset(newName);
		}
	}

	/**
	 * Sets the {@link Preset} for the given name, case insensitive.
	 */
	public static void setPreset(String name, Preset preset) {
		String key = getMatchingKey(name);
		boolean editing = true;
		if (key == null) {
			editing = false;
			key = name;
		}

		presets.remove(key);
		presets.put(key, preset);
		savePreset(key);
		if (editing)
			Tournaments.log("Set preset: " + key);
		else
			Tournaments.log("Added new preset: " + key);
	}

	/**
	 * Deletes the preset of the given name.
	 */
	public static void deletePreset(String name) {
		String key = getMatchingKey(name);
		if (key == null)
			return;
		presets.remove(key);
		File file = new File(PATH + key + ".json");
		if (file.exists())
			file.delete();
		Tournaments.log("Deleted preset: " + key + ".json");
	}
}
