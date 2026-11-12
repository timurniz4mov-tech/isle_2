package com.isle.mod.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/** Огнестрел с мгновенным попаданием (hitscan). Патроны берутся из инвентаря. */
public class GunItem extends Item {
    private final Supplier<Item> ammo;
    private final float damage;
    private final int pellets;
    private final float spread;
    private final double range;
    private final int cooldown;
    private final int burst;
    private final int burstInterval;
    private final SoundEvent sound;
    private final float pitch;

    public GunItem(Properties props, Supplier<Item> ammo, float damage, int pellets, float spread,
                   double range, int cooldown, int burst, int burstInterval, SoundEvent sound, float pitch) {
        super(props);
        this.ammo = ammo;
        this.damage = damage;
        this.pellets = pellets;
        this.spread = spread;
        this.range = range;
        this.cooldown = cooldown;
        this.burst = burst;
        this.burstInterval = burstInterval;
        this.sound = sound;
        this.pitch = pitch;
    }

    private boolean hasAmmo(Player p) {
        if (p.getAbilities().instabuild) return true;
        Item a = ammo.get();
        for (ItemStack s : p.getInventory().items) {
            if (!s.isEmpty() && s.is(a)) return true;
        }
        return false;
    }

    private boolean consumeAmmo(Player p) {
        if (p.getAbilities().instabuild) return true;
        Item a = ammo.get();
        for (ItemStack s : p.getInventory().items) {
            if (!s.isEmpty() && s.is(a)) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player p, InteractionHand hand) {
        ItemStack stack = p.getItemInHand(hand);
        if (p.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (!hasAmmo(p)) {
            if (!level.isClientSide) {
                level.playSound(null, p.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1f, 1.5f);
                p.displayClientMessage(Component.literal("Нет патронов"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            if (shoot((ServerLevel) level, p) && burst > 1) {
                CompoundTag t = stack.getOrCreateTag();
                t.putInt("Burst", burst - 1);
                t.putInt("Delay", burstInterval);
            }
        }
        p.getCooldowns().addCooldown(this, cooldown);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player p)) return;
        CompoundTag t = stack.getTag();
        if (t == null || t.getInt("Burst") <= 0) return;
        if (!selected && p.getOffhandItem() != stack) {
            t.putInt("Burst", 0);
            return;
        }
        int d = t.getInt("Delay") - 1;
        if (d > 0) {
            t.putInt("Delay", d);
            return;
        }
        if (!shoot((ServerLevel) level, p)) {
            t.putInt("Burst", 0);
            return;
        }
        t.putInt("Burst", t.getInt("Burst") - 1);
        t.putInt("Delay", burstInterval);
    }

    /** Чтобы NBT очереди не вызывал «подпрыгивание» оружия в руке. */
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    private Vec3 spreadDir(Player p) {
        Vec3 look = p.getLookAngle();
        RandomSource r = p.getRandom();
        return new Vec3(look.x + r.nextGaussian() * spread,
                look.y + r.nextGaussian() * spread,
                look.z + r.nextGaussian() * spread).normalize();
    }

    private boolean shoot(ServerLevel level, Player p) {
        if (!consumeAmmo(p)) return false;
        Vec3 eye = p.getEyePosition();
        for (int i = 0; i < pellets; i++) {
            Vec3 dir = spreadDir(p);
            Vec3 end = eye.add(dir.scale(range));
            BlockHitResult bh = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, p));
            Vec3 stop = bh.getType() == HitResult.Type.MISS ? end : bh.getLocation();
            AABB box = p.getBoundingBox().expandTowards(dir.scale(range)).inflate(1.0);
            EntityHitResult eh = ProjectileUtil.getEntityHitResult(level, p, eye, stop, box,
                    t -> !t.isSpectator() && t.isPickable() && t != p);
            if (eh != null) {
                stop = eh.getLocation();
                Entity target = eh.getEntity();
                target.invulnerableTime = 0; // чтобы все дробинки засчитывались
                target.hurt(level.damageSources().playerAttack(p), damage);
            }
            double len = eye.distanceTo(stop);
            for (double d = 1.5; d < len; d += Math.max(2.0, len / 12.0)) {
                Vec3 pt = eye.add(dir.scale(d));
                level.sendParticles(ParticleTypes.CRIT, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
            }
        }
        Vec3 m = eye.add(p.getLookAngle().scale(0.8));
        level.sendParticles(ParticleTypes.SMOKE, m.x, m.y - 0.1, m.z, 3, 0.03, 0.03, 0.03, 0.01);
        level.playSound(null, p.getX(), p.getY(), p.getZ(), sound, SoundSource.PLAYERS, 0.8f, pitch);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flag) {
        tips.add(Component.literal("§7Патроны: ").append(ammo.get().getDescription()));
        tips.add(Component.literal("§7Урон: " + damage + (pellets > 1 ? " x" + pellets : "")
                + (burst > 1 ? " (очередь " + burst + ")" : "")));
    }
}
