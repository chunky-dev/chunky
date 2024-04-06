package se.llbit.chunky.model.builder;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A helper for building models that are made out of boxes.
 */
public class BoxModelBuilder {
  private final List<BoxBuilder> boxes = new ArrayList<>();

  /**
   * Add a box to this model. A box is defined by two vectors: one for the start position (from) and one for the end position (to).
   *
   * @param from        Start position (ie. the lower x, y, z coordinates)
   * @param to          End position (ie. the upper x, y, z coordinates)
   * @param boxConsumer Function to customize the box
   * @return This box model builder
   */
  public BoxModelBuilder addBox(Vector3 from, Vector3 to, Consumer<BoxBuilder> boxConsumer) {
    BoxBuilder box = new BoxBuilder(from, to);
    boxConsumer.accept(box);
    boxes.add(box);
    return this;
  }

  /**
   * Create quads from all boxes, including all previously configured faces and taking the specified UV maps into account.
   *
   * @return Quad array
   */
  public Quad[] toQuads() {
    List<Quad> quads = new ArrayList<>();
    for (BoxBuilder box : boxes) {
      List<Quad> boxQuads = new ArrayList<>();
      UVMapHelper.Side top = box.sides[0];
      if (top != null) {
        boxQuads.add(
          new Quad(
            new Vector3(box.from.x, box.to.y, box.to.z),
            new Vector3(box.to.x, box.to.y, box.to.z),
            new Vector3(box.from.x, box.to.y, box.from.z),
            top.toVectorForQuad()
          )
        );
      }
      UVMapHelper.Side bottom = box.sides[1];
      if (bottom != null) {
        boxQuads.add(
          new Quad(
            new Vector3(box.from.x, box.from.y, box.from.z),
            new Vector3(box.to.x, box.from.y, box.from.z),
            new Vector3(box.from.x, box.from.y, box.to.z),
            bottom.toVectorForQuad()
          )
        );
      }
      UVMapHelper.Side left = box.sides[2];
      if (left != null) {
        boxQuads.add(
          new Quad(
            new Vector3(box.from.x, box.to.y, box.to.z),
            new Vector3(box.from.x, box.to.y, box.from.z),
            new Vector3(box.from.x, box.from.y, box.to.z),
            left.toVectorForQuad()
          )
        );
      }
      UVMapHelper.Side right = box.sides[3];
      if (right != null) {
        boxQuads.add(
          new Quad(
            new Vector3(box.to.x, box.to.y, box.from.z),
            new Vector3(box.to.x, box.to.y, box.to.z),
            new Vector3(box.to.x, box.from.y, box.from.z),
            right.toVectorForQuad()
          )
        );
      }
      UVMapHelper.Side front = box.sides[4];
      if (front != null) {
        boxQuads.add(
          new Quad(
            new Vector3(box.from.x, box.to.y, box.from.z),
            new Vector3(box.to.x, box.to.y, box.from.z),
            new Vector3(box.from.x, box.from.y, box.from.z),
            front.toVectorForQuad()
          )
        );
      }
      UVMapHelper.Side back = box.sides[5];
      if (back != null) {
        boxQuads.add(
          new Quad(
            new Vector3(box.to.x, box.to.y, box.to.z),
            new Vector3(box.from.x, box.to.y, box.to.z),
            new Vector3(box.to.x, box.from.y, box.to.z),
            back.toVectorForQuad()
          )
        );
      }

      Quad[] boxQuadsArray = boxQuads.toArray(new Quad[0]);
      boxQuadsArray = Model.transform(boxQuadsArray, box.transform);
      quads.addAll(Arrays.asList(boxQuadsArray));
    }
    return quads.toArray(new Quad[0]);
  }

  public static class BoxBuilder {
    private final Vector3 from;
    private final Vector3 to;
    private int widthOnTexture;
    private int lengthOnTexture;
    private int heightOnTexture;
    private final UVMapHelper.Side[] sides = new UVMapHelper.Side[6];
    private Texture texture = Texture.EMPTY_TEXTURE;
    private int textureWidth = 16;
    private int textureHeight = 16;
    private int boxU = 0;
    private int boxV = 0;
    private boolean flipX = false;
    private boolean flipY = false;
    private boolean mirrorX = false;
    private Transform transform = Transform.NONE;

    private BoxBuilder(Vector3 from, Vector3 to) {
      this.from = from;
      this.to = to;
      widthOnTexture = (int) ((to.x - from.x) * 16);
      heightOnTexture = (int) ((to.y - from.y) * 16);
      lengthOnTexture = (int) ((to.z - from.z) * 16);
    }

    /**
     * Set the texture dimension of this box, ie. the width (x axis), height (y axis) and length (z axis) this box has
     * on the texture, in pixels.
     *
     * @param width  Width (x axis) of the box in the texture, ie. the width of the front part of the box texture, in pixels
     * @param height Height (y axis) of the box in the texture, ie. the height of the front part of the box texture, in pixels
     * @param length Length (z axis) of the box in the texture, ie. the height of the top part of the box texture, in pixels
     * @return This box builder
     */
    public BoxBuilder withBoxTextureDimensions(int width, int height, int length) {
      widthOnTexture = width;
      heightOnTexture = height;
      lengthOnTexture = length;
      return this;
    }

    private UVMapHelper getUVMapHelper() {
      UVMapHelper helper = new UVMapHelper(textureWidth, textureHeight, widthOnTexture, lengthOnTexture, heightOnTexture, boxU, boxV);
      if (this.flipX) {
        helper.flipX();
      }
      if (this.flipY) {
        helper.flipY();
      }
      return helper;
    }

