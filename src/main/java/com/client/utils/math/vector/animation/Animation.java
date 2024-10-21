package com.client.utils.math.vector.animation;

public class Animation {
    private float end;
    private float ms;

    private float var;

    public Animation(float end, float ms) {
        this.end = end;
        this.ms = ms;
        var = 0f;
    }

    public void setEnd(float end) {
  //      if (end != this.end) {
         //   var = 0f;
//        }
        this.end = end;
    }

    public void setMs(float ms) {
        this.ms = ms;
    }

    public float getEnd() {
        if (var < Math.abs(end) || var > Math.abs(end)) {
            float step = Math.abs(end / ms);
            var += var > Math.abs(end) ? -step : step;
        }
        return end < 0 ? -var : var;
    }
}
