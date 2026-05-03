package com.mangzai.curiotrinketbridge.embeddedacce.networking.server;

import com.mojang.logging.LogUtils;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.BaseAccessoriesPacket;
import com.mangzai.curiotrinketbridge.embeddedacce.utils.EndecUtils;
import io.wispforest.endec.Endec;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public record NukeAccessories() implements BaseAccessoriesPacket {

    public static final Endec<NukeAccessories> ENDEC = EndecUtils.structUnit(new NukeAccessories());

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void handle(Player player) {
        // Only players in creative should be able to nuke their accessories
        if (!player.getAbilities().instabuild) {
            LOGGER.info("A given player sent a NukeAccessories packet not as a Creative Player: [Player: {}]", player.getName());

            return;
        }

        var cap = player.accessoriesCapability();

        if (cap != null) {
            cap.reset(false);

            player.containerMenu.broadcastChanges();
        }
    }
}