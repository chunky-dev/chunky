package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.VaultModel;

public class Vault extends AbstractModelBlock {
  private final String description;

  public Vault(String facing, boolean ominous, String vaultState) {
    super("vault", VaultModel.getTopTexture(ominous, vaultState));
    this.description = "facing=" + facing + ", ominous=" + ominous + ", vault_state=" + vaultState;
    this.model = new VaultModel(facing, ominous, vaultState);
  }

  @Override
  public String description() {
    return description;
  }
}
