package se.llbit.chunky.model.builder;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;

import java.util.ArrayList;
import java.util.Collections;

public class QuadModelBuilder {
  private ArrayList<Quad> quads = new ArrayList<>();
  private ArrayList<Texture> textures = new ArrayList<>();

  public QuadModelBuilder addModel(Quad[] quads, Texture[] textures) {
    Collections.addAll(this.quads, quads);
    Collections.addAll(this.textures, textures);
    return this;
  }

  public QuadModelBuilder addModel(Quad[] quads, Texture texture) {
    Collections.addAll(this.quads, quads);
    for (int i = 0; i < quads.length; i++) {
      this.textures.add(texture);
    }
    return this;
  }

  public Quad[] getQuads() {
    return quads.toArray(new Quad[0]);
  }

  public Texture[] getTextures() {
    return textures.toArray(new Texture[0]);
  }
}
