package com.client.system.setting.settings;

import com.client.impl.function.client.ClickGui;
import com.client.system.function.Function;
import com.client.system.function.FunctionManager;
import com.client.system.setting.api.AbstractSettings;
import com.client.system.setting.api.IVisible;
import com.client.system.setting.api.SettingsType;
import com.client.system.setting.manager.SettingManager;
import com.client.utils.auth.Loader;
import com.client.utils.files.SoundManager;
import com.client.utils.misc.CustomSoundInstance;
import net.minecraft.sound.SoundCategory;

import java.util.Objects;
import java.util.function.Consumer;

import static com.client.BloodyClient.mc;

public class BooleanSetting extends AbstractSettings<Boolean> {
    private Consumer<Boolean> callback = null;

    public BooleanSetting(Function function) {
        super(SettingsType.Boolean, function);
    }

    public Boolean get() {
        return value;
    }

    public void set(Boolean value) {
        boolean shouldSound = !Objects.equals(value, this.value);

        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;
        callback();

        if (shouldSound && mc.world != null && mc.player != null && FunctionManager.get(ClickGui.class).clientSound.get()) {
            CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.BUTTON_EVENT, SoundCategory.MASTER);
            customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
            mc.getSoundManager().play(customSoundInstance);
        }
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public BooleanSetting defaultValue(Boolean value) {
        this.defaultValue = value;
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public BooleanSetting name(String name) {
        this.name = name;
        return this;
    }

    public String getEnName() {
        return enName;
    }

    public BooleanSetting enName(String name) {
        this.enName = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public BooleanSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public BooleanSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    public SettingsType getType() {
        return type;
    }

    public void callback() {
        if (callback != null) {
            callback.accept(this.get());
        }
    }

    public BooleanSetting callback(Consumer<Boolean> callback) {
        this.callback = callback;
        return this;
    }

    public BooleanSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        return getName().concat(":").concat(String.valueOf(get()));
    }
}