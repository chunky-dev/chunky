package se.llbit.chunky.block;

import se.llbit.chunky.model.BellModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Bell extends MinecraftBlockTranslucent implements ModelBlock {
    private final String description;
    private final BellModel model;

    public Bell(String facing, String attachment) {
        super("bell", Texture.bellBody);
        this.description = "attachment=" + attachment + ",facing=" + facing;
        model = new BellModel(facing, attachment);
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
