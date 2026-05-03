package com.mangzai.curiotrinketbridge.menu;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CosmeticCurioSlot;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 桥接包内置的 Accessories 风格饰品菜单。
 * 菜单与槽位由本模组创建；Curios 只作为真实存储和行为后端，Trinkets 通过已有桥接进入这些槽位。
 */
public class EmbeddedAccessoriesMenu extends AbstractContainerMenu {

    private static final Field LAST_SLOTS_FIELD = findPrivateNonNullListField(0);
    private static final Field REMOTE_SLOTS_FIELD = findPrivateNonNullListField(1);
    private static final int MAX_VISIBLE_ROWS = 8;
    private static final int VANILLA_SLOT_COUNT = 41;
    private static final int BASE_SLOT_X = -26;
    private static final int COSMETIC_SLOT_X = -46;
    private static final int SLOT_START_Y = 16;
    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
    };
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    public final Player player;
    public int totalSlots;
    public boolean overMaxVisibleSlots;
    public int scrolledIndex;
    public float smoothScroll;

    private final List<AccessoryEntry> entries = new ArrayList<>();
    private boolean cosmeticsOpen = true;
    private boolean hasCosmetics;
    private int accessorySlotStartIndex = VANILLA_SLOT_COUNT;
    private int cosmeticSlotStartIndex = VANILLA_SLOT_COUNT;

    public EmbeddedAccessoriesMenu(int windowId, Inventory inventory, FriendlyByteBuf buffer) {
        this(windowId, inventory);
    }

    public EmbeddedAccessoriesMenu(int windowId, Inventory inventory) {
        super(BridgeMenus.EMBEDDED_ACCESSORIES.get(), windowId);
        this.player = inventory.player;
        this.addVanillaSlots(inventory);
        this.rebuildEntries();
        this.scrollToIndex(0);
    }

    public boolean isCosmeticsOpen() {
        return this.cosmeticsOpen && this.hasCosmetics;
    }

    public boolean hasCosmetics() {
        return this.hasCosmetics;
    }

    public int maxScrollableIndex() {
        return Math.max(0, this.totalSlots - MAX_VISIBLE_ROWS);
    }

    public void toggleCosmetics() {
        if (!this.hasCosmetics) return;
        this.cosmeticsOpen = !this.cosmeticsOpen;
        this.scrollToIndex(this.scrolledIndex);
    }

    public void scrollTo(float pos) {
        this.smoothScroll = Mth.clamp(pos, 0.0F, 1.0F);
        this.scrollToIndex(Math.round(this.smoothScroll * this.maxScrollableIndex()));
    }

    public void scrollToIndex(int index) {
        this.rebuildEntries();
        this.scrolledIndex = Mth.clamp(index, 0, this.maxScrollableIndex());
        this.smoothScroll = this.maxScrollableIndex() <= 0 ? 0.0F : this.scrolledIndex / (float) this.maxScrollableIndex();
        this.rebuildAccessorySlots();
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        if (index < 0 || index >= this.slots.size()) return ItemStack.EMPTY;

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack source = slot.getItem();
        ItemStack original = source.copy();
        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(source);

        if (index >= this.accessorySlotStartIndex) {
            if (!this.moveItemStackTo(source, 4, VANILLA_SLOT_COUNT, false)) return ItemStack.EMPTY;
        } else if (index < 4) {
            if (!this.moveItemStackTo(source, 4, VANILLA_SLOT_COUNT, false)) return ItemStack.EMPTY;
        } else if (index < VANILLA_SLOT_COUNT) {
            if (!CuriosApi.getItemStackSlots(source, player.level()).isEmpty()
                    && this.moveItemStackTo(source, this.accessorySlotStartIndex, this.cosmeticSlotStartIndex, false)) {
                // 已移入内置 Accessories 槽。
            } else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
                    && !this.slots.get(3 - equipmentSlot.getIndex()).hasItem()) {
                int armorIndex = 3 - equipmentSlot.getIndex();
                if (!this.moveItemStackTo(source, armorIndex, armorIndex + 1, false)) return ItemStack.EMPTY;
            } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(40).hasItem()) {
                if (!this.moveItemStackTo(source, 40, 41, false)) return ItemStack.EMPTY;
            } else if (index < 31) {
                if (!this.moveItemStackTo(source, 31, 40, false)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(source, 4, 31, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (source.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, source);
        return original;
    }

    private void addVanillaSlots(Inventory inventory) {
        for (int i = 0; i < 4; i++) {
            EquipmentSlot equipmentSlot = ARMOR_SLOTS[i];
            ResourceLocation icon = ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()];
            this.addSlot(new Slot(inventory, 39 - i, 8, 8 + i * 18) {
                @Override
                public void set(@Nonnull ItemStack stack) {
                    ItemStack previous = this.getItem();
                    super.set(stack);
                    EmbeddedAccessoriesMenu.this.player.onEquipItem(equipmentSlot, previous, stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return stack.canEquip(equipmentSlot, EmbeddedAccessoriesMenu.this.player);
                }

                @Override
                public boolean mayPickup(@Nonnull Player player) {
                    ItemStack stack = this.getItem();
                    return (stack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(stack)) && super.mayPickup(player);
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, icon);
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + (row + 1) * 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }

        this.addSlot(new Slot(inventory, 40, 152, 62) {
            @Override
            public void set(@Nonnull ItemStack stack) {
                ItemStack previous = this.getItem();
                super.set(stack);
                EmbeddedAccessoriesMenu.this.player.onEquipItem(EquipmentSlot.OFFHAND, previous, stack);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }

    private void rebuildEntries() {
        this.entries.clear();
        this.hasCosmetics = false;
        CuriosApi.getCuriosInventory(this.player).ifPresent(this::collectEntries);
        this.totalSlots = this.entries.size();
        this.overMaxVisibleSlots = this.totalSlots > MAX_VISIBLE_ROWS;
    }

    private void collectEntries(ICuriosItemHandler handler) {
        handler.getCurios().entrySet().stream()
                .filter(entry -> entry.getValue().isVisible())
                .sorted(Comparator.comparingInt(entry -> CuriosApi.getSlot(entry.getKey(), this.player.level())
                        .map(slotType -> slotType.getOrder()).orElse(0)))
                .forEach(entry -> this.collectEntry(entry.getKey(), entry.getValue()));
    }

    private void collectEntry(String identifier, ICurioStacksHandler stacksHandler) {
        IDynamicStackHandler stacks = stacksHandler.getStacks();
        IDynamicStackHandler cosmeticStacks = stacksHandler.getCosmeticStacks();
        NonNullList<Boolean> renders = stacksHandler.getRenders();

        if (stacksHandler.hasCosmetic()) this.hasCosmetics = true;

        for (int index = 0; index < stacks.getSlots(); index++) {
            this.entries.add(new AccessoryEntry(identifier, index, stacks, cosmeticStacks, renders,
                    stacksHandler.canToggleRendering(), stacksHandler.hasCosmetic()));
        }
    }

    private void rebuildAccessorySlots() {
        if (this.slots.size() > this.accessorySlotStartIndex) {
            this.slots.subList(this.accessorySlotStartIndex, this.slots.size()).clear();
            lastSlots(this).subList(this.accessorySlotStartIndex, lastSlots(this).size()).clear();
            remoteSlots(this).subList(this.accessorySlotStartIndex, remoteSlots(this).size()).clear();
        }

        int visibleRows = Math.min(MAX_VISIBLE_ROWS, Math.max(0, this.entries.size() - this.scrolledIndex));
        for (int row = 0; row < visibleRows; row++) {
            AccessoryEntry entry = this.entries.get(this.scrolledIndex + row);
            int y = SLOT_START_Y + row * 18;
            this.addSlot(new CurioSlot(this.player, entry.stacks(), entry.index(), entry.identifier(), BASE_SLOT_X, y,
                    entry.renders(), entry.canToggleRendering(), entry.hasCosmetic(), false));
        }

        this.cosmeticSlotStartIndex = this.slots.size();

        if (!this.isCosmeticsOpen()) return;

        for (int row = 0; row < visibleRows; row++) {
            AccessoryEntry entry = this.entries.get(this.scrolledIndex + row);
            if (!entry.hasCosmetic()) continue;
            int y = SLOT_START_Y + row * 18;
            this.addSlot(new CosmeticCurioSlot(this.player, entry.cosmeticStacks(), entry.index(), entry.identifier(), COSMETIC_SLOT_X, y));
        }
    }

    private record AccessoryEntry(String identifier, int index, IDynamicStackHandler stacks,
                                  IDynamicStackHandler cosmeticStacks, NonNullList<Boolean> renders,
                                  boolean canToggleRendering, boolean hasCosmetic) {
    }

    private static Field findPrivateNonNullListField(int privateIndex) {
        int currentIndex = 0;
        for (Field field : AbstractContainerMenu.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPrivate(modifiers) || !NonNullList.class.isAssignableFrom(field.getType())) continue;
            if (currentIndex++ == privateIndex) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalStateException("无法定位 AbstractContainerMenu 同步槽位列表");
    }

    @SuppressWarnings("unchecked")
    private static NonNullList<ItemStack> lastSlots(AbstractContainerMenu menu) {
        try {
            return (NonNullList<ItemStack>) LAST_SLOTS_FIELD.get(menu);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("无法访问菜单 lastSlots", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static NonNullList<ItemStack> remoteSlots(AbstractContainerMenu menu) {
        try {
            return (NonNullList<ItemStack>) REMOTE_SLOTS_FIELD.get(menu);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("无法访问菜单 remoteSlots", e);
        }
    }
}