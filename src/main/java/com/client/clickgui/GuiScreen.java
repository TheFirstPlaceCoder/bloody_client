package com.client.clickgui;

import com.client.system.function.Category;
import com.client.system.hud.HudFunction;
import com.client.utils.Utils;
import com.client.utils.color.ColorTransfusion;
import com.client.utils.color.ColorUtils;
import com.client.utils.math.Timer;
import com.client.utils.math.animation.Animation;
import com.client.utils.math.animation.Direction;
import com.client.utils.math.animation.impl.EaseBackIn;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.FunctionUtils;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.client.BloodyClient.mc;

public class GuiScreen extends Screen {
    public static final float WIDTH = 120, HEIGHT = 270;

    public static final Color BACK = new Color(23, 23, 23, 255);
    public static final Color INNER = new Color(17, 17, 17, 255);
    public static final Color BACK_OTHER = new Color(48, 46, 46, 255);

    public static FloatRect SCISSOR_RECT = new FloatRect();

    private static final List<FunctionTab> FUNCTION_TABS = new ArrayList<>();
    public Animation scale = new EaseBackIn(500, 1, 1);

    public static String search = "";
    public static boolean write;
    public static final List<Runnable> callback = new ArrayList<>();

    private static final Timer dotTimer = new Timer();
    private static FloatRect searchBar = new FloatRect();

    public static boolean closeInvoke = false;
    public static float y, ySpeed;
    public static double totalW, scaleOutput;

    private float alpha = 0;

    private static GuiScreen instance;

    public static GuiScreen getInstance() {
        if (instance == null) {
            instance = new GuiScreen();
        }
        return instance;
    }

