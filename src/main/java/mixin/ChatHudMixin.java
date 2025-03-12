package mixin;

import com.client.BloodyClient;
import com.client.event.events.ReceiveChatMessageEvent;
import com.client.impl.function.misc.BetterChat;
import com.client.interfaces.IChatHud;
import com.client.interfaces.IChatHudLine;
import com.client.system.function.FunctionManager;
import com.client.utils.Utils;
import com.client.utils.auth.Loader;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.math.animation.AnimationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin extends DrawableHelper implements IChatHud {
    @Shadow public abstract void clear(boolean clearHistory);
    @Shadow protected abstract boolean isChatHidden();
    @Shadow protected abstract void processMessageQueue();
    @Shadow public abstract int getVisibleLineCount();
    @Shadow @Final private List<ChatHudLine<OrderedText>> visibleMessages;
    @Shadow @Final private MinecraftClient client;
    @Shadow private int scrolledLines;
    @Shadow private static double getMessageOpacityMultiplier(int age) {
        return 0;
    }
    @Shadow @Final private Deque<Text> messageQueue;
    @Shadow private boolean hasUnreadNewMessages;
    @Shadow @Final private List<ChatHudLine<Text>> messages;
    @Unique private boolean clientCall;
    @Shadow @Final private static Logger LOGGER;
    @Shadow protected abstract void removeMessage(int messageId);
    @Shadow public abstract int getWidth();
    @Shadow public abstract double getChatScale();
    @Shadow protected abstract boolean isChatFocused();
    @Shadow public abstract void scroll(double amount);
    @Shadow @Final private List<String> messageHistory;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void addMessage(Text message, int messageId) {
        this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
        if (!message.getString().toLowerCase().contains("bloody"))
        LOGGER.info("[CHAT] {}", message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    @Override
    public void message(Text text) {
        clientCall = true;
        int id = 100000 + new Random().nextInt(100000);
        addMessage(text, id);
        clientCall = false;
    }

    @Override
    public void message(Text text, int id) {
        clientCall = true;
        addMessage(text, id);
        clientCall = false;
    }

    @Override
    public void message(Text text, int ticks, int id) {
        clientCall = true;
        addMessage(text, id, ticks, false);
        clientCall = false;
    }

    @Override
    public void clear() {
        clientCall = true;
        clear(true);
        clientCall = false;
    }

    @Override
    public void unHookClear() {
        clientCall = true;
        visibleMessages.removeIf(e -> ((OrderedText) e.getText()).toString().toLowerCase().contains("bloody"));
        messages.removeIf(e -> e.getText().getString().toLowerCase().contains("bloody"));
        clientCall = false;
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void onClear(CallbackInfo info) {
        if (!Loader.unHook && !clientCall && FunctionManager.get(BetterChat.class).getKeepHistory()) {
            info.cancel();
        }
    }

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    private void addMessage(Text message, int messageId, int timestamp, boolean refresh) {
        ReceiveChatMessageEvent chatMessage = new ReceiveChatMessageEvent(message.getString());
        chatMessage.post();

        if (FunctionManager.get(BetterChat.class).getTime() && !clientCall && !Loader.unHook) {
            message = new LiteralText(Formatting.GRAY + "<" + BloodyClient.getTime() + "> " + Formatting.RESET).append(message);
        }

        if (messageId != 0) {
            this.removeMessage(messageId);
        }

        int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
        boolean bl = this.isChatFocused();

        OrderedText orderedText;
        for(Iterator var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0, create(timestamp, orderedText, messageId))) {
            orderedText = (OrderedText)var8.next();
            if (bl && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1.0);
            }
        }

        while (this.visibleMessages.size() > (FunctionManager.get(BetterChat.class).getMoreHistory() ? FunctionManager.get(BetterChat.class).count.get() : 100)) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!refresh) {
            this.messages.add(0, new ChatHudLine<>(timestamp, message, messageId));

            while (this.messages.size() > (FunctionManager.get(BetterChat.class).getMoreHistory() ? FunctionManager.get(BetterChat.class).count.get() : 100)) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    @Unique
    private ChatHudLine<OrderedText> create(int t, OrderedText o, int m) {
        ChatHudLine<OrderedText> chatHudLine = new ChatHudLine<>(t, o, m);
        ((IChatHudLine) chatHudLine).setX(-this.client.textRenderer.getWidth(chatHudLine.getText()));
        return chatHudLine;
    }

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public void render(MatrixStack matrices, int tickDelta) {
        if (!this.isChatHidden()) {
            this.processMessageQueue();
            int i = this.getVisibleLineCount();
            int j = this.visibleMessages.size();
            if (j > 0) {
                boolean bl = this.isChatFocused();
                double d = this.getChatScale();
                int k = MathHelper.ceil((double) this.getWidth() / d);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d, d, 1.0);
                double e = this.client.options.chatOpacity * 0.8999999761581421 + 0.10000000149011612;
                double f = this.client.options.textBackgroundOpacity;
                double g = 9.0 * (this.client.options.chatLineSpacing + 1.0);
                double h = -8.0 * (this.client.options.chatLineSpacing + 1.0) + 4.0 * this.client.options.chatLineSpacing;
                int l = 0;
                int m;
                int n;
                int p;
                int q;
                for (m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; ++m) {
                    ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
                    if (chatHudLine != null) {
                        n = tickDelta - chatHudLine.getCreationTick();
                        if (n < 200 || bl) {
                            double o = bl ? 1.0 : getMessageOpacityMultiplier(n);
                            p = (int) (255.0 * o * e);
                            q = (int) (255.0 * o * f);
                            ++l;
                            if (p > 3) {
                                ((IChatHudLine)chatHudLine).setX(AnimationUtils.fast(((IChatHudLine)chatHudLine).getX(), 0F, 10F));

                                double s = (double) (-m) * g;
                                matrices.push();
                                matrices.translate(FunctionManager.get(BetterChat.class).getAnimation() && !Loader.unHook ? ((IChatHudLine)chatHudLine).getX() : 0, 0.0, 50.0);
                                fill(matrices, -2, (int) (s - g), k + 4, (int) s, q << 24);
                                RenderSystem.enableBlend();
                                matrices.translate(0, 0.0, 50.0);
                                this.client.textRenderer.drawWithShadow(matrices, chatHudLine.getText(), 0, (float) ((int) (s + h)), 16777215 + (p << 24));
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                                matrices.pop();
                            }
                        }
                    }
                }

                int t;
                if (!this.messageQueue.isEmpty()) {
                    m = (int) (128.0 * e);
                    t = (int) (255.0 * f);
                    matrices.push();
                    matrices.translate(0.0, 0.0, 50.0);
                    fill(matrices, -2, 0, k + 4, 9, t << 24);
                    RenderSystem.enableBlend();
                    matrices.translate(0.0, 0.0, 50.0);
                    this.client.textRenderer.drawWithShadow(matrices, new TranslatableText("chat.queue", this.messageQueue.size()), 0.0F, 1.0F, 16777215 + (m << 24));
                    matrices.pop();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                }

                if (bl) {
                    this.client.textRenderer.getClass();
                    m = 9;
                    RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
                    t = j * m + j;
                    n = l * m + l;
                    int u = this.scrolledLines * n / j;
                    int v = n * n / t;
                    if (t != n) {
                        p = u > 0 ? 170 : 96;
                        q = this.hasUnreadNewMessages ? 13382451 : 3355562;
                        fill(matrices, 0, -u, 2, -u - v, q + (p << 24));
                        fill(matrices, 2, -u, 1, -u - v, 13421772 + (p << 24));
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }
}