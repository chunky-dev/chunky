package se.llbit.chunky.block;

import se.llbit.chunky.model.GrindstoneModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Grindstone extends MinecraftBlockTranslucent {
    private final String face;
    private final String facing;

    public Grindstone(String face, String facing) {
        super("grindstone", Texture.grindstoneSide);
        this.face = face;
        this.facing = facing;
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return GrindstoneModel.intersect(ray, this.face, this.facing);
    }
}
