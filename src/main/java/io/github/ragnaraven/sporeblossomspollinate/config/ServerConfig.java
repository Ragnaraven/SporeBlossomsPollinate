package io.github.ragnaraven.sporeblossomspollinate.config;

import io.github.ragnaraven.sporeblossomspollinate.SporeBlossomsPollinateMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {

    public final ForgeConfigSpec.IntValue RANDOM_GROW_CHANCE;
    public final ForgeConfigSpec.IntValue RANDOM_NEIGHBOR_GROW_CHANCE;
    public final ForgeConfigSpec.IntValue MAX_SPORE_BLOSSOM_HEIGHT;
    public final ForgeConfigSpec.IntValue MAX_SPORE_BLOSSOM_RADIUS;
    public final ForgeConfigSpec.IntValue NEIGHBOR_RANGE_CHECK;

    public ServerConfig(final ForgeConfigSpec.Builder builder)
    {
        builder.push("Spore Blossoms Pollinate");
        this.RANDOM_GROW_CHANCE = buildInt(builder, "Random Crop Grow Chance", "all", 1, 0, 10000, "1 : X chance to grow the crop.");
        this.RANDOM_NEIGHBOR_GROW_CHANCE = buildInt(builder, "Random Crop Neighbor Grow Chance", "all", 3, 0, 10000, "1 : X chance to grow the crop's neighbors.");
        this.MAX_SPORE_BLOSSOM_HEIGHT = buildInt(builder, "Max Spore Blossom Height", "all", 5, 0, 16, "How far to check upwards for the spore blossom.");
        this.MAX_SPORE_BLOSSOM_RADIUS = buildInt(builder, "Max Spore Blossom Horizontal Range", "all", 4, 0, 16, "How far to check sideways for the spore blossom.");
        this.NEIGHBOR_RANGE_CHECK = buildInt(builder, "Pollinate Neighbor Check", "all", 4, 0, 10, "How far should this crop check for neighboring crops.");
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    ///////////////////////////////////////
    //FROM EA
    ///////////////////////////////////////
    private static List<Item> parseItemList(List<String> lst)
    {
        List<Item> exp = new ArrayList<>(lst.size());
        for (String s : lst) {
            Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
            if (i == null || i == Items.AIR) {
                //SporeBlossomsPollinateMod.LOGGER.error("Invalid config entry {} will be ignored from blacklist.", s);
                continue;
            }
            exp.add(i);
        }
        return exp;
    }
}
