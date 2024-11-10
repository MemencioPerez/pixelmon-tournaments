package com.hiroku.tournaments;

import java.util.Arrays;

import com.hiroku.tournaments.rules.player.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import com.hiroku.tournaments.api.reward.RewardTypeRegistrar;
import com.hiroku.tournaments.api.rule.RuleTypeRegistrar;
import com.hiroku.tournaments.api.tiers.TierLoader;
import com.hiroku.tournaments.commands.TournamentsExecutor;
import com.hiroku.tournaments.commands.elo.EloExecutor;
import com.hiroku.tournaments.config.TournamentConfig;
import com.hiroku.tournaments.elo.EloPlaceholder;
import com.hiroku.tournaments.elo.EloStorage;
import com.hiroku.tournaments.listeners.BattleListener;
import com.hiroku.tournaments.listeners.BreedListener;
import com.hiroku.tournaments.listeners.DexListener;
import com.hiroku.tournaments.listeners.DynamaxListener;
import com.hiroku.tournaments.listeners.EvolutionListener;
import com.hiroku.tournaments.listeners.ExperienceListener;
import com.hiroku.tournaments.listeners.LoginListener;
import com.hiroku.tournaments.listeners.LogoutListener;
import com.hiroku.tournaments.listeners.MegaEvoListener;
import com.hiroku.tournaments.listeners.TradeListener;
import com.hiroku.tournaments.rewards.CommandReward;
import com.hiroku.tournaments.rewards.ItemsReward;
import com.hiroku.tournaments.rewards.MoneyReward;
import com.hiroku.tournaments.rewards.PokemonReward;
import com.hiroku.tournaments.rules.decider.HealthTotal;
import com.hiroku.tournaments.rules.decider.PartyCount;
import com.hiroku.tournaments.rules.general.BattleType;
import com.hiroku.tournaments.rules.general.EloType;
import com.hiroku.tournaments.rules.general.SetParty;
import com.hiroku.tournaments.rules.general.TeamCap;
import com.hiroku.tournaments.rules.team.MaxTeamElo;
import com.hiroku.tournaments.rules.team.MinTeamElo;
import com.hiroku.tournaments.rules.team.PartyMax;
import com.hiroku.tournaments.rules.team.PartyMin;
import com.hiroku.tournaments.util.PluginLogger;
import com.hiroku.tournaments.util.TournamentUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.api.pokemon.SpecFlag;
import com.pixelmonmod.pixelmon.config.EnumForceBattleResult;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;

import net.minecraftforge.fml.common.eventhandler.EventBus;

/**
 * Base plugin class for the Tournaments plugin/API
 * 
 * @author Hiroku
 */
@Plugin(id = Tournaments.ID, 
		name = Tournaments.NAME, 
		version = Tournaments.VERSION, 
		description = Tournaments.DESCRIPTION, 
		authors = Tournaments.AUTHORS,
		dependencies = {@Dependency(id = Pixelmon.MODID), @Dependency(id = "placeholderapi", optional = true)})
public class Tournaments
{
	public static final String ID = "tournaments";
	public static final String NAME = "Tournaments";
	public static final String VERSION = "2.7.2";
	public static final String DESCRIPTION = "Advanced platform for Pixelmon tournaments";
	public static final String AUTHORS = "Hiroku";
	public static final EventBus EVENT_BUS = new EventBus();
	public static final PluginLogger LOGGER = new PluginLogger(ID);
	
	public static Tournaments INSTANCE;
	
	// Pixelmon listeners
	public static final BattleListener battleListener = new BattleListener();
	public static final ExperienceListener experienceListener = new ExperienceListener();
	public static final BreedListener breedListener = new BreedListener();
	public static final TradeListener tradeListener = new TradeListener();
	public static final DexListener dexListener = new DexListener();
	public static final MegaEvoListener megaEvoListener = new MegaEvoListener();
	public static final DynamaxListener dynamaxListener = new DynamaxListener();
	public static final EvolutionListener evolutionListener = new EvolutionListener();
	
	// Sponge listeners
	public static final LoginListener loginListener = new LoginListener();
	public static final LogoutListener logoutListener = new LogoutListener();
	
	public static void log(String msg)
	{
		LOGGER.log("Tournaments \u00BB " + msg);
	}
	
