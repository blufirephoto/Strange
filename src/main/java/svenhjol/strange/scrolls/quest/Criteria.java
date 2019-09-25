package svenhjol.strange.scrolls.quest;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import svenhjol.strange.scrolls.quest.iface.IDelegate;
import svenhjol.strange.scrolls.quest.iface.IQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class Criteria
{
    public static final String LIMIT = "limit";
    public static final String ACTION = "action";
    public static final String REWARD = "reward";

    private static final String CONDITIONS = "conditions";

    private List<Condition> conditions = new ArrayList<>();
    private IQuest quest;

    public Criteria(IQuest quest)
    {
        this.quest = quest;
    }

    public CompoundNBT toNBT()
    {
        CompoundNBT tag = new CompoundNBT();
        ListNBT conditions = new ListNBT();

        for (Condition condition : this.conditions) {
            conditions.add(condition.toNBT());
        }

        tag.put(CONDITIONS, conditions);
        return tag;
    }

    public void fromNBT(CompoundNBT tag)
    {
        ListNBT tagConditions = (ListNBT)tag.get(CONDITIONS);

        this.conditions.clear();

        if (tagConditions != null) {
            for (INBT nbt : tagConditions) {
                this.conditions.add(Condition.factory((CompoundNBT) nbt, quest));
            }
        }
    }

    public Criteria addCondition(Condition condition)
    {
        this.conditions.add(condition);
        return this;
    }

    public List<Condition> getConditions()
    {
        return conditions;
    }

    public <T extends IDelegate> List<Condition<T>> getConditions(Class<T> clazz)
    {
        // noinspection unchecked
        return conditions.stream()
            .filter(c -> clazz.isInstance(c.getDelegate()))
            .map(c -> (Condition<T>) c)
            .collect(Collectors.toList());
    }

    public <T extends IDelegate> List<Condition<T>> getConditions(String type)
    {
        // noinspection unchecked
        return conditions.stream()
            .filter(c -> c.getType().equals(type))
            .map(c -> (Condition<T>) c)
            .collect(Collectors.toList());
    }

    public boolean isSatisfied()
    {
        return conditions.stream().allMatch(Condition::isSatisfied);
    }

    public float getCompletion()
    {
        float complete = 0.0F;
        int completable = 0;

        for (Condition condition : conditions) {
            if (condition.getDelegate().isCompletable()) {
                completable++;
                complete += condition.getDelegate().getCompletion();
            }
        }

        if (complete == 0.0F) return 0.0F;
        return complete / (completable * 100) * 100;
    }
}
