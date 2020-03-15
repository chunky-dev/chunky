package se.llbit.chunky.block;

import se.llbit.chunky.model.BellModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Bell extends MinecraftBlockTranslucent {
    private final String facing;
    private final String attachment;

    public Bell(String facing, String attachment) {
        super("bell", Texture.bellBody);
        this.facing = facing;
        this.attachment = attachment;
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return BellModel.intersect(ray, this.facing, this.attachment);
    }

    @Override
    public String description() {
        return "attachment=" + attachment + ",facing=" + facing;
    }
}
