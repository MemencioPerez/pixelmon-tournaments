package com.hiroku.tournaments.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.export.ImportExportForm;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleProperty;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.PropertyValue;
import com.pixelmonmod.pixelmon.client.gui.pokemoneditor.FormData;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PixelmonUtils {
    private static Field properties_f = null;

    public static Map<BattleProperty<?>, PropertyValue<?>> getBRProperties(BattleRules br) {
        try {
            if (properties_f == null) {
                properties_f = BattleRules.class.getDeclaredField("properties");
                properties_f.setAccessible(true);
            }

            //noinspection unchecked
            return (Map<BattleProperty<?>, PropertyValue<?>>) properties_f.get(br);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getFormName(Pokemon pokemon) {
        String exportName = pokemon.getSpecies().getName();
        if (!pokemon.getForm().getName().isEmpty() && !pokemon.getForm().isForm("singlestrike") && !pokemon.getForm().isForm("amped"))
            exportName = exportName + "-" + pokemon.getForm().getName().toLowerCase(Locale.ROOT)
                    .replace("hisuian", "Hisui")
                    .replace("alolan", "Alola")
                    .replace("galarian", "Galar")
                    .replace("pompom", "Pom-Pom")
                    .replace("lowkey", "Low-Key")
                    .replace("rapidstrike", "Rapid-Strike");
        return exportName;
    }

    public static Optional<FormData> getFormData(String formName) {
        if (formName.equals("Toxtricity"))
            formName = "Toxtricity-amped";
        return ImportExportForm.getFormData(formName
                .replace("-Alola", "-alolan")
                .replace("-Galar", "-galarian")
                .replace("-Hisui", "-hisuian")
                .replace("-Pom-Pom", "-pompom"));
    }
}
