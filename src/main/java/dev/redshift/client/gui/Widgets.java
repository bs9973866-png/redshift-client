package dev.redshift.client.gui;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ModuleState;
import dev.redshift.client.utilities.Theme;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class Widgets {
    private Widgets() {}

    private static Component toggleText(String label, boolean on) {
        return Component.literal(label + ": ")
                .append(Component.literal(on ? "ON" : "OFF").withStyle(Theme.style(on ? 0x3DDC84 : 0xFF5555)));
    }

    public static RsButton toggle(int x, int y, int w, int h, String label, BooleanSupplier get, Consumer<Boolean> set) {
        return new RsButton(x, y, w, h, toggleText(label, get.getAsBoolean()), b -> {
            boolean n = !get.getAsBoolean();
            set.accept(n);
            b.setMessage(toggleText(label, n));
            ConfigManager.markDirty();
        });
    }

    public static RsButton colorButton(int x, int y, int w, int h, ModuleState s) {
        return new RsButton(x, y, w, h, colorText(s.color), b -> {
            s.color = Theme.nextPreset(s.color);
            b.setMessage(colorText(s.color));
            ConfigManager.markDirty();
        });
    }

    private static Component colorText(int rgb) {
        return Component.literal("Color: ").append(Component.literal(Theme.PRESET_NAMES[Theme.presetIndex(rgb)])
                .withStyle(Theme.style(Theme.resolve(rgb))));
    }
}
