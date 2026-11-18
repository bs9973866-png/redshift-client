package dev.redshift.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.redshift.client.animations.SwingRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "applyItemArmAttackTransform", at = @At("HEAD"), cancellable = true)
    private void redshift$swing(PoseStack poseStack, HumanoidArm arm, float swingProgress, CallbackInfo ci) {
        if (SwingRenderer.apply(poseStack, arm, swingProgress)) ci.cancel();
    }
}
