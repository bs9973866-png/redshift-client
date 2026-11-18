package dev.redshift.client.utilities;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;

/** Считает нажатия ЛКМ/ПКМ для отображения CPS. Только чтение состояния клавиш. */
public final class ClickTracker {
    private static final ArrayDeque<Long> LEFT = new ArrayDeque<>();
    private static final ArrayDeque<Long> RIGHT = new ArrayDeque<>();
    private static boolean lastL, lastR;

    private ClickTracker() {}

    public static void tick(Minecraft mc) {
        long now = System.currentTimeMillis();
        boolean l = mc.options.keyAttack.isDown();
        boolean r = mc.options.keyUse.isDown();
        if (l && !lastL) LEFT.add(now);
        if (r && !lastR) RIGHT.add(now);
        lastL = l;
        lastR = r;
        while (!LEFT.isEmpty() && now - LEFT.peekFirst() > 1000) LEFT.pollFirst();
        while (!RIGHT.isEmpty() && now - RIGHT.peekFirst() > 1000) RIGHT.pollFirst();
    }

    public static int left() { return LEFT.size(); }
    public static int right() { return RIGHT.size(); }
}
