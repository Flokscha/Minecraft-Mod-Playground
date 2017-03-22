package Flokscha.playground.Entity;


import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

@Deprecated
public class MyEntitiyVillagerCopy extends EntityVillager implements INpc, IMerchant{
    private final InventoryBasic villagerInventory;
    private MerchantRecipeList buyingList;
    private int careerId;
    private int careerLevel;

    public MyEntitiyVillagerCopy(World p_i1747_1_) {
        this(p_i1747_1_, 0);
    }

    public MyEntitiyVillagerCopy(World p_i1748_1_, int p_i1748_2_) {
        super(p_i1748_1_);
        this.villagerInventory = new InventoryBasic("Items", false, 8);
        this.setProfession(p_i1748_2_);
        this.setSize(0.6F, 1.95F);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);
    }

    public boolean processInteract(EntityPlayer p_processInteract_1_, EnumHand p_processInteract_2_) {
        ItemStack itemstack = p_processInteract_1_.getHeldItem(p_processInteract_2_);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        if(flag) {
            itemstack.interactWithEntity(p_processInteract_1_, this, p_processInteract_2_);
            return true;
        } else if(!this.holdingSpawnEggOfClass(itemstack, this.getClass()) && this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
            if(this.buyingList == null) {
                this.populateBuyingList();
            }

            if(p_processInteract_2_ == EnumHand.MAIN_HAND) {
                p_processInteract_1_.addStat(StatList.TALKED_TO_VILLAGER);
            }

            if(!this.world.isRemote && !this.buyingList.isEmpty()) {
                this.setCustomer(p_processInteract_1_);
                // implementing my OWN Villager Gui

                //FIXME uncomment
                //MyGuiHandler.openGuiForVillager(p_processInteract_1_,this ,ExampleMod.instance,777,p_processInteract_1_.getEntityWorld(),this.getPos().getX(),this.getPos().getY(),this.getPos().getZ());
                //opens Vanilla Gui
                //p_processInteract_1_.displayVillagerTradeGui(this);
            } else if(this.buyingList.isEmpty()) {
                return super.processInteract(p_processInteract_1_, p_processInteract_2_);
            }

            return true;
        } else {
            return super.processInteract(p_processInteract_1_, p_processInteract_2_);
        }
    }

    private void populateBuyingList() {
        if(this.careerId != 0 && this.careerLevel != 0) {
            ++this.careerLevel;
        } else {
            this.careerId = this.getProfessionForge().getRandomCareer(this.rand) + 1;
            this.careerLevel = 1;
        }

        if(this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        int i = this.careerId - 1;
        int j = this.careerLevel - 1;
        List trades = this.getProfessionForge().getCareer(i).getTrades(j);
        if(trades != null) {
            Iterator var4 = trades.iterator();

            while(var4.hasNext()) {
                ITradeList entityvillager$itradelist = (ITradeList)var4.next();
                entityvillager$itradelist.addMerchantRecipe(this, this.buyingList, this.rand);
            }
        }

    }
}
