package Flokscha.playground.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
    void init();
    void preInit(FMLPreInitializationEvent event);
}
