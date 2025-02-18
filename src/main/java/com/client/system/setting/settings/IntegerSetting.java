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

import static com.client.BloodyClient.mc;

public class IntegerSetting extends AbstractSettings<Integer> {
    private Integer min, max;

    public IntegerSetting(Function function) {
        super(SettingsType.Integer, function);
    }

    public Integer get() {
        return value;
    }

    public void set(Integer value) {
        boolean shouldSound = !Objects.equals(value, this.value);

        if (this.isPremium && !Loader.isPremium()) this.value = getDefaultValue();
        else this.value = value;

        if (shouldSound && mc.world != null && mc.player != null && FunctionManager.get(ClickGui.class).clientSound.get()) {
            CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.BUTTON_EVENT, SoundCategory.MASTER);
            customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
            mc.getSoundManager().play(customSoundInstance);
        }
    }

    public Integer getMax() {
        return max;
    }

    public IntegerSetting max(Integer max) {
        this.max = max;
        return this;
    }

    public Integer getMin() {
        return min;
    }

    public IntegerSetting min(Integer min) {
        this.min = min;
        return this;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public IntegerSetting defaultValue(Integer value) {
        this.defaultValue = value;
        set(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public IntegerSetting name(String name) {
        this.name = name;
        return this;
    }

    public String getEnName() {
        return enName;
    }

    public IntegerSetting enName(String name) {
        this.enName = name;
        return this;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public IntegerSetting setPremium(boolean premium) {
        this.isPremium = premium;
        return this;
    }

    public IVisible isVisible() {
        return visible;
    }

    public IntegerSetting visible(IVisible visible) {
        this.visible = visible;
        return this;
    }

    public SettingsType getType() {
        return type;
    }

    public IntegerSetting build() {
        SettingManager.register(this);
        return this;
    }

    public String toConfig() {
        return getName().concat(":").concat(String.valueOf(get()));
    }
}
