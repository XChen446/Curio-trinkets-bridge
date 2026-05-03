package com.mangzai.curiotrinketbridge.client.gui;

import com.mangzai.curiotrinketbridge.CurioTrinketBridge;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

final class AccessoriesUiTextures {

    static final ResourceLocation INVENTORY = texture("container/accessories_inventory.png");
    static final ResourceLocation SLOT = texture("slot.png");
    static final ResourceLocation BACKGROUND_PATCH = texture("sprites/background_patch.png");
    static final ResourceLocation SCROLL_BAR_PATCH = texture("sprites/scroll_bar_patch.png");
    static final ResourceLocation SCROLL_BAR = texture("sprites/scroll_bar.png");
    static final ResourceLocation BUTTON_12 = texture("sprites/widget/12x12/button.png");
    static final ResourceLocation BUTTON_12_HIGHLIGHTED = texture("sprites/widget/12x12/button_highlighted.png");
    static final ResourceLocation LINE_SHOWN = texture("sprites/widget/line_shown.png");
    static final ResourceLocation LINE_HIDDEN = texture("sprites/widget/line_hidden.png");
    static final ResourceLocation COSMETIC_SLOT_ICON = texture("slot/cosmetic.png");

    private AccessoriesUiTextures() {}

    private static ResourceLocation texture(String path) {
        return ResourceLocation.fromNamespaceAndPath(CurioTrinketBridge.MOD_ID, "textures/gui/accessories/" + path);
    }

    static void blitInventory(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blit(INVENTORY, x, y, 0, 0, width, height, 256, 256);
    }

    static void blitSlot(GuiGraphics graphics, int x, int y) {
        graphics.blit(SLOT, x, y, 0, 0, 18, 18, 18, 18);
    }

    static void blitButton12(GuiGraphics graphics, int x, int y, boolean hovered) {
        ResourceLocation texture = hovered ? BUTTON_12_HIGHLIGHTED : BUTTON_12;
        graphics.blit(texture, x, y, 0, 0, 12, 12, 12, 12);
    }

    static void blitLineToggle(GuiGraphics graphics, int x, int y, boolean shown) {
        graphics.blit(shown ? LINE_SHOWN : LINE_HIDDEN, x, y, 0, 0, 12, 12, 12, 12);
    }

    static void blitBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        blitNineSliced(graphics, BACKGROUND_PATCH, x, y, width, height, 15, 15, 5);
    }

    static void blitScrollTrack(GuiGraphics graphics, int x, int y, int width, int height) {
        blitNineSliced(graphics, SCROLL_BAR_PATCH, x, y, width, height, 6, 6, 2);
    }

    static void blitScrollBar(GuiGraphics graphics, int x, int y, int width, int height) {
        blitNineSliced(graphics, SCROLL_BAR, x, y, width, height, 6, 6, 2);
    }

    private static void blitNineSliced(GuiGraphics graphics, ResourceLocation texture, int x, int y,
                                       int width, int height, int textureWidth, int textureHeight, int border) {
        int right = x + width - border;
        int bottom = y + height - border;

        graphics.blit(texture, x, y, 0, 0, border, border, textureWidth, textureHeight);
        graphics.blit(texture, right, y, textureWidth - border, 0, border, border, textureWidth, textureHeight);
        graphics.blit(texture, x, bottom, 0, textureHeight - border, border, border, textureWidth, textureHeight);
        graphics.blit(texture, right, bottom, textureWidth - border, textureHeight - border, border, border, textureWidth, textureHeight);

        blitTiled(graphics, texture, x + border, y, width - border * 2, border,
                border, 0, textureWidth - border * 2, border, textureWidth, textureHeight);
        blitTiled(graphics, texture, x + border, bottom, width - border * 2, border,
                border, textureHeight - border, textureWidth - border * 2, border, textureWidth, textureHeight);
        blitTiled(graphics, texture, x, y + border, border, height - border * 2,
                0, border, border, textureHeight - border * 2, textureWidth, textureHeight);
        blitTiled(graphics, texture, right, y + border, border, height - border * 2,
                textureWidth - border, border, border, textureHeight - border * 2, textureWidth, textureHeight);
        blitTiled(graphics, texture, x + border, y + border, width - border * 2, height - border * 2,
                border, border, textureWidth - border * 2, textureHeight - border * 2, textureWidth, textureHeight);
    }

    private static void blitTiled(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
                                  int u, int v, int tileWidth, int tileHeight, int textureWidth, int textureHeight) {
        if (width <= 0 || height <= 0 || tileWidth <= 0 || tileHeight <= 0) return;
        for (int dx = 0; dx < width; dx += tileWidth) {
            int drawWidth = Math.min(tileWidth, width - dx);
            for (int dy = 0; dy < height; dy += tileHeight) {
                int drawHeight = Math.min(tileHeight, height - dy);
                graphics.blit(texture, x + dx, y + dy, u, v, drawWidth, drawHeight, textureWidth, textureHeight);
            }
        }
    }
}