package com.client.utils.render;

import com.client.interfaces.IMatrix4f;
import com.client.utils.Utils;
import com.client.utils.math.vector.Vec3;
import com.client.utils.math.vector.Vec4;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.Matrix4f;

import java.util.HashMap;

import static com.client.BloodyClient.mc;
import static org.lwjgl.opengl.GL11.*;

public class TagUtils {
    public static HashMap<String, String> donates = new HashMap<>() {{
        put("ᴀ", "a");
        put("ʙ", "b");
        put("ᴄ", "c");
        put("ᴅ", "d");
        put("ᴇ", "e");
        put("ғ", "f");
        put("ɢ", "g");
        put("ʜ", "h");
        put("ɪ", "i");
        put("ᴊ", "j");
        put("ᴋ", "k");
        put("ʟ", "l");
        put("ᴍ", "m");
        put("ɴ", "n");
        put("ᴏ", "o");
        put("ᴘ", "p");
        put("ᴏ̨", "q");
        put("ʀ", "r");
        put("s", "s");
        put("ᴛ", "t");
        put("ᴜ", "u");
        put("ᴠ", "v");
        put("ᴡ", "w");
        put("x", "x");
        put("ʏ", "y");
        put("ᴢ", "z");
        put("⚡","");
        put("●", "");
        put("⭐", "");
    }};

    public static HashMap<String, String> formattings = new HashMap<>() {{
        put("§a", "");
        put("§b", "");
        put("§c", "");
        put("§d", "");
        put("§e", "");
        put("§f", "");
        put("§g", "");
        put("§h", "");
        put("§i", "");
        put("§j", "");
        put("§k", "");
        put("§l", "");
        put("§m", "");
        put("§n", "");
        put("§o", "");
        put("§p", "");
        put("§q", "");
        put("§r", "");
        put("§s", "");
        put("§t", "");
        put("§u", "");
        put("§v", "");
        put("§w", "");
        put("§x", "");
        put("§y", "");
        put("§z", "");
        put("§0", "");
        put("§1", "");
        put("§2", "");
        put("§3", "");
        put("§4", "");
        put("§5", "");
        put("§6", "");
        put("§7", "");
        put("§8", "");
        put("§9", "");
    }};

    public static String replace(String str) {
        StringBuilder result = new StringBuilder();

        for (char c : str.toCharArray()) {
            String letter = String.valueOf(c);
            if (donates.containsKey(letter)) {
                result.append(donates.get(letter).toUpperCase().replace(" ", ""));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String replaceFormattings(String input) {
        StringBuilder output = new StringBuilder();
        boolean skipNext = false;

        for (int i = 0; i < input.length(); i++) {
            if (skipNext) {
                skipNext = false;
                continue;
            }

            if (i < input.length() - 1) {
                String pair = input.substring(i, i + 2);
                if (formattings.containsKey(pair)) {
                    output.append(formattings.get(pair));
                    skipNext = true;
                } else {
                    output.append(input.charAt(i));
                }
            } else {
                output.append(input.charAt(i));
            }
        }

        return output.toString();
    }

    public static String getPrefix(PlayerEntity player) {
        return player.getScoreboard().getPlayerTeam(player.getGameProfile().getName()).getPrefix().getString();
    }

    private static final Vec4 vec4 = new Vec4();
    private static final Vec4 mmMat4 = new Vec4();
    private static final Vec4 pmMat4 = new Vec4();
    private static final Vec3 camera = new Vec3();
    private static final Vec3 cameraNegated = new Vec3();
    private static Matrix4f model;
    private static Matrix4f projection;
    private static double windowScale;

    private static double scale;

    public static void onRender(MatrixStack matrices, Matrix4f projection) {
        model = matrices.peek().getModel().copy();
        TagUtils.projection = projection;

        camera.set(mc.gameRenderer.getCamera().getPos());
        cameraNegated.set(camera);
        cameraNegated.negate();

        windowScale = mc.getWindow().calculateScaleFactor(1, mc.forcesUnicodeFont());
    }

    public static boolean to2D(Vec3 pos, double scale) {
        TagUtils.scale = getScale(pos) * scale;

        vec4.set(cameraNegated.x + pos.x, cameraNegated.y + pos.y, cameraNegated.z + pos.z, 1);

        ((IMatrix4f) (Object) model).multiplyMatrix(vec4, mmMat4);
        ((IMatrix4f) (Object) projection).multiplyMatrix(mmMat4, pmMat4);

        if (pmMat4.w <= 0.0f) return false;

        pmMat4.toScreen();
        double x = pmMat4.x * mc.getWindow().getFramebufferWidth();
        double y = pmMat4.y * mc.getWindow().getFramebufferHeight();

        if (Double.isInfinite(x) || Double.isInfinite(y)) return false;

        pos.set(x / windowScale, mc.getWindow().getFramebufferHeight() - y / windowScale, pmMat4.z);
        return true;
    }

    public static void begin(Vec3 pos) {
        glPushMatrix();
        glTranslated(pos.x, pos.y, 0);
        glScaled(scale, scale, 1);
    }

    public static void end() {
        glPopMatrix();
    }

    private static double getScale(Vec3 pos) {
        double dist = camera.distanceTo(pos);
        return Utils.clamp(1 - dist * 0.01, 0.5, Integer.MAX_VALUE);
    }
}