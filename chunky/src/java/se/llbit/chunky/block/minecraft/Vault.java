package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.VaultModel;

public class Vault extends AbstractModelBlock {
  private final String description;

  public Vault(String facing, String vaultState) {
    super("vault", VaultModel.getTopTexture(vaultState));
    this.description = "facing=" + facing + ", vault_state=" + vaultState;
    this.model = new VaultModel(facing, vaultState);
  }

  @Override
  public String description() {
    return description;
  }
}
