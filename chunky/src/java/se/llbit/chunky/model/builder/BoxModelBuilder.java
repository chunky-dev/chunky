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

  public BoxModelBuilder addBox(Vector3 from, Vector3 to, Consumer<BoxBuilder> boxConsumer) {
    BoxBuilder box = new BoxBuilder(from, to);
    boxConsumer.accept(box);
    boxes.add(box);
    return this;
  }

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
    private final int widthOnTexture;
    private final int lengthOnTexture;
    private final int heightOnTexture;
    private final UVMapHelper.Side[] sides = new UVMapHelper.Side[6];
    private Texture texture = Texture.EMPTY_TEXTURE;
    private int textureWidth = 16;
    private int textureHeight = 16;
    private int boxU = 0;
    private int boxV = 0;
    private boolean flipX = false;
    private boolean flipY = false;
    private Transform transform = Transform.NONE;

    private BoxBuilder(Vector3 from, Vector3 to) {
      this.from = from;
      this.to = to;
      widthOnTexture = (int) ((to.x - from.x) * 16);
      heightOnTexture = (int) ((to.y - from.y) * 16);
      lengthOnTexture = (int) ((to.z - from.z) * 16);
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

    public BoxBuilder addTopFace() {
      sides[0] = getUVMapHelper().top();
      return this;
    }

    public BoxBuilder addTopFace(Consumer<UVMapHelper.Side> consumer) {
      addTopFace();
      consumer.accept(sides[0]);
      return this;
    }

    public BoxBuilder addBottomFace() {
      sides[1] = getUVMapHelper().bottom();
      return this;
    }

    public BoxBuilder addBottomFace(Consumer<UVMapHelper.Side> consumer) {
      addBottomFace();
      consumer.accept(sides[1]);
      return this;
    }

    public BoxBuilder addLeftFace() {
      sides[2] = getUVMapHelper().left();
      return this;
    }

    public BoxBuilder addLeftFace(Consumer<UVMapHelper.Side> consumer) {
      addLeftFace();
      consumer.accept(sides[2]);
      return this;
    }

    public BoxBuilder addRightFace() {
      sides[3] = getUVMapHelper().right();
      return this;
    }

    public BoxBuilder addRightFace(Consumer<UVMapHelper.Side> consumer) {
      addRightFace();
      consumer.accept(sides[3]);
      return this;
    }

    public BoxBuilder addFrontFace() {
      sides[4] = getUVMapHelper().front();
      return this;
    }

    public BoxBuilder addFrontFace(Consumer<UVMapHelper.Side> consumer) {
      addFrontFace();
      consumer.accept(sides[4]);
      return this;
    }

    public BoxBuilder addBackFace() {
      sides[5] = getUVMapHelper().back();
      return this;
    }

    public BoxBuilder addBackFace(Consumer<UVMapHelper.Side> consumer) {
      addBackFace();
      consumer.accept(sides[5]);
      return this;
    }

    public BoxBuilder useEntityTexture(Texture texture) {
      return forTextureSize(texture, 64, 64);
    }

    public BoxBuilder forTextureSize(Texture texture, int width, int height) {
      this.texture = texture;
      this.textureWidth = width;
      this.textureHeight = height;
      return this;
    }

    public BoxBuilder atUVCoordinates(int u, int v) {
      boxU = u;
      boxV = v;
      return this;
    }

    public BoxBuilder flipX() {
      flipX = !flipX;
      return this;
    }

    public BoxBuilder flipY() {
      flipY = !flipY;
      return this;
    }

    public BoxBuilder transform(Transform transform) {
      this.transform = this.transform.chain(transform);
      return this;
    }
  }
}
