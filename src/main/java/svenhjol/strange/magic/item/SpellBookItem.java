package svenhjol.strange.magic.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import svenhjol.meson.MesonItem;
import svenhjol.meson.MesonModule;
import svenhjol.strange.magic.helper.MagicHelper;
import svenhjol.strange.magic.module.Spells;
import svenhjol.strange.magic.spells.Spell;
import svenhjol.strange.magic.spells.Spell.Element;

import javax.annotation.Nullable;
import java.util.List;

public class SpellBookItem extends MesonItem
{
    public static final String SPELL = "spell";
    public static final String META = "meta";
    public static final String ACTIVATED = "activated";

    public SpellBookItem(MesonModule module)
    {
        super(module, "spell_book", new Item.Properties()
            .maxStackSize(16));

        // allows different item icons to be shown. Each item icon has a float ref (see model)
        addPropertyOverride(new ResourceLocation("element"), (stack, world, entity) -> {
            Spell spell = getSpell(stack);
            float out = spell != null && spell.getElement() != null ? spell.getElement().ordinal() : Element.BASE.ordinal();
            return out / 10.0F;
        });
    }

    @Override
    public int getUseDuration(ItemStack book)
    {
        Spell spell = getSpell(book);
        return spell != null ? spell.getDuration() * 20 : 20;
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        Spell spell = getSpell(stack);
        return spell != null ? spell.getTranslationKey() : this.getTranslationKey();
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (group == ItemGroup.SEARCH) {
            for (String id : Spells.spells.keySet()) {
                Spell spell = Spells.spells.get(id);
                ItemStack book = SpellBookItem.putSpell(new ItemStack(Spells.item), spell);
                book.setDisplayName(MagicHelper.getSpellInfoText(spell));
                items.add(book);
            }
        }
    }

    @Override
    public void addInformation(ItemStack book, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.addInformation(book, world, tooltip, flag);
        Spell spell = SpellBookItem.getSpell(book);
        if (spell == null) return;

        ITextComponent descriptionText = new TranslationTextComponent(spell.getDescriptionKey());
        descriptionText.setStyle((new Style()).setColor(TextFormatting.WHITE));

        ITextComponent affectText = new TranslationTextComponent(spell.getAffectKey());
        affectText.setStyle((new Style()).setColor(TextFormatting.GRAY));

        tooltip.add(descriptionText);
        tooltip.add(affectText);
    }

    public int getXpCost(ItemStack book)
    {
        Spell spell = getSpell(book);
        return spell != null ? spell.getCastCost() : 0;
    }

    @Nullable
    public static Spell getSpell(ItemStack book)
    {
        String id = book.getOrCreateTag().getString(SPELL);

        if (Spells.spells.containsKey(id)) {
            return Spells.spells.get(id);
        }

        return null;
    }

    public static ItemStack putSpell(ItemStack book, Spell spell)
    {
        book.getOrCreateTag().putString(SPELL, spell.getId()); // add new spell
        return book;
    }
}
