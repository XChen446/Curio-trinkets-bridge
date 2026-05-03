package com.mangzai.curiotrinketbridge.embeddedacce.networking.client;

import com.mangzai.curiotrinketbridge.embeddedacce.api.slot.ExtraSlotTypeProperties;
import com.mangzai.curiotrinketbridge.embeddedacce.api.slot.SlotGroup;
import com.mangzai.curiotrinketbridge.embeddedacce.api.slot.SlotType;
import com.mangzai.curiotrinketbridge.embeddedacce.api.slot.UniqueSlotHandling;
import com.mangzai.curiotrinketbridge.embeddedacce.data.EntitySlotLoader;
import com.mangzai.curiotrinketbridge.embeddedacce.data.SlotGroupLoader;
import com.mangzai.curiotrinketbridge.embeddedacce.data.SlotTypeLoader;
import com.mangzai.curiotrinketbridge.embeddedacce.endec.MinecraftEndecs;
import com.mangzai.curiotrinketbridge.embeddedacce.impl.SlotGroupImpl;
import com.mangzai.curiotrinketbridge.embeddedacce.impl.SlotTypeImpl;
import com.mangzai.curiotrinketbridge.embeddedacce.networking.BaseAccessoriesPacket;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public record SyncData(List<SlotType> slotTypes, Map<EntityType<?>, Set<String>> entitySlots, Set<SlotGroup> slotGroups, Set<String> uniqueGroups, Map<String, ExtraSlotTypeProperties> uniqueExtraProperties) implements BaseAccessoriesPacket {

    public static Endec<SyncData> ENDEC = StructEndecBuilder.of(
            SlotTypeImpl.ENDEC.listOf().fieldOf("slotTypes", SyncData::slotTypes),
            Endec.map(MinecraftEndecs.ofRegistry(BuiltInRegistries.ENTITY_TYPE), Endec.STRING.setOf()).fieldOf("entitySlots", SyncData::entitySlots),
            SlotGroupImpl.ENDEC.setOf().fieldOf("slotGroups", SyncData::slotGroups),
            Endec.STRING.setOf().fieldOf("uniqueGroups", SyncData::uniqueGroups),
            ExtraSlotTypeProperties.ENDEC.mapOf().fieldOf("uniqueExtraProperties", SyncData::uniqueExtraProperties),
            SyncData::new
    );

    public static SyncData create(){
        var allSlotTypes = SlotTypeLoader.INSTANCE.getSlotTypes(false);

        var entitySlotData = EntitySlotLoader.INSTANCE.getEntitySlotData(false);

        var entitySlots = new HashMap<EntityType<?>, Set<String>>();

        for (var entry : entitySlotData.entrySet()) {
            entitySlots.put(entry.getKey(), entry.getValue().keySet());
        }

        var slotGroups = new HashSet<SlotGroup>();

        slotGroups.addAll(SlotGroupLoader.INSTANCE.getGroups(false, false));

        return new SyncData(List.copyOf(allSlotTypes.values()), entitySlots, slotGroups, UniqueSlotHandling.getGroups(false), ExtraSlotTypeProperties.getProperties(false));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(Player player) {
        Map<String, SlotType> slotTypes = new HashMap<>();

        for (SlotType slotType : this.slotTypes()) {
            slotTypes.put(slotType.name(), slotType);
        }

        SlotTypeLoader.INSTANCE.setSlotType(slotTypes);

        UniqueSlotHandling.buildClientSlotReferences();

        Map<EntityType<?>, Map<String, SlotType>> entitySlotTypes = new HashMap<>();

        for (var entry : this.entitySlots().entrySet()) {
            var map = entry.getValue().stream()
                    .map(slotTypes::get)
                    .collect(Collectors.toUnmodifiableMap(SlotType::name, slotType -> slotType));

            entitySlotTypes.put(entry.getKey(), map);
        }

        EntitySlotLoader.INSTANCE.setEntitySlotData(entitySlotTypes);

        var slotGroups = this.slotGroups().stream()
                .collect(Collectors.toUnmodifiableMap(SlotGroup::name, group -> group));

        SlotGroupLoader.INSTANCE.setGroups(slotGroups);

        UniqueSlotHandling.setClientGroups(this.uniqueGroups());
        ExtraSlotTypeProperties.setClientPropertyMap(this.uniqueExtraProperties());
    }
}
