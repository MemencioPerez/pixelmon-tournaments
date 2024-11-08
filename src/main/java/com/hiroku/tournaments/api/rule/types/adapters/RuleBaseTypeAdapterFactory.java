package com.hiroku.tournaments.api.rule.types.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hiroku.tournaments.api.rule.RuleTypeRegistrar;
import com.hiroku.tournaments.api.rule.types.RuleBase;
import java.io.IOException;

public class RuleBaseTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (!RuleBase.class.isAssignableFrom(typeToken.getRawType())) return null;

        return (TypeAdapter<T>) new RuleBaseTypeAdapter();
    }

    public static class RuleBaseTypeAdapter extends TypeAdapter<RuleBase> {

        @Override
        public void write(JsonWriter out, RuleBase value) throws IOException {
            out.beginObject();
            String[] parts = value.getSerializationString().split(":");
            String ruleName = parts[0];
            String ruleValue;

            if (parts.length >= 3) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    builder.append(parts[i]);
                    if (i < parts.length - 1) {
                        builder.append(":");
                    }
                }
                ruleValue = builder.toString();
            } else {
                ruleValue = parts[1];
            }

            out.name(ruleName).value(ruleValue);
            out.endObject();
        }

        @Override
        public RuleBase read(JsonReader in) throws IOException {
            RuleBase ruleBase = null;
            in.beginObject();

            while(in.hasNext()) {
                try {
                    String ruleName = in.nextName();
                    String ruleValue = in.nextString();
                    ruleBase = RuleTypeRegistrar.parse(ruleName, ruleValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            in.endObject();
            return ruleBase;
        }
    }
}
