package com.isle.mod.item;

import com.isle.mod.IsleEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Дневник. Каждый прочитанный открывает следующую запись. */
public class JournalItem extends Item {
    public static final String[] PAGES = {
            "День 1. Я очнулся на берегу. Лодки нет. Радио нет. Только лес и тишина.",
            "День 2. Нашёл следы. Не человеческие. Слишком длинные. Ночью что-то стояло у костра и смотрело.",
            "День 4. Оно не любит свет, но свет его не останавливает. Оно просто ждёт, пока ты отвернёшься.",
            "День 6. Нашёл чужой лагерь. Палатка разорвана изнутри. Внутри никого.",
            "День 9. Каждый раз, когда я умираю, я просыпаюсь ближе к воде. Я начинаю запоминать, что меня убивает.",
            "День 11. Оно шепчет моим голосом. Не отвечай. Что бы оно ни говорило, не отвечай.",
            "День 13. Патронов мало. Дробовик бьёт сильно, но близко. Винтовка точная, но громкая. Оно приходит на звук.",
            "День 15. Здесь были другие. Никто не выбрался. Но дальше на север я нашёл отметки на деревьях.",
            "День 16. Если ты читаешь это, значит, я не ушёл. Ищи выход там, где оно не ходит.",
            "Последняя страница пуста. Кажется, продолжение ещё не написано..."
    };

    public JournalItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player p, InteractionHand hand) {
        ItemStack stack = p.getItemInHand(hand);
        if (!level.isClientSide) {
            CompoundTag d = IsleEvents.data(p);
            int read = d.getInt("journals");
            if (read >= PAGES.length) {
                p.sendSystemMessage(Component.literal("§7Ты прочитал все дневники. Остаётся найти выход."));
            } else {
                p.sendSystemMessage(Component.literal("§6[Дневник " + (read + 1) + "/" + PAGES.length + "] §f" + PAGES[read]));
                d.putInt("journals", read + 1);
                if (!p.getAbilities().instabuild) stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
