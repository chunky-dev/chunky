package se.llbit.chunky.block;

import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EndPortalFrame extends MinecraftBlockTranslucent {
    private final boolean hasEye;
    private final String facing;

    public EndPortalFrame(boolean eye, String facing) {
        super("end_portal_frame", Texture.endPortalFrameSide);
        this.hasEye = eye;
        this.facing = facing;
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return EndPortalFrameModel.intersect(ray, hasEye, facing);
    }

    @Override
    public String description() {
        return "eye=" + hasEye + ",facing=" + facing;
    }
}
