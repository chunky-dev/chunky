package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.GrindstoneModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Grindstone extends MinecraftBlockTranslucent implements ModelBlock {
    private final GrindstoneModel model;

    public Grindstone(String face, String facing) {
        super("grindstone", Texture.grindstoneSide);
        this.model = new GrindstoneModel(face, facing);
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
