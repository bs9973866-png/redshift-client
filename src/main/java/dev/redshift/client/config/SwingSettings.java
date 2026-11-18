package dev.redshift.client.config;

import dev.redshift.client.animations.SwingMode;

public final class SwingSettings {
    public boolean enabled = true;
    public SwingMode mode = SwingMode.SMOOTH;
    /** Множитель скорости (>=1 ускоряет, только визуально). */
    public float speed = 1f;
    public float angle = 100f;      // %
    public float smoothness = 0.5f; // 0..1
    public float intensity = 1f;
    public float handX, handY, handZ;
    public float itemX, itemY, itemZ;
    public float customRotX = -60f, customRotY = 0f, customRotZ = 0f;
    public float customMoveX = 0f, customMoveY = 0f, customMoveZ = -0.2f;

    public static SwingSettings vanilla() {
        SwingSettings s = new SwingSettings();
        s.mode = SwingMode.CLASSIC;
        return s;
    }
}
