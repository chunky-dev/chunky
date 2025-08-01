/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.model.builder.UVMapHelper;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A mob head (skull) entity.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SkullEntity extends Entity {

  public enum Kind {
    SKELETON,
    WITHER_SKELETON,
    ZOMBIE,
    PLAYER,
    CREEPER,
    DRAGON,
    PIGLIN
  }

  //#region Dragon head
  //String dragonHeadJson = "{\"elements\":[{\"from\":[2,0,2],\"to\":[14,12,14],\"faces\":{\"up\":{\"uv\":[8,2.875,9,1.875],\"texture\":\"entity/enderdragon/dragon\"},\"down\":{\"uv\":[8.9375,1.875,10,2.875],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[7,2.875,8,3.875],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[9,2.875,10,3.875],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[8,2.875,9,3.875],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[10,2.875,11,3.875],\"texture\":\"entity/enderdragon/dragon\"}}},{\"from\":[4,3,-8],\"to\":[12,6.5,3],\"faces\":{\"up\":{\"uv\":[12.75,3.75,12,2.75],\"texture\":\"entity/enderdragon/dragon\"},\"down\":{\"uv\":[12.8125,2.6875,13.4375,3.625],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[11,3.75,12.0625,4.0625],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[12.75,3.75,13.75,4.0625],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[12,3.75,12.75,4.0625],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[13.75,3.75,14.5,4.0625],\"texture\":\"entity/enderdragon/dragon\"}}},{\"from\":[4,0,-8],\"to\":[12,2.5,3],\"faces\":{\"up\":{\"uv\":[12.75,5.0625,12,4.0625],\"texture\":\"entity/enderdragon/dragon\"},\"down\":{\"uv\":[13.5,4.0625,12.75,5.0625],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[11,5.0625,12,5.3125],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[12.75,5.0625,13.75,5.3125],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[12,5.0625,12.75,5.3125],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[13.75,5.0625,14.5,5.3125],\"texture\":\"entity/enderdragon/dragon\"}}},{\"from\":[10.3,12,6.6],\"to\":[11.7,15,10.9],\"faces\":{\"up\":{\"uv\":[0.375,0.375,0.5,0],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[0.875,0.625,0.5,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[0.375,0.375,0,0.625],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[0.5,0.375,0.375,0.625],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[1,0.375,0.875,0.625],\"texture\":\"entity/enderdragon/dragon\"}}},{\"from\":[4.3,12,6.6],\"to\":[5.7,15,10.9],\"faces\":{\"up\":{\"uv\":[0.5,0.375,0.375,0],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[0,0.375,0.375,0.625],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[0.5,0.375,0.875,0.625],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[0.375,0.375,0.5,0.625],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[0.875,0.375,1,0.625],\"texture\":\"entity/enderdragon/dragon\"}}},{\"from\":[10.3,6.5,-5],\"to\":[11.7,8,-2],\"faces\":{\"up\":{\"uv\":[7.375,0,7.25,0.25],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[7.625,0.25,7.375,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[7.25,0.25,7,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[7.375,0.25,7.25,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[7.75,0.25,7.625,0.375],\"texture\":\"entity/enderdragon/dragon\"}}},{\"from\":[4.3,6.5,-5],\"to\":[5.7,8,-2],\"faces\":{\"up\":{\"uv\":[7.25,0,7.375,0.25],\"texture\":\"entity/enderdragon/dragon\"},\"east\":{\"uv\":[7,0.25,7.25,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"west\":{\"uv\":[7.375,0.25,7.625,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"north\":{\"uv\":[7.25,0.25,7.375,0.375],\"texture\":\"entity/enderdragon/dragon\"},\"south\":{\"uv\":[7.625,0.25,7.75,0.375],\"texture\":\"entity/enderdragon/dragon\"}}}]}";
  private static final Quad[] dragonHead = new BoxModelBuilder()
    // head
    .addBox(new Vector3(2 / 16., 0, 2 / 16.), new Vector3(14 / 16., 12 / 16., 14 / 16.), box ->
      box.withBoxTextureDimensions(16, 16, 16)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(112, 30)
        .addTopFace().addBottomFace().addFrontFace().addBackFace().addLeftFace().addRightFace())
    // jaw
    .addBox(new Vector3(3.5 / 16., 3 / 16., -8.5 / 16.), new Vector3(12.5 / 16., 6.75 / 16., 3.5 / 16.), box ->
      box.withBoxTextureDimensions(12, 5, 16)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(176, 44)
        .addTopFace().addBottomFace(UVMapHelper.Side::flipY).addFrontFace().addBackFace().addLeftFace().addRightFace())
    .addBox(new Vector3(3.5 / 16., 0 / 16., -8.5 / 16.), new Vector3(12.5 / 16., 3 / 16., 3.5 / 16.), box ->
      box.withBoxTextureDimensions(12, 4, 16)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(176, 65)
        .addTopFace().addBottomFace().addFrontFace().addBackFace().addLeftFace().addRightFace()
        .transform(Transform.NONE
          .translate(0, 0.5 - 3 / 16., 0.5 - 3.5 / 16.)
          .rotateX(Math.toRadians(-11.25))
          .translate(0, -0.5 + 3 / 16., -0.5 + 3.5 / 16.)
        ))
    // ears
    .addBox(new Vector3(10.3 / 16., 12 / 16., 6.6 / 16.), new Vector3(11.7 / 16., 15 / 16., 10.9 / 16.), box ->
      box.withBoxTextureDimensions(2, 4, 6)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(0, 0)
        .addTopFace().addFrontFace().addBackFace().addLeftFace().addRightFace().flipX().mirrorX())
    .addBox(new Vector3(4.3 / 16., 12 / 16., 6.6 / 16.), new Vector3(5.7 / 16., 15 / 16., 10.9 / 16.), box ->
      box.withBoxTextureDimensions(2, 4, 6)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(0, 0)
        .addTopFace().addFrontFace().addBackFace().addLeftFace().addRightFace().flipX())
    // nose
    .addBox(new Vector3(10.25 / 16., 6.5 / 16., -7 / 16.), new Vector3(11.75 / 16., 8 / 16., -4 / 16.), box ->
      box.withBoxTextureDimensions(2, 2, 4)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(112, 0)
        .addTopFace().addFrontFace().addBackFace().addLeftFace().addRightFace().mirrorX())
    .addBox(new Vector3(4.25 / 16., 6.5 / 16., -7 / 16.), new Vector3(5.75 / 16., 8 / 16., -4 / 16.), box ->
      box.withBoxTextureDimensions(2, 2, 4)
        .forTextureSize(Texture.dragon, 256, 256).atUVCoordinates(112, 0)
        .addTopFace().addFrontFace().addBackFace().addLeftFace().addRightFace())
    .toQuads();
  //#endregion

  //#region Piglin head
  private static final Quad[] piglinHead = new BoxModelBuilder()
    .addBox(new Vector3(-5 / 16., 0, -4 / 16.), new Vector3(5 / 16., 8 / 16., 4 / 16.), box ->
      box.forTextureSize(Texture.piglin, 64, 64).atUVCoordinates(0, 0).flipX()
        .addTopFace(UVMapHelper.Side::flipX)
        .addBottomFace(UVMapHelper.Side::flipY)
        .addLeftFace()
        .addRightFace()
        .addFrontFace()
        .addBackFace())
    .addBox(new Vector3(-2 / 16., 0, -5 / 16.), new Vector3(2 / 16., 4 / 16., -4 / 16.), box ->
      box.forTextureSize(Texture.piglin, 64, 64).atUVCoordinates(31, 1)
        .addTopFace()
        .addBottomFace()
        .addLeftFace()
        .addRightFace()
        .addFrontFace())
    .addBox(new Vector3(2 / 16., 0, -5 / 16.), new Vector3(3 / 16., 2 / 16., -4 / 16.), box ->
      box.forTextureSize(Texture.piglin, 64, 64).atUVCoordinates(2, 0)
        .addTopFace()
        .addBottomFace()
        .addLeftFace()
        .addRightFace()
        .addFrontFace())
    .addBox(new Vector3(-3 / 16., 0, -5 / 16.), new Vector3(-2 / 16., 2 / 16., -4 / 16.), box ->
      box.forTextureSize(Texture.piglin, 64, 64).atUVCoordinates(2, 4)
        .addTopFace()
        .addBottomFace()
        .addLeftFace()
        .addRightFace()
        .addFrontFace())
    .addBox(new Vector3(0, 0, -2 / 16.), new Vector3(1 / 16., 5 / 16., 2 / 16.), box ->
      box.forTextureSize(Texture.piglin, 64, 64).atUVCoordinates(39, 6).flipY()
        .addTopFace()
        .addBottomFace()
        .addLeftFace()
        .addRightFace()
        .addFrontFace()
        .addBackFace()
        .transform(Transform.NONE
          .translate(0.5 - 1 / 16., 0.5, 0.5)
          .rotateZ(Math.toRadians(220))
          .translate(-0.5, -0.5, -0.5)
          .translate(4.5 / 16.0, 6 / 16.0, 0 / 16.0)
        ))
    .addBox(new Vector3(-1 / 16., 0, -2 / 16.), new Vector3(0, 5 / 16., 2 / 16.), box ->
      box.forTextureSize(Texture.piglin, 64, 64).atUVCoordinates(51, 6).flipY()
        .addTopFace()
        .addBottomFace()
        .addLeftFace()
        .addRightFace()
        .addFrontFace()
        .addBackFace()
        .transform(Transform.NONE
          .translate(0.5 + 1 / 16., 0.5, 0.5)
          .rotateZ(Math.toRadians(140))
          .translate(-0.5, -0.5, -0.5)
          .translate(-4.5 / 16.0, 6 / 16.0, 0 / 16.0)
        ))
    .toQuads();
  //#endregion

  /**
   * The skull type, i.e. Creeper head, Skeleton skull, etc.
   */
  private final Kind type;

  /**
   * The rotation of the skull when attached to a wall.
   */
  private final int rotation;

  /**
   * Decides if the skull is attached to a wall or the floor.
   */
  private final int placement;

  public SkullEntity(Vector3 position, Kind type, int rotation, int placement) {
    super(position);
    this.type = type;
    this.rotation = rotation;
    this.placement = placement;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    double wallHeight = 0;
    if (placement >= 2) {
      wallHeight = 4 / 16.;
    }
    Transform transform = Transform.NONE.translate(position.x + offset.x + 0.5,
      position.y + offset.y + 4 / 16. + wallHeight,
      position.z + offset.z + 0.5);

    switch (placement) {
      case 0:
        // Unused.
        break;
      case 1:
        // On floor.
        transform = Transform.NONE.rotateY(-rotation * Math.PI / 8)
          .chain(transform);
        break;
      case 2:
        // Facing north.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .chain(transform);
        break;
      case 3:
        // Facing south.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .rotateY(Math.PI)
          .chain(transform);
        break;
      case 4:
        // Facing west.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .rotateY(QuickMath.HALF_PI)
          .chain(transform);
        break;
      case 5:
        // Facing east.
        transform = Transform.NONE.translate(0, 0, 4 / 16.)
          .rotateY(-QuickMath.HALF_PI)
          .chain(transform);
        break;
    }

    return primitives(transform);
  }

  public Collection<Primitive> primitives(Transform transform) {
    Collection<Primitive> faces = new LinkedList<>();
    EntityTexture texture;
    switch (type) {
      case SKELETON:
        texture = Texture.skeleton;
        break;
      case WITHER_SKELETON:
        texture = Texture.wither;
        break;
      case ZOMBIE:
        texture = Texture.zombie;
        break;
      case PLAYER:
        texture = Texture.steve;
        break;
      case CREEPER:
        texture = Texture.creeper;
        break;
      case DRAGON:
        return dragonHeadPrimitives(transform);
      case PIGLIN:
        return piglinHeadPrimitives(transform);
      default:
        texture = Texture.steve;
    }
    Box head = new Box(-4 / 16., 4 / 16., -4 / 16., 4 / 16., -4 / 16., 4 / 16.);
    head.transform(transform);
    head.addFrontFaces(faces, texture, texture.getUV().headFront);
    head.addBackFaces(faces, texture, texture.getUV().headBack);
    head.addTopFaces(faces, texture, texture.getUV().headTop);
    head.addBottomFaces(faces, texture, texture.getUV().headBottom);
    head.addRightFaces(faces, texture, texture.getUV().headRight);
    head.addLeftFaces(faces, texture, texture.getUV().headLeft);
    return faces;
  }

  public Collection<Primitive> dragonHeadPrimitives(Transform transform) {
    transform = Transform.NONE.translate(-0.5, -0.5, -0.5 - 1.5 / 16.)
      .chain(transform)
      .translate(0, 0.5 - 4 / 16., 0);
    Collection<Primitive> faces = new LinkedList<>();
    Material material = new TextureMaterial(Texture.dragon);
    for (Quad quad : dragonHead) {
      quad.addTriangles(faces, material, transform);
    }
    return faces;
  }

  public Collection<Primitive> piglinHeadPrimitives(Transform transform) {
    transform = Transform.NONE
      .translate(0, -4 / 16., 0)
      .chain(transform);
    Collection<Primitive> faces = new LinkedList<>();
    Material material = new TextureMaterial(Texture.piglin);
    for (Quad quad : piglinHead) {
      quad.addTriangles(faces, material, transform);
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "skull");
    json.add("position", position.toJson());
    json.add("type", type.ordinal());
    json.add("rotation", rotation);
    json.add("placement", placement);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    Kind type = Kind.values()[json.get("type").intValue(0)];
    int rotation = json.get("rotation").intValue(0);
    int placement = json.get("placement").intValue(0);
    return new SkullEntity(position, type, rotation, placement);
  }
}
