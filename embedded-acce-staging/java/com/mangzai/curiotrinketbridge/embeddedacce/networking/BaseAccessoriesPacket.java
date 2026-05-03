package com.mangzai.curiotrinketbridge.embeddedacce.networking;

import com.mangzai.curiotrinketbridge.embeddedacce.AccessoriesInternals;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.base.BaseNetworkHandler;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.base.HandledPacketPayload;

public interface BaseAccessoriesPacket extends HandledPacketPayload {
    @Override
    default BaseNetworkHandler handler() {
        return AccessoriesInternals.getNetworkHandler();
    }
}
