package mixin;

import com.client.BloodyClient;
import com.client.event.events.PacketEvent;
import com.client.impl.function.client.UnHook;
import com.client.interfaces.IChatHud;
import com.client.interfaces.IClientConnection;
import com.client.system.command.Command;
import com.client.system.command.CommandManager;
import com.client.system.config.ConfigSystem;
import com.client.system.function.FunctionManager;
import com.client.system.hud.HudManager;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.auth.Loader;
import com.client.utils.game.entity.ServerUtils;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements IClientConnection {
    @Shadow public abstract boolean isOpen();

    @Shadow protected abstract void sendQueuedPackets();

    @Shadow protected abstract void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback);

    @Shadow public abstract void send(Packet<?> packet);

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendd(Packet<?> packet, CallbackInfo ci) {
        if (BloodyClient.canUpdate()) {
            if (packet instanceof ChatMessageC2SPacket chatMessageC2SPacket) {
                if (!Loader.unHook) {
                    if (chatMessageC2SPacket.getChatMessage().equals(Command.getPrefix() + FunctionManager.get(UnHook.class).enableCommand.get())) {
                        HudManager.beforeUnhook();
                        Loader.unHook = true;
                        ConfigSystem.save();

                        FunctionManager.getFunctionList().forEach(e -> {
                            if (e.isEnabled()) e.toggle();
                        });

                        ConfigSystem.renameFolder(true);
                        ((IChatHud) mc.inGameHud.getChatHud()).unHookClear();
                        ci.cancel();
                        return;
                    }

                    if (chatMessageC2SPacket.getChatMessage().startsWith("/hub") && ServerUtils.isPvp()) {
                        NotificationManager.add(new Notification(NotificationType.CLIENT, "Вы в режиме боя!!!", 2000L), NotificationManager.NotifType.Error);
                        ci.cancel();
                    }
                    if (chatMessageC2SPacket.getChatMessage().startsWith(Command.getPrefix())) {
                        CommandManager.runCommand(chatMessageC2SPacket.getChatMessage().substring(Command.getPrefix().length()));
                        ci.cancel();
                    }
                } else {
                    if (chatMessageC2SPacket.getChatMessage().equals(Command.getPrefix() + FunctionManager.get(UnHook.class).disableCommand.get())) {
                        Loader.unHook = false;
                        ConfigSystem.renameFolder(false);
                        ConfigSystem.load();
                        HudManager.afterUnhook();
                        ci.cancel();
                    }
                }
            }
            PacketEvent.Send event = new PacketEvent.Send(packet);
            event.post();
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (BloodyClient.canUpdate()) {
            PacketEvent.Receive event = new PacketEvent.Receive(packet);
            event.post();
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("TAIL"), cancellable = true)
    private void sendTail(Packet<?> packet, CallbackInfo info) {
        if (BloodyClient.canUpdate()) {
            PacketEvent.Sent event = new PacketEvent.Sent(packet);
            event.post();
            if (event.isCancelled()) {
                info.cancel();
            }
        }
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        if (this.isOpen()) {
            this.sendQueuedPackets();
            this.sendImmediately(packet, null);
        } else {
            send(packet);
        }
    }
}