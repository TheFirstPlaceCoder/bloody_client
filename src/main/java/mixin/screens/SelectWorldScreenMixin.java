package mixin.screens;

import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.*;

import java.awt.*;
import java.util.List;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen {
    @Shadow
    @Final
    protected Screen parent;
    @Shadow
    private List<OrderedText> tooltipText;
    @Shadow
    private ButtonWidget deleteButton;
    @Shadow
    private ButtonWidget selectButton;
    @Shadow
    private ButtonWidget editButton;
    @Shadow
    private ButtonWidget recreateButton;
    @Shadow
    private WorldListWidget levelList;
    @Shadow
    public abstract void worldSelected(boolean active);

    protected SelectWorldScreenMixin(Text title) {
        super(title);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.levelList = new WorldListWidget((SelectWorldScreen) (Object) this, this.client, this.width, this.height, 48, this.height - 64, 36, () -> {
            return "";
        }, this.levelList);
        this.children.add(this.levelList);
        this.selectButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52
                , 150, 20, new TranslatableText("selectWorld.select"), (buttonWidget) -> {
            this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::play);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, new TranslatableText("selectWorld.create"), (buttonWidget) -> {
            this.client.openScreen(CreateWorldScreen.create(this));
        }));
        this.editButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 72, 20, new TranslatableText("selectWorld.edit"), (buttonWidget) -> {
            this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::edit);
        }));
        this.deleteButton = this.addButton(new ButtonWidget(this.width / 2 - 76, this.height - 28, 72, 20, new TranslatableText("selectWorld.delete"), (buttonWidget) -> {
            this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::delete);
        }));
        this.recreateButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 72, 20, new TranslatableText("selectWorld.recreate"), (buttonWidget) -> {
            this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::recreate);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            this.client.openScreen(this.parent);
        }));
        this.worldSelected(false);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltipText = null;
        this.levelList.render(matrices, mouseX, mouseY, delta);

        if (!Loader.unHook) {
            GL.prepare();
            GL.drawRoundedRect(new FloatRect(this.width / 2f - IFont.getWidth(IFont.MONTSERRAT_BOLD, this.title.getString(), 13) / 2 - 10, 12, IFont.getWidth(IFont.MONTSERRAT_BOLD, this.title.getString(), 13) + 20, IFont.getHeight(IFont.MONTSERRAT_BOLD, this.title.getString(), 13) + 6), 6, new Color(28, 30, 35, 210));
            GL.end();

            FontRenderer.color(true);
            IFont.drawCenteredX(IFont.MONTSERRAT_BOLD, this.title.getString(), this.width / 2f, 15, new Color(162, 162, 162).brighter(), 13);
            FontRenderer.color(false);
        } else {
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        }

        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderOrderedTooltip(matrices, this.tooltipText, mouseX, mouseY);
        }
    }
}
