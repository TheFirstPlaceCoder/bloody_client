#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;
varying vec2 oneTexel;
uniform float radius;
uniform float fillOpacity;
uniform float brightOutline;
uniform float power;

float exponentialGlow(float dist, float maxDist) {
    return exp(-(dist * dist) / (0.6 * (maxDist * maxDist)));
}

void main() {
    float max_dist = radius;
    vec4 center = texture2D(DiffuseSampler, texCoord);

    if (center.a != 0.0) {
        center.a *= fillOpacity;
    } else {
        for (int x = -int(radius); x <= int(radius); x++) {
            for (int y = -int(radius); y <= int(radius); y++) {
                vec2 offsetTexCoord = texCoord + vec2(float(x), float(y)) * oneTexel;
                vec4 offset = texture2D(DiffuseSampler, offsetTexCoord);
                if (offset.a != 0.0) {
                    float dist = length(vec2(float(x), float(y)));
                    float alpha = exponentialGlow(dist, max_dist) * power;
                    center = max(center, vec4(offset.rgb, offset.a * alpha));
                }
            }
        }

        if (brightOutline == 1) {
            int widthInt = 1;
            for (int x = -widthInt; x <= widthInt; x++) {
                for (int y = -widthInt; y <= widthInt; y++) {
                    vec4 offset = texture2D(DiffuseSampler, texCoord + vec2(x, y) * oneTexel);
                    if (offset.a != 0.0) center = offset;
                }
            }
        }
    }
    gl_FragColor = center;
}
