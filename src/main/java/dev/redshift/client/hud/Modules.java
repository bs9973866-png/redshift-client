package dev.redshift.client.hud;

import dev.redshift.client.utilities.ClickTracker;
import dev.redshift.client.utilities.NotificationManager;
import dev.redshift.client.utilities.SessionStats;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Все встроенные HUD-модули (только отображение). */
public final class Modules {
    private Modules() {}

    public static final class Fps extends TextModule {
        public Fps() { super("fps", "FPS Counter", 0.01f, 0.02f, true); }
        @Override protected List<String> compute(Minecraft mc) { return List.of("FPS: " + mc.getFps()); }
    }

    public static final class Cps extends TextModule {
        public Cps() { super("cps", "CPS Counter", 0.01f, 0.07f, true); }
        @Override protected List<String> compute(Minecraft mc) {
            return List.of("CPS: " + ClickTracker.left() + " | " + ClickTracker.right());
        }
    }

    public static final class Coords extends TextModule {
        public Coords() { super("coords", "Coordinates", 0.01f, 0.12f, true); }
        @Override public List<OptionDef> options() {
            return List.of(new OptionDef("decimals", "Decimals", 0, 2, 0, "%.0f"));
        }
        @Override protected List<String> compute(Minecraft mc) {
            int d = Math.round(opt("decimals"));
            String fmt = "%." + d + "f";
            var p = mc.player;
            return List.of("XYZ: " + String.format(Locale.ROOT, fmt, p.getX()) + " / "
                    + String.format(Locale.ROOT, fmt, p.getY()) + " / " + String.format(Locale.ROOT, fmt, p.getZ()));
        }
    }

    public static final class Ping extends TextModule {
        public Ping() { super("ping", "Ping Display", 0.01f, 0.17f, false); }
        @Override protected List<String> compute(Minecraft mc) {
            var con = mc.getConnection();
            if (con == null) return List.of("Ping: -");
            PlayerInfo info = con.getPlayerInfo(mc.player.getUUID());
            return List.of(info == null ? "Ping: -" : "Ping: " + info.getLatency() + " ms");
        }
    }

    public static final class Sprint extends TextModule {
        public Sprint() { super("sprint", "Sprint Status", 0.01f, 0.22f, false); }
        @Override protected List<String> compute(Minecraft mc) {
            return List.of("Sprint: " + (mc.player.isSprinting() ? "ON" : "OFF"));
        }
    }

    public static final class Direction_ extends TextModule {
        public Direction_() { super("direction", "Direction HUD", 0.45f, 0.02f, false); }
        @Override protected List<String> compute(Minecraft mc) {
            float yaw = Mth.wrapDegrees(mc.player.getYRot());
            String n = Direction.fromYRot(yaw).getName();
            n = Character.toUpperCase(n.charAt(0)) + n.substring(1);
            return List.of(n + " (" + Math.round(yaw) + ")");
        }
    }

    public static final class Session extends TextModule {
        public Session() { super("session", "Session Statistics", 0.01f, 0.27f, false); }
        @Override protected List<String> compute(Minecraft mc) {
            return List.of("Playtime: " + SessionStats.playtime(), "Deaths: " + SessionStats.deaths());
        }
    }

    public static final class Items extends TextModule {
        public Items() { super("items", "Item Counter", 0.01f, 0.35f, false); }
        @Override protected List<String> compute(Minecraft mc) {
            ItemStack held = mc.player.getMainHandItem();
            if (held.isEmpty()) return List.of("Item: -");
            int total = 0;
            var inv = mc.player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack s = inv.getItem(i);
                if (ItemStack.isSameItem(s, held)) total += s.getCount();
            }
            return List.of(held.getHoverName().getString() + " x" + total);
        }
    }

    public static final class Potions extends TextModule {
        public Potions() { super("potions", "Potion HUD", 0.88f, 0.02f, true); }
        @Override protected List<String> compute(Minecraft mc) {
            List<String> out = new ArrayList<>();
            for (MobEffectInstance e : mc.player.getActiveEffects()) {
                String time;
                if (e.isInfiniteDuration()) time = "inf";
                else {
                    int s = e.getDuration() / 20;
                    time = String.format("%d:%02d", s / 60, s % 60);
                }
                out.add(e.getEffect().value().getDisplayName().getString() + " " + (e.getAmplifier() + 1) + " [" + time + "]");
            }
            return out;
        }
    }

    public static final class Notifications extends TextModule {
        public Notifications() { super("notifications", "Custom Notifications", 0.40f, 0.08f, true); }
        @Override protected List<String> compute(Minecraft mc) { return NotificationManager.lines(); }
    }

    public static final class Armor extends HudModule {
        private static final EquipmentSlot[] SLOTS =
                {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        public Armor() { super("armor", "Armor HUD", 0.01f, 0.45f, true); }
        @Override protected int contentWidth(Font f, boolean e) { return 18 + f.width("0000"); }
        @Override protected int contentHeight(Font f, boolean e) { return 4 * 18; }
        @Override protected void drawContent(GuiGraphics g, Font f, int x, int y, int rgb, float a, boolean editing) {
            var p = Minecraft.getInstance().player;
            for (int i = 0; i < 4; i++) {
                ItemStack s = p == null ? ItemStack.EMPTY : p.getItemBySlot(SLOTS[i]);
                if (s.isEmpty()) continue;
                g.renderItem(s, x, y + i * 18);
                if (s.isDamageableItem()) {
                    g.drawString(f, String.valueOf(s.getMaxDamage() - s.getDamageValue()), x + 18, y + i * 18 + 4,
                            Theme.alpha(rgb, a), true);
                }
            }
        }
    }

    public static final class Keystrokes extends HudModule {
        private static final int K = 20, GAP = 2;
        public Keystrokes() { super("keystrokes", "Keystrokes", 0.88f, 0.60f, false); }
        @Override protected int contentWidth(Font f, boolean e) { return 3 * K + 2 * GAP; }
        @Override protected int contentHeight(Font f, boolean e) { return 3 * K + 2 * GAP; }
        private void key(GuiGraphics g, Font f, int x, int y, int w, String label, boolean down, int rgb, float a) {
            g.fill(x, y, x + w, y + K, down ? Theme.alpha(rgb, 0.55f * a) : Theme.alpha(0x000000, 0.35f * a));
            g.drawString(f, label, x + (w - f.width(label)) / 2, y + (K - 8) / 2, Theme.alpha(0xFFFFFF, a), true);
        }
        @Override protected void drawContent(GuiGraphics g, Font f, int x, int y, int rgb, float a, boolean editing) {
            var o = Minecraft.getInstance().options;
            key(g, f, x + K + GAP, y, K, "W", o.keyUp.isDown(), rgb, a);
            key(g, f, x, y + K + GAP, K, "A", o.keyLeft.isDown(), rgb, a);
            key(g, f, x + K + GAP, y + K + GAP, K, "S", o.keyDown.isDown(), rgb, a);
            key(g, f, x + 2 * (K + GAP), y + K + GAP, K, "D", o.keyRight.isDown(), rgb, a);
            int w = (3 * K + 2 * GAP - GAP) / 2;
            key(g, f, x, y + 2 * (K + GAP), w, "LMB", o.keyAttack.isDown(), rgb, a);
            key(g, f, x + w + GAP, y + 2 * (K + GAP), w, "RMB", o.keyUse.isDown(), rgb, a);
        }
    }
}
