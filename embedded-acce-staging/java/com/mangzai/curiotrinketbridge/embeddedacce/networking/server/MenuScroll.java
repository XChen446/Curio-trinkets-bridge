package com.mangzai.curiotrinketbridge.embeddedacce.networking.server;

import com.mangzai.curiotrinketbridge.embeddedacce.AccessoriesInternals;
import com.mangzai.curiotrinketbridge.embeddedacce.client.AccessoriesMenu;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.BaseAccessoriesPacket;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record MenuScroll(int index, boolean smooth) implements BaseAccessoriesPacket {

    public static final Endec<MenuScroll> ENDEC = StructEndecBuilder.of(
            Endec.VAR_INT.fieldOf("index", MenuScroll::index),
            Endec.BOOLEAN.fieldOf("smooth", MenuScroll::smooth),
            MenuScroll::new
    );

    @Override
    public void handle(Player player) {
        if(player.containerMenu instanceof AccessoriesMenu menu && menu.scrollTo(this.index, this.smooth) && player instanceof ServerPlayer serverPlayer){
            AccessoriesInternals.getNetworkHandler().sendToPlayer(serverPlayer, new MenuScroll(this.index, this.smooth));
        }
    }
}
