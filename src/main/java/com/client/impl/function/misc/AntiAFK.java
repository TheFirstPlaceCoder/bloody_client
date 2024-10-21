package com.client.impl.function.misc;

import com.client.event.events.TickEvent;
import com.client.interfaces.IMinecraftClient;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.multiboolean.MultiBooleanSetting;
import com.client.system.setting.settings.multiboolean.MultiBooleanValue;
import com.client.utils.Utils;
import com.client.utils.math.MsTimer;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AntiAFK extends Function {
    public AntiAFK() {
        super("Anti AFK", Category.MISC);
    }

    private final MultiBooleanSetting filter = MultiBoolean().name("Использовать").defaultValue(List.of(
            new MultiBooleanValue(true, "Клик"),
            new MultiBooleanValue(true, "Прыжок"),
            new MultiBooleanValue(true, "Команды"),
            new MultiBooleanValue(true, "Качание рукой"),
            new MultiBooleanValue(true, "Шифт")
    )).build();

    private final MsTimer timer = new MsTimer();
    private final List<String> strings = Arrays.asList("Как у всех дела?", "/huy", "Я люблю всех!", "/fly",
            "ОМГ ПОКО Х3", "/gm", "Че по мисту?", "/gamerule",
            "/admin", "Клоуны", "Админы привет", "/popa",
            "/gff", "eafgew", "uyio", "/hfgn",
            "/wegg", "weg", "cvsd", "qasd",
            "wegwegegw", "/scxz", "", "piposa",
            "piskhf", "gew", "khf", "sgrtrwg",
            "byff", "/fewfe", "asef", "/fefeg");

    @Override
    public void onEnable() {
        timer.reset();
    }

    @Override
    public void tick(TickEvent.Pre event) {
        List<Integer> list = new ArrayList<>();
        if (filter.get(0)) list.add(1);
        if (filter.get(1)) list.add(2);
        if (filter.get(2)) list.add(3);
        if (filter.get(3)) list.add(4);
        if (filter.get(4)) list.add(5);

        if (timer.passedS(10) && !list.isEmpty()) {
            doAction(list.get(list.size() > 1 ? Utils.random(0, list.size() - 1) : 0));
            timer.reset();
        }
    }

    private void doAction(int i) {
        if (i == 1) ((IMinecraftClient) mc).attack();
        else if (i == 2) mc.player.jump();
        else if (i == 3) mc.player.sendChatMessage(strings.get(new Random().nextInt(strings.size() - 1)));
        else if (i == 4) mc.player.swingHand(Hand.MAIN_HAND);
        else if (i == 5) mc.options.keySneak.setPressed(!mc.options.keySneak.isPressed());
    }
}
