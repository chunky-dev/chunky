package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.TrialSpawnerModel;

public class TrialSpawner extends AbstractModelBlock {
  private final String description;

  public TrialSpawner(boolean ominous, String trialSpawnerState) {
    super("trial_spawner", TrialSpawnerModel.getTopTexture(ominous, trialSpawnerState));
    this.description = "ominous=" + ominous + ", trial_spawner_state=" + trialSpawnerState;
    this.model = new TrialSpawnerModel(ominous, trialSpawnerState);
  }

  @Override
  public String description() {
    return description;
  }
}
