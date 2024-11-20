package com.client.impl.function.combat.aura.rotate.handler;

import net.minecraft.util.math.MathHelper;

public class Interpolates {
    private float s = 1.70158f;

    public float calculateInterpolate(String mode, float start, float end, float percent) {
        return switch (mode) {
            case "Линейное" -> linearEase(start, end, percent);
            case "Синусоидальное" -> sineEaseOut(start, end, percent);
            case "Экспоненциальное" -> expoEaseOut(start, end, percent);
//            case "backEaseIn" -> backEaseIn(start, end, percent, d);
//            case "backEaseOut" -> backEaseOut(start, end, percent, d);
//            case "bounceEaseOut" -> bounceEaseOut(start, end, percent, d);
//            case "cubicEaseIn" -> cubicEaseIn(start, end, percent, d);
//            case "cubicEaseOut" -> cubicEaseOut(start, end, percent, d);
//            case "circEaseIn" -> circEaseIn(start, end, percent, d);
//            case "circEaseOut" -> circEaseOut(start, end, percent, d);
//            case "elasticEaseIn" -> elasticEaseIn(start, end, percent, d);
//            case "elasticEaseOut" -> elasticEaseOut(start, end, percent, d);
//            case "expoEaseIn" -> expoEaseIn(start, end, percent, d);
//            case "expoEaseOut" -> expoEaseOut(start, end, percent, d);
//            case "linearEase" -> linearEase(start, end, percent, d);
//            case "quadEaseIn" -> quadEaseIn(start, end, percent, d);
//            case "quadEaseOut" -> quadEaseOut(start, end, percent, d);
//            case "quintEaseIn" -> quintEaseIn(start, end, percent, d);
//            case "quintEaseOut" -> quintEaseOut(start, end, percent, d);
//            case "sineEaseIn" -> sineEaseIn(start, end, percent, d);
//            case "sineEaseOut" -> sineEaseOut(start, end, percent, d);
            default -> start;
        };
    }

    public float backEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change*(percent/= d)*percent*((s+1)*percent - s) + start;
    }

    public float backEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change*((percent=percent/d-1)*percent*((s+1)*percent + s) + 1) + start;
    }

    public float bounceEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        if ((percent/=d) < (1/2.75f)) {
            return change*(7.5625f*percent*percent) + start;
        } else if (percent < (2/2.75f)) {
            return change*(7.5625f*(percent-=(1.5f/2.75f))*percent + .75f) + start;
        } else if (percent < (2.5/2.75)) {
            return change*(7.5625f*(percent-=(2.25f/2.75f))*percent + .9375f) + start;
        } else {
            return change*(7.5625f*(percent-=(2.625f/2.75f))*percent + .984375f) + start;
        }
    }

    public float cubicEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);
        return change*(percent/=d)*percent*percent + start;
    }

    public float cubicEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);
        return change*((percent=percent/d-1)*percent*percent + 1) + start;
    }

    public float circEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return -change * ((float)Math.sqrt(1 - (percent/=d)*percent) - 1) + start;
    }

    public float circEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change * (float)Math.sqrt(1 - (percent=percent/d-1)*percent) + start;
    }

    public float elasticEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        if (percent==0) return start;  if ((percent/=d)==1) return start+change;
        float p=d*.3f;
        float s=p/4;
        return -(change *(float)Math.pow(2,10*(percent-=1)) * (float)Math.sin( (percent*d-s)*(2*(float)Math.PI)/p )) + start;
    }

    public float elasticEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        if (percent==0) return s;  if ((percent/=d)==1) return start+change;
        float p=d*.3f;
        float s=p/4;
        return (change *(float)Math.pow(2,-10*percent) * (float)Math.sin( (percent*d-s)*(2*(float)Math.PI)/p ) + change + start);
    }

    public float expoEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return (percent==0) ? start : change * (float)Math.pow(2, 10 * (percent/d - 1)) + start;
    }

    public float expoEaseOut(float start, float end, float percent) {
        float d = 1;
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return (percent==d) ? start+change : change * (-(float)Math.pow(2, -10 * percent/d) + 1) + start;
    }

    public float linearEase(float start, float end, float percent) {
        float d = 0.8f;
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change*percent/d + start;
    }

    public float quadEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change*(percent/=d)*percent + start;
    }

    public float quadEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return -change *(percent/=d)*(percent-2) + start;
    }

    public float quintEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change*(percent/=d)*percent*percent*percent*percent + start;
    }

    public float quintEaseOut(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change*((percent=percent/d-1)*percent*percent*percent*percent + 1) + start;
    }

    public float sineEaseIn(float start, float end, float percent, float d) {
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return -change * (float)Math.cos(percent/d * (Math.PI/2)) + change + s;
    }

    public float sineEaseOut(float start, float end, float percent) {
        float d = 1;
        percent = Math.min(percent, 1.0f); // Ограничиваем значение до 1
        float change = (float) calculate(1, end, start);

        return change * (float)Math.sin(percent/d * (Math.PI/2)) + start;
    }

    public double calculate(double m, double a, double b) {
        double d, s;
        d = MathHelper.wrapDegrees(a - b);
        s = Math.abs(d / m);
        return s * (d >= 0 ? 1 : -1);
    }
}
