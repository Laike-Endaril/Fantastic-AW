package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.Tools;
import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinHead;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class ModelSkinHeadEdit extends ModelSkinHead
{
    public float[] headScale = null;

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading)
    {
        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
    }

    @Override
    public void render(Entity entity, Skin skin, SkinRenderData renderData)
    {
        if (skin == null) return;

        // Fix to stop head skins rendering when using the Real First-Person Render mod.
        if (entity != null && entity.equals(Minecraft.getMinecraft().player) & skinHasHead(skin))
        {
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
            {
                if (ModAddonManager.addonRealFirstPerson.isModRender()) return;
                if (ModAddonManager.addonRealFirstPerson2.isModRender()) return;
            }
        }

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            isSneak = player.isSneaking();
            isRiding = player.isRiding();
        }

        GlStateManager.pushAttrib();
        RenderHelper.enableGUIStandardItemLighting();

        if (skin.hasPaintData() & renderData.isShowSkinPaint() & ClientProxy.getTexturePaintType() == ClientProxy.TexturePaintType.TEXTURE_REPLACE)
        {
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, renderData.getSkinDye(), renderData.getExtraColours());
            st.bindTexture();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            bipedHead.render(SCALE);
            GL11.glPopAttrib();
        }

        for (SkinPart skinPart : skin.getParts())
        {
            GL11.glPushMatrix();
            if (isChild)
            {
                GL11.glScalef(0.75f, 0.75f, 0.75f);
                GL11.glTranslatef(0.0F, 16.0F * SCALE, 0.0F);
            }

            if (isSneak)
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
                GlStateManager.translate(0.0F, SCALE, 0.0F);
            }


            //FLib compat start
            CBipedAnimation playerAnimation = CBipedAnimation.getCurrent(entity);
            long millis = System.currentTimeMillis();

            if (playerAnimation.head.xPath.path != null) bipedHead.offsetX = (float) playerAnimation.head.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.head.yPath.path != null) bipedHead.offsetY = (float) playerAnimation.head.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.head.zPath.path != null) bipedHead.offsetZ = (float) playerAnimation.head.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.head.xRotPath.path != null) bipedHead.rotateAngleX = (float) Tools.posMod(playerAnimation.head.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.head.yRotPath.path != null) bipedHead.rotateAngleY = (float) Tools.posMod(playerAnimation.head.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.head.zRotPath.path != null) bipedHead.rotateAngleZ = (float) Tools.posMod(playerAnimation.head.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);

            if (playerAnimation.head.xScalePath.path != null)
            {
                headScale = new float[]{(float) playerAnimation.head.xScalePath.getRelativePosition(millis).values[0], 1, 1};
            }
            if (playerAnimation.head.yScalePath.path != null)
            {
                if (headScale == null) headScale = new float[]{1, (float) playerAnimation.head.yScalePath.getRelativePosition(millis).values[0], 1};
                else headScale[1] = (float) playerAnimation.head.yScalePath.getRelativePosition(millis).values[0];
            }
            if (playerAnimation.head.zScalePath.path != null)
            {
                if (headScale == null) headScale = new float[]{1, 1, (float) playerAnimation.head.zScalePath.getRelativePosition(millis).values[0]};
                else headScale[2] = (float) playerAnimation.head.zScalePath.getRelativePosition(millis).values[0];
            }
            if (headScale != null) GlStateManager.scale(headScale[0], headScale[1], headScale[2]);
            GL11.glTranslatef(bipedHead.offsetX, bipedHead.offsetY, bipedHead.offsetZ);
            //FLib compat end


            GL11.glColor3f(1F, 1F, 1F);
            GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
            GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleY), 0, 1, 0);
            GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleX), 1, 0, 0);

            renderHead(new SkinPartRenderData(skinPart, renderData));

            GL11.glPopMatrix();
        }
        GlStateManager.popAttrib();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void renderHead(SkinPartRenderData renderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(renderData);
        GL11.glPopMatrix();
    }
}
