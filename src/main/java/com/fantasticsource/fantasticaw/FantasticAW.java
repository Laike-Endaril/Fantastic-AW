package com.fantasticsource.fantasticaw;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FantasticAW.MODID, name = FantasticAW.NAME, version = FantasticAW.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044zzzr,);required-after:armourers_workshop@[1.12.2-0.51.0.659,)")
public class FantasticAW
{
    public static final String MODID = "fantasticaw";
    public static final String NAME = "Fantastic AW";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FantasticAW.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
