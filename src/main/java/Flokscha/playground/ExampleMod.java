package Flokscha.playground;

import Flokscha.playground.Entity.MyEntitiyVillager;
import Flokscha.playground.EventHandler.MyVillagerSpawnHandler;
import Flokscha.playground.client.gui.renderer.MyGuiHandler;
import Flokscha.playground.proxy.IProxy;
import Flokscha.playground.utility.LogHelper;
import Flokscha.playground.world.MyWorldProviderSurface;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION, useMetadata = true)
public class ExampleMod
{
    public static final String MODID = "playground";
    public static final String VERSION = "1.0";
    public DimensionType ADVENTURETIME;
    public WorldProvider ADVENTURETIME_WORLDPROVIDER;
    public World ADVENTURETIME_WORLD;
    public ResourceLocation resLoc = new ResourceLocation(MODID,"myvillager");

    public WorldServer AdventureTimeWorldServer;

    @SidedProxy(clientSide = "Flokscha.playground.proxy.ClientProxy", serverSide = "Flokscha.playground.proxy.ServerProxy", modId = MODID)
    public static IProxy proxy;
    @Mod.Instance(MODID)
    public static ExampleMod instance = new ExampleMod();


//    @SubscribeEvent
//    public void registerBlocks(RegistryEvent.Register<Block> event) {
////        event.getRegistry().registerAll(block1, block2, ...);
//    }
//    @SubscribeEvent
//    public void registerItems(RegistryEvent.Register<Item> event) {
////        event.getRegistry().registerAll(block1, block2, ...);
//    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        LogHelper.info(event.description());
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, MyGuiHandler.instance);
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LogHelper.info(event.description());
        //World OverworldWorld = DimensionManager.getWorld(0);
        DimensionManager.unregisterDimension(0);
        ADVENTURETIME = DimensionType.register("AdventureTime","_adventuretime",DimensionManager.getNextFreeDimId(), MyWorldProviderSurface.class, true);
        ADVENTURETIME_WORLDPROVIDER = ADVENTURETIME.createDimension();
        DimensionManager.registerDimension(0, ADVENTURETIME);
        MinecraftForge.EVENT_BUS.register(new MyVillagerSpawnHandler());
        MyVillagerSpawnHandler.init();
        LogHelper.info("registering MyVillager");
        EntityRegistry.registerModEntity(resLoc,MyEntitiyVillager.class,"MyVillager",77,"playground",64,4,false,170,255);

    }

    @EventHandler
    public void finished(FMLLoadCompleteEvent event){
        LogHelper.info(event.description());
    }

    @EventHandler
    public void onWorld(FMLServerStartedEvent event){

    }
    @EventHandler
    public void beforeWorld(FMLServerStartingEvent event){
        LogHelper.info(event.description());
        //LogHelper.info(DimensionManager.getProviderType(ADVENTURETIME.getId()));
        //LogHelper.info(DimensionManager.createProviderFor(ADVENTURETIME.getId()));
        //DimensionManager.initDimension(ADVENTURETIME.getId());
        //DimensionManager.setWorld(ADVENTURETIME.getId(),event.getServer().worldServerForDimension(ADVENTURETIME.getId()),event.getServer());
        World OVERWORLD = DimensionManager.getWorld(0);
        //OVERWORLD.getWorldInfo().setGameType(GameType.ADVENTURE);
    }


    /*
    boolean world.isRemote ? false -> do stuff
     */

}
