package Flokscha.playground.client.gui.renderer;

import Flokscha.playground.Entity.MyEntitiyVillager;
import Flokscha.playground.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
    private final MyEntitiyVillager theMerchant;
    private final InventoryMerchant merchantInventory;
    private final World world;
    private final Slot slot1;
    private final Slot slot2;
    private final SlotMerchantResult resultSlot;
    private final InventoryPlayer playerInventory;

    public ContainerMerchant(InventoryPlayer inventoryPlayer, MyEntitiyVillager merchant, World world) {
        this.theMerchant = merchant;
        this.world = world;
        this.playerInventory = inventoryPlayer;
        this.merchantInventory = new InventoryMerchant(inventoryPlayer.player, merchant);
        this.slot1 = new Slot(this.merchantInventory, 0, 36, 53);
//        this.slot1.();
//        this.addSlotToContainer(slot1);
        this.slot2 = new Slot(this.merchantInventory, 1, 62, 53);
//        this.slot2.
//        this.addSlotToContainer(slot2);
        int xPos = 84;
        int yPos = 25;
        int guiOffset = 18;
        this.resultSlot = new SlotMerchantResult(inventoryPlayer.player, merchant, this.merchantInventory, 2, xPos, yPos);
//        this.addSlotToContainer(resultSlot);

        //Player Inventory Slots
        int rows;
        for(rows = 0; rows < 3; ++rows) {
            for(int cols = 0; cols < 9; ++cols) {
                this.addSlotToContainer(new Slot(inventoryPlayer, cols + rows * 9 + 9, 8 + cols * 18, 84 + rows * 18 + guiOffset));
            }
        }

        for(rows = 0; rows < 9; ++rows) {
            this.addSlotToContainer(new Slot(inventoryPlayer, rows, 8 + rows * 18, 142 + guiOffset));
        }

    }

    public InventoryMerchant getMerchantInventory() {
        return this.merchantInventory;
    }

    public void onCraftMatrixChanged(IInventory p_onCraftMatrixChanged_1_) {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(p_onCraftMatrixChanged_1_);
    }

    public void onBuyButton(MerchantRecipe MR){
        this.detectAndSendChanges();
        this.theMerchant.finishUpBuy(MR);
    }

    public boolean canInteractWith(EntityPlayer p_canInteractWith_1_) {
        return this.theMerchant.getCustomer() == p_canInteractWith_1_;
    }

    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int i) {
        ItemStack slotItemStackCopy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(i);
        if(slot != null && slot.getHasStack()) {
            ItemStack slotItemStack = slot.getStack();
            slotItemStackCopy = slotItemStack.copy();
            if(i == 2) {
                // ""
                if(!this.mergeItemStack(slotItemStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(slotItemStack, slotItemStackCopy);
            } else if(i != 0 && i != 1) {
                if(i >= 3 && i < 30) {
                    // Outof bounds wenn weniger Slots es gibt.
                    if(!this.mergeItemStack(slotItemStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if(i >= 30 && i < 39 && !this.mergeItemStack(slotItemStack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
                // ""
            } else if(!this.mergeItemStack(slotItemStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if(slotItemStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if(slotItemStack.getCount() == slotItemStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(entityPlayer, slotItemStack);
        }

        return slotItemStackCopy;
    }

    public void onContainerClosed(EntityPlayer p_onContainerClosed_1_) {
        super.onContainerClosed(p_onContainerClosed_1_);
        this.theMerchant.setCustomer(null);
        super.onContainerClosed(p_onContainerClosed_1_);
        if(!this.world.isRemote) {
            ItemStack lvt_2_1_ = this.merchantInventory.removeStackFromSlot(0);
            if(!lvt_2_1_.isEmpty()) {
                p_onContainerClosed_1_.dropItem(lvt_2_1_, false);
            }

            lvt_2_1_ = this.merchantInventory.removeStackFromSlot(1);
            if(!lvt_2_1_.isEmpty()) {
                p_onContainerClosed_1_.dropItem(lvt_2_1_, false);
            }

        }
    }
}
