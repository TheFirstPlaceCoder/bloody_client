package com.client.alt;

import com.client.impl.function.client.ClickGui;
import com.client.system.function.FunctionManager;
import com.client.utils.files.SoundManager;
import com.client.utils.misc.CustomSoundInstance;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import mixin.accessor.MinecraftClientAccessor;
import net.minecraft.client.util.Session;
import net.minecraft.sound.SoundCategory;

import static com.client.BloodyClient.mc;

public class Account {
    public String name;
    public boolean isFavorite = false;

    public Account(String name, boolean isFavorite) {
        this.name = name;
        this.isFavorite = isFavorite;
    }

    public boolean login() {
        YggdrasilMinecraftSessionService service = (YggdrasilMinecraftSessionService) mc.getSessionService();
        AccountUtils.setBaseUrl(service, YggdrasilEnvironment.PROD.getSessionHost() + "/session/minecraft/");
        AccountUtils.setJoinUrl(service, YggdrasilEnvironment.PROD.getSessionHost() + "/session/minecraft/join");
        AccountUtils.setCheckUrl(service, YggdrasilEnvironment.PROD.getSessionHost() + "/session/minecraft/hasJoined");

        setSession(new Session(name, "", "", "mojang"));

        if (FunctionManager.get(ClickGui.class).clientSound.get()) {
            CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.CHIME_EVENT, SoundCategory.MASTER);
            customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
            mc.getSoundManager().play(customSoundInstance);
        }

        return true;
    }

    public void setSession(Session session) {
        ((MinecraftClientAccessor) mc).setSession(session);
        mc.getSessionProperties().clear();
    }
}