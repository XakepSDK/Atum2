package com.teammetallurgy.atum.client.model.shield;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class AtumsProtectionModel extends AbstractShieldModel {
    private final ModelRenderer shieldCore;
    private final ModelRenderer handleCore;
    private final ModelRenderer gemStone;
    private final ModelRenderer shieldTop1;
    private final ModelRenderer shieldTop2;
    private final ModelRenderer shieldTop3;
    private final ModelRenderer shieldTop4;
    private final ModelRenderer shieldBottom1;
    private final ModelRenderer shieldBottom2;
    private final ModelRenderer shieldBottom3;
    private final ModelRenderer shieldBottom4;
    private final ModelRenderer handleSide1;
    private final ModelRenderer handleSide2;

    public AtumsProtectionModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.handleCore = new ModelRenderer(this, 20, 0);
        this.handleCore.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleCore.addBox(-1.0F, -2.0F, 3.0F, 2, 4, 1, 0.0F);
        this.shieldBottom4 = new ModelRenderer(this, 0, 22);
        this.shieldBottom4.setRotationPoint(1.0F, 1.0F, 0.0F);
        this.shieldBottom4.addBox(-4.0F, 4.0F, 0.0F, 6, 1, 1, 0.0F);
        this.shieldCore = new ModelRenderer(this, 0, 10);
        this.shieldCore.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldCore.addBox(-5.0F, -2.0F, 0.0F, 10, 4, 1, 0.0F);
        this.handleSide1 = new ModelRenderer(this, 25, 3);
        this.handleSide1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleSide1.addBox(-1.0F, 1.0F, 1.0F, 2, 1, 2, 0.0F);
        this.shieldBottom3 = new ModelRenderer(this, 0, 20);
        this.shieldBottom3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldBottom3.addBox(-4.0F, 4.0F, 0.0F, 8, 1, 1, 0.0F);
        this.shieldBottom1 = new ModelRenderer(this, 0, 16);
        this.shieldBottom1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldBottom1.addBox(-6.0F, 2.0F, 0.0F, 12, 1, 1, 0.0F);
        this.shieldTop2 = new ModelRenderer(this, 0, 6);
        this.shieldTop2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop2.addBox(-5.0F, -4.0F, 0.0F, 10, 1, 1, 0.0F);
        this.gemStone = new ModelRenderer(this, 0, 24);
        this.gemStone.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gemStone.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 1, 0.0F);
        this.handleSide2 = new ModelRenderer(this, 25, 3);
        this.handleSide2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleSide2.addBox(-1.0F, -2.0F, 1.0F, 2, 1, 2, 0.0F);
        this.shieldBottom2 = new ModelRenderer(this, 0, 18);
        this.shieldBottom2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldBottom2.addBox(-5.0F, 3.0F, 0.0F, 10, 1, 1, 0.0F);
        this.shieldTop4 = new ModelRenderer(this, 0, 2);
        this.shieldTop4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop4.addBox(-3.0F, -6.0F, 0.0F, 6, 1, 1, 0.0F);
        this.shieldTop3 = new ModelRenderer(this, 0, 4);
        this.shieldTop3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop3.addBox(-4.0F, -5.0F, 0.0F, 8, 1, 1, 0.0F);
        this.shieldTop1 = new ModelRenderer(this, 0, 8);
        this.shieldTop1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop1.addBox(-6.0F, -3.0F, 0.0F, 12, 1, 1, 0.0F);
        this.shieldCore.addChild(this.shieldBottom4);
        this.handleCore.addChild(this.handleSide1);
        this.shieldCore.addChild(this.shieldBottom3);
        this.shieldCore.addChild(this.shieldBottom1);
        this.shieldCore.addChild(this.shieldTop2);
        this.handleCore.addChild(this.handleSide2);
        this.shieldCore.addChild(this.shieldBottom2);
        this.shieldCore.addChild(this.shieldTop4);
        this.shieldCore.addChild(this.shieldTop3);
        this.shieldCore.addChild(this.shieldTop1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder vertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {
        matrixStack.push();
        matrixStack.scale(1.0F / 0.75F, -1.0F / 0.75F, -1.0F / 0.75F);
        matrixStack.translate(0.0F, 0.0F, -0.03F);
        this.handleCore.render(matrixStack, vertexBuilder, i, i1, v, v1, v2, v3);
        this.shieldCore.render(matrixStack, vertexBuilder, i, i1, v, v1, v2, v3);
        this.gemStone.render(matrixStack, vertexBuilder, i, i1, v, v1, v2, v3);
        matrixStack.pop();
    }
}