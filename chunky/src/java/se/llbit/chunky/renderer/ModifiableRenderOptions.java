package se.llbit.chunky.renderer;

public class ModifiableRenderOptions implements RenderOptions {
  protected int renderThreadCount = -1;
  protected int tileWidth = TILE_WIDTH_DEFAULT;
  protected int sppPerPass = SPP_PER_PASS_DEFAULT;

  public void copyState(RenderOptions other) {
    renderThreadCount = other.getRenderThreadCount();
    tileWidth = other.getTileWidth();
    sppPerPass = other.getSppPerPass();
  }

  @Override
  public int getRenderThreadCount() {
    return renderThreadCount;
  }

  public void setRenderThreadCount(int renderThreadCount) {
    this.renderThreadCount = renderThreadCount;
  }

  @Override
  public int getTileWidth() {
    return tileWidth;
  }

  public void setTileWidth(int tileWidth) {
    this.tileWidth = tileWidth;
  }

  @Override
  public int getSppPerPass() {
    return sppPerPass;
  }

  public void setSppPerPass(int sppPerPass) {
    this.sppPerPass = sppPerPass;
  }
}
