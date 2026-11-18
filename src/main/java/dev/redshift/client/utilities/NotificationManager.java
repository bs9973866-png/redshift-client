package dev.redshift.client.utilities;

import dev.redshift.client.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public final class NotificationManager {
    private record Note(String text, long expires) {}
    private static final List<Note> NOTES = new ArrayList<>();

    private NotificationManager() {}

    public static void push(String text) {
        if (!ConfigManager.get().notifications) return;
        NOTES.add(new Note(text, System.currentTimeMillis() + 3500));
        while (NOTES.size() > 5) NOTES.remove(0);
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        NOTES.removeIf(n -> n.expires < now);
    }

    public static List<String> lines() {
        List<String> out = new ArrayList<>(NOTES.size());
        for (Note n : NOTES) out.add(n.text);
        return out;
    }
}
