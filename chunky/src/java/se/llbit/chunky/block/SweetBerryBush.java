package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class SweetBerryBush extends SpriteBlock {
    public SweetBerryBush(int age) {
        super("sweet_berry_bush", getTextureByAge(age));
    }

    protected static Texture getTextureByAge(int age) {
        switch (age) {
            case 0:
                return Texture.sweetBerryBushStage0;
            case 1:
                return Texture.sweetBerryBushStage1;
            case 2:
                return Texture.sweetBerryBushStage2;
            case 3:
            default:
                return Texture.sweetBerryBushStage3;
        }
    }
}
