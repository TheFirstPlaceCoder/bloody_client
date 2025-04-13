#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec3 color;
uniform float radius;
uniform float fillOpacity;

void main() {
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
}