package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.entity.*;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class EntityLoadingPreferences {
    private final Map<Class<?>, Boolean> loadingPreferences;
    private boolean loadOtherEntities;

    public EntityLoadingPreferences() {
        loadingPreferences = new HashMap<>();
        loadingPreferences.put(PlayerEntity.class, true);
        loadingPreferences.put(ArmorStand.class, true);
        loadingPreferences.put(Book.class, true);
        loadingPreferences.put(PaintingEntity.class, true);
        loadOtherEntities = true;
    }

    public static EntityLoadingPreferences fromJson(JsonValue json) {
        EntityLoadingPreferences preferences = new EntityLoadingPreferences();

        if (!json.isObject())
            return preferences;
        JsonObject object = json.asObject();

        for (JsonMember member : object) {
            try {
                Class<?> memberClass = Class.forName(member.name);
                boolean value = member.value.asBoolean(true);
                preferences.loadingPreferences.put(memberClass, value);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return preferences;
    }

    public JsonValue toJson() {
        JsonObject object = new JsonObject();

        loadingPreferences.forEach((entityClass, value) -> {
            object.add(entityClass.getName(), value);
        });
        object.add("other", loadOtherEntities);

        return object;
    }

    /**
     * Sets the preference for a given Entity class, or null for other entities.
     * @param entityClass
     * @param value
     */
    public void setPreference(Class<?> entityClass, boolean value) {
        if (entityClass == null)
            loadOtherEntities = value;
        else
            loadingPreferences.put(entityClass, value);
    }

    public boolean shouldLoad(Entity entity) {
        return loadingPreferences.getOrDefault(entity.getClass(), loadOtherEntities);
    }
}
