package Flokscha.playground.client.gui.renderer;

import Flokscha.playground.Entity.MyEntitiyVillager;
import Flokscha.playground.ExampleMod;
import Flokscha.playground.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MyGuiHandler implements IGuiHandler {
    public static MyGuiHandler instance = new MyGuiHandler();
    private static MyEntitiyVillager lastMerchantInteracted;
    public static void openGuiForVillager(@Nonnull EntityPlayer player, MyEntitiyVillager merchant, @Nonnull Object mod, int modGuiId, World world, int x, int y, int z){
        MyGuiHandler.setLastMerchantInteracted(merchant);
        BlockPos pos = merchant.getPos();
        merchant.getCustomer().openGui(ExampleMod.instance, modGuiId,merchant.getWorld(), pos.getX(), pos.getY(),pos.getZ());
    }

    private static MyEntitiyVillager getLastMerchantInteracted() {
        return lastMerchantInteracted;
    }

    public static void setLastMerchantInteracted(MyEntitiyVillager lastMerchantInteracted) {
        MyGuiHandler.lastMerchantInteracted = lastMerchantInteracted;
    }

    public static void onBuyButton(MerchantRecipe MR) {
        List<Integer> emeraldSlots = new ArrayList<Integer>(0);
        //checks for enough Items in the player inventory, even loose stacks.
        if (MR != null) {
            InventoryPlayer inventory = lastMerchantInteracted.getCustomer().inventory;
            for (int i = 0; i <= inventory.getSizeInventory(); i++) {
                if (inventory.getStackInSlot(i).getItem().equals(MR.getItemToBuy().getItem())) {
                    emeraldSlots.add(i);
                }
            }
            boolean searchIndividuals = true;
            for (int j : emeraldSlots) {
                // alle emeralds stacks die ich habe auf größe überprüfen
                //IF auch nur ein emerald stack groß genug ist um es zu kaufen nutze diesen Stack
                if (inventory.getStackInSlot(j).getCount() >= MR.getItemToBuy().getCount()) {
                    ItemStack IS = inventory.getStackInSlot(j);
                    IS.shrink(MR.getItemToBuy().getCount());
                    inventory.setInventorySlotContents(j, IS);
                    inventory.markDirty();
                    lastMerchantInteracted.getVillagerInventory().markDirty();
                    searchIndividuals = false;
                    break;
                }
            }
            //Wird nur gesucht wenn kein stack groß genug war
            if (searchIndividuals) {
                int toPay = MR.getItemToBuy().getCount();
                for (int j : emeraldSlots) {
                    //Else errechne die Differenz und ziehedas nötige vom stack ab.
                    int canPay = inventory.getStackInSlot(j).getCount();
                    ItemStack IS = inventory.getStackInSlot(j);
                    IS.shrink(canPay);
                    inventory.setInventorySlotContents(j, IS);
                    inventory.markDirty();
                    lastMerchantInteracted.getVillagerInventory().markDirty();
                    toPay -= canPay;
                    if (toPay <= 0) {
                        break;
                    }
                }
            }
            inventory.addItemStackToInventory(MR.getItemToSell().copy());
            // DEBUG vanilla buy.
            lastMerchantInteracted.useRecipe(MR);
        }
    }




    @Nullable
    @Override
    public Object getServerGuiElement(int GuiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (GuiID == 777){
            LogHelper.info("Server sided Gui Villager");
//            return new MyGuiMerchant(entityPlayer.inventory,MyGuiHandler.getLastMerchantInteracted(),entityPlayer.getEntityWorld());
            return new ContainerMerchant(entityPlayer.inventory,MyGuiHandler.getLastMerchantInteracted(),lastMerchantInteracted.getWorld());
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int GuiID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (GuiID == 777){
            LogHelper.info("Client sided Gui Villager");
            return new MyGuiMerchant(entityPlayer.inventory,MyGuiHandler.getLastMerchantInteracted(),entityPlayer.getEntityWorld());
        }
        return null;
    }
}
