package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.entity.*;
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
        loadingPreferences.put(PlayerEntity.class, PersistentSettings.getLoadPlayers());
        loadingPreferences.put(ArmorStand.class, PersistentSettings.getLoadArmorStands());
        loadingPreferences.put(Book.class, PersistentSettings.getLoadBooks());
        loadingPreferences.put(PaintingEntity.class, PersistentSettings.getLoadPaintings());
        loadOtherEntities = PersistentSettings.getLoadOtherEntities();
    }

    public void fromJson(JsonValue json) {
        if (json == null || !json.isObject())
            return;
        JsonObject object = json.asObject();

        for (JsonMember member : object) {
            if (member.name.equals("other")) {
                loadOtherEntities = member.value.asBoolean(PersistentSettings.getLoadOtherEntities());
                continue;
            }

            try {
                Class<?> memberClass = Class.forName(member.name);
                // TODO: the correct Persistent Setting should be retrieved instead of `true`
                boolean value = member.value.asBoolean(true);
                loadingPreferences.put(memberClass, value);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
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
     * Sets the loading preference for a given Entity class, or null for other entities.
     * @param entityClass
     * @param value true: load, false: do not load.
     */
    public void setPreference(Class<?> entityClass, boolean value) {
        if (entityClass == null)
            loadOtherEntities = value;
        else
            loadingPreferences.put(entityClass, value);
    }

    /**
     * Returns true if the entity should be loaded in the scene.
     * @param entity
     * @return
     */
    public boolean shouldLoad(Entity entity) {
        return shouldLoadClass(entity.getClass());
    }

    public boolean shouldLoadClass(Class<?> entityClass) {
        return loadingPreferences.getOrDefault(entityClass, loadOtherEntities);
    }
}
