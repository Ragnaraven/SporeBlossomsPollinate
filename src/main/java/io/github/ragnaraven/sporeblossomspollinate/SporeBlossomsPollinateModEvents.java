package io.github.ragnaraven.sporeblossomspollinate;

import io.github.ragnaraven.sporeblossomspollinate.config.ConfigHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = SporeBlossomsPollinateMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SporeBlossomsPollinateModEvents {

    static int COOLDOWN = 10;
    static Random random = new Random();

    static HashMap<BlockPos, Long> triggers = new HashMap<>();

    @SubscribeEvent
    public static void onCropGrowPre(BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
        if(cropGrowEvent.getState().getBlock() instanceof CropBlock block)
        {
            ServerLevel level = (ServerLevel) cropGrowEvent.getWorld();

            Block below = level.getBlockState(cropGrowEvent.getPos().below()).getBlock();
            if(below == Blocks.FARMLAND || below instanceof CropBlock)
            {
                ArrayList<BlockPos> sbs = GET_SPORE_BLOSSOMS(level, cropGrowEvent.getPos(), true);

                try {
                    int age = cropGrowEvent.getState().getValue(CropBlock.AGE);
                    int max = block.getMaxAge();

                    if (sbs.size() > 0 && age >= max - 3) //7 -2 = 5 == The max age of vanilla crops. stage 6 and 7 are decay stages.
                        cropGrowEvent.setResult(Event.Result.DENY);
                }
                catch (Exception e)
                { }
            }
        }
    }

    @SubscribeEvent
    public static void onCropGrow(BlockEvent.CropGrowEvent.Post cropGrowEvent) {
        if(RANDOM_IN(ConfigHolder.SERVER.RANDOM_GROW_CHANCE.get()) == 0)
        {
            BlockPos origin = cropGrowEvent.getPos();
            ServerLevel level = (ServerLevel) cropGrowEvent.getWorld();

            Block below = level.getBlockState(cropGrowEvent.getPos().below()).getBlock();
            if(below == Blocks.FARMLAND || below instanceof CropBlock) {
                ArrayList<BlockPos> sbs = new ArrayList<>();
                if (triggers.get(origin) == null || HAS_PASSED(triggers.get(origin))) {
                    sbs = GET_SPORE_BLOSSOMS(level, origin);

                    if (sbs.size() > 0) {
                        for (int x = ConfigHolder.SERVER.NEIGHBOR_RANGE_CHECK.get() / -2; x < ConfigHolder.SERVER.NEIGHBOR_RANGE_CHECK.get() / 2; x++) {
                            for (int z = ConfigHolder.SERVER.NEIGHBOR_RANGE_CHECK.get() / -2; z < ConfigHolder.SERVER.NEIGHBOR_RANGE_CHECK.get() / 2; z++) {
                                for (int y = ConfigHolder.SERVER.NEIGHBOR_RANGE_CHECK.get() / -2; y < ConfigHolder.SERVER.NEIGHBOR_RANGE_CHECK.get() / 2; y++) {
                                    BlockPos pos = new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);

                                    try {
                                        int age = cropGrowEvent.getState().getValue(CropBlock.AGE);

                                        if (x == 0 && y == 0 && z == 0) {
                                            triggers.put(pos, TIME_FROM_NOW(COOLDOWN * age + COOLDOWN));
                                            continue;
                                        }

                                        BlockState state = level.getBlockState(pos);
                                        Block block = state.getBlock();
                                        if (block instanceof CropBlock) {
                                            below = level.getBlockState(cropGrowEvent.getPos().below()).getBlock();
                                            if (below == Blocks.FARMLAND || below instanceof CropBlock) {
                                                if (RANDOM_IN(ConfigHolder.SERVER.RANDOM_NEIGHBOR_GROW_CHANCE.get()) <= sbs.size() - 1) {
                                                    if (triggers.get(pos) == null || HAS_PASSED(triggers.get(pos))) {
                                                        CropBlock cropBlock = (CropBlock) block;
                                                        int max = cropBlock.getMaxAge();

                                                        if (sbs.size() > 0 && age < max - 3)//7 -2 = 5 == The max age of vanilla crops. stage 6 and 7 are decay stages.
                                                        {
                                                            applyBonemeal(level, pos);
                                                            triggers.put(pos, TIME_FROM_NOW(COOLDOWN * (age * 2) + COOLDOWN));
                                                        }

                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    catch(Exception e)
                                    {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<BlockPos> GET_SPORE_BLOSSOMS (Level level, BlockPos origin) { return GET_SPORE_BLOSSOMS(level, origin, false); }
    public static ArrayList<BlockPos> GET_SPORE_BLOSSOMS (Level level, BlockPos origin, boolean getOneAndReturn)
    {
        ArrayList<BlockPos> sbs = new ArrayList<>();

        for (int x = ConfigHolder.SERVER.MAX_SPORE_BLOSSOM_RADIUS.get() / -2; x < ConfigHolder.SERVER.MAX_SPORE_BLOSSOM_RADIUS.get() / 2; x++)
        {
            for (int z = ConfigHolder.SERVER.MAX_SPORE_BLOSSOM_RADIUS.get() / -2; z < ConfigHolder.SERVER.MAX_SPORE_BLOSSOM_RADIUS.get() / 2; z++)
            {
                for (int y = 0; y < ConfigHolder.SERVER.MAX_SPORE_BLOSSOM_HEIGHT.get(); y++)
                {
                    BlockPos pos = new BlockPos(origin.getX() + x, origin.getY() + y + 2, origin.getZ() + z);
                    Block block = level.getBlockState(pos).getBlock();

                    if (block == Blocks.AIR)
                        continue;
                    else if (block == Blocks.SPORE_BLOSSOM)
                    {
                        sbs.add(pos);

                        if(getOneAndReturn)
                            return sbs;

                        break;
                    }
                    else
                        break;
                }
            }
        }

        return sbs;
    }


    public static boolean applyBonemeal(Level level, BlockPos blockPos) {
        BlockState blockstate = level.getBlockState(blockPos);
        if (blockstate.getBlock() instanceof BonemealableBlock)
        {
            BonemealableBlock bonemealableblock = (BonemealableBlock)blockstate.getBlock();
            if (bonemealableblock.isValidBonemealTarget(level, blockPos, blockstate, level.isClientSide))
            {
                if (level instanceof ServerLevel)
                {
                    if (bonemealableblock.isBonemealSuccess(level, level.random, blockPos, blockstate))
                    {
                        bonemealableblock.performBonemeal((ServerLevel)level, level.random, blockPos, blockstate);
                    }
                }

                return true;
            }
        }

        return false;
    }

    public static int RANDOM_IN (int bound) {
        if(bound == 0)
            return 0;

        return random.nextInt(bound);
    }

    public static long TIME_FROM_NOW (int seconds)
    {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTimeInMillis();
    }

    public static boolean HAS_PASSED (long time)
    {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();
        return calendar.compareTo(now) <= 0;
    }

}
