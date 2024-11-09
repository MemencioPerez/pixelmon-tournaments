package com.hiroku.tournaments.api.tiers;

import com.hiroku.tournaments.Tournaments;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.helpers.CollectionHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Automatic loader of list-based tiers. All files fitting the name <code>./config/tournaments/tiers/*.tier.txt</code>
 * will be automatically loaded. It is expected that each tier file has a line of the form <code>display name = [name]</code>.
 * The rest is just a column list of Pokémon names that are contained in the tier.
 *
 * @author Hiroku
 */
public class TierLoader {
	public static final String PATH = "config/tournaments/tiers/";

	/**
	 * Loads all tiers and the custom tiers that could be found in the tier directory.
	 */
	public static void load() {
		Tier.tiers.clear();

		Tier.loadDefaultTiers();

		File dir = new File(PATH);
		if (!dir.exists()) {
            try {
                Files.createDirectories(dir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		List<File> tierFiles = new ArrayList<>();
		getFiles(PATH, tierFiles);
		for (File file : tierFiles) {
			try {
				final List<PokemonSpecification> pokemon = new ArrayList<>();
				String key = file.getName().substring(0, file.getName().indexOf("."));
				String displayName = key;
				Predicate<Pokemon> condition = p -> CollectionHelper.find(pokemon, spec -> spec.matches(p)) != null;
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("//"))
						continue;
					if (line.replaceAll(" ", "").startsWith("displayname="))
						displayName = line.split("=")[1].trim();
					else {
						line = line.replaceAll(",", "").trim();
						pokemon.add(PokemonSpecificationProxy.create(line.split(" ")));
					}
				}

				if (!pokemon.isEmpty()) {
					Tier.tiers.add(new Tier(key, displayName, condition));
					Tournaments.log("Loaded custom tier: " + displayName + ". Size: " + pokemon.size());
				}

				br.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Recursive algorithm to collect all .tier.txt files in a directory.
	 *
	 * @param directoryPath - The current directory to search.
	 * @param files         - The latest list of non-directory {@link File}s that
	 *                      are recognized as tier files.
	 */
	public static void getFiles(String directoryPath, List<File> files) {
		File directory = new File(directoryPath);

		if (!directory.exists() || !directory.isDirectory()) {
			return;
		}

		String[] fileList = directory.list();
		if (fileList == null) {
			return;
		}

		for (String name : fileList) {
			Path subFilePath = Paths.get(directoryPath, name);
			File subFile = subFilePath.toFile();

			if (subFile.isFile() && name.endsWith(".tier.txt")) {
				files.add(subFile);
			} else if (subFile.isDirectory()) {
				getFiles(subFilePath.toString(), files);
			}
		}
	}
}
