#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;
varying vec2 oneTexel;
uniform vec2 resolution;
uniform float radius;
uniform float fillOpacity;
uniform float time;
uniform float renderMode;
uniform float glowMode;
uniform float power;

vec3 rgb_from_hsv(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 hsv_from_rgb(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    return vec3(abs(q.z + (q.w - q.y) / max(d, 1e-10)), d / (q.x + 1e-10), q.x);
}

vec3 get_fragment(vec2 uv) {
    vec3 rgb = vec3(1.0);
    vec3 hsv = hsv_from_rgb(rgb);
    hsv.x += (uv.x - uv.y) * 0.5 - time;
    hsv.y = min(hsv.y + 0.35, 1.0);
    hsv.z = min(hsv.z + 0.35, 1.0);
    return rgb_from_hsv(hsv);
}

float exponentialGlow(float dist, float maxDist) {
    return exp(-(dist * dist) / (0.6 * (maxDist * maxDist)));
}

void main() {
    float max_dist = radius;
    vec4 center = texture2D(DiffuseSampler, texCoord);

    if (renderMode == 0) {
        if (center.a != 0.0) {
            center.a *= fillOpacity;
        } else {
            for (int x = -int(radius); x <= int(radius); x++) {
                for (int y = -int(radius); y <= int(radius); y++) {
                    vec2 offsetTexCoord = texCoord + vec2(float(x), float(y)) * oneTexel;
                    vec4 offset = texture2D(DiffuseSampler, offsetTexCoord);
                    if (offset.a != 0.0) {
                        float dist = length(vec2(float(x), float(y)));
                        float alpha = glowMode == 1 ? exponentialGlow(dist, max_dist) * power : offset.a;
                        center = max(center, vec4(offset.rgb, offset.a * alpha));
                    }
                }
            }
        }
        gl_FragColor = center;
    } else {
        vec4 centerCol = texture2D(DiffuseSampler, texCoord);
        vec3 col = get_fragment(gl_FragCoord.xy / resolution.xy);

        if (centerCol.a != 0) {
            gl_FragColor = vec4(col.rgb, centerCol.a * fillOpacity);
            return;
        }

        float alphaOutline = 0.0;
        for (float y = -radius; y < radius; y++) {
            for (float x = -radius; x < radius; x++) {
                vec2 offset = vec2(x * oneTexel.x, y * oneTexel.y);
                vec4 currentColor = texture2D(DiffuseSampler, texCoord + offset);
                if (currentColor.a != 0.0) {
                    float dist = length(vec2(x, y));
                    float al = glowMode == 1 ? exponentialGlow(dist, max_dist) * power : currentColor.a;
                    alphaOutline = max(alphaOutline, currentColor.a * al);
                }
            }
        }
        gl_FragColor = vec4(col, alphaOutline);
    }
}
