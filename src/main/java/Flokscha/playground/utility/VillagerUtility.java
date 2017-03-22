package Flokscha.playground.utility;

import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class VillagerUtility {
    private static VillagerRegistry.VillagerProfession smith;

    public VillagerUtility() {
        smith = new VillagerRegistry.VillagerProfession("playground:smith",
                                                        "minecraft:textures/entity/villager/smith.png",
                                                        "minecraft:textures/entity/zombie_villager/zombie_smith.png");

    }

    public static VillagerRegistry.VillagerProfession getSmith() {
        return smith;
    }
}
