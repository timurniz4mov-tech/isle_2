package com.isle.mod;

import com.isle.mod.entity.ModEntities;
import com.isle.mod.item.ModItems;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IsleMod.MODID)
public class IsleMod {
    public static final String MODID = "isle";

    public IsleMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModTabs.TABS.register(bus);
        bus.addListener(this::attributes);
        bus.addListener(this::spawnPlacements);
        MinecraftForge.EVENT_BUS.register(new IsleEvents());
    }

    private void attributes(EntityAttributeCreationEvent e) {
        e.put(ModEntities.STALKER.get(), com.isle.mod.entity.StalkerEntity.createAttributes().build());
    }

    private void spawnPlacements(SpawnPlacementRegisterEvent e) {
        e.register(ModEntities.STALKER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
    }
}
