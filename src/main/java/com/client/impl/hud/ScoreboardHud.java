package com.client.impl.hud;

import com.client.system.hud.HudFunction;
import com.client.utils.math.rect.FloatRect;

public class ScoreboardHud extends HudFunction {
    public ScoreboardHud() {
        super(new FloatRect(400, 400, 100, 300), "Scoreboard-Hud");
    }

    @Override
    public void draw(float alpha) {
    }
}
