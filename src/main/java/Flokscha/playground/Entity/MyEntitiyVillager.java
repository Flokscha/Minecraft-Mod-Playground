package Flokscha.playground.Entity;


import Flokscha.playground.ExampleMod;
import Flokscha.playground.client.gui.renderer.MyGuiHandler;
import Flokscha.playground.items.ModItems;
import Flokscha.playground.utility.LogHelper;
import Flokscha.playground.utility.VillagerItem;
import Flokscha.playground.utility.VillagerUtility;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class MyEntitiyVillager extends EntityVillager implements INpc, IMerchant{
    private static final DataParameter<Integer> PROFESSION;
    private final InventoryBasic villagerInventory;
    private MerchantRecipeList buyingList;
    private int careerId;
    private int careerLevel;
    private VillagerRegistry.VillagerProfession prof;
    private VillagerUtility VilUtil = new VillagerUtility();
    private int wealth;
    public boolean MaxReached = false;

    public MyEntitiyVillager(World p_i1747_1_) {
        this(p_i1747_1_, 0);
    }

    public MyEntitiyVillager(World p_i1748_1_, int p_i1748_2_) {
        super(p_i1748_1_);
        this.villagerInventory = new InventoryBasic("Items", false, 8);
    }
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(PROFESSION, Integer.valueOf(0));
    }

    public void setProfession(VillagerRegistry.VillagerProfession profession) {
        // jede nicht meine Profession in meine Professions wandeln
        if (!profession.getRegistryName().equals(VillagerUtility.getSmith().getRegistryName()) ){
//            LogHelper.info("Registred Profession" + profession.getRegistryName());
            profession = VillagerRegistry.instance().getRegistry().getValue(new ResourceLocation("playground:smith"));
        }
//        this.prof = profession;
        super.setProfession(profession);
//        this.setProfession(VillagerRegistry.getId(profession));
    }

    public boolean processInteract(EntityPlayer p_processInteract_1_, EnumHand p_processInteract_2_) {
        ItemStack itemstack = p_processInteract_1_.getHeldItem(p_processInteract_2_);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        boolean isStockUpgrade = itemstack.getItem() == ModItems.stockupgrade;
        if(flag) {
            itemstack.interactWithEntity(p_processInteract_1_, this, p_processInteract_2_);
            return true;
        } else if (isStockUpgrade && !this.isChild() && this.buyingList != null && !this.isTrading()){
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
                MyGuiHandler.openGuiForVillager(p_processInteract_1_,this ,ExampleMod.instance,777,p_processInteract_1_.getEntityWorld(),this.getPos().getX(),this.getPos().getY(),this.getPos().getZ());
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

    public void populateBuyingList() {
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
        } else {
            this.MaxReached = true;
        }
//        LogHelper.info(this.getProfessionForge().getRegistryName() );
//        LogHelper.info(this.getProfessionForge().getCareer(this.careerId).getName() );
    }

    public void finishUpBuy(MerchantRecipe Mr) {
        //debug careerLevel increase
        // infinite Uses of recipes
        //p_useRecipe_1_.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());

//        if(Mr.getItemToBuy().getItem() == Items.EMERALD) {
//            this.wealth += Mr.getItemToBuy().getCount();
//        }

        if(Mr.getRewardsExp()) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, 5));
        }
    }
    public void soundAndExp(){
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, 15));
    }

    static {
        PROFESSION = EntityDataManager.createKey(MyEntitiyVillager.class, DataSerializers.VARINT);
    }


    public static ITradeList[] getTrades(String career,int level) {
        EntityVillager.ITradeList[] trades = null;
        VillagerItem villagerItem = new VillagerItem();
        int preis = 1;
        int preisGold = 1;
        if (career == "playground:armorer"){
            switch (level){
                case 1:
                    // Preise der Waren LEATHER ARMOR 4 emeralds
                    preis = 4;
                    itemForEmeralds LEATHER_HELMET = new itemForEmeralds(Items.LEATHER_HELMET,preis);
                    itemForEmeralds LEATHER_CHESTPLATE = new itemForEmeralds(Items.LEATHER_CHESTPLATE,preis);
                    itemForEmeralds LEATHER_LEGGINGS = new itemForEmeralds(Items.LEATHER_LEGGINGS,preis);
                    itemForEmeralds LEATHER_BOOTS = new itemForEmeralds(Items.LEATHER_BOOTS,preis);

                    trades = new EntityVillager.ITradeList[]{
                            LEATHER_HELMET,
                            LEATHER_CHESTPLATE,
                            LEATHER_LEGGINGS,
                            LEATHER_BOOTS
                    };
                    break;
                case 2:
                    // Preise der Waren IRON ARMOR 10 emeralds
                    preis = 10;
                    itemForEmeralds IRON_HELMET = new itemForEmeralds(Items.IRON_HELMET, preis);
                    itemForEmeralds IRON_CHESTPLATE = new itemForEmeralds(Items.IRON_CHESTPLATE, preis);
                    itemForEmeralds IRON_LEGGINGS = new itemForEmeralds(Items.IRON_LEGGINGS, preis);
                    itemForEmeralds IRON_BOOTS = new itemForEmeralds(Items.IRON_BOOTS, preis);
                    trades = new EntityVillager.ITradeList[]{
                            IRON_HELMET,
                            IRON_CHESTPLATE,
                            IRON_LEGGINGS,
                            IRON_BOOTS
                    };
                    break;
                case 3:
                    // Preise der Waren IRON ARMOR 10 emeralds
                    preis = 14;
                    itemForEmeralds GOLDEN_HELMET = new itemForEmeralds(Items.GOLDEN_HELMET, preis);
                    villagerItem.addEnchantment(GOLDEN_HELMET.itemToBuy,"protection",1);
                    itemForEmeralds GOLDEN_CHESTPLATE = new itemForEmeralds(Items.GOLDEN_CHESTPLATE, preis);
                    villagerItem.addEnchantment(GOLDEN_CHESTPLATE.itemToBuy,"projectile_protection",1);
                    itemForEmeralds GOLDEN_LEGGINGS = new itemForEmeralds(Items.GOLDEN_LEGGINGS, preis);
                    villagerItem.addEnchantment(GOLDEN_LEGGINGS.itemToBuy,"blast_protection",1);
                    itemForEmeralds GOLDEN_BOOTS = new itemForEmeralds(Items.GOLDEN_BOOTS, preis);
                    villagerItem.addEnchantment(GOLDEN_BOOTS.itemToBuy,"feather_falling",1);
                    trades = new EntityVillager.ITradeList[]{
                            GOLDEN_HELMET,
                            GOLDEN_CHESTPLATE,
                            GOLDEN_LEGGINGS,
                            GOLDEN_BOOTS
                    };
                    break;
                case 4:
                    // Preise der Waren IRON ARMOR 10 emeralds
                    preis = 22;
                    itemForEmeralds diamondHelmet = new itemForEmeralds(Items.DIAMOND_HELMET, preis);
                    itemForEmeralds diamondChestplate = new itemForEmeralds(Items.DIAMOND_CHESTPLATE, preis);
                    itemForEmeralds diamondLeggings = new itemForEmeralds(Items.DIAMOND_LEGGINGS, preis);
                    itemForEmeralds diamondBoots = new itemForEmeralds(Items.DIAMOND_BOOTS, preis);
                    trades = new EntityVillager.ITradeList[]{
                            diamondHelmet,
                            diamondChestplate,
                            diamondLeggings,
                            diamondBoots
                    };
                    break;
            }
        } else if (career == "playground:blacksmith"){
            switch (level){
                case 1:
                    preis = 2;
                    itemForEmeralds woodenAxe = new itemForEmeralds(Items.WOODEN_AXE.setMaxDamage(64), preis);
                    itemForEmeralds woodensword = new itemForEmeralds(Items.WOODEN_SWORD.setMaxDamage(64), preis);
                    trades = new EntityVillager.ITradeList[]{
                            woodenAxe,
                            woodensword
                    };
                    break;
                case 2:
                    preis = 5;
                    itemForEmeralds stoneAxe = new itemForEmeralds(Items.STONE_AXE.setMaxDamage(80), preis);
                    itemForEmeralds stoneSword = new itemForEmeralds(Items.STONE_SWORD.setMaxDamage(80), preis);
                    itemForEmeralds weakShield = new itemForEmeralds(Items.SHIELD.setMaxDamage(32), 7);
                    weakShield.itemToBuy.setStackDisplayName("Weak Shield");
                    itemForEmeralds goldenAxe = new itemForEmeralds(Items.GOLDEN_AXE.setMaxDamage(64), preis);
                    villagerItem.addEnchantment(goldenAxe.itemToBuy, Enchantments.SHARPNESS,2);
                    itemForEmeralds goldenSword = new itemForEmeralds(Items.GOLDEN_SWORD.setMaxDamage(64), preis);
                    villagerItem.addEnchantment(goldenSword.itemToBuy, "sweeping",2);
                    villagerItem.addEnchantment(goldenSword.itemToBuy, Enchantments.SHARPNESS,1);
                    trades = new EntityVillager.ITradeList[]{
                            stoneAxe,
                            stoneSword,
                            weakShield,
                            goldenAxe,
                            goldenSword
                    };
                    break;
                case 3:
                    preis = 12;
                    itemForEmeralds ironAxe = new itemForEmeralds(Items.IRON_AXE.setMaxDamage(128), preis);
                    itemForEmeralds ironSword = new itemForEmeralds(Items.IRON_SWORD.setMaxDamage(128), preis);
                    itemForEmeralds shield = new itemForEmeralds(Items.SHIELD.setMaxDamage(96), 15);
                    trades = new EntityVillager.ITradeList[]{
                            ironAxe,
                            ironSword,
                            shield
                    };
                    break;
                case 4:
                    preis = 26;
                    itemForEmeralds diaAxe = new itemForEmeralds(Items.DIAMOND_AXE.setMaxDamage(256), preis);
                    itemForEmeralds diaSword = new itemForEmeralds(Items.DIAMOND_SWORD.setMaxDamage(256), preis);
                    itemForEmeralds hardenedShield = new itemForEmeralds(Items.SHIELD.setMaxDamage(192), 20);
                    hardenedShield.itemToBuy.setStackDisplayName("Hardened Shield");
                    trades = new EntityVillager.ITradeList[]{
                            diaAxe,
                            diaSword,
                            hardenedShield
                    };
                    break;
            }
        } else if (career == "playground:toolsmith") {
            switch (level) {
                case 1:
                    preis = 2;
                    itemForEmeralds woodPick = new itemForEmeralds(Items.WOODEN_PICKAXE.setMaxDamage(64), preis);
                    villagerItem.destroys(woodPick.itemToBuy,"coal_ore");
                    itemForEmeralds woodAxe = new itemForEmeralds(Items.WOODEN_AXE.setMaxDamage(64), preis);
                    villagerItem.destroys(woodAxe.itemToBuy,"log","log2");
                    itemForEmeralds woodShov = new itemForEmeralds(Items.WOODEN_SHOVEL.setMaxDamage(64), preis);
                    //TODO einen Bereich festlege WO man das Item nutzen darf in zusammenschluss mit was. -> OnItemUse
//                    villagerItem.destroys(woodShov.itemToBuy,"farmland");
                    itemForEmeralds woodHoe = new itemForEmeralds(Items.WOODEN_HOE.setMaxDamage(64), preis);
                    villagerItem.destroys(woodHoe.itemToBuy,Blocks.WHEAT);
                    villagerItem.interactsWith(woodHoe.itemToBuy,"dirt");
                    villagerItem.interactsWith(woodHoe.itemToBuy,"grass");
                    villagerItem.interactsWith(woodHoe.itemToBuy,"farmland");
                    // can this be used on dirt in adventure mode?
//                    woodHoe.itemToBuy.canEditBlocks();
                    trades = new EntityVillager.ITradeList[]{
                            woodPick,
                            woodAxe,
                            woodShov,
                            woodHoe
                    };
                    break;
                case 2:
                    preis = 5;
                    preisGold = 7;
                    itemForEmeralds stoPick = new itemForEmeralds(Items.STONE_PICKAXE.setMaxDamage(128), preis);
                    villagerItem.destroys(stoPick.itemToBuy,"coal_ore","iron_ore");
                    itemForEmeralds stoAxe = new itemForEmeralds(Items.STONE_AXE.setMaxDamage(64), preis);
                    villagerItem.destroys(stoAxe.itemToBuy,"log","log2");
                    itemForEmeralds stoShov = new itemForEmeralds(Items.STONE_SHOVEL.setMaxDamage(128), preis);
                    itemForEmeralds stoHoe = new itemForEmeralds(Items.STONE_HOE.setMaxDamage(128), preis);
                    villagerItem.destroys(stoHoe.itemToBuy,Blocks.WHEAT,Blocks.CARROTS,Blocks.POTATOES);
                    villagerItem.interactsWith(stoHoe.itemToBuy,"dirt");
                    villagerItem.interactsWith(stoHoe.itemToBuy,"grass");
                    villagerItem.interactsWith(stoHoe.itemToBuy,"farmland");
                    //GOLDEN
                    itemForEmeralds golPic = new itemForEmeralds(Items.GOLDEN_PICKAXE.setMaxDamage(128), preisGold);
                    villagerItem.addEnchantment(golPic.itemToBuy,Enchantments.FORTUNE,1);
                    villagerItem.addEnchantment(golPic.itemToBuy,Enchantments.UNBREAKING,1);
                    villagerItem.destroys(golPic.itemToBuy,"coal_ore","iron_ore");
                    itemForEmeralds goldAxe = new itemForEmeralds(Items.GOLDEN_AXE.setMaxDamage(64), preisGold);
                    villagerItem.addEnchantment(goldAxe.itemToBuy,Enchantments.EFFICIENCY,1);
                    villagerItem.destroys(goldAxe.itemToBuy,"log","log2");
                    itemForEmeralds goldSho = new itemForEmeralds(Items.GOLDEN_SHOVEL.setMaxDamage(128), preisGold);
                    itemForEmeralds goldHoe = new itemForEmeralds(Items.GOLDEN_HOE.setMaxDamage(128), preisGold);
                    villagerItem.addEnchantment(goldHoe.itemToBuy,Enchantments.UNBREAKING,2);
                    villagerItem.destroys(goldHoe.itemToBuy,Blocks.WHEAT,Blocks.CARROTS,Blocks.POTATOES);
                    villagerItem.interactsWith(goldHoe.itemToBuy,"dirt","grass","farmland");
                    trades = new EntityVillager.ITradeList[]{
                            stoPick,
                            stoAxe,
                            stoShov,
                            stoHoe,
                            //Golden stuff sollte enchanted sein
                            golPic,
                            goldAxe,
                            goldSho,
                            goldHoe
                    };
                    break;
                case 3:
                    preis = 12;
                    itemForEmeralds iroPic = new itemForEmeralds(Items.IRON_PICKAXE.setMaxDamage(256), preis);
                    villagerItem.destroys(iroPic.itemToBuy,"coal_ore","iron_ore","gold_ore","quartz_ore","lapis_ore","redstone_ore","lit_redstone_ore");
                    itemForEmeralds iroAxe = new itemForEmeralds(Items.IRON_AXE.setMaxDamage(128), preis);
                    villagerItem.destroys(iroAxe.itemToBuy,"log","log2");
                    itemForEmeralds iroSho = new itemForEmeralds(Items.IRON_SHOVEL.setMaxDamage(256), preis);
                    itemForEmeralds iroHoe = new itemForEmeralds(Items.IRON_HOE.setMaxDamage(256), preis);
                    villagerItem.destroys(iroHoe.itemToBuy,Blocks.WHEAT,Blocks.CARROTS,Blocks.POTATOES,Blocks.BEETROOTS,Blocks.MELON_BLOCK,Blocks.PUMPKIN);
                    villagerItem.interactsWith(iroHoe.itemToBuy,"dirt","grass","farmland");
                    trades = new EntityVillager.ITradeList[]{
                            iroPic,
                            iroAxe,
                            iroSho,
                            iroHoe
                    };
                    break;
                case 4:
                    preis = 26;
                    itemForEmeralds diaPic = new itemForEmeralds(Items.DIAMOND_PICKAXE.setMaxDamage(512), preis);
                    villagerItem.destroys(diaPic.itemToBuy,"coal_ore","iron_ore","gold_ore","diamond_ore");
                    villagerItem.destroys(diaPic.itemToBuy,"quartz_ore","lapis_ore","redstone_ore","lit_redstone_ore");
                    itemForEmeralds diaAxe = new itemForEmeralds(Items.DIAMOND_AXE.setMaxDamage(256), preis);
                    villagerItem.destroys(diaAxe.itemToBuy,"log","log2");
                    itemForEmeralds diaSho = new itemForEmeralds(Items.DIAMOND_SHOVEL.setMaxDamage(512), preis);
                    itemForEmeralds diaHoe = new itemForEmeralds(Items.DIAMOND_HOE.setMaxDamage(512), preis);
                    villagerItem.destroys(diaHoe.itemToBuy,Blocks.WHEAT,Blocks.CARROTS,Blocks.POTATOES,Blocks.BEETROOTS,Blocks.MELON_BLOCK,Blocks.PUMPKIN);
                    villagerItem.interactsWith(diaHoe.itemToBuy,"dirt","grass","farmland");
                    trades = new EntityVillager.ITradeList[]{
                            diaPic,
                            diaAxe,
                            diaSho,
                            diaHoe
                    };
                    break;
            }
        }

        return trades;
    }
    public static class itemForEmeralds implements EntityVillager.ITradeList {
        public ItemStack itemToBuy;
        public int priceInfo;

        public itemForEmeralds(Item item, int preis) {
            this.itemToBuy = new ItemStack(item,1);
            this.priceInfo = preis;
        }

        public itemForEmeralds(ItemStack itemStack, int preis) {
            this.itemToBuy = itemStack;
            this.priceInfo = preis;
        }

        public void addMerchantRecipe(IMerchant p_addMerchantRecipe_1_, MerchantRecipeList p_addMerchantRecipe_2_, Random p_addMerchantRecipe_3_) {
            int emeraldAmount = 1;
            if(this.priceInfo != 0) {
                emeraldAmount = this.priceInfo;
            }
            ItemStack Emeralds;
            Emeralds = new ItemStack(Items.EMERALD, emeraldAmount, 0);

            MerchantRecipe e = new MerchantRecipe(Emeralds, itemToBuy);
            p_addMerchantRecipe_2_.add(e);
        }
    }
}


