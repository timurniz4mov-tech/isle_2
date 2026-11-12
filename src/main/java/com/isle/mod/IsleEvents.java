package com.isle.mod;

import com.isle.mod.entity.StalkerEntity;
import com.isle.mod.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IsleEvents {

    /** Данные игрока, которые переживают смерть. */
    public static CompoundTag data(Player p) {
        CompoundTag root = p.getPersistentData();
        if (!root.contains(Player.PERSISTED_NBT_TAG, 10)) {
            root.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return root.getCompound(Player.PERSISTED_NBT_TAG);
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        CompoundTag d = data(p);
        if (!d.getBoolean("intro")) {
            d.putBoolean("intro", true);
            p.sendSystemMessage(Component.literal("§8Тебя привезли сюда против твоей воли."));
            p.sendSystemMessage(Component.literal("§8Никто ещё не выбрался отсюда живым..."));
            p.sendSystemMessage(Component.literal("§8Что-то бродит рядом. Ищи дневники. Учись на каждой смерти."));
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        CompoundTag d = data(p);
        int n = d.getInt("deaths") + 1;
        d.putInt("deaths", n);
        Entity src = e.getSource().getEntity();
        String cause = src != null ? src.getName().getString() : e.getSource().getMsgId();
        p.sendSystemMessage(Component.literal("§7Смерть №" + n + ". Причина: " + cause + ". Запомни это."));
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone e) {
        CompoundTag old = e.getOriginal().getPersistentData();
        if (old.contains(Player.PERSISTED_NBT_TAG, 10)) {
            e.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG,
                    old.getCompound(Player.PERSISTED_NBT_TAG).copy());
        }
    }

    /** Дневники и патроны выпадают из монстров. Из Сталкера дневник выпадает всегда. */
    @SubscribeEvent
    public void onDrops(LivingDropsEvent e) {
        if (!(e.getEntity() instanceof Monster m) || m.level().isClientSide) return;
        boolean stalker = m instanceof StalkerEntity;
        var r = m.getRandom();
        if (stalker || r.nextFloat() < 0.06f) {
            e.getDrops().add(new ItemEntity(m.level(), m.getX(), m.getY(), m.getZ(),
                    new ItemStack(ModItems.JOURNAL.get())));
        }
        if (stalker) {
            e.getDrops().add(new ItemEntity(m.level(), m.getX(), m.getY(), m.getZ(),
                    new ItemStack(ModItems.RIFLE_AMMO.get(), 2 + r.nextInt(5))));
        } else if (r.nextFloat() < 0.10f) {
            e.getDrops().add(new ItemEntity(m.level(), m.getX(), m.getY(), m.getZ(),
                    new ItemStack(ModItems.SMG_AMMO.get(), 3 + r.nextInt(6))));
        }
    }
}
