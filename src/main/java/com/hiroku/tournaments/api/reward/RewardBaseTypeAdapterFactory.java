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
            String[] parts = value.getSerializationString().split(":");
            String rewardId = parts[0];
            String rewardValue;

            if (parts.length >= 3) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    builder.append(parts[i]);
                    if (i < parts.length - 1) {
                        builder.append(":");
                    }
                }
                rewardValue = builder.toString();
            } else {
                rewardValue = parts[1];
            }

            out.name(rewardId).value(rewardValue);
            out.endObject();
        }

        public RewardBase read(JsonReader in) throws IOException {
            RewardBase rewardBase = null;
            in.beginObject();

            while(in.hasNext()) {
                try {
                    String rewardId = in.nextName();
                    String rewardValue = in.nextString();
                    rewardBase = RewardTypeRegistrar.parse(rewardId, rewardValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            in.endObject();
            return rewardBase;
        }
    }
}
