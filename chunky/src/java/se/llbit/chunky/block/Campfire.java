package se.llbit.chunky.block;

import se.llbit.chunky.model.CampfireModel;
import se.llbit.chunky.model.StonecutterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Campfire extends MinecraftBlockTranslucent {
    private final String facing;
    public final boolean isLit;

    public Campfire(String facing, boolean lit) {
        super("campfire", Texture.campfireLog);
        localIntersect = true;
        this.facing = facing;
        this.isLit = lit;
        // TODO the fire is 1/16th higher than a block, so this must be an entity
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return CampfireModel.intersect(ray, facing, isLit);
    }
}
