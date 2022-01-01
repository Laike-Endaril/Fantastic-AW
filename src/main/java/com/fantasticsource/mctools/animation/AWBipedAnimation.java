package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.tools.ReflectionTool;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererHeldItem;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.List;

public class AWBipedAnimation
{
    public static final Field
            SKIN_MODEL_RENDER_HELPER_MODEL_HEAD_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelHead"),
            SKIN_MODEL_RENDER_HELPER_MODEL_CHEST_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelChest"),
            SKIN_MODEL_RENDER_HELPER_MODEL_LEGS_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelLegs"),
            SKIN_MODEL_RENDER_HELPER_MODEL_FEET_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelFeet"),
            SKIN_MODEL_RENDER_HELPER_MODEL_WINGS_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelWings"),
            SKIN_MODEL_RENDER_HELPER_MODEL_OUTFIT_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelOutfit");


    private AWBipedAnimation()
    {
    }


    public static void init(FMLPostInitializationEvent event)
    {
        CBipedAnimation.init(event);

        ClientTickTimer.schedule(2, () ->
        {
            MinecraftForge.EVENT_BUS.register(AWBipedAnimation.class);

            SkinModelRenderHelper skinModelRenderHelper = SkinModelRenderHelper.INSTANCE;

            ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_HEAD_FIELD, skinModelRenderHelper, new ModelSkinHeadEdit());
            skinModelRenderHelper.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinHead, skinModelRenderHelper.modelHead);

            ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_CHEST_FIELD, skinModelRenderHelper, new ModelSkinChestEdit());
            skinModelRenderHelper.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinChest, skinModelRenderHelper.modelChest);

            ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_LEGS_FIELD, skinModelRenderHelper, new ModelSkinLegsEdit());
            skinModelRenderHelper.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinLegs, skinModelRenderHelper.modelLegs);

            ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_FEET_FIELD, skinModelRenderHelper, new ModelSkinFeetEdit());
            skinModelRenderHelper.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinFeet, skinModelRenderHelper.modelFeet);

            ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_WINGS_FIELD, skinModelRenderHelper, new ModelSkinWingsEdit());
            skinModelRenderHelper.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinWings, skinModelRenderHelper.modelWings);

            ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_OUTFIT_FIELD, skinModelRenderHelper, new ModelSkinOutfitEdit());
            skinModelRenderHelper.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinOutfit, skinModelRenderHelper.modelOutfit);
        });
    }

    @SubscribeEvent
    public static void renderLivingBase(RenderLivingEvent.Pre event)
    {
        List<LayerRenderer> layers = (List<LayerRenderer>) ReflectionTool.get(CBipedAnimation.RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD, event.getRenderer());
        for (int i = 0; i < layers.size(); i++)
        {
            LayerRenderer layer = layers.get(i);
            if (layer.getClass() == SkinLayerRendererHeldItem.class)
            {
                layers.set(i, new SkinLayerRendererHeldItemEdit((RenderLivingBase<?>) ReflectionTool.get(CBipedAnimation.LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD, layer), (LayerHeldItem) layer));
            }
        }
    }
}
