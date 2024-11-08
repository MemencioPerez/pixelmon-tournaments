package com.hiroku.tournaments.api.reward;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class RewardBaseTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!RewardBase.class.isAssignableFrom(type.getRawType())) return null;

        return (TypeAdapter<T>) new RewardBaseTypeAdapter();
    }

    public static class RewardBaseTypeAdapter extends TypeAdapter<RewardBase> {
        public void write(JsonWriter out, RewardBase value) throws IOException {
            out.beginObject();
            System.out.println(value.getSerializationString());
            String[] parts = value.getSerializationString().split(":");
            String ruleName = parts[0];
            String ruleValue = parts[1];
            out.name(ruleName).value(ruleValue);
            out.endObject();
        }

        public RewardBase read(JsonReader in) throws IOException {
            RewardBase rewardBase = null;
            in.beginObject();

            while(in.hasNext()) {
                try {
                    String ruleName = in.nextName();
                    String ruleValue = in.nextString();
                    rewardBase = RewardTypeRegistrar.parse(ruleName, ruleValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            in.endObject();
            return rewardBase;
        }
    }
}
