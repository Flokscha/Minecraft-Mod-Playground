package Flokscha.playground.EventHandler;

import Flokscha.playground.Entity.MyEntitiyVillager;
import Flokscha.playground.utility.LogHelper;
import Flokscha.playground.utility.VillagerUtility;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;


public class MyVillagerSpawnHandler {
    private static VillagerUtility VilUtil = new VillagerUtility();

    public static void init(){
        LogHelper.info("register Profession");

        VillagerRegistry.VillagerProfession prof = VillagerUtility.getSmith();
        VillagerRegistry.instance().register(prof);


        (new VillagerRegistry.VillagerCareer(prof, "armorer"))
                .addTrade(1,MyEntitiyVillager.getTrades("playground:armorer", 1))
                .addTrade(2,MyEntitiyVillager.getTrades("playground:armorer", 2))
                .addTrade(3,MyEntitiyVillager.getTrades("playground:armorer", 3));
        (new VillagerRegistry.VillagerCareer(prof, "blacksmith"))
                .addTrade(1,MyEntitiyVillager.getTrades("playground:blacksmith", 1))
                .addTrade(2,MyEntitiyVillager.getTrades("playground:blacksmith", 2))
                .addTrade(3,MyEntitiyVillager.getTrades("playground:blacksmith", 3))
                .addTrade(4,MyEntitiyVillager.getTrades("playground:blacksmith", 4));
        (new VillagerRegistry.VillagerCareer(prof, "toolsmith"))
                .addTrade(1,MyEntitiyVillager.getTrades("playground:toolsmith", 1))
                .addTrade(2,MyEntitiyVillager.getTrades("playground:toolsmith", 2))
                .addTrade(3,MyEntitiyVillager.getTrades("playground:toolsmith", 3))
                .addTrade(4,MyEntitiyVillager.getTrades("playground:toolsmith", 4));
        // MOd compatiblity
        LogHelper.info("register Profession Complete");
    }


    @SubscribeEvent
    public void villagerEvent(LivingSpawnEvent event){
//        if (event.getEntity() instanceof EntityVillager){
            //EntityVillager nV = new EntityVillager(event.getWorld(),0);
            //nV.setPositionAndRotation(event.getEntity().posX,event.getEntity().posY,event.getEntity().posZ, event.getEntity().rotationYaw, event.getEntity().rotationPitch);
            //((EntityVillager) event.getEntity()).setProfession(VilUtil.getSmith());
            //event.getWorld().spawnEntity(nV);
            //event.getEntity().setDead();
//        }
    }
}
