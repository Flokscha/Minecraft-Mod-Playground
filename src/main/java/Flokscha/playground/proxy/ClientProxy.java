package Flokscha.playground.proxy;


import Flokscha.playground.utility.LogHelper;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy{

    public void preInit(){
        LogHelper.info("Client Proxy PreInit");
    }
    public void init(){
        LogHelper.info("ClientProxy Init");
        MinecraftForge.EVENT_BUS.register(this);
    }
}
