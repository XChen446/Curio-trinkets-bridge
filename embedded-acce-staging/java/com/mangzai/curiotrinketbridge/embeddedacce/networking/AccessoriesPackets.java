package com.mangzai.curiotrinketbridge.embeddedacce.networking;

import com.mangzai.curiotrinketbridge.embeddedacce.networking.base.NetworkBuilderRegister;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.client.AccessoryBreak;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.client.SyncContainerData;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.client.SyncData;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.client.SyncEntireContainer;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.holder.SyncHolderChange;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.server.MenuScroll;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.server.NukeAccessories;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.server.ScreenOpen;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.server.SyncCosmeticToggle;

public class AccessoriesPackets {

    public static void register(NetworkBuilderRegister register) {
        register.registerBuilderC2S(ScreenOpen.class, ScreenOpen.ENDEC);
        register.registerBuilderC2S(NukeAccessories.class, NukeAccessories.ENDEC);
        register.registerBuilderC2S(SyncCosmeticToggle.class, SyncCosmeticToggle.ENDEC);

        register.registerBuilderS2C(SyncEntireContainer.class, SyncEntireContainer.ENDEC);
        register.registerBuilderS2C(SyncContainerData.class, SyncContainerData.ENDEC);
        register.registerBuilderS2C(SyncData.class, SyncData.ENDEC);
        register.registerBuilderS2C(AccessoryBreak.class, AccessoryBreak.ENDEC);

        register.registerBuilderBiDi(MenuScroll.class, MenuScroll.ENDEC);
        register.registerBuilderBiDi(SyncHolderChange.class, SyncHolderChange.ENDEC);
    }
}
