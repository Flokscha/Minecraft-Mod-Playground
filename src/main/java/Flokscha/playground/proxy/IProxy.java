package Flokscha.playground.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
    void init(FMLInitializationEvent event);
    void preInit(FMLPreInitializationEvent event);
}
