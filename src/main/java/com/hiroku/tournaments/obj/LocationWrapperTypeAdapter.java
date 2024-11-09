package com.hiroku.tournaments.obj;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.IOException;

public class LocationWrapperTypeAdapter extends TypeAdapter<LocationWrapper> {

    @Override
    public void write(JsonWriter out, LocationWrapper value) throws IOException {
        out.beginObject();

        ResourceLocation location = value.dimensionKey.getLocation();
        out.name("dimension").value(location.getNamespace() + ":" + location.getPath());

        out.name("position");
        out.beginObject();
        Vector3d position = value.position;
        out.name("x").value(position.x);
        out.name("y").value(position.y);
        out.name("z").value(position.z);
        out.endObject();

        out.name("position");
        out.beginObject();
        Vector2f rotation = value.rotation;
        out.name("pitch").value(rotation.x);
        out.name("yaw").value(rotation.y);
        out.endObject();

        out.endObject();
    }

    @Override
    public LocationWrapper read(JsonReader in) throws IOException {
        in.beginObject();

        RegistryKey<World> dimensionKey = null;
        Vector3d position = null;
        Vector2f rotation = null;

        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "dimension":
                    String dimensionString = in.nextString();
                    ResourceLocation location = new ResourceLocation(dimensionString);
                    dimensionKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, location);
                    break;
                case "position":
                    in.beginObject();
                    double x = 0, y = 0, z = 0;
                    if (in.hasNext()) {
                        x = in.nextDouble();
                    }
                    if (in.hasNext()) {
                        y = in.nextDouble();
                    }
                    if (in.hasNext()) {
                        z = in.nextDouble();
                    }
                    position = new Vector3d(x, y, z);
                    in.endObject();
                    break;
                case "rotation":
                    in.beginObject();
                    float pitch = 0, yaw = 0;
                    if (in.hasNext()) {
                        pitch = (float) in.nextDouble();
                    }
                    if (in.hasNext()) {
                        yaw = (float) in.nextDouble();
                    }
                    rotation = new Vector2f(pitch, yaw);
                    in.endObject();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();

        return new LocationWrapper(dimensionKey, position, rotation);
    }
}
