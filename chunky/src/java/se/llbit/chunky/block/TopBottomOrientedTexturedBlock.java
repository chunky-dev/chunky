package se.llbit.chunky.block;

import se.llbit.chunky.model.TopBottomOrientedTexturedBlockModel;
import se.llbit.chunky.resources.texture.AbstractTexture;

/**
 * A textured block that can have one of four orientations but have a fixed top and bottom that rotates, too.
 * E.g. furnaces, pumpkins, looms.
 * If the top and bottom should NOT rotate with the rest of the block, use FixedTopBottomRotatableTexturedBlock.
 */
public class TopBottomOrientedTexturedBlock extends AbstractModelBlock {
    public TopBottomOrientedTexturedBlock(String name, String facing, AbstractTexture front, AbstractTexture side, AbstractTexture top) {
        this(name, facing, front, side, side, side, top, top);
    }

    public TopBottomOrientedTexturedBlock(String name, String facing, AbstractTexture front, AbstractTexture side, AbstractTexture top, AbstractTexture bottom) {
        this(name, facing, front, side, side, side, top, bottom);
    }

    public TopBottomOrientedTexturedBlock(String name, String facing, AbstractTexture front, AbstractTexture south, AbstractTexture east, AbstractTexture west, AbstractTexture top, AbstractTexture bottom) {
        super(name, front);
        this.model = new TopBottomOrientedTexturedBlockModel(facing, front, south, east, west, top, bottom);
        solid = true;
        opaque = true;
    }
}
