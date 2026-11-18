package dev.redshift.client.gui;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.config.RedshiftConfig;
import dev.redshift.client.profiles.ProfileManager;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilesScreen extends RsScreen {
    private EditBox nameBox, addrBox;

    public ProfilesScreen() { super("Server Profiles", Page.PROFILES); }

    @Override
    protected void build() {
        RedshiftConfig cfg = ConfigManager.get();
        int colW = (cw - 8) / 2, y = top();
        int i = 0;
        for (ProfileData p : new ArrayList<>(cfg.profiles.values())) {
            int x = cx + (i % 2) * (colW + 8), yy = y + (i / 2) * 22;
            boolean active = p.name.equals(cfg.activeProfile);
            int w = p.builtin ? colW : colW - 24;
            addRenderableWidget(new RsButton(x, yy, w, 20,
                    Component.literal((active ? "> " : "") + p.name).withStyle(Theme.style(p.accent)), b -> {
                ProfileManager.switchTo(p.name);
                rebuildWidgets();
            }));
            if (!p.builtin) {
                addRenderableWidget(new RsButton(x + colW - 22, yy, 22, 20, Component.literal("X"), b -> {
                    cfg.profiles.remove(p.name);
                    if (cfg.activeProfile.equals(p.name)) cfg.activeProfile = "default";
                    ConfigManager.markDirty();
                    rebuildWidgets();
                }));
            }
            i++;
        }
        int rows = (i + 1) / 2;
        int by = y + rows * 22 + 8;
        ProfileData cur = ConfigManager.profile();

        nameBox = new EditBox(font, cx, by, colW - 64, 20, Component.literal("name"));
        nameBox.setHint(Component.literal("New profile name"));
        addRenderableWidget(nameBox);
        addRenderableWidget(new RsButton(cx + colW - 60, by, 60, 20, Component.literal("Create"), b -> {
            String n = nameBox.getValue().trim();
            if (n.isEmpty() || cfg.profiles.containsKey(n)) return;
            ProfileData copy = ConfigManager.copyProfile(ConfigManager.profile());
            copy.name = n; copy.builtin = false; copy.addresses = new ArrayList<>();
            cfg.profiles.put(n, copy);
            ProfileManager.switchTo(n);
            ConfigManager.markDirty();
            rebuildWidgets();
        }));
        addRenderableWidget(Widgets.toggle(cx + colW + 8, by, colW, 20, "Auto-detect server", () -> cfg.autoDetect, v -> cfg.autoDetect = v));
        by += 24;
        addrBox = new EditBox(font, cx, by, colW - 64, 20, Component.literal("addr"));
        addrBox.setHint(Component.literal("Address match: a, b"));
        addrBox.setMaxLength(120);
        addrBox.setValue(String.join(", ", cur.addresses));
        addRenderableWidget(addrBox);
        addRenderableWidget(new RsButton(cx + colW - 60, by, 60, 20, Component.literal("Apply"), b -> {
            List<String> l = new ArrayList<>();
            for (String s : Arrays.asList(addrBox.getValue().split(","))) if (!s.isBlank()) l.add(s.trim());
            cur.addresses = l;
            ConfigManager.markDirty();
        }));
        addRenderableWidget(new RsButton(cx + colW + 8, by, colW, 20, Component.literal("Accent color: cycle"), b -> {
            cur.accent = Theme.nextAccent(cur.accent);
            ConfigManager.markDirty();
            rebuildWidgets();
        }));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        ProfileData cur = ConfigManager.profile();
        int y = height - 40;
        g.drawString(font, "Active: " + cur.name + " | Each profile stores HUD layout, swing settings, cosmetics, accent.", cx, y, Theme.MUTED, false);
        g.drawString(font, "New profiles copy the active one. Address match = substring of server IP.", cx, y + 12, Theme.MUTED, false);
    }
}
