package Flokscha.playground.items;


import Flokscha.playground.ExampleMod;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class VillagerStockUpgrade extends Item{
    private String name = "stockupgrade";
    public VillagerStockUpgrade() {
        setRegistryName(name);        // The unique name (within your mod) that identifies this item
        setUnlocalizedName(ExampleMod.MODID + "."+name);     // Used for localization (en_US.lang)
        GameRegistry.register(this);
//        setCreativeTab()
    }
}
