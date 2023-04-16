package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class TorchflowerCrop extends SpriteBlock {
    public TorchflowerCrop(int age) {
        super("torchflower_crop", getTextureByAge(age));
    }

    protected static Texture getTextureByAge(int age) {
        switch (age) {
            case 0:
                return Texture.torchflowerCropStage0;
            case 1:
                return Texture.torchflowerCropStage1;
            case 2:
            default:
                return Texture.torchflowerCropStage2;
        }
    }
}
