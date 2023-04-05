package com.github.alexthe668.cloudstorage.client.gui;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.inventory.CloudChestMenu;
import com.github.alexthe668.cloudstorage.network.MessageSearchCloudChest;
import com.github.alexthe668.cloudstorage.network.MessageSortCloudChest;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;
import java.util.Objects;

public class CloudChestScreen extends AbstractContainerScreen<CloudChestMenu> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("cloudstorage:textures/gui/cloud_chest_gui.png");
    private final int slots;
    private float prevScrollOffs;
    private float scrollOffs;
    private boolean scrolling;
    private CloudMiniButton balloonButton;
    private CloudMiniButton sortButton;
    private CloudMiniButton searchButton;
    private EditBox searchBox;
    public int mode = 0;
    private boolean ignoreTextInput;

    public CloudChestScreen(CloudChestMenu menu, Inventory playerInv, Component component) {
        super(menu, playerInv, component);
        this.imageWidth = 195;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
        this.slots = CloudStorage.PROXY.getVisibleCloudSlots();
    }

    @Override
    protected void init() {
        super.init();
        resetButtons();
    }

    public void resetButtons() {
        this.clearWidgets();
        int i = this.leftPos;
        int j = this.topPos;
        this.addRenderableWidget(balloonButton = new CloudMiniButton(this, i + 174, j + 141, 0, (button) -> {
            searchFor("");
            mode = 0;
            resetButtons();
        }));
        this.addRenderableWidget(sortButton = new CloudMiniButton(this, i + 174, j + 141 + 16, 1, (button) -> {
            searchFor("");
            mode = 1;
            resetButtons();
        }));
        this.addRenderableWidget(searchButton = new CloudMiniButton(this, i + 174, j + 141 + 32, 2, (button) -> {
            mode = 2;
            resetButtons();
        }));
        this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(mode == 2);
        this.searchBox.setFocused(mode == 2);
        this.searchBox.setTextColor(16777215);
        this.addWidget(this.searchBox);
        balloonButton.active = mode != 0;
        sortButton.active = mode != 1;
        searchButton.active = mode != 2;
    }

    @Override
    public void render(PoseStack stack, int x, int y, float partialTicks) {
        this.renderBackground(stack);
        this.renderBg(stack, partialTicks, x, y);
        super.render(stack, x, y, partialTicks);
        this.renderGraySlots(stack, x, y);
        this.renderTooltip(stack, x, y);
    }

    private void renderGraySlots(PoseStack poseStack, int x, int y) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        int i = this.leftPos;
        int j = this.topPos;
        int clampedSize = Math.min(this.slots, 54);
        int ySlots = clampedSize / 9;
        for (int k = 0; k < ySlots; ++k) {
            for (int l = 0; l < 9 && l + k * 9 < clampedSize; ++l) {
                if(menu.isSlotGray(l + k * 9)){
                    this.blit(poseStack, i + 7 + l * 18, j + 17 + k * 18, 126, 222, 18, 18);
                }
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean charTyped(char p_98521_, int p_98522_) {
        if (this.ignoreTextInput) {
            return false;
        }else if (this.mode != 2) {
            return false;
        } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.charTyped(p_98521_, p_98522_)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    private void refreshSearchResults() {
        String s = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        searchFor(s);
    }

    private void searchFor(String s) {
        this.menu.scrollTo(0.0F, true);
        CloudStorage.NETWORK_WRAPPER.sendToServer(new MessageSearchCloudChest(s));
        this.menu.updateGrays(minecraft.player, s);
    }

    protected void renderLabels(PoseStack stack, int x, int y) {
        if(mode == 2){
            this.font.draw(stack, Component.translatable("cloudstorage.container.cloud_chest.searchbar"), (float)this.titleLabelX + 20, (float)this.titleLabelY, 4210752);
        }else{
            this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        }
        this.font.draw(stack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }

    public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
        this.ignoreTextInput = false;
        if (mode == 2){
            boolean flag = this.hoveredSlot != null && this.hoveredSlot.hasItem();
            boolean flag1 = InputConstants.getKey(p_98547_, p_98548_).getNumericKeyValue().isPresent();
            if (flag && flag1 && this.checkHotbarKeyPressed(p_98547_, p_98548_)) {
                this.ignoreTextInput = true;
                return true;
            } else {
                String s = this.searchBox.getValue();
                if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
                    if (!Objects.equals(s, this.searchBox.getValue())) {
                        this.refreshSearchResults();
                    }

                    return true;
                } else {
                    return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 ? true : super.keyPressed(p_98547_, p_98548_, p_98549_);
                }
            }
        }
        return super.keyPressed(p_98547_, p_98548_, p_98549_);
    }

    public boolean keyReleased(int p_98612_, int p_98613_, int p_98614_) {
        this.ignoreTextInput = false;
        return super.keyReleased(p_98612_, p_98613_, p_98614_);
    }

    public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_) {
        if (!this.canScroll()) {
            return false;
        } else {
            int i = (slots + 9 - 1) / 9 - 5;
            float f = (float) (p_98529_ / (double) i);
            this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs, true);
            return true;
        }
    }

    public boolean mouseDragged(double p_98535_, double p_98536_, int p_98537_, double p_98538_, double p_98539_) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 112;
            this.scrollOffs = ((float) p_98536_ - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs, true);
            return true;
        } else {
            return super.mouseDragged(p_98535_, p_98536_, p_98537_, p_98538_, p_98539_);
        }
    }

    public boolean mouseClicked(double x, double y, int mouseBtn) {
        if (mouseBtn == 0 && this.insideScrollbar(x, y)) {
            double d0 = x - (double) this.leftPos;
            double d1 = y - (double) this.topPos;
            this.scrolling = this.canScroll();
        }
        return super.mouseClicked(x, y, mouseBtn);
    }

    protected boolean insideScrollbar(double p_98524_, double p_98525_) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        return p_98524_ >= (double) k && p_98525_ >= (double) l && p_98524_ < (double) i1 && p_98525_ < (double) j1;
    }

    private boolean canScroll() {
        return slots > 54;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int clampedSize = Math.min(this.slots, 54);
        int ySlots = (int)Math.ceil(clampedSize / 9F);
        for (int k = 0; k < ySlots; ++k) {
            for (int l = 0; l < 9 && l + k * 9 < clampedSize; ++l) {
                boolean hidden = menu.isSlotGray(l + k * 9);
                this.blit(poseStack, i + 7 + l * 18, j + 17 + k * 18, hidden ? 18 : 0, 222, 18, 18);
            }
        }
        int k = j + 18;
        int l = j + 130;
        float scrollOffsForRender = this.prevScrollOffs + (this.scrollOffs - this.prevScrollOffs) * partialTicks;
        this.blit(poseStack, i + 175, k + (int) ((float) (l - k - 17) * scrollOffsForRender), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        if(this.mode == 2){
            this.blit(poseStack, i + 79, k - 14, 36, 222, 90, 12);
            this.searchBox.render(poseStack, x, y, partialTicks);
        }
    }

    public void onClose() {
        this.searchFor("");
        super.onClose();
    }

    protected void containerTick() {
        if(this.prevScrollOffs != scrollOffs){
            this.menu.updateGrays(minecraft.player, this.searchBox.getValue().toLowerCase(Locale.ROOT));
        }
        this.prevScrollOffs = scrollOffs;
        if(this.mode == 1){ // sort
            this.mode = 0;
            this.menu.scrollTo(0.0F, true);
            CloudStorage.NETWORK_WRAPPER.sendToServer(new MessageSortCloudChest(0));
            this.resetButtons();
        }
    }
}
