package com.mangzai.curiotrinketbridge.embeddedacce.networking.base;

import net.minecraft.resources.ResourceLocation;

public record Type<T extends HandledPacketPayload>(ResourceLocation location) { }
