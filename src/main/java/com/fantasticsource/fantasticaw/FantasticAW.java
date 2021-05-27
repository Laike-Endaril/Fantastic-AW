package com.fantasticsource.fantasticaw;

import com.fantasticsource.mctools.animation.AWBipedAnimation;
import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.mctools.aw.TransientAWSkinHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FantasticAW.MODID, name = FantasticAW.NAME, version = FantasticAW.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044zzzzg,);required-after:armourers_workshop@[1.12.2-0.51.0.659,)")
public class FantasticAW
{
    public static final String MODID = "fantasticaw";
    public static final String NAME = "Fantastic AW";
    public static final String VERSION = "1.12.2.000d";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(FantasticAW.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) throws IllegalAccessException
    {
        RenderModes.init();

        MinecraftForge.EVENT_BUS.register(TransientAWSkinHandler.class);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) ForcedAWSkinOverrides.clientInit();

        if (event.getSide() == Side.CLIENT) AWBipedAnimation.init(event);
    }

    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new Commands());
    }
}
