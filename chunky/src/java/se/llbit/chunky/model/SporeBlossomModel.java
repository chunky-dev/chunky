package se.llbit.chunky.model;

import java.util.Collection;
import java.util.LinkedList;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;

public class SporeBlossomModel {

  private static final Quad[] quads = Model.join(
      // base
      new Quad[]{
          new Quad(
              new Vector3(1 / 16.0, 15.9 / 16.0, 15 / 16.0),
              new Vector3(15 / 16.0, 15.9 / 16.0, 15 / 16.0),
              new Vector3(1 / 16.0, 15.9 / 16.0, 1 / 16.0),
              new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
          ),
          new Quad(
              new Vector3(1 / 16.0, 15.9 / 16.0, 1 / 16.0),
              new Vector3(15 / 16.0, 15.9 / 16.0, 1 / 16.0),
              new Vector3(1 / 16.0, 15.9 / 16.0, 15 / 16.0),
              new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)
          ),
      },
      // flower
      Model.rotateZ(new Quad[]{
          new Quad(
              new Vector3(24 / 16.0, 15.7 / 16.0, 16 / 16.0),
              new Vector3(24 / 16.0, 15.7 / 16.0, 0 / 16.0),
              new Vector3(8 / 16.0, 15.7 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 15.7 / 16.0, 16 / 16.0),
              new Vector3(8 / 16.0, 15.7 / 16.0, 0 / 16.0),
              new Vector3(24 / 16.0, 15.7 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
      }, Math.toRadians(-22.5), new Vector3(0.5, 1, 0)),
      Model.rotateZ(new Quad[]{
          new Quad(
              new Vector3(-8 / 16.0, 15.7 / 16.0, 0 / 16.0),
              new Vector3(-8 / 16.0, 15.7 / 16.0, 16 / 16.0),
              new Vector3(8 / 16.0, 15.7 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 15.7 / 16.0, 0 / 16.0),
              new Vector3(8 / 16.0, 15.7 / 16.0, 16 / 16.0),
              new Vector3(-8 / 16.0, 15.7 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
          )
      }, Math.toRadians(22.5), new Vector3(0.5, 1, 0)),
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(0 / 16.0, 15.7 / 16.0, 24 / 16.0),
              new Vector3(16 / 16.0, 15.7 / 16.0, 24 / 16.0),
              new Vector3(0 / 16.0, 15.7 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15.7 / 16.0, 8 / 16.0),
              new Vector3(16 / 16.0, 15.7 / 16.0, 8 / 16.0),
              new Vector3(0 / 16.0, 15.7 / 16.0, 24 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
          )
      }, Math.toRadians(22.5), new Vector3(0, 1, 0.5)),
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(0 / 16.0, 15.7 / 16.0, 8 / 16.0),
              new Vector3(16 / 16.0, 15.7 / 16.0, 8 / 16.0),
              new Vector3(0 / 16.0, 15.7 / 16.0, -8 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15.7 / 16.0, -8 / 16.0),
              new Vector3(16 / 16.0, 15.7 / 16.0, -8 / 16.0),
              new Vector3(0 / 16.0, 15.7 / 16.0, 8 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(-22.5), new Vector3(0, 1, 0.5))
  );

  public static Collection<Primitive> primitives(Transform transform) {
    Collection<Primitive> faces = new LinkedList<>();
    TextureMaterial baseMaterial = new TextureMaterial(Texture.sporeBlossomBase);
    TextureMaterial flowerMaterial = new TextureMaterial(Texture.sporeBlossom);

    for (int i = 0; i < quads.length; i++) {
      quads[i].addTriangles(faces, i < 2 ? baseMaterial : flowerMaterial, transform);
    }

    return faces;
  }
}
