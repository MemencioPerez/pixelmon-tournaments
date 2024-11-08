package com.hiroku.tournaments.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.Zones;
import com.hiroku.tournaments.api.reward.RewardBase;
import com.hiroku.tournaments.api.rule.RuleSet;
import com.hiroku.tournaments.obj.Zone;
import com.hiroku.tournaments.util.PixelmonUtils;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleProperty;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.PropertyValue;
import net.minecraft.util.text.TextFormatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A preset of rules, rewards, and zones.
 *
 * @author Hiroku
 */
public class Preset {
    /**
     * The {@link RuleSet} for a preset.
     */
    public RuleSet ruleSet;
    /**
     * The {@link RewardBase}s for a preset.
     */
    public List<RewardBase> rewards;
    /**
     * The {@link Zone}s that should be used for tournaments of this preset. If empty, all will be used.
     */
    public List<Zone> zones;
    /**
     * The BattleRules for the preset.
     */
    public BattleRules battleRules;

    public Preset(RuleSet ruleSet, List<RewardBase> rewards, List<Zone> zones) {
        this.ruleSet = ruleSet;
        this.rewards = rewards;
        this.zones = zones;
        this.battleRules = ruleSet.battleRules;
    }

    public Text getDisplayText() {
        Text.Builder builder = Text.builder()
                .append(ruleSet.getDisplayText(), "\n\n", TextFormatting.GOLD, TextFormatting.UNDERLINE, "Rewards:");
        for (RewardBase reward : rewards) {
            if (reward.getDisplayText() != null) {
                builder.append("\n", reward.getDisplayText());
            }
        }

        if (!zones.isEmpty()) {
            builder.append("\n", TextFormatting.GOLD, "Zones: ", TextFormatting.DARK_AQUA, zones.size());
        }

        for (Map.Entry<BattleProperty<?>, PropertyValue<?>> entry : PixelmonUtils.getBRProperties(battleRules).entrySet()) {
            builder.append("\n", TextFormatting.DARK_AQUA, entry.getKey().getId()).append(": ").append(entry.getValue().get());
        }

        return builder.build();
    }

    public static class Serializer implements JsonSerializer<Preset> {
        public JsonElement serialize(Preset src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("ruleSet", context.serialize(src.ruleSet));
            jsonObject.add("rewards", context.serialize(src.rewards));
            JsonArray zoneIds = new JsonArray();
            if (src.zones != null) {
                for (Zone zone : src.zones) {
                    zoneIds.add(new JsonPrimitive(zone.uid));
                }
            }

            jsonObject.add("zones", zoneIds);
            return jsonObject;
        }
    }

    public static class Deserializer implements JsonDeserializer<Preset> {
        public Preset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject jsonObject = json.getAsJsonObject();
            RuleSet ruleSet = context.deserialize(jsonObject.get("ruleSet"), RuleSet.class);
            List<RewardBase> rewards = context.deserialize(jsonObject.get("rewards"), (new TypeToken<List<RewardBase>>() {}).getType());
            List<Zone> zones = new ArrayList<>();
            JsonArray zoneUIDsArray = jsonObject.get("zones").getAsJsonArray();
            if (zoneUIDsArray != null) {
                for (JsonElement jsonElement : zoneUIDsArray) {
                    zones.add(Zones.INSTANCE.getZone(jsonElement.getAsInt()));
                }
            }

            return new Preset(ruleSet, rewards, zones);
        }
    }
}