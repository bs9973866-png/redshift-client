package dev.redshift.client.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RedshiftConfig {
    public String activeProfile = "default";
    public boolean autoDetect = true;
    public boolean notifications = true;
    public boolean hudVisible = true;
    public boolean autoReduceEffects = true;
    public float effectDensity = 1f;
    public int statsInterval = 2;
    public long playtimeSeconds = 0;
    public Map<String, ProfileData> profiles = new LinkedHashMap<>();
    public List<String> favorites = new ArrayList<>();
    public List<String> owned = new ArrayList<>();
}
