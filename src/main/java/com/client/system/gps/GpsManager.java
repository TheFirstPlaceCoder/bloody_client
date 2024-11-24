package com.client.system.gps;

import api.interfaces.EventHandler;
import com.client.BloodyClient;
import com.client.event.events.GpsRenderEvent;
import com.client.event.events.Render2DEvent;
import com.client.event.events.Render3DEvent;
import com.client.impl.function.client.ClickGui;
import com.client.impl.function.client.GPS;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.files.StreamUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.render.Matrices;
import com.client.utils.render.text.TextRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.TextureGL;
import com.client.utils.render.wisetree.render.render3d.Renderer3D;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.client.system.function.Function.mc;
import static com.client.utils.math.MathUtils.getRotations;

public class GpsManager {
    private static final List<GpsPoint> points = new ArrayList<>();
    private static final Identifier arrow = new Identifier("bloody-client", "/client/arrows.png");
    private static final Identifier gps = new Identifier("bloody-client", "/client/gps.png");
    private static float yaw;
    public static boolean show = true;

    private static final String[] BUILTIN_ICONS = {"gps"};

    private static final Color TEXT = new Color(255, 255, 255);

    public static final Map<String, AbstractTexture> icons = new HashMap<>();

    public static void init() {
        if (Loader.sizeLong != 86032109746L) try {
            System.out.println("J");
            throw new IOException();
        } catch (IOException e) {
            Runtime.getRuntime().halt(0);
            System.exit(-1);
            for(;;) {}
        }

        File iconsFolder = new File(BloodyClient.GPS_FOLDER, "gps");
        iconsFolder.mkdirs();

        for (String builtinIcon : BUILTIN_ICONS) {
            File iconFile = new File(iconsFolder, builtinIcon + ".png");
            if (!iconFile.exists()) copyIcon(iconFile);
        }

        File[] files = iconsFolder.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                try {
                    String name = file.getName().replace(".png", "");
                    AbstractTexture texture = new NativeImageBackedTexture(NativeImage.read(new FileInputStream(file)));
                    icons.put(name, texture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean add(GpsPoint gpsPoint) {
        for (GpsPoint point : points) {
            if (point.name.equals(gpsPoint.name)) {
                return false;
            }
        }

        points.add(gpsPoint);
        return true;
    }

    public static boolean remove(String name) {
        GpsPoint remove = null;
        for (GpsPoint point : points) {
            if (point.name.replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
                remove = point;
                break;
            }
        }

        if (remove == null) return false;
        points.remove(remove);

        return true;
    }

    public static List<GpsPoint> get() {
        return points;
    }

    public static void clear() {
        points.clear();
    }

    public Vec3d getCoords(GpsPoint waypoint) {
        double x = waypoint.x;
        double y = waypoint.y;
        double z = waypoint.z;

        return new Vec3d(x, y, z);
    }

    @EventHandler
    private void onRender2DEvent(Render2DEvent event) {
        yaw = AnimationUtils.fast(yaw, mc.player.renderYaw);

        if (show && !Loader.unHook) {
            Utils.rescaling(() -> {
                for (GpsPoint point : points) {
                    drawArrow(point);
                }
            });
        }
    }

    @EventHandler
    private void onRender3DEvent(GpsRenderEvent event) {
        if (show && !Loader.unHook) {
            GPS gg = FunctionManager.get(GPS.class);

            for (GpsPoint waypoint : get()) {
                Camera camera = mc.gameRenderer.getCamera();

                double x = getCoords(waypoint).x;
                double y = getCoords(waypoint).y;
                double z = getCoords(waypoint).z;

                // Compute scale
                double dist = PlayerUtils.distanceToCamera(x, y, z);
                double scale = 0.01 * gg.scaleGps.get();
                if(dist > 8) scale *= dist / 8;

                double a = 1;
                if (dist < 10) {
                    a = dist / 10;
                    if (a < 0.1) continue;
                }

                double maxViewDist = mc.options.viewDistance * 16;
                if (dist > maxViewDist) {
                    double dx = x - camera.getPos().x;
                    double dy = y - camera.getPos().y;
                    double dz = z - camera.getPos().z;

                    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    dx /= length;
                    dy /= length;
                    dz /= length;

                    dx *= maxViewDist;
                    dy *= maxViewDist;
                    dz *= maxViewDist;

                    x = camera.getPos().x + dx;
                    y = camera.getPos().y + dy;
                    z = camera.getPos().z + dz;

                    scale /= dist / 15;
                    scale *= maxViewDist / 15;
                }

                // Setup the rotation
                Matrices.push();
                Matrices.translate(x + 0.5 - event.offsetX, y - event.offsetY, z + 0.5 - event.offsetZ);
                Matrices.translate(0, -0.5 + gg.scaleGps.get() - 1, 0);
                Matrices.rotate(-camera.getYaw(), 0, 1, 0);
                Matrices.rotate(camera.getPitch(), 1, 0, 0);
                Matrices.translate(0, 0.5, 0);
                Matrices.scale(-scale, -scale, scale);

                // Render background
                TextRenderer.get().begin(gg.scaleText.get(), false, true);
                double w = TextRenderer.get().getWidth(waypoint.name) / 2.0;
                double h = TextRenderer.get().getHeight();

                // TODO: I HATE EVERYTHING ABOUT HOW RENDERING ROTATING THINGS WORKS AND I CANNOT BE ASKED TO WORK THIS OUT, THE WHOLE THING NEEDS TO BE RECODED REEEEEEEEEEEEEEEEEEEE
                // sounds like a personal problem
            /*MB.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
            MB.quad(-w - 1, -h + 1, 0, -w - 1, 9 - h, 0, w + 1, 9 - h, 0, w + 1, -h + 1, 0, BACKGROUND);
            MB.quad(-w2 - 1, 0, 0, -w2 - 1, 8, 0, w2 + 1, 8, 0, w2 + 1, 0, 0, BACKGROUND);
            MB.end();*/

                waypoint.renderIcon(-8, h, 0, a, 16);

                // Render name text
                TextRenderer.get().render(waypoint.name, -w, 0, TEXT);

                TextRenderer.get().end();
                Matrices.pop();
            }
        }
    }

    private static void drawArrow(GpsPoint gpsPoint) {
        float x = (float) mc.getWindow().getWidth() / 4;
        float y = (float) mc.getWindow().getHeight() / 8;

        float look = getRotations(gpsPoint.get()) - yaw;

        double rad = Math.toRadians(look);
        double sin = Math.sin(rad) * 50;
        double cos = Math.cos(rad) * 50;

        GL11.glPushMatrix();
        GL11.glTranslated(x + sin, y - cos, 0);
        GL11.glScalef(12F / 128f, 12F / 128f, 12F / 128f);
        GL11.glRotated(getRotations(x, y, (float) (x + sin), (float) (y - cos)), 0, 0, 1);

        TextureGL.create().bind(arrow).draw(new TextureGL.TextureRegion(128, 128), true, getColor(gpsPoint));

        GL11.glPopMatrix();

        MatrixStack text_stack = new MatrixStack();
        text_stack.translate(x + sin, y - cos, 0);
        text_stack.scale(1f, 1f, 1f);
        String dist = Math.round(PlayerUtils.distanceTo(gpsPoint.get()) * 10.0) / 10.0 + "m";
        IFont.drawWithShadow(IFont.COMFORTAAB, gpsPoint.name + " " + dist, -IFont.getWidth(IFont.COMFORTAAB, gpsPoint.name + dist, 7) / 2, 10, Color.WHITE, 7, text_stack);
    }

    private static float getSize(BlockPos pos) {
        float distanceTo = (float) PlayerUtils.distanceTo(pos);

        if (distanceTo > 100) return 10;
        else return 10 * Math.min(Math.max(distanceTo / 100, 0), 1);
    }

    private static Color getColor(GpsPoint point) {
        if (PlayerUtils.distanceTo(point.get()) >= 1000) return Color.RED;
        Color color1 = Color.RED;
        Color color2 = Color.GREEN;
        double progress = MathHelper.clamp(PlayerUtils.distanceTo(point.get()) / 1000f, 0, 1);
        int red = (int)Math.abs(progress * (double)(color1.getRed()) + (1.0 - progress) * (double)(color2.getRed()));
        int green = (int)Math.abs(progress * (double)(color1.getGreen()) + (1.0 - progress) * (double)(color2.getGreen()));
        int blue = (int)Math.abs(progress * (double)(color1.getBlue()) + (1.0 - progress) * (double)(color2.getBlue()));
        return new Color(MathHelper.clamp(red, 0, 255), MathHelper.clamp(green, 0, 255), MathHelper.clamp(blue, 0, 255), 255);
    }

    private static void copyIcon(File file) {
        StreamUtils.copy(BloodyClient.class.getResourceAsStream("/assets/bloody-client/client/" + file.getName()), file);
    }
}
