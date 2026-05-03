package com.mangzai.curiotrinketbridge.client;

import com.mangzai.curiotrinketbridge.CurioTrinketBridge;
import com.mangzai.curiotrinketbridge.bridge.TrinketDetector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * 客户端启动桥接：
 * 1. 把已注册了 Trinkets 渲染器的物品桥接为 Curios 渲染器；
 * 2. UI 由 Curios 原生界面（或后续内嵌的 Accessories 渲染层）负责，
 *    本类不再接管或替换任何 MenuScreen。
 */
@OnlyIn(Dist.CLIENT)
public final class ClientBridgeSetup {

    private ClientBridgeSetup() {}

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (!TrinketDetector.isTrinketsLoaded()) return;

            int registered = 0;
            for (Item item : BuiltInRegistries.ITEM) {
                if (!TrinketDetector.isTrinket(item)) continue;
                Object trinketRenderer = TrinketDetector.getTrinketRenderer(item);
                if (trinketRenderer == null) continue;
                final Item targetItem = item;
                final Object renderer = trinketRenderer;
                CuriosRendererRegistry.register(targetItem,
                        () -> new TrinketRendererBridge(targetItem, renderer));
                registered++;
            }
            CurioTrinketBridge.LOGGER.info("[CurioTrinketBridge] 已桥接 {} 个 Trinket 渲染器到 Curios 系统", registered);
        });
    }
}