    /**
     * Add a top face (facing up, towards positive y).
     *
     * @return This box builder
     */
    public BoxBuilder addTopFace() {
      sides[0] = getUVMapHelper().top();
      return this;
    }

    /**
     * Add a top face (facing up, towards positive y).
     *
     * @param consumer Function to customize the added face
     * @return This box builder
     */
    public BoxBuilder addTopFace(Consumer<UVMapHelper.Side> consumer) {
      addTopFace();
      consumer.accept(sides[0]);
      return this;
    }

    /**
     * Add a bottom face (facing down, towards negative y).
     *
     * @return This box builder
     */
    public BoxBuilder addBottomFace() {
      sides[1] = getUVMapHelper().bottom();
      return this;
    }

    /**
     * Add a bottom face (facing down, towards negative y).
     *
     * @param consumer Function to customize the added face
     * @return This box builder
     */
    public BoxBuilder addBottomFace(Consumer<UVMapHelper.Side> consumer) {
      addBottomFace();
      consumer.accept(sides[1]);
      return this;
    }

    /**
     * Add a left face (facing west, towards negative x).
     *
     * @return This box builder
     */
    public BoxBuilder addLeftFace() {
      sides[2] = getUVMapHelper().left();
      return this;
    }

    /**
     * Add a left face (facing west, towards negative x).
     *
     * @param consumer Function to customize the added face
     * @return This box builder
     */
    public BoxBuilder addLeftFace(Consumer<UVMapHelper.Side> consumer) {
      addLeftFace();
      consumer.accept(sides[2]);
      return this;
    }

    /**
     * Add a right face (facing east, towards positive x).
     *
     * @return This box builder
     */
    public BoxBuilder addRightFace() {
      sides[3] = getUVMapHelper().right();
      return this;
    }

    /**
     * Add a right face (facing east, towards positive x).
     *
     * @param consumer Function to customize the added face
     * @return This box builder
     */
    public BoxBuilder addRightFace(Consumer<UVMapHelper.Side> consumer) {
      addRightFace();
      consumer.accept(sides[3]);
      return this;
    }

    /**
     * Add a front face (facing north, towards negative z).
     *
     * @return This box builder
     */
    public BoxBuilder addFrontFace() {
      sides[4] = getUVMapHelper().front();
      return this;
    }

    /**
     * Add a front face (facing north, towards negative z).
     *
     * @param consumer Function to customize the added face
     * @return This box builder
     */
    public BoxBuilder addFrontFace(Consumer<UVMapHelper.Side> consumer) {
      addFrontFace();
      consumer.accept(sides[4]);
      return this;
    }

    /**
     * Add a back face (facing south, towards positive z).
     *
     * @return This box builder
     */
    public BoxBuilder addBackFace() {
      sides[5] = getUVMapHelper().back();
      return this;
    }

    /**
     * Add a back face (facing south, towards positive z).
     *
     * @param consumer Function to customize the added face
     * @return This box builder
     */
    public BoxBuilder addBackFace(Consumer<UVMapHelper.Side> consumer) {
      addBackFace();
      consumer.accept(sides[5]);
      return this;
    }

    /**
     * Configure this box to use the given texture with the given dimensions.
     * These dimensions are used for convenient UV calculations, it's recommended to set them to the Vanilla Minecraft texture dimensions.
     * The actual size of the texture can be different, as all values are normalized to [0..1] in for the generated models.
     *
     * @param texture Texture to be used
     * @param width   Texture width, eg. 64 for piglin texture, 256 for enderdragon texture
     * @param width   Texture height, eg. 64 for piglin texture, 256 for enderdragon texture
     * @return This box builder
     */
    public BoxBuilder forTextureSize(Texture texture, int width, int height) {
      this.texture = texture;
      this.textureWidth = width;
      this.textureHeight = height;
      return this;
    }

    /**
     * Set the UV coordinates where this boxes textures start. Minecraft uses the same texture layout for all boxes,
     * this specifies the top/left position of that layout for this box.
     *
     * @param u X coordinate of the box texture starting point
     * @param v Y coordinate of the box texture starting point
     * @return This box builder
     */
    public BoxBuilder atUVCoordinates(int u, int v) {
      boxU = u;
      boxV = v;
      return this;
    }

    /**
     * Mirror all textures horizontally. This applies to all sides that were already added to this box as well as
     * to sides that will be added after calling this method.
     *
     * @return This box builder
     */
    public BoxBuilder flipX() {
      for (UVMapHelper.Side side : sides) {
        if (side != null) {
          side.flipX();
        }
      }

      flipX = !flipX;
      return this;
    }

    /**
     * Mirror all textures vertically. This applies to all sides that were already added to this box as well as
     * to sides that will be added after calling this method.
     *
     * @return This box builder
     */
    public BoxBuilder flipY() {
      for (UVMapHelper.Side side : sides) {
        if (side != null) {
          side.flipY();
        }
      }

      flipY = !flipY;
      return this;
    }

    /**
     * Add a transformation that will be applied to this box when it is converted into a model.
     * Multiple calls to this method chain the transformations.
     *
     * @param transform Transformation
     * @return This box builder
     */
    public BoxBuilder transform(Transform transform) {
      this.transform = this.transform.chain(transform);
      return this;
    }

    /**
     * Mirror this box accross the x axis (ie. west/east axis).
     *
     * @return This box builder
     */
    public BoxBuilder mirrorX() {
      // NOTE: once we support rotating faces, we actually have to mirror the model instead of just changing textures
      flipX();
      UVMapHelper.Side tmp = this.sides[2];
      this.sides[2] = this.sides[3];
      this.sides[3] = tmp;
      return this;
    }
  }
}
