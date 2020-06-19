package se.llbit.chunky.resources;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.ResourcepackBlockProvider;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.model.JsonModel;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;

import java.io.IOException;
import java.nio.file.Files;

import static se.llbit.chunky.block.ResourcepackBlockProvider.blocks;

public class BlockModelLoader implements ResourcePackLoader.PackLoader {
  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    DataPackUtil.forEachBlockstate(resourcePacks, block -> {
      String blockName = block.name();
      String fqBlockName = block.getNamespacedName();

      if (blocks.containsKey(fqBlockName)) {
        // this block was already provided by a different resource pack
        return;
      }
      ResourcepackBlockProvider.JsonModelLoader modelLoader = new ResourcepackBlockProvider.JsonModelLoader();

      try (JsonParser parser =
             new JsonParser(Files.newInputStream(block.path()))) {
        ResourcepackBlockProvider.BlockVariants variants = new ResourcepackBlockProvider.BlockVariants();

        JsonObject blockStates = parser.parse().object();
        if (blockStates.get("variants").isObject()) {
          for (JsonMember blockState :
            blockStates.get("variants").object().members) {
            // TODO add support for pseudo-random models
            JsonObject blockDefinition =
              blockState.getValue().isArray()
                ? blockState.getValue().array().get(0).object()
                : blockState.getValue().object();
            String modelName =
              blockDefinition.get("model").stringValue("unknown:unknown");
            if (modelName.equals("minecraft:block/air")) {
              variants.variants.add(new ResourcepackBlockProvider.SimpleBlockVariant(Air.INSTANCE));
            } else {
              Block model =
                modelLoader.loadBlockModel(
                  resourcePacks, modelName, fqBlockName);
              if (model instanceof ResourcepackBlockProvider.JsonModel) {
                if (blockDefinition.get("x").doubleValue(0) > 0) {
                  ((ResourcepackBlockProvider.JsonModel) model)
                    .rotateX(
                      blockDefinition.get("x").intValue(0),
                      blockDefinition.get("uvlock").boolValue(false));
                }
                if (blockDefinition.get("y").doubleValue(0) > 0) {
                  ((ResourcepackBlockProvider.JsonModel) model)
                    .rotateY(
                      blockDefinition.get("y").intValue(0),
                      blockDefinition.get("uvlock").boolValue(false));
                }
                if (blockDefinition.get("z").doubleValue(0) > 0) {
                  ((ResourcepackBlockProvider.JsonModel) model)
                    .rotateZ(
                      blockDefinition.get("z").intValue(0),
                      blockDefinition.get("uvlock").boolValue(false));
                }
              }

              variants.variants.add(
                new ResourcepackBlockProvider.VariantsBlockVariant(blockState.getName(), model));
            }
          }
        } else if (blockStates.get("multipart").isArray()) {
          ResourcepackBlockProvider.BlockVariantMultipart multipartBlockVariant =
            new ResourcepackBlockProvider.BlockVariantMultipart(blockName);
          for (JsonValue part : blockStates.get("multipart").array()) {
            JsonObject blockDefinition =
              part.object().get("apply").isArray()
                ? part.object().get("apply").array().get(0).object()
                : part.object().get("apply").object();
            String modelName =
              blockDefinition.get("model").stringValue("unknown:unknown");

            Block model =
              modelLoader.loadBlockModel(
                resourcePacks, modelName, fqBlockName);

            if (model instanceof ResourcepackBlockProvider.JsonModel) {
              if (blockDefinition.get("x").doubleValue(0) > 0) {
                ((ResourcepackBlockProvider.JsonModel) model)
                  .rotateX(
                    blockDefinition.get("x").intValue(0),
                    blockDefinition.get("uvlock").boolValue(false));
              }
              if (blockDefinition.get("y").doubleValue(0) > 0) {
                ((ResourcepackBlockProvider.JsonModel) model)
                  .rotateY(
                    blockDefinition.get("y").intValue(0),
                    blockDefinition.get("uvlock").boolValue(false));
              }
              if (blockDefinition.get("z").doubleValue(0) > 0) {
                ((ResourcepackBlockProvider.JsonModel) model)
                  .rotateZ(
                    blockDefinition.get("z").intValue(0),
                    blockDefinition.get("uvlock").boolValue(false));
              }
            }
            JsonObject conditions = part.object().get("when").object();
            if (conditions.get("OR").isArray()) {
              multipartBlockVariant.addPart(
                new ResourcepackBlockProvider.MultipartBlockVariant(
                  conditions.get("OR").array(), model));
            } else {
              multipartBlockVariant.addPart(
                new ResourcepackBlockProvider.MultipartBlockVariant(conditions, model));
            }
          }
          variants.variants.add(multipartBlockVariant);
        } else {
          throw new RuntimeException("Unsupported block " + fqBlockName);
        }

        blocks.put(fqBlockName, variants);
      } catch (IOException | JsonParser.SyntaxError | RuntimeException e) {
        System.out.println(
          "Could not load block "
            + fqBlockName
            + " from "
            + block.path().toString());
      }
    });
    return false;
  }
}
