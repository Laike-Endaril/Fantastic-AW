package com.fantasticsource.fantasticaw;

import com.fantasticsource.mctools.aw.RenderModes;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.fantasticaw.FantasticAW.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(RenderModesPacketHandler.class, RenderModesPacket.class, discriminator++, Side.CLIENT);
    }


    public static class RenderModesPacket implements IMessage
    {
        public LinkedHashMap<String, String> renderModes;

        public RenderModesPacket()
        {
            //Required
        }

        public RenderModesPacket(Entity entity)
        {
            renderModes = RenderModes.getRenderModes(entity);
            if (renderModes == null) renderModes = new LinkedHashMap<>();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(renderModes.size());
            for (Map.Entry<String, String> entry : renderModes.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());
                ByteBufUtils.writeUTF8String(buf, entry.getValue());
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            renderModes = new LinkedHashMap<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                renderModes.put(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
            }
        }
    }

    public static class RenderModesPacketHandler implements IMessageHandler<RenderModesPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(RenderModesPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                EntityPlayer player = mc.player;
                for (Map.Entry<String, String> entry : packet.renderModes.entrySet())
                {
                    RenderModes.setRenderMode(player, entry.getKey(), entry.getValue());
                }
            });
            return null;
        }
    }
}
