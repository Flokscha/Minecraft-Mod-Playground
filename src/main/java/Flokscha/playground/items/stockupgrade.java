package Flokscha.playground.items;


import Flokscha.playground.Entity.MyEntitiyVillager;
import Flokscha.playground.ExampleMod;
import Flokscha.playground.utility.LogHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class stockupgrade extends Item{
    private String name = "stockupgrade";
    public stockupgrade() {
        setRegistryName(name);        // The unique name (within your mod) that identifies this items
        setUnlocalizedName(ExampleMod.MODID + "."+name);     // Used for localization (en_US.lang)
        setCreativeTab(CreativeTabs.TOOLS);
        GameRegistry.register(this);
    }
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target instanceof MyEntitiyVillager){
            MyEntitiyVillager merchant = (MyEntitiyVillager)target;
            if (!merchant.MaxReached){
                merchant.populateBuyingList();
                merchant.soundAndExp();
                stack.shrink(1);
            }
            return true;
        } else
            return false;
    }
}
