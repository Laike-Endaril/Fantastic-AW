package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.tools.Tools;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow;
import moe.plushie.armourers_workshop.client.model.skin.ModelTypeHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;

public class SkinLayerRendererHeldItemEdit extends LayerHeldItem
{
    public float[] leftItemScale = null, rightItemScale = null;

    public LayerHeldItem awLayer;

    public SkinLayerRendererHeldItemEdit(RenderLivingBase<?> livingEntityRendererIn, LayerHeldItem awLayer)
    {
        super(livingEntityRendererIn);
        this.awLayer = awLayer;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack leftStack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack rightStack = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

        if (!leftStack.isEmpty() || !rightStack.isEmpty())
        {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
            GlStateManager.pushMatrix();

            if (livingEntityRenderer.getMainModel().isChild)
            {
                GlStateManager.translate(0.0F, 0.75F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }


            CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entitylivingbaseIn);
            long millis = System.currentTimeMillis();
            if (playerAnimation != null && playerAnimation.handItemSwap != null && playerAnimation.handItemSwap.getRelativePosition(millis).values[0] < 0)
            {
                ForcedAWSkinOverrides.tryEnableAWSkinOverrideHack(leftStack);
                renderHeldItem(entitylivingbaseIn, leftStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability, millis);
                ForcedAWSkinOverrides.tryDisableAWSkinOverrideHack(leftStack);

                ForcedAWSkinOverrides.tryEnableAWSkinOverrideHack(rightStack);
                renderHeldItem(entitylivingbaseIn, rightStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability, millis);
                ForcedAWSkinOverrides.tryDisableAWSkinOverrideHack(rightStack);
            }
            else
            {
                ForcedAWSkinOverrides.tryEnableAWSkinOverrideHack(rightStack);
                renderHeldItem(entitylivingbaseIn, rightStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability, millis);
                ForcedAWSkinOverrides.tryDisableAWSkinOverrideHack(rightStack);

                ForcedAWSkinOverrides.tryEnableAWSkinOverrideHack(leftStack);
                renderHeldItem(entitylivingbaseIn, leftStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability, millis);
                ForcedAWSkinOverrides.tryDisableAWSkinOverrideHack(leftStack);
            }


            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, IEntitySkinCapability skinCapability, long millis)
    {
        if (!itemStack.isEmpty())
        {
            GlStateManager.pushMatrix();

            if (entityLivingBase.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            translateToHand(handSide);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);

            ISkinTypeRegistry skinTypeRegistry = SkinTypeRegistry.INSTANCE;
            ISkinType[] skinTypes = new ISkinType[]
                    {
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:sword"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:shield"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:bow"),

                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:pickaxe"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:axe"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:shovel"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:hoe"),

                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:item"),
                    };

            boolean slim = false;
            if (entityLivingBase instanceof EntityPlayer)
            {
                slim = ModelPlayerEdit.isSmallArms((ModelPlayer) livingEntityRenderer.getMainModel());
            }

            boolean didRender = false;
            for (int i = 0; i < ModAddonManager.ItemOverrideType.values().length; i++)
            {
                ModAddonManager.ItemOverrideType overrideType = ModAddonManager.ItemOverrideType.values()[i];
                if (ModAddonManager.isOverrideItem(overrideType, itemStack.getItem()))
                {
                    ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
                    if (descriptor == null) descriptor = skinCapability.getSkinDescriptor(skinTypes[i], 0);
                    if (descriptor != null)
                    {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableCull();
                        GlStateManager.scale(-1, -1, 1);
                        GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
                        if (flag)
                        {
                            GlStateManager.scale(-1, 1, 1);
                            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
                        }
                        SkinModelRenderHelper skinModelRenderHelper = SkinModelRenderHelper.INSTANCE;
                        ClientSkinCache clientSkinCache = ClientSkinCache.INSTANCE;
                        Skin skin = clientSkinCache.getSkin(descriptor);
                        if (skin != null)
                        {
                            if (overrideType != ModAddonManager.ItemOverrideType.BOW)
                            {
                                if (slim) GL11.glScaled(0.75F, 1F, 1F);
                                ModelTypeHelper model = skinModelRenderHelper.getTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, descriptor.getIdentifier().getSkinType());


                                //FLib compat start
                                CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityLivingBase);
                                if (playerAnimation != null)
                                {
                                    if (handSide == EnumHandSide.LEFT)
                                    {
                                        if (playerAnimation.leftItem.xScalePath != null)
                                        {
                                            leftItemScale = new float[]{(float) playerAnimation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.leftItem.yScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else leftItemScale[1] = (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.leftItem.zScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else leftItemScale[2] = (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        GL11.glTranslatef(playerAnimation.leftItem.xPath == null ? 0 : (float) playerAnimation.leftItem.xPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.yPath == null ? 0 : -(float) playerAnimation.leftItem.yPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.zPath == null ? 0 : (float) playerAnimation.leftItem.zPath.getRelativePosition(millis).values[0]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, -1);
                                        if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                                        if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), -1, 0, 0);
                                    }
                                    else
                                    {
                                        if (playerAnimation.rightItem.xScalePath != null)
                                        {
                                            rightItemScale = new float[]{(float) playerAnimation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.rightItem.yScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else rightItemScale[1] = (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.rightItem.zScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else rightItemScale[2] = (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        GL11.glTranslatef(playerAnimation.rightItem.xPath == null ? 0 : -(float) playerAnimation.rightItem.xPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.yPath == null ? 0 : -(float) playerAnimation.rightItem.yPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.zPath == null ? 0 : (float) playerAnimation.rightItem.zPath.getRelativePosition(millis).values[0]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                                        if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, -1, 0);
                                        if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), -1, 0, 0);
                                    }
                                }
                                //FLib compat end


                                model.render(entityLivingBase, skin, (ModelBiped) livingEntityRenderer.getMainModel(), false, descriptor.getSkinDye(), null, true, 0, true);
                            }
                            else
                            {
                                ModelSkinBow model = skinModelRenderHelper.modelBow;
                                model.frame = getAnimationFrame(entityLivingBase.getItemInUseMaxCount());


                                //FLib compat start
                                CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityLivingBase);
                                if (playerAnimation != null)
                                {
                                    if (handSide == EnumHandSide.LEFT)
                                    {
                                        if (playerAnimation.leftItem.xScalePath != null)
                                        {
                                            leftItemScale = new float[]{(float) playerAnimation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.leftItem.yScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else leftItemScale[1] = (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.leftItem.zScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else leftItemScale[2] = (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

                                        GL11.glTranslatef(playerAnimation.leftItem.xPath == null ? 0 : (float) playerAnimation.leftItem.xPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.yPath == null ? 0 : (float) playerAnimation.leftItem.yPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.zPath == null ? 0 : (float) playerAnimation.leftItem.zPath.getRelativePosition(millis).values[0]);

                                        if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                                        if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                                        if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
                                    }
                                    else
                                    {
                                        if (playerAnimation.rightItem.xScalePath != null)
                                        {
                                            rightItemScale = new float[]{(float) playerAnimation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.rightItem.yScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else rightItemScale[1] = (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.rightItem.zScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else rightItemScale[2] = (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

                                        GL11.glTranslatef(playerAnimation.rightItem.xPath == null ? 0 : (float) playerAnimation.rightItem.xPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.yPath == null ? 0 : (float) playerAnimation.rightItem.yPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.zPath == null ? 0 : (float) playerAnimation.rightItem.zPath.getRelativePosition(millis).values[0]);

                                        if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                                        if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                                        if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
                                    }
                                }
                                //FLib compat end


                                model.render(entityLivingBase, skin, false, descriptor.getSkinDye(), null, false, 0, false);
                            }
                        }

                        if (flag)
                        {
                            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                        }
                        GlStateManager.disableCull();
                        GlStateManager.popMatrix();
                        didRender = true;
                        break;
                    }
                }
            }
            if (!didRender)
            {
                //FLib compat start
                CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityLivingBase);
                if (playerAnimation != null)
                {
                    if (handSide == EnumHandSide.LEFT)
                    {
                        if (playerAnimation.leftItem.xScalePath != null)
                        {
                            leftItemScale = new float[]{(float) playerAnimation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                        }
                        if (playerAnimation.leftItem.yScalePath != null)
                        {
                            if (leftItemScale == null) leftItemScale = new float[]{1, (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                            else leftItemScale[1] = (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0];
                        }
                        if (playerAnimation.leftItem.zScalePath != null)
                        {
                            if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                            else leftItemScale[2] = (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0];
                        }
                        if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

                        GL11.glTranslatef(playerAnimation.leftItem.xPath == null ? 0 : (float) playerAnimation.leftItem.xPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.yPath == null ? 0 : (float) playerAnimation.leftItem.yPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.zPath == null ? 0 : (float) playerAnimation.leftItem.zPath.getRelativePosition(millis).values[0]);

                        if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                        if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                        if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
                    }
                    else
                    {
                        if (playerAnimation.rightItem.xScalePath != null)
                        {
                            rightItemScale = new float[]{(float) playerAnimation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                        }
                        if (playerAnimation.rightItem.yScalePath != null)
                        {
                            if (rightItemScale == null) rightItemScale = new float[]{1, (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                            else rightItemScale[1] = (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0];
                        }
                        if (playerAnimation.rightItem.zScalePath != null)
                        {
                            if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                            else rightItemScale[2] = (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0];
                        }
                        if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

                        GL11.glTranslatef(playerAnimation.rightItem.xPath == null ? 0 : (float) playerAnimation.rightItem.xPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.yPath == null ? 0 : (float) playerAnimation.rightItem.yPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.zPath == null ? 0 : (float) playerAnimation.rightItem.zPath.getRelativePosition(millis).values[0]);

                        if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                        if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                        if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
                    }
                }
                //FLib compat end


                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, itemStack, transformType, flag);
            }
            GlStateManager.popMatrix();
        }
    }

    private int getAnimationFrame(int useCount)
    {
        if (useCount >= 18) return 2;
        if (useCount > 13) return 1;
        return 0;
    }
}
