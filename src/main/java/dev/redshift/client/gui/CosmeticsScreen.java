package dev.redshift.client.gui;

import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.cosmetics.*;
import dev.redshift.client.utilities.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CosmeticsScreen extends RsScreen {
    private static int filter = 0;   // 0 ALL, 1 FAVORITES, 2 EQUIPPED, 3.. категории
    private static int page = 0;
    private static float yaw = 0f;
    private static int zoom = 55;
    private static final int PER_PAGE = 8;
    private int px, pw;
    private boolean draggingPreview;

    public CosmeticsScreen() { super("Cosmetics", Page.COSMETICS); }

    private String filterName() {
        if (filter == 0) return "All";
        if (filter == 1) return "Favorites";
        if (filter == 2) return "Equipped";
        return CosmeticCategory.values()[filter - 3].label;
    }

    private List<Cosmetic> items() {
        List<Cosmetic> out = new ArrayList<>();
        for (Cosmetic c : CosmeticCatalog.all()) {
            boolean ok = switch (filter) {
                case 0 -> true;
                case 1 -> CosmeticManager.favorite(c);
                case 2 -> CosmeticManager.equipped(c);
                default -> c.category() == CosmeticCategory.values()[filter - 3];
            };
            if (ok) out.add(c);
        }
        return out;
    }

    @Override
    protected void build() {
        int listW = Math.min(240, cw - 130);
        px = cx + listW + 10;
        pw = cw - listW - 10;
        int y = top();
        int total = CosmeticCategory.values().length + 3;
        addRenderableWidget(new RsButton(cx, y, listW, 20, Component.literal("Filter: " + filterName()), b -> {
            filter = (filter + 1) % total; page = 0; rebuildWidgets();
        }));
        y += 24;
        List<Cosmetic> list = items();
        int pages = Math.max(1, (list.size() + PER_PAGE - 1) / PER_PAGE);
        page = Math.min(page, pages - 1);
        for (int i = page * PER_PAGE; i < Math.min(list.size(), (page + 1) * PER_PAGE); i++) {
            Cosmetic c = list.get(i);
            boolean unlocked = CosmeticManager.unlocked(c);
            String suffix = CosmeticManager.equipped(c) ? "  [EQUIPPED]"
                    : unlocked ? "" : "  [locked: " + c.rarity().unlockMinutes + " min]";
            RsButton main = new RsButton(cx, y, listW - 24, 20,
                    Component.literal(c.name() + suffix).withStyle(Theme.style(c.rarity().color)), b -> {
                CosmeticManager.toggleEquip(c);
                rebuildWidgets();
            });
            main.active = unlocked || CosmeticManager.equipped(c);
            addRenderableWidget(main);
            addRenderableWidget(new RsButton(cx + listW - 22, y, 22, 20,
                    Component.literal(CosmeticManager.favorite(c) ? "\u2605" : "\u2606"), b -> {
                CosmeticManager.toggleFavorite(c);
                b.setMessage(Component.literal(CosmeticManager.favorite(c) ? "\u2605" : "\u2606"));
            }));
            y += 22;
        }
        int by = top() + 24 + PER_PAGE * 22 + 2;
        RsButton prev = new RsButton(cx, by, 60, 20, Component.literal("< Prev"), b -> { page--; rebuildWidgets(); });
        RsButton next = new RsButton(cx + listW - 60, by, 60, 20, Component.literal("Next >"), b -> { page++; rebuildWidgets(); });
        prev.active = page > 0;
        next.active = page < pages - 1;
        addRenderableWidget(prev);
        addRenderableWidget(next);
        addRenderableWidget(new RsButton(px, by, 40, 20, Component.literal("<"), b -> yaw -= 25f));
        addRenderableWidget(new RsButton(px + 44, by, 40, 20, Component.literal(">"), b -> yaw += 25f));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        int listW = Math.min(240, cw - 130);
        int pyTop = top(), ph = 24 + PER_PAGE * 22 - 4;
        g.fill(px, pyTop, px + pw, pyTop + ph, 0xFF0D0716);
        Theme.outline(g, px, pyTop, pw, ph, Theme.alpha(Theme.PURPLE, 0.5f));
        if (minecraft.player != null) {
            float fx = px + pw / 2f - yaw * 1.2f, fy = pyTop + ph * 0.35f;
            InventoryScreen.renderEntityInInventoryFollowsMouse(g, px, pyTop, px + pw, pyTop + ph, zoom, 0.0625f,
                    fx, fy, minecraft.player);
        }
        g.drawString(font, "Scroll = zoom, drag / < > = rotate", px + 4, pyTop + 4, Theme.MUTED, false);
        if (items().isEmpty()) {
            g.drawString(font, "No items here.", cx + 4, top() + 30, Theme.MUTED, false);
            g.drawString(font, "Wings/capes/hats/pets need 3D models: not implemented yet.", cx + 4, top() + 42, Theme.MUTED, false);
        }
        long min = ConfigManager.get().playtimeSeconds / 60;
        g.drawString(font, "Playtime: " + min + " min (unlocks by playtime, local)", cx, top() + 24 + PER_PAGE * 22 + 28, Theme.MUTED, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
        if (super.mouseClicked(e, dbl)) return true;
        if (e.x() >= px && e.x() <= px + pw && e.y() >= top() && e.y() <= top() + 24 + PER_PAGE * 22) {
            draggingPreview = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (draggingPreview) { yaw += (float) dx * 0.8f; return true; }
        return super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        draggingPreview = false;
        return super.mouseReleased(e);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (mx >= px && mx <= px + pw) {
            zoom = Math.max(25, Math.min(110, zoom + (int) (sy * 5)));
            return true;
        }
        return super.mouseScrolled(mx, my, sx, sy);
    }
}
