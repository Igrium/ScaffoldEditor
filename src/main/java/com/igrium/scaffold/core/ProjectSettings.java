package com.igrium.scaffold.core;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

public final class ProjectSettings {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    private static class SearchPathsSerializer
            implements JsonSerializer<List<Path>>, JsonDeserializer<List<Path>> {

        @Override
        public List<Path> deserialize(JsonElement element, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray array = element.getAsJsonArray();
            List<Path> list = new ArrayList<>(array.size());
            array.forEach(e -> list.add(Paths.get(e.getAsString())));
            return list;
        }

        @Override
        public JsonElement serialize(List<Path> obj, Type type, JsonSerializationContext context) {
            JsonArray array = new JsonArray(obj.size());
            obj.forEach(p -> array.add(p.toString()));
            return array;
        }
    }

    public ProjectSettings() {};

    
    @JsonAdapter(SearchPathsSerializer.class)
    private List<Path> searchPaths = new ArrayList<>();

    public List<Path> getSearchPaths() {
        return searchPaths;
    }

    public static String toJson(ProjectSettings settings) {
        return GSON.toJson(settings);
    }

    public static ProjectSettings fromJson(String json) {
        return GSON.fromJson(json, ProjectSettings.class);
    }
}
