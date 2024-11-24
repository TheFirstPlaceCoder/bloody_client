package mixin;

import api.main.EventUtils;
import com.client.event.events.RenderSlotEvent;
import com.client.impl.command.DropCommand;
import com.client.impl.function.client.AutoBuy;
import com.client.impl.function.client.HelpItems;
import com.client.impl.function.misc.ItemScroller;
import com.client.impl.function.visual.ShulkerPreview;
import com.client.system.command.CommandManager;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudFunction;
import com.client.utils.auth.Loader;
import com.client.utils.color.ColorUtils;
import com.client.utils.game.entity.EntityUtils;
import com.client.utils.game.entity.PlayerUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

import static com.client.system.function.Function.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

@Mixin(HandledScreen.class)
public abstract class HandleScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    protected HandleScreenMixin(Text title) {
        super(title);
    }

    @Unique private final ItemScroller itemScroller = FunctionManager.get(ItemScroller.class);
    @Unique private final ShulkerPreview shulkerPreview = FunctionManager.get(ShulkerPreview.class);
    @Unique private final Identifier texture = new Identifier("bloody-client", "/client/container.png");

    @Shadow @Nullable protected abstract Slot getSlotAt(double xPosition, double yPosition);
    @Shadow protected abstract void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType);
    @Shadow @Nullable protected Slot focusedSlot;
    @Shadow protected int backgroundHeight;
    @Unique private FloatRect rect = new FloatRect();

    @Inject(method = "mouseDragged", at = @At("TAIL"))
    private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> info) {
        if (button != GLFW_MOUSE_BUTTON_LEFT || Loader.unHook || !itemScroller.isEnabled() || itemScroller.time > System.currentTimeMillis()) return;
        Slot slot = getSlotAt(mouseX, mouseY);
        if (slot != null && slot.hasStack() && hasShiftDown()) {
            onMouseClick(slot, slot.id, button, SlotActionType.QUICK_MOVE);
            itemScroller.time = System.currentTimeMillis() + itemScroller.delay.get();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        HelpItems.x = this.x;
        HelpItems.y = this.y;
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        float w = IFont.getWidth(IFont.COMFORTAAB, "   Выбросить   ", 9);
        rect = new FloatRect(cx - w /2, cy - (float) backgroundHeight / 2 - IFont.getHeight(IFont.COMFORTAAB, "   Выбросить   ", 9) - 12, w, IFont.getHeight(IFont.COMFORTAAB, "   Выбросить   ", 9) + 8);

        if (focusedSlot != null && !focusedSlot.getStack().isEmpty() && shulkerPreview.isEnabled()) {
            if (ShulkerPreview.hasItems(focusedSlot.getStack())) {
                NbtCompound compoundTag = focusedSlot.getStack().getSubTag("BlockEntityTag");
                if (compoundTag != null) {
                    DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                    Inventories.readNbt(compoundTag, itemStacks);
                    draw(matrices, itemStacks, mouseX, mouseY, getShulkerColor(focusedSlot.getStack()));
                }
            }
        }

        RenderSlotEvent event = new RenderSlotEvent();
        EventUtils.post(event);

        if (event.minCountSlot != null)
            GL.drawQuad(this.x + event.minCountSlot.x, this.y + event.minCountSlot.y, 16, 16, ColorUtils.injectAlpha(event.minCountColor, 100));

        if (event.minSlot != null && event.minCountSlot != event.minSlot)
            GL.drawQuad(this.x + event.minSlot.x, this.y + event.minSlot.y, 16, 16, ColorUtils.injectAlpha(event.minColor, 100));

        if (mc.world != null && mc.player != null && !EntityUtils.getGameMode(mc.player).isCreative() && !Loader.unHook) {
            HudFunction.drawRect(rect, 1f);
            IFont.drawCenteredXY(IFont.COMFORTAAB, "   Выбросить   ", rect.getCenteredX(), rect.getCenteredY(), Color.WHITE, 9);
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (rect.intersect(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1 && mc.world != null && mc.player != null && !EntityUtils.getGameMode(mc.player).isCreative()) {
            CommandManager.get(DropCommand.class).drop();
        }
    }

    @Unique
    private void draw(MatrixStack matrices, DefaultedList<ItemStack> itemStacks, int mouseX, int mouseY, Color color) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        mouseX += 8;
        mouseY -= 12;

        drawBackground(matrices, mouseX, mouseY, color);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DiffuseLighting.enable();

        int row = 0;
        int i = 0;
        for (ItemStack itemStack : itemStacks) {
            drawItem(itemStack, mouseX + 8 + i * 18, mouseY + 7 + row * 18);

            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }

        DiffuseLighting.disable();
        RenderSystem.enableDepthTest();
    }

    @Unique
    private Color getShulkerColor(ItemStack shulkerItem) {
        if (!(shulkerItem.getItem() instanceof BlockItem)) return Color.WHITE;
        Block block = ((BlockItem) shulkerItem.getItem()).getBlock();
        if (!(block instanceof ShulkerBoxBlock)) return Color.WHITE;
        ShulkerBoxBlock shulkerBlock = (ShulkerBoxBlock) ShulkerBoxBlock.getBlockFromItem(shulkerItem.getItem());
        DyeColor dye = shulkerBlock.getColor();
        if (dye == null) return Color.WHITE;
        final float[] colors = dye.getColorComponents();
        return new Color(colors[0], colors[1], colors[2], 1f);
    }

    @Unique
    private void drawItem(ItemStack itemStack, int x, int y) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        DiffuseLighting.enable();
        client.getItemRenderer().renderGuiItemIcon(itemStack, x, y);
        client.getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, itemStack, x, y, null);
        DiffuseLighting.disable();
        RenderSystem.enableDepthTest();
    }

    @Unique
    private void drawBackground(MatrixStack matrices, int x, int y, Color color) {
        RenderSystem.color4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        client.getTextureManager().bindTexture(texture);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 0, 176, 67, 67, 176);
    }
}
