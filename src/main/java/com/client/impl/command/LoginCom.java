package com.client.impl.command;

import com.client.system.command.Command;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.utils.game.entity.ServerUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import mixin.accessor.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Session;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class LoginCom extends Command {
    public LoginCom() {
        super("Login", List.of("login"), List.of("login <ник>"));
    }

    @Override
    public void command(String[] args) {
        if (ServerUtils.isPvp()) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Вы в режиме боя!", 2000L), NotificationManager.NotifType.Error);
            return;
        }

        if (mc.isInSingleplayer()) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Вы в одиночном мире", 2000L), NotificationManager.NotifType.Error);
            return;
        }

        String ip = mc.getCurrentServerEntry().address;
        String serverName = mc.getCurrentServerEntry().name;

        mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.of("")));

        String name = args[0];
        setSession(new Session(name, resolveUUID(name).toString(), "", "mojang"));

        mc.openScreen(new ConnectScreen(new TitleScreen(), mc, new ServerInfo(serverName, ip, false)));
    }

    public UUID resolveUUID(String name) {
        UUID uUID;
        InputStreamReader in;
        try {
            in = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return UUID.randomUUID();
        }
        try {
            uUID = UUID.fromString(new Gson().fromJson(in, JsonObject.class).get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Throwable uuid) {
            try {
                try {
                    in.close();
                }
                catch (Throwable throwable) {
                    uuid.addSuppressed(throwable);
                }
                throw uuid;
            } catch (Throwable ignored) {
                return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uUID;
    }

    public void setSession(Session session) {
        YggdrasilMinecraftSessionService service = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
        setBaseUrl(service, YggdrasilEnvironment.PROD.getSessionHost() + "/session/minecraft/");
        setJoinUrl(service, YggdrasilEnvironment.PROD.getSessionHost() + "/session/minecraft/join");
        setCheckUrl(service, YggdrasilEnvironment.PROD.getSessionHost() + "/session/minecraft/hasJoined");

        ((MinecraftClientAccessor) mc).setSession(session);
    }

    public void setBaseUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(service, url);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void setJoinUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("joinUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setCheckUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("checkUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error() {
        warning(".login <ник>");
    }
}