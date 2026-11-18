package dev.redshift.client.cosmetics;

public enum CosmeticCategory {
    WINGS("Wings", null), CAPES("Capes", null), HATS("Hats", null), MASKS("Masks", null), GLASSES("Glasses", null),
    ACCESSORIES("Accessories", null), BACKPACKS("Backpacks", null), PETS("Pets", null),
    TRAILS("Trails", EffectMode.TRAIL), AURAS("Auras", EffectMode.AURA), PARTICLES("Particles", EffectMode.PARTICLES),
    FOOTSTEPS("Footsteps", EffectMode.FOOTSTEPS), AMBIENT("Ambient", EffectMode.AMBIENT);

    public final String label;
    public final EffectMode mode;

    CosmeticCategory(String label, EffectMode mode) { this.label = label; this.mode = mode; }

    public boolean isEffect() { return mode != null; }
}
