package com.client.clickgui;

import com.client.utils.Utils;
import com.client.utils.math.Timer;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.client.BloodyClient.mc;

public class PrintBar implements Impl {
    public final FloatRect data;
    public final Runnable task;
    private boolean write;
    private String search;
    private final Timer dot = new Timer();
    private final boolean enterTask;

    public PrintBar(FloatRect searchBar, Runnable task) {
        this(searchBar, task, false);
    }

    public PrintBar(FloatRect searchBar, Runnable task, boolean enterTask) {
        this.data = searchBar;
        this.task = task;
        search = "";
        this.enterTask = enterTask;
    }

    @Override
    public void draw(double mouseX, double mouseY, float alp) {
        GL.drawQuad(data, new Color(26, 26, 26, 180));
        GL.drawOutlineQuad(data, Color.GRAY);

        if (write) {
            dot.tick();
            dot.resetIfPassed(20);
        } else {
            dot.reset();
        }

        if (search != null) {
            IFont.drawWithShadowCenteredY(IFont.MONTSERRAT_MEDIUM, search + (dot.passed(10) ? "_" : ""), data.getX() + 2, data.getCenteredY(), Color.LIGHT_GRAY, 8);
        }
    }

    public void reset() {
        write = false;
    }

    public String toLowerCase() {
        return search.toLowerCase();
    }

    public boolean isEmpty() {
        return search.isEmpty();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public void click(double mouseX, double mouseY, int button) {
        if (data.intersect(mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) {
                write = true;
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
                    search = "";
                }
            }
        }
    }

    @Override
    public void release(double mouseX, double mouseY, int button) {

    }

    @Override
    public void key(int key) {
        if (write) {
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (!search.isEmpty()) {
                    search = Utils.getStringIgnoreLastChar(search);
                    runTask();
                } else {
                    write = false;
                    runTask();
                }
            }
            if (key == GLFW.GLFW_KEY_ENTER) {
                write = false;
                if (enterTask) {
                    if (task != null) {
                        task.run();
                    }
                }
            }
            if (key == GLFW.GLFW_KEY_DELETE) {
                write = false;
                search = "";
            }
            if (key == GLFW.GLFW_KEY_V && Screen.hasControlDown() && !getPasteData().isEmpty()) {
                search += getPasteData();
            }
        }
    }

    private String getPasteData() {
        return mc.keyboard.getClipboard();
    }

    @Override
    public void scroll(double screenX, double screenY, double amount) {

    }

    @Override
    public void symbol(char chr) {
        if (write) {
            search += chr;
            runTask();
        }
    }

    private void runTask() {
        if (enterTask) return;
        if (task != null) {
            task.run();
        }
    }

    @Override
    public void close() {

    }
}
