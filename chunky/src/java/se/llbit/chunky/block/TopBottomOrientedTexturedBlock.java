package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

/**
 * A textured block that can have one of four orientations but have a fixed top and bottom that rotates, too.
 * E.g. furnaces, pumpkins, looms.
 */
public class TopBottomOrientedTexturedBlock extends OrientedTexturedBlock {
    public TopBottomOrientedTexturedBlock(String name, String facing, Texture front, Texture side, Texture top) {
        this(name, facing, front, side, side, side, top, top);
    }

    public TopBottomOrientedTexturedBlock(String name, String facing, Texture front, Texture side, Texture top, Texture bottom) {
        this(name, facing, front, side, side, side, top, bottom);
    }

    public TopBottomOrientedTexturedBlock(String name, String facing, Texture front, Texture south, Texture east, Texture west, Texture top, Texture bottom) {
        super(name, facing, front, south, east, west, top, bottom);
    }
}
