package Flokscha.playground.proxy;


import Flokscha.playground.items.ModItems;
import Flokscha.playground.utility.LogHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        LogHelper.info(e.description() + "Client Proxy");

//        OBJLoader.INSTANCE.addDomain(ExampleMod.MODID);
//        ModelLoaderRegistry.registerLoader(new BakedModelLoader());

        // Typically initialization of models and such goes here:
        //ModBlocks.initModels();
        ModItems.initModels();
    }

    @Override
    public void init(FMLInitializationEvent e){
        super.init(e);
        LogHelper.info(e.description() + "Client Proxy");

//        // Initialize our input handler so we can listen to keys
//        MinecraftForge.EVENT_BUS.register(new InputHandler());
//        KeyBindings.init();
//
//        ModBlocks.initItemModels();
    }
}
