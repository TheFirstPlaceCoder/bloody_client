package com.client.impl.function.hud;

import api.interfaces.EventHandler;
import com.client.event.events.GameEvent;
import com.client.event.events.TickEvent;
import com.client.system.config.ConfigSystem;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import com.client.system.setting.settings.ListSetting;
import com.client.system.setting.settings.StringSetting;
import com.client.utils.Utils;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MusicHud extends Function {
    private final ListSetting mode = List().name("Режим").enName("Mode").list(List.of("Ссылка на mp3", "Файлы")).defaultValue("Файлы").build();
    public final StringSetting link = String().name("Ссылка на mp3").enName("mp3 link").defaultValue("https://icast.connectmedia.hu/5202/live.mp3").visible(() -> !mode.get().equals("Файлы")).build();

    private final IntegerSetting volume = Integer().name("Громкость").enName("Volume").defaultValue(50).min(0).max(100).build();

    public MusicHud() {
        super("Music", Category.HUD);
    }

    public AdvancedPlayer player;
    private List<String> list = new ArrayList<>();
    private boolean isFiles = false;
    private int currentSong = 0;
    public int totalTime = 0, maxTime = 0;
    private Thread timeThread;

    @Override
    public void onEnable() {
        isFiles = mode.get().equals("Файлы");
        currentSong = 0;
        totalTime = 0;
        maxTime = 0;
        if (isFiles) setStrings();
        playRadio();
    }

    @Override
    public void onDisable() {
        stopRadio();
    }

    @EventHandler
    public void left_event(GameEvent.Left e) {
        stopRadio();
    }

    public String getSongName() {
        if (list.isEmpty() || list.get(currentSong) == null || !isEnabled()) return "None";
        if (!isFiles) return "Ссылка";

        String a = list.get(currentSong).substring(list.get(currentSong).lastIndexOf("/") + 1);
        return a.substring(0, a.length() - 4);
    }

    public void setStrings() {
        list.clear();
        for (String name : Objects.requireNonNull(new File(ConfigSystem.PATH + "/music").list())) {
            if (!name.endsWith("mp3")) continue;

            File file = new File(ConfigSystem.PATH + "/music/" + name);

            if (!file.isDirectory() && file.exists()) {
                try {
                    list.add(file.toURL().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateSongs() {
        currentSong++;

        if (currentSong > list.size() - 1) currentSong = 0;
    }

    private void updateSongsBack() {
        currentSong--;

        if (currentSong < 0) currentSong = list.size() - 1;
    }

    public void buttonBack() {
        if (isEnabled() && mode.get().equals("Файлы")) {
            if (player != null) player.close();
            if (timeThread != null) timeThread.stop();
            totalTime = 0;

            updateSongsBack();

            playRadio();
        }
    }

    public void buttonNext() {
        if (isEnabled() && mode.get().equals("Файлы")) {
            if (player != null) player.close();
            if (timeThread != null) timeThread.stop();
            totalTime = 0;

            updateSongs();

            playRadio();
        }
    }

    public void playRadio() {
        try {
            String selectedRadioUrl = isFiles ? list.isEmpty() ? null : list.get(currentSong) : (!link.get().isEmpty() && !link.get().isBlank() && link.get().endsWith("mp3") ? link.get() : null);
            if (selectedRadioUrl != null) {
                URL radioStream = new URL(selectedRadioUrl);
                Bitstream bitstream = new Bitstream(radioStream.openStream());
                Header header = bitstream.readFrame();

                float totalSeconds = header.total_ms(radioStream.openStream().available()) / 1000f;
                maxTime = (int) totalSeconds;

                timeThread = new Thread(() -> {
                    int elapsedSeconds = 0;
                    while (elapsedSeconds < totalSeconds) {
                        try {
                            Thread.sleep(1000); // Обновление каждую секунду
                            elapsedSeconds++;
                            totalTime = elapsedSeconds;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                timeThread.start();

                player = new AdvancedPlayer(radioStream.openStream(), FactoryRegistry.systemRegistry().createAudioDevice());
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        if (isFiles) updateSongs();
                        playRadio();
                    }
                });

                setVolume(volume.get() / 2);

                CompletableFuture.runAsync(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick(TickEvent.Pre e) {
        setVolume(volume.get() / 2f);
    }

    private void setVolume(float volume) {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfo) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (!mixer.isLineSupported(Port.Info.SPEAKER)) continue;
            Port port;
            try {
                port = (Port)mixer.getLine(Port.Info.SPEAKER);
                port.open();
            } catch (LineUnavailableException e) {
                return;
            }
            if (port.isControlSupported(FloatControl.Type.VOLUME)) {
                FloatControl vol = (FloatControl)port.getControl(FloatControl.Type.VOLUME);
                vol.setValue(volume / 100.0f);
            }
            port.close();
        }
    }

    public void stopRadio() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (timeThread != null) timeThread.stop();
        totalTime = 0;
    }
}