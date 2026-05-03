package com.mangzai.curiotrinketbridge.menu;

import com.mangzai.curiotrinketbridge.CurioTrinketBridge;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BridgeMenus {

    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CurioTrinketBridge.MOD_ID);

    public static final RegistryObject<MenuType<EmbeddedAccessoriesMenu>> EMBEDDED_ACCESSORIES =
            MENUS.register("embedded_accessories", () -> IForgeMenuType.create(EmbeddedAccessoriesMenu::new));

    private BridgeMenus() {}

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
