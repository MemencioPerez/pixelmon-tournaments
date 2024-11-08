package com.hiroku.tournaments.api.rule.types.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import java.io.IOException;

public class BattleRulesTypeAdapter extends TypeAdapter<BattleRules> {
   public void write(JsonWriter out, BattleRules value) throws IOException {
      out.beginObject();
      String[] rules = value.exportText().split("\n");
      for (String rule : rules) {
         String[] parts = rule.split(":");
         String ruleName = parts[0];
         String ruleValue = parts[1].trim();
         out.name(ruleName).value(ruleValue);
      }

      out.endObject();
   }

   public BattleRules read(JsonReader in) throws IOException {
      in.beginObject();
      StringBuilder builder = new StringBuilder();

      while(in.hasNext()) {
         if (builder.length() != 0) {
            builder.append("\n");
         }

         String ruleName = in.nextName();
         String ruleValue = in.nextString();
         builder.append(ruleName).append(": ").append(ruleValue);
      }

      in.endObject();
      return new BattleRules(builder.toString());
   }
}
