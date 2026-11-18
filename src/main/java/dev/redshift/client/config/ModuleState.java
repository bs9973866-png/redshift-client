package dev.redshift.client.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModuleState {
    public boolean enabled;
    /** Позиция в долях экрана (0..1), левый верхний угол. */
    public float x, y;
    public float scale = 1f;
    public float opacity = 1f;
    /** 0 = цвет акцента профиля, иначе RGB. */
    public int color = 0;
    public boolean background = true;
    public Map<String, Float> options = new LinkedHashMap<>();
}
