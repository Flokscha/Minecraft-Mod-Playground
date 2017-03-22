package Flokscha.playground.client.gui.renderer;

import Flokscha.playground.Entity.MyEntitiyVillager;
import Flokscha.playground.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import net.minecraft.inventory.ContainerMerchant;

@SideOnly(Side.CLIENT)
public class MyGuiMerchant extends GuiContainer {
    //My Own GuiContainer to Fix the Inventory Problem? or just think about how to solvew it without
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("playground","textures/gui/myvillagercontainer.png");

    private final MyEntitiyVillager merchant;
    private final InventoryPlayer inventoryPlayer;
    private final int guiOffset;
    private MyGuiMerchant.MerchantButton nextButton;
    private MyGuiMerchant.MerchantButton previousButton;
    private final ITextComponent chatComponent;


    int guiFix = 0;
    private List<ItemPanel> panels = new ArrayList<ItemPanel>();
    private int from;
    private int to;

    public MyGuiMerchant(InventoryPlayer p_i45500_1_, MyEntitiyVillager p_i45500_2_, World p_i45500_3_) {
        super(new ContainerMerchant(p_i45500_1_, p_i45500_2_, p_i45500_3_));
        this.inventoryPlayer = p_i45500_1_;
        this.merchant = p_i45500_2_;
        this.chatComponent = p_i45500_2_.getDisplayName();
        this.guiFix = 37;
        this.guiOffset = 18;
    }

