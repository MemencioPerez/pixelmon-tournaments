package com.hiroku.tournaments.util;

import com.pixelmonmod.pixelmon.api.pokemon.ImportExportForm;
import com.pixelmonmod.pixelmon.client.gui.pokemoneditor.FormData;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumGreninja;
import com.pixelmonmod.pixelmon.enums.forms.ICosmeticForm;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import org.spongepowered.api.entity.living.player.User;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public class PokemonUtils
{
	public static final String FORGE_KEY = "ForgeData";
	public static ImportExportForm importExportFormInstance;

	static {
		try {
			Method getInstanceMethod = ImportExportForm.class.getDeclaredMethod("getInstance");
			getInstanceMethod.setAccessible(true);
			importExportFormInstance = (ImportExportForm) getInstanceMethod.invoke(null);
			addSpeciesFormData(EnumSpecies.Darmanitan, "Zen", "Galar", "Galar-Zen");
			addFormData(EnumSpecies.Zygarde, (short) 0, "50%");
			addSpeciesFormData(EnumSpecies.Necrozma, "Dusk Mane", "Dawn Wings");
			addFormData(EnumSpecies.Urshifu, (short) 0, "");
			addFormData(EnumSpecies.Urshifu, (short) 1, "Rapid-Strike");
			addSpeciesFormData(EnumSpecies.Calyrex, "Ice", "Shadow");
		} catch (ClassCastException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Failed to invoke getInstance method", e);
		}
	}

	/** Converts a {@link PokemonSpec} to a string */
	public static String serializePokemonSpec(PokemonSpec spec)
	{
		String line = "";
		if (spec.name != null)
			line += spec.name;
		if (spec.ability != null)
			line += ",ab:" + spec.ability;
		else if (spec.shiny != null && spec.shiny)
			line += ",s";
		else if (spec.ball != null)
			line += ",ba:" + spec.ball;
		else if (spec.boss != null)
			line += "boss:" + spec.boss;
		else if (spec.form != null)
			line += "form:" + spec.form;
		else if (spec.gender != null)
			line += "gender:" + spec.gender;
		else if (spec.growth != null)
			line += "growth:" + spec.growth;
		else if (spec.level != null)
			line += "level:" + spec.level;
		else if (spec.nature != null)
			line += "nature:" + spec.nature;
		return line;
	}

	public static void stripHeldItem(User user, Pokemon pokemon)
	{
		ItemStack heldItem = pokemon.getHeldItem();
		if (heldItem != ItemStack.EMPTY)
			user.getInventory().offer((org.spongepowered.api.item.inventory.ItemStack)(Object)heldItem);
	}

	public static void addSpeciesFormData(EnumSpecies species, String... formNames)
	{
		try
		{
			Method addSpeciesFormDataMethod = ImportExportForm.class.getDeclaredMethod("addSpeciesFormData", EnumSpecies.class, String[].class);
			addSpeciesFormDataMethod.setAccessible(true);
			addSpeciesFormDataMethod.invoke(importExportFormInstance, species, formNames);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to invoke addSpeciesFormData method", e);
		}
	}

	public static void addFormData(EnumSpecies species, short formIndex, String formName)
	{
		try
		{
			Method addFormDataMethod = ImportExportForm.class.getDeclaredMethod("addFormData", EnumSpecies.class, short.class, String.class);
			addFormDataMethod.setAccessible(true);
			addFormDataMethod.invoke(importExportFormInstance, species, formIndex, formName);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to invoke addFormData method", e);
		}
	}

	public static String getFormName(EnumSpecies species, short formIndex)
	{
		IEnumForm form = species.getFormEnum(formIndex);
		if (!(form.isDefaultForm() || form.getFormSuffix().isEmpty() || form instanceof Gender || form instanceof ICosmeticForm && ((ICosmeticForm)(form)).isCosmetic()))
		{
			if (form == EnumGreninja.BATTLE_BOND || form == EnumGreninja.ZOMBIE_BATTLE_BOND || form == EnumGreninja.ALTER_BATTLE_BOND)
				return species.name;
			if (getSpeciesMap() != null && getSpeciesMap().containsKey(species) && getSpeciesMap().get(species).containsKey(formIndex))
				return getSpeciesMap().get(species).get(formIndex);
			String s = form.getFormSuffix();
			return species.name + "-" + s.substring(1, 2).toUpperCase() + s.substring(2);
		}
		return species.name;
	}

	@SuppressWarnings("unchecked")
	public static Optional<FormData> getFormData(String speciesName)
	{
		try
		{
			Method getFormDataMethod = ImportExportForm.class.getDeclaredMethod("getFormData", String.class);
			getFormDataMethod.setAccessible(true);
			return (Optional<FormData>) getFormDataMethod.invoke(importExportFormInstance, speciesName);
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException("Failed to invoke getFormData method", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<EnumSpecies, Map<Short, String>> getSpeciesMap()
	{
		try
		{
			Field speciesMapField = ImportExportForm.class.getDeclaredField("speciesMap");
			speciesMapField.setAccessible(true);
			return (Map<EnumSpecies, Map<Short, String>>) speciesMapField.get(importExportFormInstance);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			throw new RuntimeException("Failed to get speciesMap field", e);
		}
	}
}