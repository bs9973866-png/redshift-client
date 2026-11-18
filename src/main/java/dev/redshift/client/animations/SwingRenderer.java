package dev.redshift.client.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.redshift.client.config.ConfigManager;
import dev.redshift.client.config.ProfileData;
import dev.redshift.client.config.SwingSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Чисто клиентская трансформация руки/предмета. Механику атаки не затрагивает. */
public final class SwingRenderer {
    private SwingRenderer() {}

    public static SwingSettings settingsFor(ProfileData prof, ItemStack st) {
        return st.is(ItemTags.SWORDS) ? prof.swingSword : prof.swingOther;
    }

    /** Форма прогресса: ускорение, сглаживание, реверс. */
    public static float shape(SwingSettings s, float p) {
        if (s.mode == SwingMode.CLASSIC) return p;
        float lin = Mth.clamp(p * Math.max(0.1f, s.speed), 0f, 1f);
        float eased = lin * lin * (3f - 2f * lin);
        float q = lin + (eased - lin) * s.smoothness;
        return s.mode == SwingMode.REVERSE ? 1f - q : q;
    }

    private static void vanilla(PoseStack ps, float side, float q, float a) {
        float f = Mth.sin(q * q * (float) Math.PI);
        ps.mulPose(Axis.YP.rotationDegrees(side * (45f + f * -20f * a)));
        float g = Mth.sin(Mth.sqrt(q) * (float) Math.PI);
        ps.mulPose(Axis.ZP.rotationDegrees(side * g * -20f * a));
        ps.mulPose(Axis.XP.rotationDegrees(g * -80f * a));
        ps.mulPose(Axis.YP.rotationDegrees(side * -45f));
    }

    /** @return true если ванильная трансформация должна быть отменена. */
    public static boolean apply(PoseStack ps, HumanoidArm arm, float p) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return false;
        ProfileData prof = ConfigManager.profile();
        if (!prof.swingEnabled) return false;
        InteractionHand hand = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        SwingSettings s = settingsFor(prof, player.getItemInHand(hand));
        if (!s.enabled) return false;

        boolean offsets = s.handX != 0 || s.handY != 0 || s.handZ != 0 || s.itemX != 0 || s.itemY != 0 || s.itemZ != 0;
        if (s.mode == SwingMode.CLASSIC && !offsets) return false;

        float side = arm == HumanoidArm.RIGHT ? 1f : -1f;
        float a = s.angle / 100f, in = s.intensity;
        float q = shape(s, p);
        float sw = Mth.sin(q * (float) Math.PI);

        ps.translate(side * s.handX, s.handY, s.handZ);
        switch (s.mode) {
            case CLASSIC -> vanilla(ps, side, p, 1f);
            case SMOOTH, REVERSE -> vanilla(ps, side, q, a * in);
            case DYNAMIC -> {
                vanilla(ps, side, (float) Math.pow(q, 0.75), a * in);
                ps.translate(0f, -0.08f * in * sw, -0.12f * in * sw);
                ps.mulPose(Axis.ZP.rotationDegrees(side * Mth.sin(q * 2f * (float) Math.PI) * 8f * in));
            }
            case SPIN -> {
                int rev = Math.max(1, Math.round(a * in));
                ps.translate(0f, 0f, -0.2f * sw);
                ps.mulPose(Axis.ZP.rotationDegrees(side * 360f * rev * q));
                ps.mulPose(Axis.XP.rotationDegrees(-30f * sw));
            }
            case SLIDE -> {
                ps.translate(-side * 0.4f * in * a * sw, 0.05f * sw, -0.3f * in * a * sw);
                ps.mulPose(Axis.XP.rotationDegrees(-25f * a * sw));
                ps.mulPose(Axis.ZP.rotationDegrees(side * 10f * sw));
            }
            case CUSTOM -> {
                ps.translate(side * s.customMoveX * sw, s.customMoveY * sw, s.customMoveZ * sw);
                ps.mulPose(Axis.XP.rotationDegrees(s.customRotX * sw * a));
                ps.mulPose(Axis.YP.rotationDegrees(side * s.customRotY * sw * a));
                ps.mulPose(Axis.ZP.rotationDegrees(side * s.customRotZ * sw * a));
            }
        }
        ps.translate(side * s.itemX, s.itemY, s.itemZ);
        return true;
    }
}
