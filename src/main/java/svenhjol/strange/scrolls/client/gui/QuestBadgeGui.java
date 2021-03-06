package svenhjol.strange.scrolls.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import svenhjol.strange.Strange;
import svenhjol.strange.scrolls.module.Quests;
import svenhjol.strange.scrolls.quest.iface.IQuest;

public class QuestBadgeGui extends AbstractGui {
    private final int x0;
    private final int x1;
    private final int y0;
    private final int y1;
    private int buttonX0, buttonY0, buttonX1, buttonY1;
    private final IQuest quest;
    private final Minecraft mc;
    public static final int WIDTH = 100;
    public static final int HEIGHT = 14;
    public static final int BUTTON_PADDING = 2;

    public QuestBadgeGui(IQuest quest, int x, int y) {
        this.quest = quest;
        this.mc = Minecraft.getInstance();

        x0 = x;
        x1 = x + WIDTH;
        y0 = y;
        y1 = y + HEIGHT;

        // Build button background and name
        this.buildButton(x0 - BUTTON_PADDING, y0 - BUTTON_PADDING, WIDTH + BUTTON_PADDING * 2, HEIGHT + BUTTON_PADDING * 2);
        drawCenteredString(mc.fontRenderer, I18n.format(quest.getTitle()), x + 50, y, 0xFFFFFF);

        // progress
        float completion = quest.getCriteria().getCompletion();
        boolean isComplete = quest.getCriteria().isSatisfied();

        int color = isComplete ? 0x8800FF00 : 0x88FFFF00;
        AbstractGui.fill(x0, y1 - 3, x1, y1, 0x88333333);
        AbstractGui.fill(x0, y1 - 3, x0 + (int) completion, y1, color);
    }

    public void buildButton(int butX, int butY, int butWidth, int butHeight) {
        buttonX0 = butX;
        buttonX1 = butX + butWidth;
        buttonY0 = butY;
        buttonY1 = butY + butHeight;

        AbstractGui.fill(buttonX0, buttonY0, buttonX1, buttonY1, 0x88AAAAAA);
        AbstractGui.fill(buttonX0 - 1, buttonY0 - 1, buttonX1 + 1, buttonY0, 0x88FFFFFF);
        AbstractGui.fill(buttonX0 - 1, buttonY0 - 1, buttonX0, buttonY1 + 1, 0x88FFFFFF);
        AbstractGui.fill(buttonX0 - 1, buttonY1, buttonX1 + 1, buttonY1 + 1, 0x88000000);
        AbstractGui.fill(buttonX1, buttonY0 - 1, buttonX1 + 1, buttonY1 + 1, 0x88000000);
    }

    public boolean isInBox(double x, double y) {
        return x >= x0 && x <= x1 && y >= y0 && y <= y1;
    }

    public void onLeftClick() {
        Strange.LOG.debug(Strange.CLIENT, "[CLIENT] clicked quest badge: " + quest.getId());
        Quests.client.showQuest(quest);
    }
}