    public GuiScreen() {
        super(Text.of("gui"));
        init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX1, int mouseY1, float delta) {
        int mouseX = (int) (mc.mouse.getX() / 2);
        int mouseY = (int) (mc.mouse.getY() / 2);

        scale.setDirection(closeInvoke ? Direction.BACKWARDS : Direction.FORWARDS);
      // scaleOutput = scale.getOutput();

      // if (closeInvoke) {
      //     if (scaleOutput <= 0.95f) {
      //         if (y < (float) mc.getWindow().getScaledHeight() / 2) {
      //             y += ySpeed;
      //             ySpeed += 2.7F;
      //             y = MathHelper.clamp(y, 0, (float) mc.getWindow().getScaledHeight() / 2);
      //         }
      //     }
      // } else {
      //     if (y > 0) {
      //         y -= ySpeed;
      //         ySpeed -= 2.7F;
      //         y = MathHelper.clamp(y, 0, (float) mc.getWindow().getScaledHeight() / 2);
      //     } else {
      //         ySpeed = 0;
      //     }
      // }

        if (closeInvoke)
            alpha -= 20f / 255f;
        else
            alpha += 20f / 255f;

        alpha = MathHelper.clamp(alpha, 0f, 1f);

        FloatRect rect = new FloatRect(FUNCTION_TABS.get(2).innerRect);
        rect.setY(rect.getY());
        float w = (float) totalW;
        float h = rect.getH();
        float cenX = rect.getCenteredX() - ((w ) / 2);
        float cenY = rect.getCenteredY() - ((h ) / 2);

        SCISSOR_RECT = new FloatRect(cenX, cenY, w , h );

       //GL11.glPushMatrix();
       //GL11.glTranslated((double) mc.getWindow().getScaledWidth() / 2, (double) mc.getWindow().getScaledHeight() / 2 + y, 0);
       //GL11.glScaled(scaleOutput, scaleOutput, scaleOutput);
       //GL11.glTranslated((double) -mc.getWindow().getScaledWidth() / 2, (double) -mc.getWindow().getScaledHeight() / 2 + y, 0);

        Utils.rescaling(() -> {
                    for (FunctionTab functionTab : FUNCTION_TABS) {
                        functionTab.draw(mouseX, mouseY, alpha);
                    }

                    HudFunction.drawRectGui(searchBar, alpha);
                    //GL.drawRoundedRect(searchBar, 4, ColorUtils.injectAlpha(BACK, (int) (alpha * 255)));

                    if (write) {
                        dotTimer.tick();
                        dotTimer.resetIfPassed(20);
                    }

                    ScissorUtils.enableScissor(searchBar);
                    IFont.drawCenteredY(IFont.MONTSERRAT_BOLD, write ? search + (dotTimer.passed(10) ? "." : "") : "Поиск", searchBar.getX() + 3, searchBar.getCenteredY(), ColorUtils.injectAlpha(Color.WHITE, (int) (alpha * 255)), 8);
                    ScissorUtils.disableScissor();
                });

        //GL.drawShadowRect(new FloatRect(searchBar.getX() + 2f, searchBar.getY() + 1f, 5f, searchBar.getH() - 2f), GL.Direction.LEFT, ColorUtils.injectAlpha(BACK, (int) (alpha * 255)));
        //GL.drawShadowRect(new FloatRect(searchBar.getX2() - 7f, searchBar.getY() + 1f, 5f, searchBar.getH() - 2f), GL.Direction.RIGHT, ColorUtils.injectAlpha(BACK, (int) (alpha * 255)));

       // GL11.glPopMatrix();

        if (closeInvoke && alpha <= 0) {
            mc.openScreen(null);
            closeInvoke = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (FunctionTab functionTab : FUNCTION_TABS) {
            functionTab.click((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), button);
        }
        if (searchBar.intersect((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2)) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            write = true;
            search = "";
            callback.forEach(Runnable::run);
        }
        if (searchBar.intersect((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2)) && button == GLFW.GLFW_MOUSE_BUTTON_3) {
            search = "";
            callback.forEach(Runnable::run);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (FunctionTab functionTab : FUNCTION_TABS) {
            functionTab.release((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), button);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (FunctionTab functionTab : FUNCTION_TABS) {
            functionTab.key(keyCode);
        }
        if (write) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                write = false;
                callback.forEach(Runnable::run);
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
                search = getStringIgnoreLastChar(search);
                callback.forEach(Runnable::run);
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (write) {
            search += chr;
            callback.forEach(Runnable::run);
        }
        for (FunctionTab functionTab : FUNCTION_TABS) {
            functionTab.symbol(chr);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (FunctionTab functionTab : FUNCTION_TABS) {
            functionTab.scroll((int) (mc.mouse.getX() / 2), (int) (mc.mouse.getY() / 2), amount);
        }
        return false;
    }


    @Override
    public void onClose() {
        for (FunctionTab functionTab : FUNCTION_TABS) {
            functionTab.close();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static String getStringIgnoreLastChar(String str) {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < str.toCharArray().length - 1; i++) {
            s.append(str.toCharArray()[i]);
        }

        return s.toString();
    }

    public static FloatRect scaleRect(FloatRect in) {
        FloatRect rect = new FloatRect(FUNCTION_TABS.get(2).innerRect);
        rect.setY(in.getY() + y);
        rect.setH(in.getH());
        float w = (float) totalW;
        float h = rect.getH();
        float cenX = (float) (rect.getCenteredX() - ((w * scaleOutput) / 2));
        float cenY = (float) (rect.getCenteredY() - ((h * scaleOutput) / 2));
        return new FloatRect(cenX, cenY, w * scaleOutput, Math.min(h * scaleOutput, h));
    }

    public static FloatRect scaleRect(FloatRect in, double sc) {
        FloatRect rect = new FloatRect(in);
        float w = rect.getW();
        float h = rect.getH();
        float cenX = (float) (rect.getCenteredX() - ((w * sc) / 2));
        float cenY = (float) (rect.getCenteredY() - ((h * sc) / 2));
        return new FloatRect(cenX, cenY, w * sc, h * sc);
    }

    public void init() {
        StringWriteStack.setCurrent(null);
        scaleOutput = 0;
        closeInvoke = false;
        FUNCTION_TABS.clear();
        search = "";
        write = false;
        dotTimer.reset();
        callback.clear();

        scale = new EaseBackIn(500, 1, 1);
      //  y = (float) mc.getWindow().getScaledHeight() / 2;
        y = 0;
        ySpeed = 38;

        float x_center = (float) mc.getWindow().getWidth() / 4;
        float offset = WIDTH + 8;
        float tab_width = 0;

        for (Category cat : Category.values()) tab_width += offset;

        totalW = tab_width - 8;

        float x = (x_center - tab_width / 2);
        float y = (float) mc.getWindow().getHeight() / 4 - HEIGHT / 2;

        for (Category cat : Category.values()) {
            FUNCTION_TABS.add(new FunctionTab(new FloatRect(x, y, WIDTH, HEIGHT), cat));
            x += offset;
        }

        searchBar = new FloatRect(FUNCTION_TABS.get(2).rect.getCenteredX() - 50, (float) mc.getWindow().getHeight() / 4 + HEIGHT / 2 + 15, 100, 22);
    }
}
