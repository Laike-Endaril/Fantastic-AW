package com.fantasticsource.fantasticaw.apinatives;

import com.fantasticsource.fantasticaw.api.IFantasticAWNatives;
import com.fantasticsource.mctools.aw.RenderModes;
import net.minecraft.entity.Entity;

public class FantasticAWAPI implements IFantasticAWNatives
{
    public static FantasticAWAPI NATIVES = new FantasticAWAPI();

    @Override
    public void refreshRenderModes(Entity entity)
    {
        RenderModes.refresh(entity);
    }
}
