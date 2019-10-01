package svenhjol.strange.totems.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import svenhjol.meson.MesonItem;
import svenhjol.meson.MesonModule;
import svenhjol.meson.helper.ItemNBTHelper;
import svenhjol.strange.base.TotemHelper;
import svenhjol.strange.base.UtilHelper;

import javax.annotation.Nullable;
import java.util.List;

public class TotemOfReturningItem extends MesonItem
{
    private static final String POS = "pos";
    private static final String DIM = "dim";

    public TotemOfReturningItem(MesonModule module)
    {
        super(module, "totem_of_returning", new Item.Properties()
            .group(ItemGroup.TRANSPORTATION)
            .rarity(Rarity.UNCOMMON)
            .maxStackSize(1)
        );
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return getPos(stack) != null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos = getPos(stack); // the position to teleport to
        int dim = getDim(stack); // the dimension to teleport to
        ItemStack held = player.getHeldItem(hand);

        // bind this totem to current location and exit
        if (player.isSneaking()) {
            BlockPos playerPos = player.getPosition();
            int playerDim = player.dimension.getId();
            setPos(stack, playerPos);
            setDim(stack, playerDim);

            player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.8F);
            return super.onItemRightClick(world, player, hand);
        }

        // teleport the player
        if (!world.isRemote) {
            ((ServerPlayerEntity) player).teleport((ServerWorld) world, pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);
            BlockPos updateDest = world.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
            player.setPositionAndUpdate(updateDest.getX(), updateDest.getY() + 1, updateDest.getZ()); // TODO check landing block
        }

        TotemHelper.destroy(player, held);

        return super.onItemRightClick(world, player, hand);
    }

    @Nullable
    public static BlockPos getPos(ItemStack stack)
    {
        long pos = ItemNBTHelper.getLong(stack, POS, 0);
        return pos == 0 ? null : BlockPos.fromLong(pos);
    }

    public static int getDim(ItemStack stack)
    {
        return ItemNBTHelper.getInt(stack, DIM, 0);
    }

    public static void setPos(ItemStack stack, BlockPos pos)
    {
        ItemNBTHelper.setLong(stack, POS, pos.toLong());
    }

    public static void setDim(ItemStack stack, int dim)
    {
        ItemNBTHelper.setInt(stack, DIM, dim);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> strings, ITooltipFlag flag)
    {
        BlockPos pos = getPos(stack);
        if (pos != null) {
            String strDim = String.valueOf(getDim(stack));
            String strPos = UtilHelper.formatBlockPos(pos);
            strings.add(new StringTextComponent(I18n.format("totem.strange.returning", strPos, strDim)));
        }
        super.addInformation(stack, world, strings, flag);
    }
}
