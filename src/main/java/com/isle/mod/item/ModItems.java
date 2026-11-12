package com.isle.mod.item;

import com.isle.mod.IsleMod;
import com.isle.mod.entity.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IsleMod.MODID);

    // Патроны
    public static final RegistryObject<Item> RIFLE_AMMO = ITEMS.register("rifle_ammo", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SMG_AMMO = ITEMS.register("smg_ammo", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHOTGUN_SHELL = ITEMS.register("shotgun_shell", () -> new Item(new Item.Properties()));

    // Оружие: (props, патрон, урон, дробинок, разброс, дальность, перезарядка, выстрелов в очереди, интервал, звук, тон)
    public static final RegistryObject<Item> RIFLE = ITEMS.register("rifle", () ->
            new GunItem(new Item.Properties().stacksTo(1), RIFLE_AMMO, 10f, 1, 0.004f, 96, 8, 1, 0,
                    SoundEvents.FIREWORK_ROCKET_BLAST, 0.7f));
    public static final RegistryObject<Item> SMG = ITEMS.register("smg", () ->
            new GunItem(new Item.Properties().stacksTo(1), SMG_AMMO, 3.5f, 1, 0.03f, 48, 10, 3, 2,
                    SoundEvents.FIREWORK_ROCKET_SHOOT, 1.4f));
    public static final RegistryObject<Item> SHOTGUN = ITEMS.register("shotgun", () ->
            new GunItem(new Item.Properties().stacksTo(1), SHOTGUN_SHELL, 3f, 8, 0.09f, 24, 22, 1, 0,
                    SoundEvents.GENERIC_EXPLODE, 1.8f));

    // Остальное
    public static final RegistryObject<Item> JOURNAL = ITEMS.register("journal", () -> new JournalItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage", () -> new BandageItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> STALKER_SPAWN_EGG = ITEMS.register("stalker_spawn_egg", () ->
            new ForgeSpawnEggItem(ModEntities.STALKER, 0x1b1b24, 0x8b0000, new Item.Properties()));
}
