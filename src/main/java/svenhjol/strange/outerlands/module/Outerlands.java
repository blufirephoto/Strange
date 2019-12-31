package svenhjol.strange.outerlands.module;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.tweaks.client.AmbientMusicClient;
import svenhjol.charm.tweaks.module.AmbientMusicImprovements;
import svenhjol.meson.MesonModule;
import svenhjol.meson.helper.ClientHelper;
import svenhjol.meson.iface.Config;
import svenhjol.meson.iface.Module;
import svenhjol.strange.Strange;
import svenhjol.strange.base.StrangeCategories;
import svenhjol.strange.base.StrangeSounds;

import java.util.Random;

@Module(mod = Strange.MOD_ID, category = StrangeCategories.OUTERLANDS, hasSubscriptions = true)
public class Outerlands extends MesonModule
{
    @Config(name = "Threshold", description = "X or Z axis values greater than this value are considered 'outer lands'.")
    public static int threshold = 12000000;

    @Config(name = "Scale by distance", description = "If true, some features will scale their difficulty or reward based on distance from spawn point.")
    public static boolean scaleDistance = true;

    @Config(name = "Scary monsters", description = "If true, monsters will use the distance scaling to become more challenging in the Outerlands.")
    public static boolean scaryMonsters = true;

    @Config(name = "Play outerlands music", description = "If true, extra music may play when the player is in the Outerlands.\n" +
        "Charm's 'Ambient Music Improvements' module must be enabled for this to work.")
    public static boolean music = true;

    @Override
    public void setupClient(FMLClientSetupEvent event)
    {
        if (!Charm.hasModule(AmbientMusicImprovements.class)) return;

        AmbientMusicClient.conditions.add(new AmbientMusicClient.AmbientMusicCondition(StrangeSounds.MUSIC_THARNA, 1200, 3600, mc -> {
            PlayerEntity player = ClientHelper.getClientPlayer();
            if (player == null) return false;
            return Outerlands.isOuterPos(player.getPosition()) && player.world.rand.nextFloat() < 0.25F;
        }));

        AmbientMusicClient.conditions.add(new AmbientMusicClient.AmbientMusicCondition(StrangeSounds.MUSIC_STEINN, 1200, 3600, mc -> {
            PlayerEntity player = ClientHelper.getClientPlayer();
            if (player == null) return false;
            return Outerlands.isOuterPos(player.getPosition())
                && player.getPosition().getY() < 48
                && player.world.rand.nextFloat() < 0.25F;
        }));
    }

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent event)
    {
        if (!scaleDistance) return;

        IWorld world = event.getWorld();
        if (world.getWorld().isRemote) return;

        Random rand = world.getRandom();

        double x = event.getX();
        double z = event.getZ();
        BlockPos pos = new BlockPos(x, 0, z);

        if (!isOuterPos(pos)) return;

        LivingEntity entity = event.getEntityLiving();
        float multiplier = getScaledMultiplier(world, pos);

        if (entity instanceof MonsterEntity) {
            if (rand.nextFloat() < (0.0025F * multiplier)) {
                entity.addPotionEffect(new EffectInstance(Effects.HEALTH_BOOST, 1000, (int) (multiplier * multiplier)));
                entity.setHealth(entity.getMaxHealth());
            }
            if (rand.nextFloat() < (0.0015F * multiplier)) {
                entity.addPotionEffect(new EffectInstance(Effects.STRENGTH, 1000, (int) (1 * multiplier)));
            }
            if (rand.nextFloat() < (0.0005F * multiplier)) {
                entity.addPotionEffect(new EffectInstance(Effects.SPEED, 1000, (int) (1 * multiplier)));
            }
        }
    }

    @SubscribeEvent
    public void onDropXp(LivingExperienceDropEvent event)
    {
        if (!scaleDistance) return;
        if (event.getAttackingPlayer() == null
            || event.getEntityLiving() == null
        ) {
            return;
        }

        World world = event.getAttackingPlayer().world;
        if (world.isRemote) return;

        PlayerEntity player = event.getAttackingPlayer();

        double x = player.getPosition().getX();
        double z = player.getPosition().getZ();
        BlockPos pos = new BlockPos(x, 0, z);

        if (!isOuterPos(pos)) return;
        int xp = event.getOriginalExperience();

        if (event.getEntityLiving() instanceof MonsterEntity) {
            int newXp = (int) (xp * getScaledMultiplier(world, pos));
            event.setDroppedExperience(newXp);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        IWorld world = event.getWorld();
        if (world == null) return;

        // don't let outer area be greater than worldborder
        threshold = Math.min(threshold, getMaxDistance(world));
    }

    public static BlockPos getInnerPos(World world, Random rand)
    {
        int x = rand.nextInt(threshold * 2) - threshold;
        int z = rand.nextInt(threshold * 2) - threshold;
        return new BlockPos(x, 0, z);
    }

    public static BlockPos getOuterPos(World world, Random rand)
    {
        int x = rand.nextInt(world.getWorldBorder().getSize() - threshold) + threshold;
        int z = rand.nextInt(world.getWorldBorder().getSize() - threshold) + threshold;

        x = rand.nextFloat() < 0.5 ? x : -x;
        z = rand.nextFloat() < 0.5 ? z : -z;

        return new BlockPos(x, 0, z);
    }

    public static int getMaxDistance(IWorld world)
    {
        return world.getWorldBorder().getSize() - 10000;
    }

    public static float getScaledMultiplier(IWorld world, BlockPos pos)
    {
        if (!Strange.loader.hasModule(Outerlands.class)
            || !scaleDistance
            || isInnerPos(pos)
        ) {
            return 1.0F;
        }

        float dist = Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())) - threshold;
        float max = getMaxDistance(world) - threshold;

        return 1.0F + (dist / max);
    }

    public static boolean isInnerPos(BlockPos pos)
    {
        return Math.abs(pos.getX()) <= threshold && Math.abs(pos.getZ()) <= threshold;
    }

    public static boolean isOuterPos(BlockPos pos)
    {
        return Math.abs(pos.getX()) > threshold || Math.abs(pos.getZ()) > threshold;
    }
}
