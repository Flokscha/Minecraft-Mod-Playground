package Flokscha.playground.proxy;

import Flokscha.playground.items.ModItems;
import Flokscha.playground.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class CommonProxy implements IProxy, IGuiHandler {
    public void preInit(FMLPreInitializationEvent event){
        LogHelper.info("Items Initialized");
        ModItems.init();
    }
    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        return null;
    }
    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        return null;
    }
}
