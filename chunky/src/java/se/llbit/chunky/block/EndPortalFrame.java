package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EndPortalFrame extends MinecraftBlockTranslucent implements ModelBlock {
    private final EndPortalFrameModel model;
    private final String description;

    public EndPortalFrame(boolean eye, String facing) {
        super("end_portal_frame", Texture.endPortalFrameSide);
        this.description = "eye=" + eye + ",facing=" + facing;
        this.model = new EndPortalFrameModel(eye, facing);
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
