package com.isle.mod;

import com.isle.mod.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IsleMod.MODID);

    public static final RegistryObject<CreativeModeTab> ISLE = TABS.register("isle", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.isle"))
                    .icon(() -> new ItemStack(ModItems.RIFLE.get()))
                    .displayItems((params, out) ->
                            ModItems.ITEMS.getEntries().forEach(i -> out.accept(i.get())))
                    .build());
}
