package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class RespawnAnchor extends TexturedBlock {
    private static final Texture[] sideTextures = new Texture[]{
            Texture.respawnAnchorSide0,
            Texture.respawnAnchorSide1,
            Texture.respawnAnchorSide2,
            Texture.respawnAnchorSide3,
            Texture.respawnAnchorSide4
    };

    public final int charges;

    public RespawnAnchor(int charges) {
        super("respawn_anchor", sideTextures[charges], Texture.respawnAnchorTop, Texture.respawnAnchorBottom);
        this.charges = charges;
    }
}
