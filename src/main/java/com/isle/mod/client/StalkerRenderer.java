package com.isle.mod.client;

import com.isle.mod.IsleMod;
import com.isle.mod.entity.StalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StalkerRenderer extends HumanoidMobRenderer<StalkerEntity, HumanoidModel<StalkerEntity>> {
    private static final ResourceLocation TEX = new ResourceLocation(IsleMod.MODID, "textures/entity/stalker.png");

    public StalkerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(StalkerEntity e) { return TEX; }

    @Override
    protected void scale(StalkerEntity e, PoseStack ps, float partial) {
        ps.scale(1.0f, 1.25f, 1.0f); // выше и тоньше обычного зомби
    }
}
