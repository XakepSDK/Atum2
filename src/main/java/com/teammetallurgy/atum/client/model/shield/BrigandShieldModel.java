package com.teammetallurgy.atum.client.model.shield;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrigandShieldModel extends ShieldModel {
    private RendererModel shieldCore;
    private RendererModel handleCore;
    private RendererModel shieldTop1;
    private RendererModel shieldTop2;
    private RendererModel shieldTop3;
    private RendererModel shieldTop4;
    private RendererModel shieldBottom1;
    private RendererModel shieldBottom2;
    private RendererModel shieldBottom3;
    private RendererModel shieldBottom4;
    private RendererModel handleSide1;
    private RendererModel handleSide2;

    public BrigandShieldModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.handleSide1 = new RendererModel(this, 25, 3);
        this.handleSide1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleSide1.addBox(-1.0F, 1.0F, 1.0F, 2, 1, 2, 0.0F);
        this.handleSide2 = new RendererModel(this, 25, 3);
        this.handleSide2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleSide2.addBox(-1.0F, -2.0F, 1.0F, 2, 1, 2, 0.0F);
        this.shieldTop2 = new RendererModel(this, 0, 6);
        this.shieldTop2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop2.addBox(-5.0F, -4.0F, 0.0F, 10, 1, 1, 0.0F);
        this.shieldTop3 = new RendererModel(this, 0, 4);
        this.shieldTop3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop3.addBox(-4.0F, -5.0F, 0.0F, 8, 1, 1, 0.0F);
        this.handleCore = new RendererModel(this, 20, 0);
        this.handleCore.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.handleCore.addBox(-1.0F, -2.0F, 3.0F, 2, 4, 1, 0.0F);
        this.shieldBottom1 = new RendererModel(this, 0, 16);
        this.shieldBottom1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldBottom1.addBox(-6.0F, 2.0F, 0.0F, 12, 1, 1, 0.0F);
        this.shieldBottom4 = new RendererModel(this, 0, 22);
        this.shieldBottom4.setRotationPoint(1.0F, 1.0F, 0.0F);
        this.shieldBottom4.addBox(-4.0F, 4.0F, 0.0F, 6, 1, 1, 0.0F);
        this.shieldBottom2 = new RendererModel(this, 0, 18);
        this.shieldBottom2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldBottom2.addBox(-5.0F, 3.0F, 0.0F, 10, 1, 1, 0.0F);
        this.shieldCore = new RendererModel(this, 0, 10);
        this.shieldCore.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldCore.addBox(-6.0F, -2.0F, 0.0F, 12, 4, 1, 0.0F);
        this.shieldBottom3 = new RendererModel(this, 0, 20);
        this.shieldBottom3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldBottom3.addBox(-4.0F, 4.0F, 0.0F, 8, 1, 1, 0.0F);
        this.shieldTop4 = new RendererModel(this, 0, 2);
        this.shieldTop4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop4.addBox(-3.0F, -6.0F, 0.0F, 6, 1, 1, 0.0F);
        this.shieldTop1 = new RendererModel(this, 0, 8);
        this.shieldTop1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shieldTop1.addBox(-6.0F, -3.0F, 0.0F, 12, 1, 1, 0.0F);
        this.handleCore.addChild(this.handleSide1);
        this.handleCore.addChild(this.handleSide2);
        this.shieldCore.addChild(this.shieldTop2);
        this.shieldCore.addChild(this.shieldTop3);
        this.shieldCore.addChild(this.shieldBottom1);
        this.shieldCore.addChild(this.shieldBottom4);
        this.shieldCore.addChild(this.shieldBottom2);
        this.shieldCore.addChild(this.shieldBottom3);
        this.shieldCore.addChild(this.shieldTop4);
        this.shieldCore.addChild(this.shieldTop1);
    }

    @Override
    public void render() {
        GlStateManager.pushMatrix();
        GlStateManager.scaled(1.0D / 0.78D, -1.0D / 0.78D, -1.0D / 0.78D);
        GlStateManager.translatef(0.0F, 0.0F, -0.025F);
        this.handleCore.render(0.0625F);
        this.shieldCore.render(0.0625F);
        GlStateManager.popMatrix();
    }
}