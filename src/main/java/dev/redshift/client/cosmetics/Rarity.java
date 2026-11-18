package dev.redshift.client.cosmetics;

public enum Rarity {
    COMMON(0xB0B0B0, 0), UNCOMMON(0x3DDC84, 5), RARE(0x4DA3FF, 20), EPIC(0xB44DFF, 45), LEGENDARY(0xFFB02E, 90);

    public final int color;
    /** Минуты игрового времени для разблокировки (локальная система). */
    public final int unlockMinutes;

    Rarity(int color, int unlockMinutes) { this.color = color; this.unlockMinutes = unlockMinutes; }
}
