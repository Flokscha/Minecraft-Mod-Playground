package Flokscha.playground.utility;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class VillagerItem {
    public VillagerItem(){
    }

    public ItemStack addEnchantment(ItemStack itemStack, String enchantment,int level){
        itemStack.addEnchantment(Enchantment.getEnchantmentByLocation(enchantment),level);
        return itemStack;
    }
    public ItemStack addEnchantment(ItemStack itemStack, Enchantment enchantment,int level){
        itemStack.addEnchantment(enchantment,level);
        return itemStack;
    }
    private void addCompound(ItemStack itemStack,String TagName, String blockName){
        boolean flagPlace = itemStack.canPlaceOn(Block.getBlockFromName(blockName));
        boolean flagDestroy = itemStack.canDestroy(Block.getBlockFromName(blockName));
        if (flagPlace||flagDestroy) {
            LogHelper.info("already set CanPlaceOn:CanDestroy " + flagPlace + flagDestroy);
        } else {
            if (itemStack.hasTagCompound()&&itemStack.getTagCompound().hasKey(TagName)){
                NBTTagCompound tagCompund = itemStack.getTagCompound();
                NBTTagList tagList = tagCompund.getTagList(TagName,tagCompund.getTag(TagName).getId()-1);
                NBTTagString tagCompound = new NBTTagString(blockName);
                tagList.appendTag(tagCompound);
            } else {
                NBTTagString tagCompound = new NBTTagString(blockName);
                NBTTagList tagList = new NBTTagList();
                tagList.appendTag(tagCompound);
                itemStack.setTagInfo(TagName,tagList);
            }
        }
    }
    private void addCompoundByBlock(ItemStack itemStack,String TagName, Block block){
        boolean flagPlace = itemStack.canPlaceOn(block);
        boolean flagDestroy = itemStack.canDestroy(block);
        if (flagPlace||flagDestroy) {
            LogHelper.info("already set CanPlaceOn:CanDestroy " + flagPlace + flagDestroy);
        } else {
            if (itemStack.hasTagCompound()&&itemStack.getTagCompound().hasKey(TagName)){
                NBTTagCompound tagCompund = itemStack.getTagCompound();
                NBTTagList tagList = tagCompund.getTagList(TagName,tagCompund.getTag(TagName).getId()-1);
                NBTTagString tagCompound = new NBTTagString(block.getRegistryName().getResourcePath());
                tagList.appendTag(tagCompound);
            } else {
                NBTTagString tagCompound = new NBTTagString(block.getRegistryName().getResourcePath());
                NBTTagList tagList = new NBTTagList();
                tagList.appendTag(tagCompound);
                itemStack.setTagInfo(TagName,tagList);
            }
        }
    }
    public void destroys(ItemStack itemStack, String blockName){
        addCompound(itemStack,"CanDestroy",blockName);
    }
    public void destroys(ItemStack itemStack, Block block){
        addCompoundByBlock(itemStack,"CanDestroy",block);
    }
    public void destroys(ItemStack itemStack, String ...BlockNames){
        for (String blockName:BlockNames){
            addCompound(itemStack,"CanDestroy",blockName);
        }
    }
    public void destroys(ItemStack itemStack, Block ...Blocks){
        for (Block blockName:Blocks){
            addCompoundByBlock(itemStack,"CanDestroy",blockName);
        }
    }
    public void interactsWith(ItemStack itemStack, String blockName){
        addCompound(itemStack,"CanPlaceOn",blockName);
    }
    public void interactsWith(ItemStack itemStack, Block block){
        addCompoundByBlock(itemStack,"CanPlaceOn",block);
    }
    public void interactsWith(ItemStack itemStack, String ...BlockNames){
        for (String blockName:BlockNames){
            addCompound(itemStack,"CanPlaceOn",blockName);
        }
    }
    public void interactsWith(ItemStack itemStack, Block ...Blocks){
        for (Block block:Blocks){
            addCompoundByBlock(itemStack,"CanPlaceOn",block);
        }
    }
}