    @Override
    public void initGui() {
        super.initGui();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2 + guiOffset;
        this.nextButton = this.addButton(new MyGuiMerchant.MerchantButton(1, lvt_1_1_ + 120 + 38, lvt_2_1_ + 15 - 1, true));
        this.previousButton = this.addButton(new MyGuiMerchant.MerchantButton(2, lvt_1_1_ + 4, lvt_2_1_ + 15 - 1, false));
        this.nextButton.enabled = false;
        this.previousButton.enabled = false;

        int counter = 0;
        for (MerchantRecipe MR: this.merchant.getRecipes(getMerchant().getCustomer()) ){
            boolean isEven = (counter%2)==1;
            ItemPanel panel = new ItemPanel(isEven,MR,this);
            panels.add(panel);
            ++counter;
        }
        this.from = 0;
        // zeigt nur 4 Panels an.
        this.to = this.from + 4;
        if (panels.size()<4)
            this.to = panels.size();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_) {
        String lvt_3_1_ = this.chatComponent.getUnformattedText();
        this.fontRendererObj.drawString(lvt_3_1_, this.xSize / 2 - this.fontRendererObj.getStringWidth(lvt_3_1_) / 2, 6 - guiFix + guiOffset, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2 + guiOffset, 4210752);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        MerchantRecipeList recipeList = this.merchant.getRecipes(getMerchant().getCustomer());
        if(recipeList != null) {
            this.nextButton.enabled = (this.to < panels.size());
            this.previousButton.enabled = (this.from != 0);
        }
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        if(p_actionPerformed_1_ == this.nextButton) {
            for (ItemPanel IP: panels.subList(this.from,this.to)){
                IP.disableButton();
            }
            // wenn es nicht genau 4 passende panels gibt:
            if (this.to+4 < panels.size()){
                //wenn kleiner als 4 dann kommen 4 weitere
                this.from += 4;
                this.to = this.from + 4;
            } else {
                //wenn nicht müssen nur der rest rein kommen.
                // panels.size() - this.from = der rest.
                int rest = panels.size()%this.to;
                this.from +=4;
                this.to = this.from + rest;
            }

        } else if(p_actionPerformed_1_ == this.previousButton) {
            for (ItemPanel IP: panels.subList(this.from,this.to)){
                IP.disableButton();
            }
            this.from -= 4;
            this.to = this.from + 4;
        } else if (p_actionPerformed_1_ != null){
            for (ItemPanel IP: panels.subList(this.from,this.to)){
                LogHelper.info("BuyButton Number: " + IP.buyButton.id + " checked");
                if (IP.buyButton.enabled&&IP.buyButton.equals(p_actionPerformed_1_)){
                    LogHelper.info(p_actionPerformed_1_.id + " Button Pressed!");
                    MyGuiHandler.onBuyButton(IP.MR);
                    ((ContainerMerchant)this.inventorySlots).onBuyButton(IP.MR);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_, int p_drawGuiContainerBackgroundLayer_3_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        // Set the Texture for the GUI
        this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int XMidPoint = (this.width - this.xSize) / 2;
        int YMidPoint = (this.height - this.ySize) / 2 - guiFix + guiOffset;
        this.drawTexturedModalRect(XMidPoint, YMidPoint, 0, 0, this.xSize, this.ySize + guiFix);
    }

    @SideOnly(Side.CLIENT)
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        MerchantRecipeList lvt_4_1_ = this.merchant.getRecipes(getMerchant().getCustomer());
        if(lvt_4_1_ != null && !lvt_4_1_.isEmpty()) {

            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();


            // For Loop für alle items
            int offset = 0;
            int counter = 0;
            ItemStack itemToBuy;
            for (ItemPanel IP: panels.subList(this.from,this.to)){

                this.itemRender.zLevel = 100.0F;
                boolean isEven = (counter%2)==1;
                IP.drawPanel(p_drawScreen_1_, p_drawScreen_2_, offset, counter);
                ++counter;
                if (isEven){
                    offset = offset + 48;
                }

                // update BuyButton
                itemToBuy = IP.MR.getItemToBuy();
                if (IP.MR.isRecipeDisabled() ){
                    IP.buyButton.enabled = false;
                } else {
                    if (this.inventoryPlayer.hasItemStack(itemToBuy)){
                        int EmeraldsCount = 0;
                        //checks for enough Items in the player inventory, even loose stacks.
                        for (int i=0;i<=this.inventoryPlayer.getSizeInventory();i++){
                            if (this.inventoryPlayer.getStackInSlot(i).getItem().equals(itemToBuy.getItem() ) ) {
                                EmeraldsCount += this.inventoryPlayer.getStackInSlot(i).getCount();
                            }
                        }
                        IP.buyButton.enabled = EmeraldsCount >= itemToBuy.getCount();
                    } else {
                        IP.buyButton.enabled = false;
                    }
                }

            }


            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
        }

    }

    //@Override
    public IMerchant getMerchant() {
        return this.merchant;
    }

    @SideOnly(Side.CLIENT)
    static class MerchantButton extends GuiButton {
        private final boolean forward;

        public MerchantButton(int p_i1095_1_, int p_i1095_2_, int p_i1095_3_, boolean p_i1095_4_) {
            super(p_i1095_1_, p_i1095_2_, p_i1095_3_, 12, 19, "");
            this.forward = p_i1095_4_;
        }

        public void drawButton(Minecraft p_drawButton_1_, int p_drawButton_2_, int p_drawButton_3_) {
            if(this.visible) {
                p_drawButton_1_.getTextureManager().bindTexture(MyGuiMerchant.MERCHANT_GUI_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                boolean lvt_4_1_ = p_drawButton_2_ >= this.xPosition && p_drawButton_3_ >= this.yPosition && p_drawButton_2_ < this.xPosition + this.width && p_drawButton_3_ < this.yPosition + this.height;
                int lvt_5_1_ = 0;
                int lvt_6_1_ = 176;
                if(!this.enabled) {
                    lvt_6_1_ += this.width * 2;
                } else if(lvt_4_1_) {
                    lvt_6_1_ += this.width;
                }

                if(!this.forward) {
                    lvt_5_1_ += this.height;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, lvt_6_1_, lvt_5_1_, this.width, this.height);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public class ItemPanel {
        private final MerchantRecipe MR;
        private final boolean odd;
        public GuiButton buyButton;
        private final ItemStack itemToSell;
        private final ItemStack itemToBuy2;
        private final ItemStack itemToBuy;
        private final MyGuiMerchant that;
        private int lvt_5_1_;
        private int lvt_6_1_;

        public ItemPanel(boolean odd,MerchantRecipe MR,MyGuiMerchant that){
            this.odd = odd;
            // Position ist ja nur Odd abhängig
            this.MR = MR;
            this.itemToBuy = MR.getItemToBuy();
            this.itemToBuy2 = MR.getSecondItemToBuy();
            this.itemToSell = MR.getItemToSell();
            this.that = that;
            this.lvt_5_1_ = (that.width - that.xSize) / 2;
            this.lvt_6_1_ = (that.height - that.ySize) / 2;
        }

        public void drawPanel(int p_drawScreen_1_,int p_drawScreen_2_,int offset,int counter){
            int xOffset = 0;
            int xSpacing = 24;
            int yOffset = 28 + guiOffset;
            int ySpacing = 22;
            int startPoint = 24;
            int xPos = lvt_5_1_ + startPoint;
            int yPos = lvt_6_1_ + startPoint;
            if (odd){
                xOffset = 76;
            }
            //localize
            String ButtonString = "buy";
            buyButton = new GuiButton(counter,xPos + 24 + xOffset,yPos +yOffset-4 - offset,24,16+4,ButtonString);
            buyButton.visible = true;
            //First item
            that.itemRender.renderItemAndEffectIntoGUI(itemToBuy, xPos + xOffset, lvt_6_1_+yOffset - offset);
            that.itemRender.renderItemOverlays(that.fontRendererObj, itemToBuy, xPos + xOffset, lvt_6_1_+yOffset - offset);
            //second Item
            if(!itemToBuy2.isEmpty()) {
                that.itemRender.renderItemAndEffectIntoGUI(itemToBuy2, xPos + xSpacing + xOffset, lvt_6_1_+yOffset - offset);
                that.itemRender.renderItemOverlays(that.fontRendererObj, itemToBuy2, xPos + xSpacing + xOffset, lvt_6_1_+yOffset - offset);
            }
            //third Item (Right hand side
            that.itemRender.renderItemAndEffectIntoGUI(itemToSell, xPos + xOffset, lvt_6_1_+yOffset + ySpacing - offset);
            that.itemRender.renderItemOverlays(that.fontRendererObj, itemToSell, xPos + xOffset, lvt_6_1_+yOffset + ySpacing - offset);
            //Buy button
            that.addButton(buyButton);

            //Tooltips
            that.itemRender.zLevel = 0.0F;
            GlStateManager.disableLighting();
            if(that.isPointInRegion(startPoint + xOffset, yOffset - offset, 16, 16, p_drawScreen_1_, p_drawScreen_2_) && !itemToBuy.isEmpty()) {
                //Left hand Side tooltip 1
                that.renderToolTip(itemToBuy, p_drawScreen_1_, p_drawScreen_2_);
            } else if(!itemToBuy2.isEmpty() && that.isPointInRegion(startPoint + xSpacing + xOffset, yOffset - offset, 16, 16 , p_drawScreen_1_, p_drawScreen_2_) && !MR.getSecondItemToBuy().isEmpty()) {
                //Left Hand side tooltip 2
                that.renderToolTip(itemToBuy2, p_drawScreen_1_, p_drawScreen_2_);
            } else if(!itemToSell.isEmpty() && that.isPointInRegion(startPoint + xOffset, yOffset + ySpacing - offset, 16, 16, p_drawScreen_1_, p_drawScreen_2_) && !MR.getItemToSell().isEmpty()) {
                //Right hand side tooltip
                that.renderToolTip(itemToSell, p_drawScreen_1_, p_drawScreen_2_);
            } else if(MR.isRecipeDisabled()) {
                buyButton.enabled = false;
            }
        }

        public void disableButton(){
            this.buyButton.visible = false;
            this.buyButton.enabled = false;
        }
    }
}