	@Listener
	public void onGameInit(GameInitializationEvent event)
	{
		INSTANCE = this;
		
		log("Initializing Tournaments version " + VERSION + ", last updated for Pixelmon " + Pixelmon.getVersion() + "...");
		
		TournamentUtils.createDir("config/tournaments");
		TournamentUtils.createDir("data/tournaments");
		
		Pixelmon.EVENT_BUS.register(battleListener);
		Pixelmon.EVENT_BUS.register(experienceListener);
		Pixelmon.EVENT_BUS.register(breedListener);
		Pixelmon.EVENT_BUS.register(tradeListener);
		Pixelmon.EVENT_BUS.register(dexListener);
		Pixelmon.EVENT_BUS.register(megaEvoListener);
		Pixelmon.EVENT_BUS.register(dynamaxListener);
		Pixelmon.EVENT_BUS.register(evolutionListener);
		
		Sponge.getEventManager().registerListeners(this, loginListener);
		Sponge.getEventManager().registerListeners(this, logoutListener);
		
		registerDefaultRules();
		registerDefaultRewards();
		
		TournamentConfig.load();
		TierLoader.load();
		Zones.load();
		Presets.load();
		EloStorage.load();
		
		if (PixelmonConfig.forceEndBattleResult != EnumForceBattleResult.ABNORMAL)
		{
			if (TournamentConfig.INSTANCE.overrideForceEndBattleOption)
				PixelmonConfig.forceEndBattleResult = EnumForceBattleResult.ABNORMAL;
			else
				log("WARNING: forceEndBattleResult in pixelmon.hocon should be set to 2. Ending bugged tournament battles will not go well!");
		}
		
		if (Sponge.getPluginManager().isLoaded("placeholderapi"))
			EloPlaceholder.addPlaceholder();
		
		PokemonSpec.extraSpecTypes.add(new SpecFlag("rental"));
	}
		
	@Listener
	public void onGameStart(GamePostInitializationEvent event)
	{
		Sponge.getCommandManager().register(this, TournamentsExecutor.getSpec(), TournamentConfig.INSTANCE.baseCommandAliases);
		Sponge.getCommandManager().register(this, EloExecutor.getSpec(), TournamentConfig.INSTANCE.baseEloCommandAliases);
	}
	
	public void registerDefaultRules()
	{
		RuleTypeRegistrar.registerRuleType(Arrays.asList("setparty", "setlevel"), SetParty.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("randompokemon", "randoms"), RandomPokemon.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("helditems", "helditemsallowed"), HeldItems.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("bannedability", "bannedabilities", "disallowedability", "disallowedabilities"), DisallowedAbility.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("pokemontiers", "pokemonsets", "tiers", "sets", "tier", "set"), Tiers.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("partymax", "partymaximum"), PartyMax.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("partymin", "partyminimum"), PartyMin.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("levelmax", "levelmaximum", "maxlevel", "maximumlevel"), LevelMax.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("levelmin", "levelminimum", "minlevel", "minimumlevel"), LevelMin.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("healing", "healallowed", "healingallowed"), Healing.class); 
		RuleTypeRegistrar.registerRuleType(Arrays.asList("battletype"), BattleType.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("teamcap", "teams"), TeamCap.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("partycount", "partydecider", "deciderparty"), PartyCount.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("healthtotal", "healthdecider", "deciderhealth"), HealthTotal.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("bannedmove", "bannedmoves", "disallowedmove", "disallowedmoves"), DisallowedMove.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("bannedpokemon", "bannedpokemons", "disallowedpokemon", "disallowedpokemons"), DisallowedPokemon.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("bannedmechanic", "bannedmechanics", "disallowedmechanic", "disallowedmechanics"), DisallowedMechanic.class);
		
		// Elo rules
		RuleTypeRegistrar.registerRuleType(Arrays.asList("elotype"), EloType.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("minelo", "minimumelo"), MinElo.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("maxelo", "maximumelo"), MaxElo.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("minteamelo", "minimumteamelo"), MinTeamElo.class);
		RuleTypeRegistrar.registerRuleType(Arrays.asList("maxteamelo", "maximumteamelo"), MaxTeamElo.class);
	}
	
	public void registerDefaultRewards()
	{
		RewardTypeRegistrar.registerRewardType(Arrays.asList("command", "cmd"), CommandReward.class);
		RewardTypeRegistrar.registerRewardType(Arrays.asList("poke", "pokemon", "mon"), PokemonReward.class);
		RewardTypeRegistrar.registerRewardType(Arrays.asList("items", "item"), ItemsReward.class);
		RewardTypeRegistrar.registerRewardType(Arrays.asList("money", "cash", "cashmoney", "bank", "coin", "dosh", "$$$"), MoneyReward.class);
	}
}
