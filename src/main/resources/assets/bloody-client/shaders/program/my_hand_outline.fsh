#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec3 color;
uniform vec2 resolution;
uniform float radius;
uniform float fillOpacity;
uniform float time;

uniform float renderMode;

vec3 rgb_from_hsv(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 hsv_from_rgb(vec3 c)
{
    vec4 K = vec4(0.0, -1.10 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-0;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 get_fragment(vec2 uv) {
    vec3 rgb =  vec3(1.0);
    vec3 hsv = hsv_from_rgb(rgb);
    hsv.x += (uv.x - uv.y) * 0.5 - time;
    hsv.y = min(hsv.y + 0.35, 1.0);
    hsv.z = min(hsv.z + 0.35, 1.0);
    return rgb_from_hsv(hsv);
}

void main() {
    if (renderMode == 0) {
        vec4 center = texture2D(DiffuseSampler, texCoord);

        if (center.a != 0.0) {
            center = vec4(color.rgb, center.a * fillOpacity);
        } else {
            int g = int(radius);

            for (int x = -g; x <= g; x++) {
                for (int y = -g; y <= g; y++) {
                    vec2 offsetTexCoord = texCoord + vec2(float(x), float(y)) * oneTexel;
                    vec4 offset = texture2D(DiffuseSampler, offsetTexCoord);

                    if (offset.a != 0.0) {
                        center = vec4(color.rgb, offset.a);
                    }
                }
            }
        }

        gl_FragColor = center;
    } else {
        vec4 centerCol = texture2D(DiffuseSampler, texCoord);
        vec3 col = get_fragment(gl_FragCoord.xy/resolution.xy);

        if (centerCol.a != 0) {
            gl_FragColor = vec4(col.rgb, centerCol.a * fillOpacity);
            return;
        }

        float alphaOutline = 0;
        for (float y = -radius; y < radius; y += 1) {
            for (float x = -radius; x < radius; x += 1) {
                vec2 offset = vec2(x * oneTexel.x, y * oneTexel.y);
                vec4 currentColor = texture2D(DiffuseSampler, texCoord.xy + offset);
                if (currentColor.a != 0) {
                    alphaOutline = currentColor.a;
                }
            }
        }
        gl_FragColor = vec4(col, alphaOutline);
    }
}


//uniform float lineWidth;
//uniform float fillOpacity;
//uniform vec3 colorHand;
//
//void main() {
//    vec4 centerCol = texture2D(DiffuseSampler, texCoord);
//
//    if (centerCol.a != 0.0) {
//        gl_FragColor = vec4(colorHand.rgb, centerCol.a * fillOpacity);
//    } else {
//        int g = int(lineWidth);
//        vec4 finalColor = vec4(0.0);
//        float max_dist = float(length(vec2(g)));
//
//        for (int x = -g; x <= g; x++) {
//            for (int y = -g; y <= g; y++) {
//                vec2 offsetTexCoord = texCoord + vec2(float(x), float(y)) * oneTexel;
//                vec4 offset = texture2D(DiffuseSampler, offsetTexCoord);
//
//                if (offset.a != 0.0) {
//                    vec3 fillColor = colorHand.rgb;
//                    float dist = length(vec2(float(x), float(y)));
//                    float alpha = 1.0 - dist / max_dist;
//                    vec4 colorWithGlow = vec4(fillColor, offset.a * alpha);
//                    finalColor = max(finalColor, colorWithGlow);
//                }
//            }
//        }
//
//        gl_FragColor = finalColor;
//    }
//}