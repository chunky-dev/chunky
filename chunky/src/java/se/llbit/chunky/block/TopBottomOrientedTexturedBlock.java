package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

/**
 * A textured block that can have one of four orientations but have a fixed top and bottom that rotates, too.
 * E.g. furnaces, pumpkins, looms.
 */
public class TopBottomOrientedTexturedBlock extends OrientedTexturedBlock {
    private static final int[][] uvRotationMap = {
            {0, 0, 0, 0, 0, 0}, // up, unused
            {0, 0, 0, 0, 0, 0}, // down, unused
            {0, 0, 0, 0, 2, 0}, // north
            {0, 0, 0, 0, 0, 2}, // south
            {0, 0, 0, 0, 1, 1}, // west
            {0, 0, 0, 0, 3, 3}, // east
    };

    private static final int[][] textureOrientationMap = {
            {0, 0, 0, 0, 0, 0}, // up, unused
            {0, 0, 0, 0, 0, 0}, // down, unused
            {0, 1, 2, 3, 4, 5}, // north
            {3, 0, 1, 2, 4, 5}, // south
            {2, 3, 0, 1, 4, 5}, // west
            {1, 2, 3, 0, 4, 5}, // east
    };

    public TopBottomOrientedTexturedBlock(String name, String facing, Texture front, Texture side, Texture top) {
        this(name, facing, front, side, side, side, top, top);
    }

    public TopBottomOrientedTexturedBlock(String name, String facing, Texture front, Texture side, Texture top, Texture bottom) {
        this(name, facing, front, side, side, side, top, bottom);
    }

    public TopBottomOrientedTexturedBlock(String name, String facing, Texture front, Texture south, Texture east, Texture west, Texture top, Texture bottom) {
        super(name, facing, front, south, east, west, top, bottom);
    }

    @Override
    protected int[][] getUvRotationMap() {
        return uvRotationMap;
    }

    @Override
    protected int[][] getTextureOrientationMap() {
        return textureOrientationMap;
    }
}
