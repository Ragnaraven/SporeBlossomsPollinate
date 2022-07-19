package io.github.ragnaraven.sporeblossomspollinate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class SBPSavedData extends SavedData {

    public static final String NAME = SporeBlossomsPollinateMod.MODID + "_storage";
    private static final String TAG_SBP = "TAG_SBP";

    private final Map<BlockPos, Long> storage = new HashMap<>();

    public static SBPSavedData load(CompoundTag tag) {

        var result = new SBPSavedData();

        CompoundTag storageTag = tag.getCompound(TAG_SBP);
        for (String blockPos : storageTag.getAllKeys())
        {
            //result.storage.put(BlockPosFromString((blockPos), storageTag.getLong(blockPos));
        }

        return result;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        CompoundTag storageTag = new CompoundTag();
        for (Map.Entry<BlockPos, Long> entry : storage.entrySet())
            storageTag.putLong(BlockPosToString(entry.getKey()), entry.getValue());

        tag.put(TAG_SBP, storageTag);

        return tag;
    }

    public static String BlockPosToString (BlockPos blockPos)
    {
        return blockPos.getX() + ":" + blockPos.getY() + ":" + blockPos.getZ();
    }

    public static BlockPos BlockPosFromString (String blockPos)
    {
        String[] xyz = blockPos.split(":");
        return new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
    }
}
