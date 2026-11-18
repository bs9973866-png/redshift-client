package dev.redshift.client.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProfileData {
    public String name;
    public boolean builtin;
    /** Подстроки адреса сервера для автоопределения. */
    public List<String> addresses = new ArrayList<>();
    public int accent = 0xE0243C;
    public Map<String, ModuleState> modules = new LinkedHashMap<>();
    public boolean swingEnabled = true;
    public SwingSettings swingSword = new SwingSettings();
    public SwingSettings swingOther = new SwingSettings();
    public List<String> equipped = new ArrayList<>();
    public int gridSize = 10;
    public boolean showGrid = true;
}
