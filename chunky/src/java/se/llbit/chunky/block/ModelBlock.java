package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.plugin.PluginApi;

@PluginApi
public interface ModelBlock {

  @PluginApi
  BlockModel getModel();
}
