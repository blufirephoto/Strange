package svenhjol.strange.base;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import svenhjol.strange.enchantments.enchantment.BaseTreasureEnchantment;
import svenhjol.strange.enchantments.module.TreasureEnchantments;

import java.util.List;
import java.util.Map;

public class StrangeAsmHooks {
    public static boolean canApplyEnchantments(List<EnchantmentData> enchantments) {
        boolean canApply = true;
        for (EnchantmentData ench : enchantments) {
            canApply = canApply && canApplyEnchantment(ench.enchantment);
        }
        return canApply;
    }

    public static boolean canApplyEnchantment(Enchantment enchantment) {
        if (!TreasureEnchantments.obtainable) {
            return !(enchantment instanceof BaseTreasureEnchantment);
        }
        return true;
    }

    public static ItemStack modifyStackEnchantments(ItemStack stack) {
        final Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            final Enchantment ench = entry.getKey();
            if (ench instanceof BaseTreasureEnchantment)
                return ItemStack.EMPTY;
        }
        return stack;
    }
}
