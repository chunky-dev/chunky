package se.llbit.chunky.block;

import java.util.Collection;

public interface BlockProviderRegistry {

  void registerBlockProvider(BlockProvider blockProvider);

  Collection<BlockProvider> getBlockProviders();
}
