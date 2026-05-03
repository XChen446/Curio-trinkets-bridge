package com.mangzai.curiotrinketbridge.embeddedacce.api;

import com.mangzai.curiotrinketbridge.embeddedacce.api.slot.SlotReference;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

/**
 * Represents a sound event with volume and pitch data.
 *
 * @see Accessory#getEquipSound(ItemStack, SlotReference)
 */
public record SoundEventData(SoundEvent event, float volume, float pitch) { }
