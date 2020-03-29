package se.llbit.chunky.block;

import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.model.FlowerPotModel;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.chunky.resources.ShulkerTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;
import se.llbit.util.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockSpec {
    public final Tag tag;

    public BlockSpec(@NotNull Tag tag) {
        this.tag = tag;
    }

    public static BlockSpec deserialize(DataInputStream in) throws IOException {
        Tag tag = CompoundTag.read(in);
        if (tag.isError()) {
            throw new IOException("Error while reading block palette: " + tag.error());
        }
        return new BlockSpec(tag);
    }

    public void serialize(DataOutputStream out) throws IOException {
        System.out.format("tag: 0x%08X %s%n", tag.hashCode(), tag.toString());
        tag.write(out);
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BlockSpec) && ((BlockSpec) obj).tag.equals(tag);
    }

    /**
     * Converts NBT block data to Chunky block object.
     */
    public Block toBlock() {
        // Reference: https://minecraft.gamepedia.com/Java_Edition_data_values#Blocks
        String name = tag.get("Name").stringValue("unknown:unknown");
        if (name.startsWith("minecraft:")) {
            return maybeWaterlogged(blockFromTag(name.substring(10)));
        }
        return Air.INSTANCE;
    }

    private Block maybeWaterlogged(Block block) {
        if (tag.get("Properties").get("waterlogged").stringValue("").equals("true")) {
            block.waterlogged = true;
        }
        return block;
    }

    private Block blockFromTag(String name) {
        // TODO: convert all old block IDs to the new block types.
        // TODO: clean up - register block loaders in hash map instead?
        switch (name) {
            case "air":
            case "cave_air":
            case "void_air":
                return Air.INSTANCE;
            case "infested_stone":
            case "stone":
                return new MinecraftBlock(name, Texture.stone);
            case "granite":
                return new MinecraftBlock(name, Texture.granite);
            case "polished_granite":
                return new MinecraftBlock(name, Texture.smoothGranite);
            case "diorite":
                return new MinecraftBlock(name, Texture.diorite);
            case "polished_diorite":
                return new MinecraftBlock(name, Texture.smoothDiorite);
            case "andesite":
                return new MinecraftBlock(name, Texture.andesite);
            case "polished_andesite":
                return new MinecraftBlock(name, Texture.smoothAndesite);
            case "grass_block":
                return snowCovered(tag, new GrassBlock());
            case "dirt":
                return new MinecraftBlock(name, Texture.dirt);
            case "coarse_dirt":
                return new MinecraftBlock(name, Texture.coarseDirt);
            case "podzol":
                return snowCovered(tag,
                        new TexturedBlock(name, Texture.podzolSide, Texture.podzolTop, Texture.dirt));
            case "infested_cobblestone":
            case "cobblestone":
                return new MinecraftBlock(name, Texture.cobblestone);
            case "oak_planks":
                return new MinecraftBlock(name, Texture.oakPlanks);
            case "spruce_planks":
                return new MinecraftBlock(name, Texture.sprucePlanks);
            case "birch_planks":
                return new MinecraftBlock(name, Texture.birchPlanks);
            case "jungle_planks":
                return new MinecraftBlock(name, Texture.jungleTreePlanks);
            case "acacia_planks":
                return new MinecraftBlock(name, Texture.acaciaPlanks);
            case "dark_oak_planks":
                return new MinecraftBlock(name, Texture.darkOakPlanks);
            case "oak_sapling":
                return new SpriteBlock(name, Texture.oakSapling);
            case "spruce_sapling":
                return new SpriteBlock(name, Texture.spruceSapling);
            case "birch_sapling":
                return new SpriteBlock(name, Texture.birchSapling);
            case "jungle_sapling":
                return new SpriteBlock(name, Texture.jungleSapling);
            case "acacia_sapling":
                return new SpriteBlock(name, Texture.acaciaSapling);
            case "dark_oak_sapling":
                return new SpriteBlock(name, Texture.darkOakSapling);
            case "water": {
                int level = stringToInt(tag.get("Properties").get("level"), 0);
                return new Water(level);
            }
            case "bubble_column":
                return new Water(0); // TODO: render bubbles!
            case "lava": {
                int level = stringToInt(tag.get("Properties").get("level"), 0);
                return new Lava(level);
            }
            case "bedrock":
                return new MinecraftBlock(name, Texture.bedrock);
            case "sand":
                return new MinecraftBlock(name, Texture.sand);
            case "red_sand":
                return new MinecraftBlock(name, Texture.redSand);
            case "gravel":
                return new MinecraftBlock(name, Texture.gravel);
            case "gold_ore":
                return new MinecraftBlock(name, Texture.goldOre);
            case "iron_ore":
                return new MinecraftBlock(name, Texture.ironOre);
            case "coal_ore":
                return new MinecraftBlock(name, Texture.coalOre);
            case "oak_log":
                return log(tag, Texture.oakWood, Texture.oakWoodTop);
            case "spruce_log":
                return log(tag, Texture.spruceWood, Texture.spruceWoodTop);
            case "birch_log":
                return log(tag, Texture.birchWood, Texture.birchWoodTop);
            case "jungle_log":
                return log(tag, Texture.jungleWood, Texture.jungleTreeTop);
            case "acacia_log":
                return log(tag, Texture.acaciaWood, Texture.acaciaWoodTop);
            case "dark_oak_log":
                return log(tag, Texture.darkOakWood, Texture.darkOakWoodTop);
            case "stripped_oak_log":
                return log(tag, Texture.strippedOakLog, Texture.strippedOakLogTop);
            case "stripped_spruce_log":
                return log(tag, Texture.strippedSpruceLog, Texture.strippedSpruceLogTop);
            case "stripped_birch_log":
                return log(tag, Texture.strippedBirchLog, Texture.strippedBirchLogTop);
            case "stripped_jungle_log":
                return log(tag, Texture.strippedJungleLog, Texture.strippedJungleLogTop);
            case "stripped_acacia_log":
                return log(tag, Texture.strippedAcaciaLog, Texture.strippedAcaciaLogTop);
            case "stripped_dark_oak_log":
                return log(tag, Texture.strippedDarkOakLog, Texture.strippedDarkOakLogTop);
            case "stripped_oak_wood":
                return new MinecraftBlock(name, Texture.strippedOakLog);
            case "stripped_spruce_wood":
                return new MinecraftBlock(name, Texture.strippedSpruceLog);
            case "stripped_birch_wood":
                return new MinecraftBlock(name, Texture.strippedBirchLog);
            case "stripped_jungle_wood":
                return new MinecraftBlock(name, Texture.strippedJungleLog);
            case "stripped_acacia_wood":
                return new MinecraftBlock(name, Texture.strippedAcaciaLog);
            case "stripped_dark_oak_wood":
                return new MinecraftBlock(name, Texture.strippedDarkOakLog);
            case "oak_wood":
                return new MinecraftBlock(name, Texture.oakWood);
            case "spruce_wood":
                return new MinecraftBlock(name, Texture.spruceWood);
            case "birch_wood":
                return new MinecraftBlock(name, Texture.birchWood);
            case "jungle_wood":
                return new MinecraftBlock(name, Texture.jungleWood);
            case "acacia_wood":
                return new MinecraftBlock(name, Texture.acaciaWood);
            case "dark_oak_wood":
                return new MinecraftBlock(name, Texture.darkOakWood);
            case "oak_leaves":
                return new Leaves(name, Texture.oakLeaves);
            case "spruce_leaves":
                return new Leaves(name, Texture.spruceLeaves);
            case "birch_leaves":
                return new Leaves(name, Texture.birchLeaves);
            case "jungle_leaves":
                return new Leaves(name, Texture.jungleTreeLeaves);
            case "acacia_leaves":
                return new Leaves(name, Texture.acaciaLeaves);
            case "dark_oak_leaves":
                return new Leaves(name, Texture.darkOakLeaves);
            case "sponge":
                return new MinecraftBlock(name, Texture.sponge);
            case "wet_sponge":
                return new MinecraftBlock(name, Texture.wetSponge);
            case "glass":
                return new Glass(name, Texture.glass);
            case "lapis_ore":
                return new MinecraftBlock(name, Texture.lapisOre);
            case "lapis_block":
                return new MinecraftBlock(name, Texture.lapisBlock);
            case "dispenser":
                return dispenser(tag);
            case "sandstone":
                return new TexturedBlock(name, Texture.sandstoneSide,
                        Texture.sandstoneTop, Texture.sandstoneBottom);
            case "chiseled_sandstone":
                return new TexturedBlock(name, Texture.sandstoneDecorated,
                        Texture.sandstoneTop, Texture.sandstoneBottom);
            case "cut_sandstone":
                return new TexturedBlock(name, Texture.sandstoneCut,
                        Texture.sandstoneTop, Texture.sandstoneBottom);
            case "note_block":
                return new MinecraftBlock(name, Texture.jukeboxSide);
            case "powered_rail":
                return poweredRail(tag);
            case "detector_rail": {
                Tag properties = tag.get("Properties");
                String powered = properties.get("powered").stringValue("false");
                Texture straightTrack = powered.equals("true")
                        ? Texture.detectorRailOn
                        : Texture.detectorRail;
                return rail(tag, straightTrack);
            }
            case "sticky_piston":
                return piston(tag, true);
            case "cobweb":
                return new SpriteBlock(name, Texture.cobweb);
            case "grass":
                return new Grass();
            case "fern":
                return new Fern();
            case "dead_bush":
                return new SpriteBlock(name, Texture.deadBush);
            case "seagrass":
                return new SpriteBlock(name, Texture.seagrass);
            case "tall_seagrass": {
                String half = tag.get("Properties").get("half").stringValue("lower");
                return new SpriteBlock(name, half.equals("lower")
                        ? Texture.tallSeagrassBottom
                        : Texture.tallSeagrassTop);
            }
            case "sea_pickle":
                return seaPickle(tag);
            case "piston":
                return piston(tag, false);
            case "piston_head":
                return pistonHead(tag);
            case "moving_piston":
                // Invisible.
                return Air.INSTANCE;
            case "white_wool":
                return new MinecraftBlock(name, Texture.whiteWool);
            case "orange_wool":
                return new MinecraftBlock(name, Texture.orangeWool);
            case "magenta_wool":
                return new MinecraftBlock(name, Texture.magentaWool);
            case "light_blue_wool":
                return new MinecraftBlock(name, Texture.lightBlueWool);
            case "yellow_wool":
                return new MinecraftBlock(name, Texture.yellowWool);
            case "lime_wool":
                return new MinecraftBlock(name, Texture.limeWool);
            case "pink_wool":
                return new MinecraftBlock(name, Texture.pinkWool);
            case "gray_wool":
                return new MinecraftBlock(name, Texture.grayWool);
            case "light_gray_wool":
                return new MinecraftBlock(name, Texture.lightGrayWool);
            case "cyan_wool":
                return new MinecraftBlock(name, Texture.cyanWool);
            case "purple_wool":
                return new MinecraftBlock(name, Texture.purpleWool);
            case "blue_wool":
                return new MinecraftBlock(name, Texture.blueWool);
            case "brown_wool":
                return new MinecraftBlock(name, Texture.brownWool);
            case "green_wool":
                return new MinecraftBlock(name, Texture.greenWool);
            case "red_wool":
                return new MinecraftBlock(name, Texture.redWool);
            case "black_wool":
                return new MinecraftBlock(name, Texture.blackWool);
            case "dandelion":
                return new SpriteBlock(name, Texture.dandelion);
            case "poppy":
                return new SpriteBlock(name, Texture.poppy);
            case "blue_orchid":
                return new SpriteBlock(name, Texture.blueOrchid);
            case "allium":
                return new SpriteBlock(name, Texture.allium);
            case "azure_bluet":
                return new SpriteBlock(name, Texture.azureBluet);
            case "red_tulip":
                return new SpriteBlock(name, Texture.redTulip);
            case "orange_tulip":
                return new SpriteBlock(name, Texture.orangeTulip);
            case "white_tulip":
                return new SpriteBlock(name, Texture.whiteTulip);
            case "pink_tulip":
                return new SpriteBlock(name, Texture.pinkTulip);
            case "oxeye_daisy":
                return new SpriteBlock(name, Texture.oxeyeDaisy);
            case "cornflower":
                return new SpriteBlock(name, Texture.cornflower);
            case "lily_of_the_valley":
                return new SpriteBlock(name, Texture.lilyOfTheValley);
            case "wither_rose":
                return new SpriteBlock(name, Texture.witherRose);
            case "brown_mushroom":
                return new SpriteBlock(name, Texture.brownMushroom);
            case "red_mushroom":
                return new SpriteBlock(name, Texture.redMushroom);
            case "gold_block":
                return new MinecraftBlock(name, Texture.goldBlock);
            case "iron_block":
                return new MinecraftBlock(name, Texture.ironBlock);
            case "oak_slab":
                return slab(tag, Texture.oakPlanks);
            case "spruce_slab":
                return slab(tag, Texture.sprucePlanks);
            case "birch_slab":
                return slab(tag, Texture.birchPlanks);
            case "jungle_slab":
                return slab(tag, Texture.jungleTreePlanks);
            case "acacia_slab":
                return slab(tag, Texture.acaciaPlanks);
            case "dark_oak_slab":
                return slab(tag, Texture.darkOakPlanks);
            case "stone_slab":
                return slab(tag, Texture.stone);
            case "smooth_stone_slab": // introduced in 1.14, previously called stone_slab (and the stone_slab above didn't exist)
                return slab(tag, Texture.smoothStoneSlabSide, Texture.smoothStone);
            case "sandstone_slab":
                return slab(tag, Texture.sandstoneSide, Texture.sandstoneTop);
            case "petrified_oak_slab":
                return slab(tag, Texture.oakPlanks);
            case "cobblestone_slab":
                return slab(tag, Texture.cobblestone);
            case "brick_slab":
                return slab(tag, Texture.brick);
            case "stone_brick_slab":
                return slab(tag, Texture.stoneBrick);
            case "nether_brick_slab":
                return slab(tag, Texture.netherBrick);
            case "quartz_slab":
                return slab(tag, Texture.quartzSide, Texture.quartzTop);
            case "red_sandstone_slab":
                return slab(tag, Texture.redSandstoneSide, Texture.redSandstoneTop);
            case "purpur_slab":
                return slab(tag, Texture.purpurBlock);
            case "prismarine_slab":
                return slab(tag, Texture.prismarine);
            case "prismarine_brick_slab":
                return slab(tag, Texture.prismarineBricks);
            case "dark_prismarine_slab":
                return slab(tag, Texture.darkPrismarine);
            case "smooth_quartz":
                return new MinecraftBlock(name, Texture.quartzBottom);
            case "smooth_red_sandstone":
                return new MinecraftBlock(name, Texture.redSandstoneTop);
            case "smooth_sandstone":
                return new MinecraftBlock(name, Texture.sandstoneTop);
            case "smooth_stone":
                return new MinecraftBlock(name, Texture.smoothStone);
            case "bricks":
                return new MinecraftBlock(name, Texture.brick);
            case "tnt":
                return new TexturedBlock(name, Texture.tntSide, Texture.tntTop, Texture.tntBottom);
            case "bookshelf":
                return new TexturedBlock(name, Texture.bookshelf, Texture.oakPlanks);
            case "mossy_cobblestone":
                return new MinecraftBlock(name, Texture.mossStone);
            case "obsidian":
                return new MinecraftBlock(name, Texture.obsidian);
            case "torch":
                return new Torch(name, Texture.torch);
            case "wall_torch":
                return wallTorch(tag, Texture.torch);
            case "end_rod":
                return endRod(tag);
            case "chorus_plant": {
                Tag properties = tag.get("Properties");
                String north = properties.get("north").stringValue("false");
                String south = properties.get("south").stringValue("false");
                String east = properties.get("east").stringValue("false");
                String west = properties.get("west").stringValue("false");
                String up = properties.get("up").stringValue("false");
                String down = properties.get("down").stringValue("false");
                return new ChorusPlant(
                        north.equals("true"),
                        south.equals("true"),
                        east.equals("true"),
                        west.equals("true"),
                        up.equals("true"),
                        down.equals("true"));
            }
            case "chorus_flower": {
                int age = stringToInt(tag.get("Properties").get("age"), 0);
                return new ChorusFlower(age);
            }
            case "purpur_block":
                return new MinecraftBlock(name, Texture.purpurBlock);
            case "purpur_pillar":
                return log(tag, Texture.purpurPillarSide, Texture.purpurPillarTop);
            case "purpur_stairs":
                return stairs(tag, Texture.purpurBlock);
            case "oak_stairs":
                return stairs(tag, Texture.oakPlanks);
            case "spruce_stairs":
                return stairs(tag, Texture.sprucePlanks);
            case "birch_stairs":
                return stairs(tag, Texture.birchPlanks);
            case "jungle_stairs":
                return stairs(tag, Texture.jungleTreePlanks);
            case "acacia_stairs":
                return stairs(tag, Texture.acaciaPlanks);
            case "dark_oak_stairs":
                return stairs(tag, Texture.darkOakPlanks);
            case "chest":
                return chest(tag);
            case "diamond_ore":
                return new MinecraftBlock(name, Texture.diamondOre);
            case "diamond_block":
                return new MinecraftBlock(name, Texture.diamondBlock);
            case "crafting_table":
                return new TexturedBlock(name, Texture.workbenchFront, Texture.workbenchSide,
                        Texture.workbenchSide, Texture.workbenchFront, Texture.workbenchTop, Texture.oakPlanks);
            case "farmland": {
                int moisture = stringToInt(tag.get("Properties").get("moisture"), 0);
                return new Farmland(moisture);
            }
            case "furnace":
                return furnace(tag);
            case "ladder": {
                String facing = getFacing(tag, "north");
                return new Ladder(facing);
            }
            case "rail":
                return rail(tag, Texture.rails);
            case "cobblestone_stairs":
                return stairs(tag, Texture.cobblestone);
            case "lever":
                return lever(tag);
            case "stone_pressure_plate":
                return new PressurePlate(name, Texture.stone);
            case "oak_pressure_plate":
                return new PressurePlate(name, Texture.oakPlanks);
            case "spruce_pressure_plate":
                return new PressurePlate(name, Texture.sprucePlanks);
            case "birch_pressure_plate":
                return new PressurePlate(name, Texture.birchPlanks);
            case "jungle_pressure_plate":
                return new PressurePlate(name, Texture.jungleTreePlanks);
            case "acacia_pressure_plate":
                return new PressurePlate(name, Texture.acaciaPlanks);
            case "dark_oak_pressure_plate":
                return new PressurePlate(name, Texture.darkOakPlanks);
            case "redstone_ore":
                return new MinecraftBlock(name, Texture.redstoneOre);
            case "redstone_torch":
                return redstoneTorch(tag);
            case "redstone_wall_torch":
                return redstoneWallTorch(tag);
            case "stone_button":
                return button(tag, Texture.stone);
            case "snow": {
                int layers = stringToInt(tag.get("Properties").get("layers"), 1);
                return new Snow(layers);
            }
            case "ice":
                return new MinecraftBlock(name, Texture.ice);
            case "snow_block":
                return new MinecraftBlock(name, Texture.snowBlock);
            case "cactus":
                return new Cactus();
            case "clay":
                return new MinecraftBlock(name, Texture.clay);
            case "jukebox":
                return new TexturedBlock(name, Texture.jukeboxSide,
                        Texture.jukeboxTop, Texture.jukeboxSide);
            case "oak_fence":
                return fence(tag, Texture.oakPlanks);
            case "spruce_fence":
                return fence(tag, Texture.sprucePlanks);
            case "birch_fence":
                return fence(tag, Texture.birchPlanks);
            case "jungle_fence":
                return fence(tag, Texture.jungleTreePlanks);
            case "acacia_fence":
                return fence(tag, Texture.acaciaPlanks);
            case "dark_oak_fence":
                return fence(tag, Texture.darkOakPlanks);
            case "pumpkin":
                return new TexturedBlock(name, Texture.pumpkinSide, Texture.pumpkinTop);
            case "carved_pumpkin":
                return new TopBottomOrientedTexturedBlock(name, getFacing(tag, "north"), Texture.pumpkinFront, Texture.pumpkinSide, Texture.pumpkinTop);
            case "netherrack":
                return new MinecraftBlock(name, Texture.netherrack);
            case "soul_sand":
                return new MinecraftBlock(name, Texture.soulsand);
            case "glowstone":
                return new MinecraftBlock(name, Texture.glowstone);
            case "jack_o_lantern":
                return new TopBottomOrientedTexturedBlock(name, getFacing(tag, "north"), Texture.jackolanternFront, Texture.pumpkinSide, Texture.pumpkinTop);
            case "oak_trapdoor":
                return trapdoor(tag, Texture.trapdoor);
            case "spruce_trapdoor":
                return trapdoor(tag, Texture.spruceTrapdoor);
            case "birch_trapdoor":
                return trapdoor(tag, Texture.birchTrapdoor);
            case "jungle_trapdoor":
                return trapdoor(tag, Texture.jungleTrapdoor);
            case "acacia_trapdoor":
                return trapdoor(tag, Texture.acaciaTrapdoor);
            case "dark_oak_trapdoor":
                return trapdoor(tag, Texture.darkOakTrapdoor);
            case "infested_stone_bricks":
            case "stone_bricks":
                return new MinecraftBlock(name, Texture.stoneBrick);
            case "infested_mossy_stone_bricks":
            case "mossy_stone_bricks":
                return new MinecraftBlock(name, Texture.mossyStoneBrick);
            case "infested_cracked_stone_bricks":
            case "cracked_stone_bricks":
                return new MinecraftBlock(name, Texture.crackedStoneBrick);
            case "infested_chiseled_stone_bricks":
            case "chiseled_stone_bricks":
                return new MinecraftBlock(name, Texture.circleStoneBrick);
            case "nether_bricks":
                return new MinecraftBlock(name, Texture.netherBrick);
            case "brown_mushroom_block":
                return hugeMushroom(tag, Texture.hugeBrownMushroom);
            case "red_mushroom_block":
                return hugeMushroom(tag, Texture.hugeRedMushroom);
            case "mushroom_stem":
                return hugeMushroom(tag, Texture.mushroomStem);
            case "iron_bars":
                return ironBars(tag);
            case "glass_pane":
                return glassPane(tag, Texture.glass, Texture.glassPaneTop);
            case "melon":
                return new TexturedBlock(name, Texture.melonSide, Texture.melonTop);
            case "vine":
                return vine(tag);
            case "oak_fence_gate":
                return fenceGate(tag, Texture.oakPlanks);
            case "spruce_fence_gate":
                return fenceGate(tag, Texture.sprucePlanks);
            case "birch_fence_gate":
                return fenceGate(tag, Texture.sprucePlanks);
            case "jungle_fence_gate":
                return fenceGate(tag, Texture.jungleTreePlanks);
            case "acacia_fence_gate":
                return fenceGate(tag, Texture.acaciaPlanks);
            case "dark_oak_fence_gate":
                return fenceGate(tag, Texture.darkOakPlanks);
            case "brick_stairs":
                return stairs(tag, Texture.brick);
            case "stone_brick_stairs":
                return stairs(tag, Texture.stoneBrick);
            case "mycelium":
                return snowCovered(tag,
                        new TexturedBlock(name, Texture.myceliumSide, Texture.myceliumTop, Texture.dirt));
            case "lily_pad":
                return new LilyPad();
            case "nether_brick_fence":
                return fence(tag, Texture.netherBrick);
            case "nether_brick_stairs":
                return stairs(tag, Texture.netherBrick);
            case "enchanting_table":
                return new EnchantingTable();
            case "end_portal_frame": {
                String eye = tag.get("Properties").get("eye").stringValue("false");
                String facing = getFacing(tag, "north");
                return new EndPortalFrame(eye.equals("true"), facing);
            }
            case "end_stone":
                return new MinecraftBlock(name, Texture.endStone);
            case "end_stone_bricks":
                return new MinecraftBlock(name, Texture.endBricks);
            case "redstone_lamp": {
                String lit = tag.get("Properties").get("lit").stringValue("false");
                return new RedstoneLamp(lit.equals("true"));
            }
            case "cocoa":
                return cocoa(tag);
            case "sandstone_stairs":
                return stairs(tag, Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom);
            case "emerald_ore":
                return new MinecraftBlock(name, Texture.emeraldOre);
            case "ender_chest": {
                String facing = getFacing(tag, "north");
                return new EnderChest(facing);
            }
            case "tripwire_hook":
                return tripwireHook(tag);
            case "tripwire":
                return tripwire(tag);
            case "emerald_block":
                return new MinecraftBlock(name, Texture.emeraldBlock);
            case "beacon":
                return new Beacon();
            case "cobblestone_wall":
                return wall(tag, Texture.cobblestone);
            case "mossy_cobblestone_wall":
                return wall(tag, Texture.mossStone);
            case "oak_button":
                return button(tag, Texture.oakPlanks);
            case "spruce_button":
                return button(tag, Texture.sprucePlanks);
            case "birch_button":
                return button(tag, Texture.birchPlanks);
            case "jungle_button":
                return button(tag, Texture.jungleTreePlanks);
            case "acacia_button":
                return button(tag, Texture.acaciaPlanks);
            case "dark_oak_button":
                return button(tag, Texture.darkOakPlanks);
            case "anvil":
                return anvil(tag, 0);
            case "chipped_anvil":
                return anvil(tag, 1);
            case "damaged_anvil":
                return anvil(tag, 2);
            case "trapped_chest":
                return chest(tag);
            case "light_weighted_pressure_plate":
                return new PressurePlate(name, Texture.goldBlock);
            case "heavy_weighted_pressure_plate":
                return new PressurePlate(name, Texture.ironBlock);
            case "daylight_detector": {
                String inverted = tag.get("Properties").get("inverted").stringValue("false");
                return new DaylightDetector(inverted.equals("true"));
            }
            case "redstone_block":
                return new MinecraftBlock(name, Texture.redstoneBlock);
            case "nether_quartz_ore":
                return new MinecraftBlock(name, Texture.netherQuartzOre);
            case "hopper": {
                String facing = getFacing(tag, "down");
                return new Hopper(facing);
            }
            case "chiseled_quartz_block":
                return new TexturedBlock(name, Texture.quartzChiseled, Texture.quartzChiseledTop);
            case "quartz_block":
                return new MinecraftBlock(name, Texture.quartzSide);
            case "quartz_pillar":
                return log(tag, Texture.quartzPillar, Texture.quartzPillarTop);
            case "quartz_stairs":
                return stairs(tag, Texture.quartzSide, Texture.quartzTop, Texture.quartzBottom);
            case "activator_rail": {
                Tag properties = tag.get("Properties");
                String powered = properties.get("powered").stringValue("false");
                Texture straightTrack = powered.equals("true")
                        ? Texture.activatorRailPowered
                        : Texture.activatorRail;
                return rail(tag, straightTrack);
            }
            case "dropper": {
                String facing = getFacing(tag, "south");
                return new Dropper(facing);
            }
            case "white_terracotta":
                return new MinecraftBlock(name, Texture.whiteClay);
            case "orange_terracotta":
                return new MinecraftBlock(name, Texture.orangeClay);
            case "magenta_terracotta":
                return new MinecraftBlock(name, Texture.magentaClay);
            case "light_blue_terracotta":
                return new MinecraftBlock(name, Texture.lightBlueClay);
            case "yellow_terracotta":
                return new MinecraftBlock(name, Texture.yellowClay);
            case "lime_terracotta":
                return new MinecraftBlock(name, Texture.limeClay);
            case "pink_terracotta":
                return new MinecraftBlock(name, Texture.pinkClay);
            case "gray_terracotta":
                return new MinecraftBlock(name, Texture.grayClay);
            case "light_gray_terracotta":
                return new MinecraftBlock(name, Texture.lightGrayClay);
            case "cyan_terracotta":
                return new MinecraftBlock(name, Texture.cyanClay);
            case "purple_terracotta":
                return new MinecraftBlock(name, Texture.purpleClay);
            case "blue_terracotta":
                return new MinecraftBlock(name, Texture.blueClay);
            case "brown_terracotta":
                return new MinecraftBlock(name, Texture.brownClay);
            case "green_terracotta":
                return new MinecraftBlock(name, Texture.greenClay);
            case "red_terracotta":
                return new MinecraftBlock(name, Texture.redClay);
            case "black_terracotta":
                return new MinecraftBlock(name, Texture.blackClay);
            case "iron_trapdoor":
                return trapdoor(tag, Texture.ironTrapdoor);
            case "hay_block":
                return log(tag, Texture.hayBlockSide, Texture.hayBlockTop);
            case "white_carpet":
                return new Carpet(name, Texture.whiteWool);
            case "orange_carpet":
                return new Carpet(name, Texture.orangeWool);
            case "magenta_carpet":
                return new Carpet(name, Texture.magentaWool);
            case "light_blue_carpet":
                return new Carpet(name, Texture.lightBlueWool);
            case "yellow_carpet":
                return new Carpet(name, Texture.yellowWool);
            case "lime_carpet":
                return new Carpet(name, Texture.limeWool);
            case "pink_carpet":
                return new Carpet(name, Texture.pinkWool);
            case "gray_carpet":
                return new Carpet(name, Texture.grayWool);
            case "light_gray_carpet":
                return new Carpet(name, Texture.lightGrayWool);
            case "cyan_carpet":
                return new Carpet(name, Texture.cyanWool);
            case "purple_carpet":
                return new Carpet(name, Texture.purpleWool);
            case "blue_carpet":
                return new Carpet(name, Texture.blueWool);
            case "brown_carpet":
                return new Carpet(name, Texture.brownWool);
            case "green_carpet":
                return new Carpet(name, Texture.greenWool);
            case "red_carpet":
                return new Carpet(name, Texture.redWool);
            case "black_carpet":
                return new Carpet(name, Texture.blackWool);
            case "terracotta":
                return new MinecraftBlock(name, Texture.hardenedClay);
            case "coal_block":
                return new MinecraftBlock(name, Texture.coalBlock);
            case "packed_ice":
                return new MinecraftBlock(name, Texture.packedIce);
            case "slime_block":
                return new Slime();
            case "grass_path":
                return new GrassPath();
            case "sunflower": {
                String half = tag.get("Properties").get("half").stringValue("lower");
                return new Sunflower(half);
            }
            case "lilac":
                return largeFlower(tag, Texture.lilacTop, Texture.lilacBottom);
            case "rose_bush":
                return largeFlower(tag, Texture.roseBushTop, Texture.roseBushBottom);
            case "peony":
                return largeFlower(tag, Texture.peonyTop, Texture.peonyBottom);
            case "tall_grass": {
                String half = tag.get("Properties").get("half").stringValue("lower");
                return new TallGrass(half);
            }
            case "large_fern": {
                String half = tag.get("Properties").get("half").stringValue("lower");
                return new LargeFern(half);
            }
            case "white_stained_glass":
                return new Glass(name, Texture.whiteGlass);
            case "orange_stained_glass":
                return new Glass(name, Texture.orangeGlass);
            case "magenta_stained_glass":
                return new Glass(name, Texture.magentaGlass);
            case "light_blue_stained_glass":
                return new Glass(name, Texture.lightBlueGlass);
            case "yellow_stained_glass":
                return new Glass(name, Texture.yellowGlass);
            case "lime_stained_glass":
                return new Glass(name, Texture.limeGlass);
            case "pink_stained_glass":
                return new Glass(name, Texture.pinkGlass);
            case "gray_stained_glass":
                return new Glass(name, Texture.grayGlass);
            case "light_gray_stained_glass":
                return new Glass(name, Texture.lightGrayGlass);
            case "cyan_stained_glass":
                return new Glass(name, Texture.cyanGlass);
            case "purple_stained_glass":
                return new Glass(name, Texture.purpleGlass);
            case "blue_stained_glass":
                return new Glass(name, Texture.blueGlass);
            case "brown_stained_glass":
                return new Glass(name, Texture.brownGlass);
            case "green_stained_glass":
                return new Glass(name, Texture.greenGlass);
            case "red_stained_glass":
                return new Glass(name, Texture.redGlass);
            case "black_stained_glass":
                return new Glass(name, Texture.blackGlass);
            case "white_stained_glass_pane":
                return glassPane(tag, Texture.whiteGlass, Texture.whiteGlassPaneSide);
            case "orange_stained_glass_pane":
                return glassPane(tag, Texture.orangeGlass, Texture.orangeGlassPaneSide);
            case "magenta_stained_glass_pane":
                return glassPane(tag, Texture.magentaGlass, Texture.magentaGlassPaneSide);
            case "light_blue_stained_glass_pane":
                return glassPane(tag, Texture.lightBlueGlass, Texture.lightBlueGlassPaneSide);
            case "yellow_stained_glass_pane":
                return glassPane(tag, Texture.yellowGlass, Texture.yellowGlassPaneSide);
            case "lime_stained_glass_pane":
                return glassPane(tag, Texture.limeGlass, Texture.limeGlassPaneSide);
            case "pink_stained_glass_pane":
                return glassPane(tag, Texture.pinkGlass, Texture.pinkGlassPaneSide);
            case "gray_stained_glass_pane":
                return glassPane(tag, Texture.grayGlass, Texture.grayGlassPaneSide);
            case "light_gray_stained_glass_pane":
                return glassPane(tag, Texture.lightGrayGlass, Texture.lightGrayGlassPaneSide);
            case "cyan_stained_glass_pane":
                return glassPane(tag, Texture.cyanGlass, Texture.cyanGlassPaneSide);
            case "purple_stained_glass_pane":
                return glassPane(tag, Texture.purpleGlass, Texture.purpleGlassPaneSide);
            case "blue_stained_glass_pane":
                return glassPane(tag, Texture.blueGlass, Texture.blueGlassPaneSide);
            case "brown_stained_glass_pane":
                return glassPane(tag, Texture.brownGlass, Texture.brownGlassPaneSide);
            case "green_stained_glass_pane":
                return glassPane(tag, Texture.greenGlass, Texture.greenGlassPaneSide);
            case "red_stained_glass_pane":
                return glassPane(tag, Texture.redGlass, Texture.redGlassPaneSide);
            case "black_stained_glass_pane":
                return glassPane(tag, Texture.blackGlass, Texture.blackGlassPaneSide);
            case "prismarine":
                return new MinecraftBlock(name, Texture.prismarine);
            case "prismarine_bricks":
                return new MinecraftBlock(name, Texture.prismarineBricks);
            case "dark_prismarine":
                return new MinecraftBlock(name, Texture.darkPrismarine);
            case "prismarine_stairs":
                return stairs(tag, Texture.prismarine);
            case "prismarine_brick_stairs":
                return stairs(tag, Texture.prismarineBricks);
            case "dark_prismarine_stairs":
                return stairs(tag, Texture.darkPrismarine);
            case "sea_lantern":
                return new MinecraftBlock(name, Texture.seaLantern);
            case "red_sandstone":
                return new TexturedBlock(name, Texture.redSandstoneSide,
                        Texture.redSandstoneTop, Texture.redSandstoneBottom);
            case "chiseled_red_sandstone":
                return new TexturedBlock(name, Texture.redSandstoneDecorated,
                        Texture.redSandstoneTop, Texture.redSandstoneBottom);
            case "cut_red_sandstone":
                return new TexturedBlock(name, Texture.redSandstoneCut,
                        Texture.redSandstoneTop, Texture.redSandstoneBottom);
            case "red_sandstone_stairs":
                return stairs(tag, Texture.redSandstoneSide,
                        Texture.redSandstoneTop, Texture.redSandstoneBottom);
            case "magma_block": {
                Block block = new MinecraftBlock(name, Texture.magma);
                block.emittance = 0.6f;
                return block;
            }
            case "nether_wart_block":
                return new MinecraftBlock(name, Texture.netherWartBlock);
            case "red_nether_bricks":
                return new MinecraftBlock(name, Texture.redNetherBrick);
            case "bone_block":
                return log(tag, Texture.boneSide, Texture.boneTop);
            case "observer": {
                Tag properties = tag.get("Properties");
                String facing = getFacing(tag, "south");
                String powered = properties.get("powered").stringValue("false");
                return new Observer(facing, powered.equals("true"));
            }
            case "shulker_box":
                return shulkerBox(tag, Texture.shulker);
            case "white_shulker_box":
                return shulkerBox(tag, Texture.shulkerWhite);
            case "orange_shulker_box":
                return shulkerBox(tag, Texture.shulkerOrange);
            case "magenta_shulker_box":
                return shulkerBox(tag, Texture.shulkerMagenta);
            case "light_blue_shulker_box":
                return shulkerBox(tag, Texture.shulkerLightBlue);
            case "yellow_shulker_box":
                return shulkerBox(tag, Texture.shulkerYellow);
            case "lime_shulker_box":
                return shulkerBox(tag, Texture.shulkerLime);
            case "pink_shulker_box":
                return shulkerBox(tag, Texture.shulkerPink);
            case "gray_shulker_box":
                return shulkerBox(tag, Texture.shulkerGray);
            case "light_gray_shulker_box":
                return shulkerBox(tag, Texture.shulkerSilver);
            case "cyan_shulker_box":
                return shulkerBox(tag, Texture.shulkerCyan);
            case "purple_shulker_box":
                return shulkerBox(tag, Texture.shulkerPurple);
            case "blue_shulker_box":
                return shulkerBox(tag, Texture.shulkerBlue);
            case "brown_shulker_box":
                return shulkerBox(tag, Texture.shulkerBrown);
            case "green_shulker_box":
                return shulkerBox(tag, Texture.shulkerGreen);
            case "red_shulker_box":
                return shulkerBox(tag, Texture.shulkerRed);
            case "black_shulker_box":
                return shulkerBox(tag, Texture.shulkerBlack);
            case "white_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaWhite);
            case "orange_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaOrange);
            case "magenta_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaMagenta);
            case "light_blue_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaLightBlue);
            case "yellow_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaYellow);
            case "lime_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaLime);
            case "pink_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaPink);
            case "gray_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaGray);
            case "light_gray_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaSilver);
            case "cyan_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaCyan);
            case "purple_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaPurple);
            case "blue_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaBlue);
            case "brown_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaBrown);
            case "green_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaGreen);
            case "red_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaRed);
            case "black_glazed_terracotta":
                return glazedTerracotta(tag, Texture.terracottaBlack);
            case "white_concrete":
                return new MinecraftBlock(name, Texture.concreteWhite);
            case "orange_concrete":
                return new MinecraftBlock(name, Texture.concreteOrange);
            case "magenta_concrete":
                return new MinecraftBlock(name, Texture.concreteMagenta);
            case "light_blue_concrete":
                return new MinecraftBlock(name, Texture.concreteLightBlue);
            case "yellow_concrete":
                return new MinecraftBlock(name, Texture.concreteYellow);
            case "lime_concrete":
                return new MinecraftBlock(name, Texture.concreteLime);
            case "pink_concrete":
                return new MinecraftBlock(name, Texture.concretePink);
            case "gray_concrete":
                return new MinecraftBlock(name, Texture.concreteGray);
            case "light_gray_concrete":
                return new MinecraftBlock(name, Texture.concreteSilver);
            case "cyan_concrete":
                return new MinecraftBlock(name, Texture.concreteCyan);
            case "purple_concrete":
                return new MinecraftBlock(name, Texture.concretePurple);
            case "blue_concrete":
                return new MinecraftBlock(name, Texture.concreteBlue);
            case "brown_concrete":
                return new MinecraftBlock(name, Texture.concreteBrown);
            case "green_concrete":
                return new MinecraftBlock(name, Texture.concreteGreen);
            case "red_concrete":
                return new MinecraftBlock(name, Texture.concreteRed);
            case "black_concrete":
                return new MinecraftBlock(name, Texture.concreteBlack);
            case "white_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderWhite);
            case "orange_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderOrange);
            case "magenta_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderMagenta);
            case "light_blue_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderLightBlue);
            case "yellow_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderYellow);
            case "lime_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderLime);
            case "pink_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderPink);
            case "gray_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderGray);
            case "light_gray_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderSilver);
            case "cyan_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderCyan);
            case "purple_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderPurple);
            case "blue_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderBlue);
            case "brown_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderBrown);
            case "green_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderGreen);
            case "red_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderRed);
            case "black_concrete_powder":
                return new MinecraftBlock(name, Texture.concretePowderBlack);
            case "turtle_egg":
                return turtleEgg(tag);
            case "dead_tube_coral_block":
                return new MinecraftBlock(name, Texture.deadTubeCoralBlock);
            case "dead_brain_coral_block":
                return new MinecraftBlock(name, Texture.deadBrainCoralBlock);
            case "dead_bubble_coral_block":
                return new MinecraftBlock(name, Texture.deadBubbleCoralBlock);
            case "dead_fire_coral_block":
                return new MinecraftBlock(name, Texture.deadFireCoralBlock);
            case "dead_horn_coral_block":
                return new MinecraftBlock(name, Texture.deadHornCoralBlock);
            case "tube_coral_block":
                return new MinecraftBlock(name, Texture.tubeCoralBlock);
            case "brain_coral_block":
                return new MinecraftBlock(name, Texture.brainCoralBlock);
            case "bubble_coral_block":
                return new MinecraftBlock(name, Texture.bubbleCoralBlock);
            case "fire_coral_block":
                return new MinecraftBlock(name, Texture.fireCoralBlock);
            case "horn_coral_block":
                return new MinecraftBlock(name, Texture.hornCoralBlock);
            case "tube_coral":
                return new SpriteBlock(name, Texture.tubeCoral);
            case "brain_coral":
                return new SpriteBlock(name, Texture.hornCoral);
            case "bubble_coral":
                return new SpriteBlock(name, Texture.bubbleCoral);
            case "fire_coral":
                return new SpriteBlock(name, Texture.fireCoral);
            case "horn_coral":
                return new SpriteBlock(name, Texture.hornCoral);
            case "dead_tube_coral":
                return new SpriteBlock(name, Texture.deadTubeCoral);
            case "dead_brain_coral":
                return new SpriteBlock(name, Texture.deadBrainCoral);
            case "dead_bubble_coral":
                return new SpriteBlock(name, Texture.deadBubbleCoral);
            case "dead_fire_coral":
                return new SpriteBlock(name, Texture.deadFireCoral);
            case "dead_horn_coral":
                return new SpriteBlock(name, Texture.deadHornCoral);
            case "tube_coral_fan":
                return new CoralFan(name, "tube");
            case "tube_coral_wall_fan":
                return new WallCoralFan(name, "tube", getFacing(tag, "north"));
            case "brain_coral_fan":
                return new CoralFan(name, "brain");
            case "brain_coral_wall_fan":
                return new WallCoralFan(name, "brain", getFacing(tag, "north"));
            case "bubble_coral_fan":
                return new CoralFan(name, "bubble");
            case "bubble_coral_wall_fan":
                return new WallCoralFan(name, "bubble", getFacing(tag, "north"));
            case "fire_coral_fan":
                return new CoralFan(name, "fire");
            case "fire_coral_wall_fan":
                return new WallCoralFan(name, "fire", getFacing(tag, "north"));
            case "horn_coral_fan":
                return new CoralFan(name, "horn");
            case "horn_coral_wall_fan":
                return new WallCoralFan(name, "horn", getFacing(tag, "north"));
            case "dead_tube_coral_fan":
                return new CoralFan(name, "dead_tube");
            case "dead_tube_coral_wall_fan":
                return new WallCoralFan(name, "dead_tube", getFacing(tag, "north"));
            case "dead_brain_coral_fan":
                return new CoralFan(name, "dead_brain");
            case "dead_brain_coral_wall_fan":
                return new WallCoralFan(name, "dead_brain", getFacing(tag, "north"));
            case "dead_bubble_coral_fan":
                return new CoralFan(name, "dead_bubble");
            case "dead_bubble_coral_wall_fan":
                return new WallCoralFan(name, "dead_bubble", getFacing(tag, "north"));
            case "dead_fire_coral_fan":
                return new CoralFan(name, "dead_fire");
            case "dead_fire_coral_wall_fan":
                return new WallCoralFan(name, "dead_fire", getFacing(tag, "north"));
            case "dead_horn_coral_fan":
                return new CoralFan(name, "dead_horn");
            case "dead_horn_coral_wall_fan":
                return new WallCoralFan(name, "dead_horn", getFacing(tag, "north"));
            case "blue_ice":
                return new MinecraftBlock(name, Texture.blueIce);
            case "conduit":
                return new Conduit();
            case "polished_granite_stairs":
                return stairs(tag, Texture.smoothGranite);
            case "smooth_red_sandstone_stairs":
                return stairs(tag, Texture.redSandstoneTop);
            case "mossy_stone_brick_stairs":
                return stairs(tag, Texture.mossyStoneBrick);
            case "polished_diorite_stairs":
                return stairs(tag, Texture.smoothDiorite);
            case "mossy_cobblestone_stairs":
                return stairs(tag, Texture.mossStone);
            case "end_stone_brick_stairs":
                return stairs(tag, Texture.endBricks);
            case "stone_stairs":
                return stairs(tag, Texture.stone);
            case "smooth_sandstone_stairs":
                return stairs(tag, Texture.sandstoneTop);
            case "smooth_quartz_stairs":
                return stairs(tag, Texture.quartzBottom);
            case "granite_stairs":
                return stairs(tag, Texture.granite);
            case "andesite_stairs":
                return stairs(tag, Texture.andesite);
            case "red_nether_brick_stairs":
                return stairs(tag, Texture.redNetherBrick);
            case "polished_andesite_stairs":
                return stairs(tag, Texture.smoothAndesite);
            case "diorite_stairs":
                return stairs(tag, Texture.diorite);
            case "polished_granite_slab":
                return slab(tag, Texture.smoothGranite);
            case "smooth_red_sandstone_slab":
                return slab(tag, Texture.redSandstoneTop);
            case "mossy_stone_brick_slab":
                return slab(tag, Texture.mossyStoneBrick);
            case "polished_diorite_slab":
                return slab(tag, Texture.smoothDiorite);
            case "mossy_cobblestone_slab":
                return slab(tag, Texture.mossStone);
            case "end_stone_brick_slab":
                return slab(tag, Texture.endBricks);
            case "smooth_sandstone_slab":
                return slab(tag, Texture.sandstoneTop);
            case "smooth_quartz_slab":
                return slab(tag, Texture.quartzBottom, Texture.quartzBottom);
            case "granite_slab":
                return slab(tag, Texture.granite);
            case "andesite_slab":
                return slab(tag, Texture.andesite);
            case "red_nether_brick_slab":
                return slab(tag, Texture.redNetherBrick);
            case "polished_andesite_slab":
                return slab(tag, Texture.smoothAndesite);
            case "diorite_slab":
                return slab(tag, Texture.diorite);
            case "brick_wall":
                return wall(tag, Texture.brick);
            case "prismarine_wall":
                return wall(tag, Texture.prismarineBricks);
            case "red_sandstone_wall":
                return wall(tag, Texture.redSandstoneSide);
            case "mossy_stone_brick_wall":
                return wall(tag, Texture.mossyStoneBrick);
            case "granite_wall":
                return wall(tag, Texture.granite);
            case "stone_brick_wall":
                return wall(tag, Texture.stoneBrick);
            case "nether_brick_wall":
                return wall(tag, Texture.netherBrick);
            case "andesite_wall":
                return wall(tag, Texture.andesite);
            case "red_nether_brick_wall":
                return wall(tag, Texture.redNetherBrick);
            case "sandstone_wall":
                return wall(tag, Texture.sandstoneSide);
            case "end_stone_brick_wall":
                return wall(tag, Texture.endBricks);
            case "diorite_wall":
                return wall(tag, Texture.diorite);
            case "scaffolding":
                return new Scaffolding(tag.get("Properties").get("bottom").stringValue("false").equals("true"));
            case "oak_door":
                return door(tag, Texture.oakDoorTop, Texture.oakDoorBottom);
            case "iron_door":
                return door(tag, Texture.ironDoorTop, Texture.ironDoorBottom);
            case "spruce_door":
                return door(tag, Texture.spruceDoorTop, Texture.spruceDoorBottom);
            case "birch_door":
                return door(tag, Texture.birchDoorTop, Texture.birchDoorBottom);
            case "jungle_door":
                return door(tag, Texture.jungleDoorTop, Texture.jungleDoorBottom);
            case "acacia_door":
                return door(tag, Texture.acaciaDoorTop, Texture.acaciaDoorBottom);
            case "dark_oak_door":
                return door(tag, Texture.darkOakDoorTop, Texture.darkOakDoorBottom);
            case "repeater":
                return repeater(tag);
            case "comparator":
                return comparator(tag);
            case "composter":
                return composter(tag);
            case "fire":
                return new Fire();
            case "wheat":
                return wheat(tag);
            case "sign":
            case "oak_sign":
                return sign(tag, "oak");
            case "wall_sign":
            case "oak_wall_sign":
                return wallSign(tag, "oak");
            case "spruce_sign":
                return sign(tag, "spruce");
            case "spruce_wall_sign":
                return wallSign(tag, "spruce");
            case "birch_sign":
                return sign(tag, "birch");
            case "birch_wall_sign":
                return wallSign(tag, "birch");
            case "jungle_sign":
                return sign(tag, "jungle");
            case "jungle_wall_sign":
                return wallSign(tag, "jungle");
            case "acacia_sign":
                return sign(tag, "acacia");
            case "acacia_wall_sign":
                return wallSign(tag, "acacia");
            case "dark_oak_sign":
                return sign(tag, "dark_oak");
            case "dark_oak_wall_sign":
                return wallSign(tag, "dark_oak");
            case "redstone_wire":
                return redstoneWire(tag);
            case "sugar_cane":
                return new SpriteBlock(name, Texture.sugarCane);
            case "kelp":
                return new SpriteBlock(name, Texture.kelp);
            case "kelp_plant":
                return new SpriteBlock(name, Texture.kelpPlant);
            case "dried_kelp_block":
                return new TexturedBlock(name,
                        Texture.driedKelpSide, Texture.driedKelpTop, Texture.driedKelpBottom);
            case "bamboo":
                return bamboo(tag);
            case "bamboo_sapling":
                return new SpriteBlock(name, Texture.bambooSapling);
            case "cake": {
                int bites = stringToInt(tag.get("Properties").get("bites"), 0);
                return new Cake(bites);
            }
            case "white_bed":
                return bed(tag, Texture.bedWhite);
            case "orange_bed":
                return bed(tag, Texture.bedOrange);
            case "magenta_bed":
                return bed(tag, Texture.bedMagenta);
            case "light_blue_bed":
                return bed(tag, Texture.bedLightBlue);
            case "yellow_bed":
                return bed(tag, Texture.bedYellow);
            case "lime_bed":
                return bed(tag, Texture.bedLime);
            case "pink_bed":
                return bed(tag, Texture.bedPink);
            case "gray_bed":
                return bed(tag, Texture.bedGray);
            case "light_gray_bed":
                return bed(tag, Texture.bedSilver);
            case "cyan_bed":
                return bed(tag, Texture.bedCyan);
            case "purple_bed":
                return bed(tag, Texture.bedPurple);
            case "blue_bed":
                return bed(tag, Texture.bedBlue);
            case "brown_bed":
                return bed(tag, Texture.bedBrown);
            case "green_bed":
                return bed(tag, Texture.bedGreen);
            case "red_bed":
                return bed(tag, Texture.bedRed);
            case "black_bed":
                return bed(tag, Texture.bedBlack);
            case "pumpkin_stem": {
                int age = stringToInt(tag.get("Properties").get("age"), 7);
                return new Stem(name, age);
            }
            case "attached_pumpkin_stem":
                return new AttachedStem(name, getFacing(tag, "north"));
            case "melon_stem": {
                int age = stringToInt(tag.get("Properties").get("age"), 7);
                return new Stem(name, age);
            }
            case "attached_melon_stem":
                return new AttachedStem(name, getFacing(tag, "north"));
            case "nether_wart": {
                int age = stringToInt(tag.get("Properties").get("age"), 3);
                return new NetherWart(age);
            }
            case "brewing_stand": {
                Tag properties = tag.get("Properties");
                String bottle0 = properties.get("has_bottle_0").stringValue("false");
                String bottle1 = properties.get("has_bottle_1").stringValue("false");
                String bottle2 = properties.get("has_bottle_2").stringValue("false");
                return new BrewingStand(
                        bottle0.equals("true"), bottle1.equals("true"), bottle2.equals("true"));
            }
            case "cauldron": {
                int level = stringToInt(tag.get("Properties").get("level"), 3);
                return new Cauldron(level);
            }
            case "flower_pot":
                return new FlowerPot(name, FlowerPotModel.Kind.NONE);
            case "potted_poppy":
                return new FlowerPot(name, FlowerPotModel.Kind.POPPY);
            case "potted_dandelion":
                return new FlowerPot(name, FlowerPotModel.Kind.DANDELION);
            case "potted_oak_sapling":
                return new FlowerPot(name, FlowerPotModel.Kind.OAK_SAPLING);
            case "potted_spruce_sapling":
                return new FlowerPot(name, FlowerPotModel.Kind.SPRUCE_SAPLING);
            case "potted_birch_sapling":
                return new FlowerPot(name, FlowerPotModel.Kind.BIRCH_SAPLING);
            case "potted_jungle_sapling":
                return new FlowerPot(name, FlowerPotModel.Kind.JUNGLE_SAPLING);
            case "potted_red_mushroom":
                return new FlowerPot(name, FlowerPotModel.Kind.RED_MUSHROOM);
            case "potted_brown_mushroom":
                return new FlowerPot(name, FlowerPotModel.Kind.BROWN_MUSHROOM);
            case "potted_cactus":
                return new FlowerPot(name, FlowerPotModel.Kind.CACTUS);
            case "potted_dead_bush":
                return new FlowerPot(name, FlowerPotModel.Kind.DEAD_BUSH);
            case "potted_fern":
                return new FlowerPot(name, FlowerPotModel.Kind.FERN);
            case "potted_acacia_sapling":
                return new FlowerPot(name, FlowerPotModel.Kind.ACACIA_SAPLING);
            case "potted_dark_oak_sapling":
                return new FlowerPot(name, FlowerPotModel.Kind.DARK_OAK_SAPLING);
            case "potted_blue_orchid":
                return new FlowerPot(name, FlowerPotModel.Kind.BLUE_ORCHID);
            case "potted_allium":
                return new FlowerPot(name, FlowerPotModel.Kind.ALLIUM);
            case "potted_azure_bluet":
                return new FlowerPot(name, FlowerPotModel.Kind.AZURE_BLUET);
            case "potted_red_tulip":
                return new FlowerPot(name, FlowerPotModel.Kind.RED_TULIP);
            case "potted_orange_tulip":
                return new FlowerPot(name, FlowerPotModel.Kind.ORANGE_TULIP);
            case "potted_white_tulip":
                return new FlowerPot(name, FlowerPotModel.Kind.WHITE_TULIP);
            case "potted_pink_tulip":
                return new FlowerPot(name, FlowerPotModel.Kind.PINK_TULIP);
            case "potted_oxeye_daisy":
                return new FlowerPot(name, FlowerPotModel.Kind.OXEYE_DAISY);
            case "potted_bamboo":
                return new FlowerPot(name, FlowerPotModel.Kind.BAMBOO);
            case "potted_cornflower":
                return new FlowerPot(name, FlowerPotModel.Kind.CORNFLOWER);
            case "potted_lily_of_the_valley":
                return new FlowerPot(name, FlowerPotModel.Kind.LILY_OF_THE_VALLEY);
            case "potted_wither_rose":
                return new FlowerPot(name, FlowerPotModel.Kind.WITHER_ROSE);
            case "potted_warped_fungus":
                return new FlowerPot(name, FlowerPotModel.Kind.WARPED_FUNGUS);
            case "potted_crimson_fungus":
                return new FlowerPot(name, FlowerPotModel.Kind.CRIMSON_FUNGUS);
            case "carrots": {
                int age = stringToInt(tag.get("Properties").get("age"), 7);
                return new Carrots(age);
            }
            case "potatoes": {
                int age = stringToInt(tag.get("Properties").get("age"), 7);
                return new Potatoes(age);
            }
            case "skeleton_skull":
                return skull(tag, Texture.skeleton, SkullEntity.Kind.SKELETON);
            case "skeleton_wall_skull":
                return wallSkull(tag, Texture.skeleton, SkullEntity.Kind.SKELETON);
            case "wither_skeleton_skull":
                return skull(tag, Texture.wither, SkullEntity.Kind.WITHER_SKELETON);
            case "wither_skeleton_wall_skull":
                return wallSkull(tag, Texture.wither, SkullEntity.Kind.WITHER_SKELETON);
            case "zombie_head":
                return skull(tag, Texture.zombie, SkullEntity.Kind.ZOMBIE);
            case "zombie_wall_head":
                return wallSkull(tag, Texture.zombie, SkullEntity.Kind.ZOMBIE);
            case "player_head":
                return skull(tag, Texture.steve, SkullEntity.Kind.PLAYER);
            case "player_wall_head":
                return wallSkull(tag, Texture.steve, SkullEntity.Kind.PLAYER);
            case "creeper_head":
                return skull(tag, Texture.creeper, SkullEntity.Kind.CREEPER);
            case "creeper_wall_head":
                return wallSkull(tag, Texture.creeper, SkullEntity.Kind.CREEPER);
            case "dragon_head":
                return skull(tag, Texture.steve, SkullEntity.Kind.DRAGON);
            case "dragon_wall_head":
                return wallSkull(tag, Texture.steve, SkullEntity.Kind.DRAGON);
            case "white_banner":
                return banner(tag, Texture.whiteWool, BlockData.COLOR_WHITE);
            case "orange_banner":
                return banner(tag, Texture.orangeWool, BlockData.COLOR_ORANGE);
            case "magenta_banner":
                return banner(tag, Texture.magentaWool, BlockData.COLOR_MAGENTA);
            case "light_blue_banner":
                return banner(tag, Texture.lightBlueWool, BlockData.COLOR_LIGHT_BLUE);
            case "yellow_banner":
                return banner(tag, Texture.yellowWool, BlockData.COLOR_YELLOW);
            case "lime_banner":
                return banner(tag, Texture.limeWool, BlockData.COLOR_LIME);
            case "pink_banner":
                return banner(tag, Texture.pinkWool, BlockData.COLOR_PINK);
            case "gray_banner":
                return banner(tag, Texture.grayWool, BlockData.COLOR_GRAY);
            case "light_gray_banner":
                return banner(tag, Texture.lightGrayWool, BlockData.COLOR_SILVER);
            case "cyan_banner":
                return banner(tag, Texture.cyanWool, BlockData.COLOR_CYAN);
            case "purple_banner":
                return banner(tag, Texture.purpleWool, BlockData.COLOR_PURPLE);
            case "blue_banner":
                return banner(tag, Texture.blueWool, BlockData.COLOR_BLUE);
            case "brown_banner":
                return banner(tag, Texture.brownWool, BlockData.COLOR_BROWN);
            case "green_banner":
                return banner(tag, Texture.greenWool, BlockData.COLOR_GREEN);
            case "red_banner":
                return banner(tag, Texture.redWool, BlockData.COLOR_RED);
            case "black_banner":
                return banner(tag, Texture.blackWool, BlockData.COLOR_BLACK);
            case "white_wall_banner":
                return wallBanner(tag, Texture.whiteWool, BlockData.COLOR_WHITE);
            case "orange_wall_banner":
                return wallBanner(tag, Texture.orangeWool, BlockData.COLOR_ORANGE);
            case "magenta_wall_banner":
                return wallBanner(tag, Texture.magentaWool, BlockData.COLOR_MAGENTA);
            case "light_blue_wall_banner":
                return wallBanner(tag, Texture.lightBlueWool, BlockData.COLOR_LIGHT_BLUE);
            case "yellow_wall_banner":
                return wallBanner(tag, Texture.yellowWool, BlockData.COLOR_YELLOW);
            case "lime_wall_banner":
                return wallBanner(tag, Texture.limeWool, BlockData.COLOR_LIME);
            case "pink_wall_banner":
                return wallBanner(tag, Texture.pinkWool, BlockData.COLOR_PINK);
            case "gray_wall_banner":
                return wallBanner(tag, Texture.grayWool, BlockData.COLOR_GRAY);
            case "light_gray_wall_banner":
                return wallBanner(tag, Texture.lightGrayWool, BlockData.COLOR_SILVER);
            case "cyan_wall_banner":
                return wallBanner(tag, Texture.cyanWool, BlockData.COLOR_CYAN);
            case "purple_wall_banner":
                return wallBanner(tag, Texture.purpleWool, BlockData.COLOR_PURPLE);
            case "blue_wall_banner":
                return wallBanner(tag, Texture.blueWool, BlockData.COLOR_BLUE);
            case "brown_wall_banner":
                return wallBanner(tag, Texture.brownWool, BlockData.COLOR_BROWN);
            case "green_wall_banner":
                return wallBanner(tag, Texture.greenWool, BlockData.COLOR_GREEN);
            case "red_wall_banner":
                return wallBanner(tag, Texture.redWool, BlockData.COLOR_RED);
            case "black_wall_banner":
                return wallBanner(tag, Texture.blackWool, BlockData.COLOR_BLACK);
            case "beetroots": {
                int age = stringToInt(tag.get("Properties").get("age"), 3);
                return new Beetroots(age);
            }
            case "loom":
                return new TopBottomOrientedTexturedBlock(name, getFacing(tag, "north"), Texture.loomFront, Texture.loomSide, Texture.loomTop, Texture.loomBottom);
            case "barrel":
                return new OrientedTexturedBlock(name, getFacing(tag, "north"), Texture.barrelSide, Texture.barrelTop, Texture.barrelBottom);
            case "smoker":
                return smoker(tag);
            case "blast_furnace":
                return blastFurnace(tag);
            case "cartography_table":
                return new TexturedBlock(name, Texture.cartographyTableSide3, Texture.cartographyTableSide1, Texture.cartographyTableSide2, Texture.cartographyTableSide3, Texture.cartographyTableTop, Texture.darkOakPlanks);
            case "fletching_table":
                return new TexturedBlock(name, Texture.fletchingTableFront, Texture.fletchingTableFront, Texture.fletchingTableSide, Texture.fletchingTableSide, Texture.fletchingTableTop, Texture.birchPlanks);
            case "grindstone": {
                String face = tag.get("Properties").get("face").stringValue("floor");
                String facing = getFacing(tag, "north");
                return new Grindstone(face, facing);
            }
            case "lectern":
                return new Lectern(getFacing(tag, "north"));
            case "smithing_table":
                return new TexturedBlock(name, Texture.smithingTableFront, Texture.smithingTableFront, Texture.smithingTableSide, Texture.smithingTableSide, Texture.smithingTableTop, Texture.smithingTableBottom);
            case "stonecutter":
                return new Stonecutter(getFacing(tag, "north"));
            case "bell":
                return new Bell(getFacing(tag, "north"), tag.get("Properties").get("attachment").stringValue("floor"));
            case "lantern":
                return new Lantern("lantern", Texture.lantern, tag.get("Properties").get("hanging").stringValue("false").equals("true"));
            case "sweet_berry_bush": {
                int age = stringToInt(tag.get("Properties").get("age"), 3);
                return new SweetBerryBush(age);
            }
            case "campfire":
                return new Campfire(getFacing(tag, "north"), isLit(tag));
            case "cut_sandstone_slab":
                return slab(tag, Texture.sandstoneCut, Texture.sandstoneTop);
            case "cut_red_sandstone_slab":
                return slab(tag, Texture.redSandstoneCut, Texture.redSandstoneTop);
            case "frosted_ice": {
                int age = stringToInt(tag.get("Properties").get("age"), 3);
                return new FrostedIce(age);
            }
            case "honey_block":
                return new Honey();
            case "bee_nest":
                return beeNest(tag);
            case "beehive":
                return beehive(tag);
            case "honeycomb_block":
                return new MinecraftBlock("honeycomb_block", Texture.honeycombBlock);
            case "spawner":
                return new MinecraftBlockTranslucent(name, Texture.monsterSpawner);
            case "nether_portal": {
                String axis = tag.get("Properties").get("axis").stringValue("north");
                return new NetherPortal(axis);
            }
            case "end_portal":
                return new EndPortal();
            case "end_gateway":
                return new MinecraftBlock(name, Texture.black);
            case "command_block": {
                Tag properties = tag.get("Properties");
                String conditional = properties.get("conditional").stringValue("false");
                return new CommandBlock(getFacing(tag, "south"), conditional.equals("true"));
            }
            case "chain_command_block": {
                Tag properties = tag.get("Properties");
                String facing = getFacing(tag, "south");
                String conditional = properties.get("conditional").stringValue("false");
                return new ChainCommandBlock(facing, conditional.equals("true"));
            }
            case "repeating_command_block": {
                Tag properties = tag.get("Properties");
                String facing = getFacing(tag, "south");
                String conditional = properties.get("conditional").stringValue("false");
                return new RepeatingCommandBlock(facing, conditional.equals("true"));
            }
            case "structure_block":
                return structureBlock(tag);
            case "jigsaw_block":
                // TODO
                return new UnknownBlock(name);
            case "soul_soil":
                return new MinecraftBlock("soul_soil", Texture.soulSoil);
            case "crimson_nylium":
                return new TexturedBlock("crimson_nylium", Texture.crimsonNyliumSide, Texture.crimsonNylium, Texture.netherrack);
            case "warped_nylium":
                return new TexturedBlock("warped_nylium", Texture.warpedNyliumSide, Texture.warpedNylium, Texture.netherrack);
            case "nether_gold_ore":
                return new MinecraftBlock("nether_gold_ore", Texture.netherGoldOre);
            case "target":
                return new TexturedBlock("target", Texture.targetSide, Texture.targetTop);
            case "netherite_block":
                return new MinecraftBlock("netherite_block", Texture.netheriteBlock);
            case "shroomlight":
                return new MinecraftBlock("shroomlight", Texture.shroomlight);
            case "warped_wart_block":
                return new MinecraftBlock("warped_wart_block", Texture.warpedWartBlock);
            case "basalt":
                return log(tag, Texture.basaltSide, Texture.basaltTop);
            case "polished_basalt":
                return log(tag, Texture.polishedBasaltSide, Texture.polishedBasaltTop);
            case "ancient_debris":
                return new TexturedBlock("ancient_debris", Texture.ancientDebrisSide, Texture.ancientDebrisTop);
            case "warped_fungus":
                return new SpriteBlock("warped_fungus", Texture.warpedFungus);
            case "crimson_fungus":
                return new SpriteBlock("crimson_fungus", Texture.crimsonFungus);
            case "nether_sprouts":
                return new SpriteBlock("nether_sprouts", Texture.netherSprouts);
            case "warped_roots":
                return new SpriteBlock("warped_roots", Texture.warpedRoots);
            case "crimson_roots":
                return new SpriteBlock("crimson_roots", Texture.crimsonRoots);
            case "crying_obsidian":
                return new MinecraftBlock("crying_obsidian", Texture.cryingObsidian);
            case "warped_hyphae":
                return log(tag, Texture.warpedStem, Texture.warpedStem);
            case "stripped_warped_hyphae":
                return log(tag, Texture.strippedWarpedStem, Texture.strippedWarpedStem);
            case "warped_stem":
                return log(tag, Texture.warpedStem, Texture.warpedStemTop);
            case "stripped_warped_stem":
                return log(tag, Texture.strippedWarpedStem, Texture.strippedWarpedStemTop);
            case "crimson_hyphae":
                return log(tag, Texture.crimsonStem, Texture.crimsonStem);
            case "stripped_crimson_hyphae":
                return log(tag, Texture.strippedCrimsonStem, Texture.strippedCrimsonStem);
            case "crimson_stem":
                return log(tag, Texture.crimsonStem, Texture.crimsonStemTop);
            case "stripped_crimson_stem":
                return log(tag, Texture.strippedCrimsonStem, Texture.strippedCrimsonStemTop);
            case "soul_fire_lantern":
                return new Lantern("soul_fire_lantern", Texture.soulFireLantern, tag.get("Properties").get("hanging").stringValue("false").equals("true"));
            case "twisting_vines":
                return new SpriteBlock("twisting_vines", Texture.twistingVines);
            case "twisting_vines_plant":
                return new SpriteBlock("twisting_vines_plant", Texture.twistingVinesPlant);
            case "weeping_vines":
                return new SpriteBlock("weeping_vines", Texture.weepingVines);
            case "weeping_vines_plant":
                return new SpriteBlock("weeping_vines_plant", Texture.weepingVinesPlant);
            case "soul_fire_torch":
                return new Torch(name, Texture.soulFireTorch);
            case "soul_fire_wall_torch":
                return wallTorch(tag, Texture.soulFireTorch);
            case "respawn_anchor":
                return new RespawnAnchor(stringToInt(tag.get("Properties").get("charges"), 0));
            case "crimson_sign":
                return sign(tag, "crimson");
            case "crimson_wall_sign":
                return wallSign(tag, "crimson");
            case "warped_sign":
                return sign(tag, "warped");
            case "warped_wall_sign":
                return wallSign(tag, "warped");
            case "crimson_planks":
                return new MinecraftBlock(name, Texture.crimsonPlanks);
            case "warped_planks":
                return new MinecraftBlock(name, Texture.warpedPlanks);
            case "crimson_pressure_plate":
                return new PressurePlate(name, Texture.crimsonPlanks);
            case "warped_pressure_plate":
                return new PressurePlate(name, Texture.warpedPlanks);
            case "crimson_slab":
                return slab(tag, Texture.crimsonPlanks);
            case "warped_slab":
                return slab(tag, Texture.warpedPlanks);
            case "crimson_stairs":
                return stairs(tag, Texture.crimsonPlanks);
            case "warped_stairs":
                return stairs(tag, Texture.warpedPlanks);
            case "crimson_fence":
                return fence(tag, Texture.crimsonPlanks);
            case "warped_fence":
                return fence(tag, Texture.warpedPlanks);
            case "crimson_fence_gate":
                return fenceGate(tag, Texture.crimsonPlanks);
            case "warped_fence_gate":
                return fenceGate(tag, Texture.warpedPlanks);
            case "crimson_button":
                return button(tag, Texture.crimsonPlanks);
            case "warped_button":
                return button(tag, Texture.warpedPlanks);
            case "crimson_door":
                return door(tag, Texture.crimsonDoorTop, Texture.crimsonDoorBottom);
            case "warped_door":
                return door(tag, Texture.warpedDoorTop, Texture.warpedDoorBottom);
            case "crimson_trapdoor":
                return trapdoor(tag, Texture.crimsonTrapdoor);
            case "warped_trapdoor":
                return trapdoor(tag, Texture.warpedTrapdoor);
            case "soul_fire":
                return new SoulFire();
            case "lodestone":
                return new TexturedBlock("lodestone", Texture.lodestoneSide, Texture.lodestoneTop);
            case "structure_void":
            case "barrier":
                // Invisible.
                return Air.INSTANCE;
        }
        return new UnknownBlock(name);
    }

    private static String getFacing(Tag tag, String defaultValue) {
        return tag.get("Properties").get("facing").stringValue(defaultValue);
    }

    private static int stringToInt(Tag tag, int defaultValue) {
        if (!(tag instanceof StringTag)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(tag.stringValue());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static String blockName(Tag tag) {
        String name = tag.get("Name").stringValue();
        if (name.startsWith("minecraft:")) {
            name = name.substring(10); // Remove "minecraft:" prefix.
        }
        return name;
    }

    private static Block largeFlower(Tag tag, Texture top, Texture bottom) {
        String name = tag.get("Name").stringValue();
        String half = tag.get("Properties").get("half").stringValue("lower");
        return new SpriteBlock(String.format("%s (half=%s)", name, half),
                half.equals("upper") ? top : bottom);
    }

    private static Block log(Tag tag, Texture side, Texture top) {
        String name = blockName(tag);
        String axis = tag.get("Properties").get("axis").stringValue("y");
        return new Log(name, side, top, axis);
    }

    private static Block slab(Tag tag, Texture texture) {
        String name = blockName(tag);
        String type = tag.get("Properties").get("type").stringValue("bottom");
        return new Slab(name, texture, type);
    }

    private static Block slab(Tag tag, Texture sideTexture, Texture topTexture) {
        String name = blockName(tag);
        String type = tag.get("Properties").get("type").stringValue("bottom");
        return new Slab(name, sideTexture, topTexture, type);
    }

    private static Block stairs(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String half = properties.get("half").stringValue("bottom");
        String shape = properties.get("shape").stringValue("straight");
        String facing = getFacing(tag, "south");
        return new Stairs(name, texture, half, shape, facing);
    }

    private static Block stairs(Tag tag, Texture side, Texture top, Texture bottom) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String half = properties.get("half").stringValue("bottom");
        String shape = properties.get("shape").stringValue("straight");
        String facing = getFacing(tag, "south");
        return new Stairs(name, side, top, bottom, half, shape, facing);
    }

    private static Block glazedTerracotta(Tag tag, Texture texture) {
        String name = blockName(tag);
        String facing = getFacing(tag, "south");
        return new GlazedTerracotta(name, texture, facing);
    }

    private static Block bed(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String part = properties.get("part").stringValue("head");
        String facing = getFacing(tag, "south");
        return new Bed(name, texture, part, facing);
    }

    private static Block hugeMushroom(Tag tag, Texture skin) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String east = properties.get("east").stringValue("true");
        String west = properties.get("west").stringValue("true");
        String north = properties.get("north").stringValue("true");
        String south = properties.get("south").stringValue("true");
        String up = properties.get("up").stringValue("true");
        String down = properties.get("down").stringValue("true");
        return new TexturedBlock(name,
                north.equals("true") ? skin : Texture.mushroomPores,
                south.equals("true") ? skin : Texture.mushroomPores,
                west.equals("true") ? skin : Texture.mushroomPores,
                east.equals("true") ? skin : Texture.mushroomPores,
                up.equals("true") ? skin : Texture.mushroomPores,
                down.equals("true") ? skin : Texture.mushroomPores);
    }

    private static Block snowCovered(Tag tag, Block block) {
        String snowy = tag.get("Properties").get("snowy").stringValue("false");
        if (snowy.equals("true")) {
            block = new SnowCovered(block);
        }
        return block;
    }

    private static Block piston(Tag tag, boolean isSticky) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String extended = properties.get("extended").stringValue("false");
        String facing = getFacing(tag, "north");
        return new Piston(name, isSticky, extended.equals("true"), facing);
    }

    private static Block pistonHead(Tag tag) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String type = properties.get("type").stringValue("normal");
        return new PistonHead(name, type.equals("sticky"), facing);
    }

    private static Block dispenser(Tag tag) {
        String facing = getFacing(tag, "north");
        return new Dispenser(facing);
    }

    private static Block rail(Tag tag, Texture straightTrack) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String shape = properties.get("shape").stringValue("north-south");
        return new Rail(name, straightTrack, shape);
    }

    private static Block poweredRail(Tag tag) {
        Tag properties = tag.get("Properties");
        String powered = properties.get("powered").stringValue("false");
        Texture straightTrack = powered.equals("true")
                ? Texture.poweredRailOn
                : Texture.poweredRailOff;
        return rail(tag, straightTrack);
    }

    private static boolean isLit(Tag tag) {
        return tag.get("Properties").get("lit").stringValue("false").equals("true");
    }

    private static Block redstoneTorch(Tag tag) {
        return new Torch("redstone_torch", isLit(tag)
                ? Texture.redstoneTorchOn
                : Texture.redstoneTorchOff);
    }

    private static Block redstoneWallTorch(Tag tag) {
        return wallTorch(tag, isLit(tag)
                ? Texture.redstoneTorchOn
                : Texture.redstoneTorchOff);
    }

    private static Block wallTorch(Tag tag, Texture texture) {
        String name = blockName(tag);
        String facing = getFacing(tag, "north");
        return new WallTorch(name, texture, facing);
    }

    private static Block redstoneWire(Tag tag) {
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("none");
        String south = properties.get("south").stringValue("none");
        String east = properties.get("east").stringValue("none");
        String west = properties.get("west").stringValue("none");
        int power = stringToInt(properties.get("power"), 0);
        return new RedstoneWire(power, north, south, east, west);
    }

    private static Block chest(Tag tag) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String type = properties.get("type").stringValue("single");
        return new Chest(name, type, facing);
    }

    private static Block endRod(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "up");
        return new EndRod(facing);
    }

    private static Block furnace(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String lit = properties.get("lit").stringValue("false");
        return new Furnace(facing, lit.equals("true"));
    }

    private static Block smoker(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String lit = properties.get("lit").stringValue("false");
        return new TopBottomOrientedTexturedBlock("smoker", facing, lit.equals("true") ? Texture.smokerFrontOn : Texture.smokerFront, Texture.smokerSide, Texture.smokerTop, Texture.smokerBottom);
    }

    private static Block blastFurnace(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String lit = properties.get("lit").stringValue("false");
        return new TopBottomOrientedTexturedBlock("blast_furnace", facing, lit.equals("true") ? Texture.blastFurnaceFrontOn : Texture.blastFurnaceFront, Texture.blastFurnaceSide, Texture.blastFurnaceTop);
    }

    private static Block composter(Tag tag) {
        Tag properties = tag.get("Properties");
        int level = stringToInt(properties.get("level"), 0);
        return new Composter(level);
    }

    private static Block bamboo(Tag tag) {
        Tag properties = tag.get("Properties");
        int age = stringToInt(properties.get("age"), 0);
        String leaves = properties.get("leaves").stringValue("none");
        return new Bamboo(age, leaves);
    }

    private static Block beeNest(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        int honeyLevel = stringToInt(properties.get("honey_level"), 0);
        return new TopBottomOrientedTexturedBlock("smoker", facing, honeyLevel == 5 ? Texture.beeNestFrontHoney : Texture.beeNestFront, Texture.beeNestSide, Texture.beeNestTop, Texture.beeNestBottom);
    }

    private static Block beehive(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        int honeyLevel = stringToInt(properties.get("honey_level"), 0);
        return new TopBottomOrientedTexturedBlock("smoker", facing, honeyLevel == 5 ? Texture.beehiveFrontHoney : Texture.beehiveFront, Texture.beehiveSide, Texture.beehiveEnd, Texture.beehiveEnd);
    }

    private static Block wheat(Tag tag) {
        int age = stringToInt(tag.get("Properties").get("age"), 7);
        return new Wheat(age);
    }

    private static Block door(Tag tag, Texture upper, Texture lower) {
        Tag properties = tag.get("Properties");
        String name = blockName(tag);
        String facing = getFacing(tag, "north");
        String half = properties.get("half").stringValue("upper");
        String hinge = properties.get("hinge").stringValue("right");
        String open = properties.get("open").stringValue("false");
        return new Door(name, half.equals("upper") ? upper : lower,
                facing, half, hinge, open.equals("true"));
    }

    private static Block shulkerBox(Tag tag, ShulkerTexture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "up");
        return new ShulkerBox(name, texture.side, texture.top, texture.bottom, facing);
    }

    private static Block sign(Tag tag, String material) {
        String name = blockName(tag);
        int rotation = stringToInt(tag.get("Properties").get("rotation"), 0);
        return new Sign(name, material, rotation);
    }

    private static Block banner(Tag tag, Texture texture, int color) {
        String name = blockName(tag);
        int rotation = stringToInt(tag.get("Properties").get("rotation"), 0);
        return new Banner(name, texture, rotation, color);
    }

    private static Block wallBanner(Tag tag, Texture texture, int color) {
        String name = blockName(tag);
        String facing = getFacing(tag, "north");
        return new WallBanner(name, texture, facing, color);
    }

    private static Block wallSign(Tag tag, String material) {
        String name = blockName(tag);
        String facing = getFacing(tag, "north");
        return new WallSign(name, material, facing);
    }

    private static Block lever(Tag tag) {
        Tag properties = tag.get("Properties");
        String face = properties.get("face").stringValue("floor");
        String facing = getFacing(tag, "north");
        String powered = properties.get("powered").stringValue("false");
        return new Lever(face, facing, powered.equals("true"));
    }

    private static Block button(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String face = properties.get("face").stringValue("floor");
        String facing = getFacing(tag, "north");
        String powered = properties.get("powered").stringValue("false");
        return new Button(name, texture, face, facing, powered.equals("true"));
    }

    private static Block repeater(Tag tag) {
        Tag properties = tag.get("Properties");
        int delay = stringToInt(properties.get("delay"), 1);
        String facing = getFacing(tag, "north");
        String powered = properties.get("powered").stringValue("false");
        String locked = properties.get("locked").stringValue("false");
        return new Repeater(delay, facing, powered.equals("true"), locked.equals("true"));
    }

    private static Block comparator(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String powered = properties.get("powered").stringValue("false");
        String mode = properties.get("mode").stringValue("compare");
        return new Comparator(facing, mode, powered.equals("true"));
    }

    private Block fence(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        return new Fence(name, texture,
                north.equals("true"),
                south.equals("true"),
                east.equals("true"),
                west.equals("true"));
    }

    private Block fenceGate(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String in_wall = properties.get("in_wall").stringValue("false");
        String open = properties.get("open").stringValue("false");
        return new FenceGate(name, texture,
                facing,
                in_wall.equals("true"),
                open.equals("true"));
    }

    private Block glassPane(Tag tag, Texture side, Texture top) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        return new GlassPane(name, side, top,
                north.equals("true"),
                south.equals("true"),
                east.equals("true"),
                west.equals("true"));
    }

    private Block ironBars(Tag tag) {
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        return new IronBars(north.equals("true"),
                south.equals("true"),
                east.equals("true"),
                west.equals("true"));
    }

    private Block trapdoor(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String half = properties.get("half").stringValue("bottom");
        String facing = getFacing(tag, "north");
        String open = properties.get("open").stringValue("false");
        return new Trapdoor(name, texture,
                half,
                facing,
                open.equals("true"));
    }

    private Block vine(Tag tag) {
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        String up = properties.get("up").stringValue("false");
        return new Vine(north.equals("true"),
                south.equals("true"),
                east.equals("true"),
                west.equals("true"),
                up.equals("true"));
    }

    private Block tripwire(Tag tag) {
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        return new Tripwire(north.equals("true"),
                south.equals("true"),
                east.equals("true"),
                west.equals("true"));
    }

    private Block tripwireHook(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        String attached = properties.get("attached").stringValue("false"); // TODO!
        String powered = properties.get("powered").stringValue("false");
        return new TripwireHook(facing, attached.equals("true"), powered.equals("true"));
    }

    private Block cocoa(Tag tag) {
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        int age = stringToInt(properties.get("age"), 2);
        return new Cocoa(facing, age);
    }

    private Block wall(Tag tag, Texture texture) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        String up = properties.get("up").stringValue("false");
        return new Wall(name, texture,
                north.equals("true"),
                south.equals("true"),
                east.equals("true"),
                west.equals("true"),
                up.equals("true"));
    }

    private Block skull(Tag tag, EntityTexture texture, SkullEntity.Kind type) {
        String name = blockName(tag);
        int rotation = stringToInt(tag.get("Properties").get("rotation"), 0);
        return new Head(name, texture, type, rotation);
    }

    private Block wallSkull(Tag tag, EntityTexture texture, SkullEntity.Kind type) {
        String name = blockName(tag);
        String facing = getFacing(tag, "north");
        return new WallHead(name, texture, type, facing);
    }

    private Block anvil(Tag tag, int damage) {
        String name = blockName(tag);
        Tag properties = tag.get("Properties");
        String facing = getFacing(tag, "north");
        return new Anvil(name, facing, damage);
    }

    private static Block turtleEgg(Tag tag) {
        Tag properties = tag.get("Properties");
        int eggs = stringToInt(properties.get("eggs"), 1);
        int hatch = stringToInt(properties.get("hatch"), 0);
        return new TurtleEgg(eggs, hatch);
    }

    private static Block seaPickle(Tag tag) {
        Tag properties = tag.get("Properties");
        int pickles = stringToInt(properties.get("pickles"), 1);
        return new SeaPickle(pickles, properties.get("waterlogged").stringValue("").equals("true"));
    }

    private static Block structureBlock(Tag tag) {
        Tag properties = tag.get("Properties");
        Texture texture = Texture.structureBlock;
        String mode = properties.get("mode").stringValue("");
        switch (mode) {
            case "corner":
                texture = Texture.structureBlockCorner;
                break;
            case "data":
                texture = Texture.structureBlockData;
                break;
            case "load":
                texture = Texture.structureBlockLoad;
                break;
            case "save":
                texture = Texture.structureBlockSave;
                break;
        }
        return new MinecraftBlock("structure_block", texture);
    }
}
