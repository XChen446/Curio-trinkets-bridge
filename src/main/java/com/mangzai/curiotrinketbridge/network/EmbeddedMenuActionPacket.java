package com.mangzai.curiotrinketbridge.network;

import com.mangzai.curiotrinketbridge.menu.EmbeddedAccessoriesMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class EmbeddedMenuActionPacket {

    private static final int ACTION_SCROLL = 0;
    private static final int ACTION_TOGGLE_COSMETICS = 1;

    private final int windowId;
    private final int action;
    private final int value;

    private EmbeddedMenuActionPacket(int windowId, int action, int value) {
        this.windowId = windowId;
        this.action = action;
        this.value = value;
    }

    public static EmbeddedMenuActionPacket scroll(int windowId, int index) {
        return new EmbeddedMenuActionPacket(windowId, ACTION_SCROLL, index);
    }

    public static EmbeddedMenuActionPacket toggleCosmetics(int windowId) {
        return new EmbeddedMenuActionPacket(windowId, ACTION_TOGGLE_COSMETICS, 0);
    }

    public static void encode(EmbeddedMenuActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.windowId);
        buffer.writeVarInt(packet.action);
        buffer.writeVarInt(packet.value);
    }

    public static EmbeddedMenuActionPacket decode(FriendlyByteBuf buffer) {
        return new EmbeddedMenuActionPacket(buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handle(EmbeddedMenuActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null || sender.containerMenu.containerId != packet.windowId) return;
            if (!(sender.containerMenu instanceof EmbeddedAccessoriesMenu menu)) return;

            if (packet.action == ACTION_SCROLL) {
                menu.scrollToIndex(packet.value);
            } else if (packet.action == ACTION_TOGGLE_COSMETICS) {
                menu.toggleCosmetics();
            }
        });
        context.setPacketHandled(true);
    }
}