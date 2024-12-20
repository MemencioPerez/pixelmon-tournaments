package com.hiroku.tournaments.util;

import com.hiroku.tournaments.rules.player.Healing;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.NbtKeys;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

/**
 * Representation of a state that a Pokémon was last seen, in terms of HP and status. This is used for the {@link Healing} rule.
 *
 * @author Hiroku
 */
public class PokemonState {
	/**
	 * The unique Pokémon ID (Pixelmon's)
	 */
	public final UUID id;
	/**
	 * The HP the Pokémon was last seen on
	 */
	public final float hp;
	/**
	 * The status the Pokémon last had
	 */
	public StatusType status = StatusType.None;

	/**
	 * Generates a {@link PokemonState} based on a provided {@link Pokemon}
	 *
	 * @param pokemon - The Pokémon currently
	 */
	public PokemonState(Pokemon pokemon) {
		this.id = pokemon.getUUID();
		this.hp = pokemon.getHealth();
		if (pokemon.getStatus().type != StatusType.None) {
			this.status = pokemon.getStatus().type;
		}
	}

	/**
	 * Determines whether the Pokémon has been healed since this state. Losing health or gaining a status is ignored
	 *
	 * @param nbt - The {@link CompoundNBT} for the Pokémon to be checked
	 * @return - true if they have healed in any way. Otherwise, false.
	 */
	public boolean hasHealed(CompoundNBT nbt) {
		if (this.hp < nbt.getFloat(NbtKeys.HEALTH)) {
			return true;
		}

		return this.status != StatusType.None && (!nbt.contains(NbtKeys.STATUS) || nbt.getInt(NbtKeys.STATUS) != this.status.ordinal());
	}

	/**
	 * Determines whether the Pokémon has been healed since this state. Losing health or gaining a status is ignored
	 *
	 * @param pokemon - The Pokémon to be checked
	 * @return - true if they have healed in any way. Otherwise, false.
	 */
	public boolean hasHealed(Pokemon pokemon) {
		if (this.hp < pokemon.getHealth()) {
			return true;
		}

		if (this.status == StatusType.None) {
			return false;
		}

		return this.status != pokemon.getStatus().type;
	}
}
