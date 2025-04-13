package com.client.impl.function.hud;

import api.interfaces.EventHandler;
import com.client.event.events.GameEvent;
import com.client.event.events.TickEvent;
import com.client.system.config.ConfigSystem;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.setting.settings.IntegerSetting;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import mixin.accessor.JavaSoundAudioDeviceAccessor;
import mixin.accessor.JavaZoomAccessor;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicHud extends Function {
    private final IntegerSetting volume = Integer().name("Громкость").enName("Volume").defaultValue(50).min(0).max(100).build();

    public MusicHud() {
        super("Music", Category.HUD);
    }

    private final List<File> musicFiles = new ArrayList<>();
    private int currentTrackIndex;
    private AdvancedPlayer player;
    private FileInputStream fis;
    private Thread musicThread;
    private String currentTrackName;
    private File currentTrackFile;
    private int skippedFrames = 0, playedFrames = 0;
    public boolean isPaused;

    private void loadMusicFiles() {
        File musicDir = new File(ConfigSystem.PATH + "/music");
        if (!musicDir.exists() && !musicDir.mkdirs()) {
            return;
        }

        File[] files = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (files != null) {
            musicFiles.addAll(Arrays.asList(files));
            if (!musicFiles.isEmpty()) {
                currentTrackIndex = 0;
                playTrack(musicFiles.get(currentTrackIndex), 0);
            }
        } else {
            System.err.println("No music files found in directory: " + musicDir.getAbsolutePath());
        }
    }

    private void playTrack(File trackFile, int frameToSkip) {
        try {
            if (player != null) {
                stop();
            }
            currentTrackName = trackFile.getName();
            fis = new FileInputStream(trackFile);
            player = new AdvancedPlayer(fis, FactoryRegistry.systemRegistry().createAudioDevice());
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    playNextTrack();
                }
            });
            currentTrackFile = trackFile;
            skippedFrames = frameToSkip;

            musicThread = new Thread(() -> {
                try {
                    player.play(frameToSkip, getMaxFrames());
                } catch (JavaLayerException e) {
                    System.err.println("Error playing music: " + e.getMessage());
                } finally {
                    stop();
                    playNextTrack();
                }
            });
            musicThread.start();

            setVolume(volume.get());
        } catch (IOException | JavaLayerException e) {
            System.err.println("Error loading or playing music: " + e.getMessage());
        }
    }


    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }

        try {
            musicThread.stop();
        } catch (Exception ignored) {} finally {
            musicThread = null;
        }

        if(fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                System.err.println("Error closing FileInputStream " + e.getMessage());
            }
        }

        player = null;
        fis = null;
        isPaused = false;
        com.client.impl.hud.MusicHud.nameSongX = 0;
    }

    public void pauseResume() {
        isPaused = !isPaused;
        if(player != null) {
            if (isPaused) {
                musicThread.suspend();
            } else {
                musicThread.resume();
            }
        }
    }

    public void playPreviousTrack() {
        if (musicFiles.isEmpty()) return;
        currentTrackIndex = (currentTrackIndex - 1 + musicFiles.size()) % musicFiles.size();
        playTrack(musicFiles.get(currentTrackIndex), 0);
    }

    public void playNextTrack() {
        if (musicFiles.isEmpty()) return;
        currentTrackIndex = (currentTrackIndex + 1) % musicFiles.size();
        playTrack(musicFiles.get(currentTrackIndex), 0);
    }

    public String getCurrentTrackName() {
        return currentTrackName != null ? currentTrackName.replace(".mp3", "") : "Отсутствует";
    }

    public void seekTo(int currentFrame) {
        if (currentTrackFile == null) return;

        playTrack(currentTrackFile, currentFrame);
    }

    public int getFramePosition() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getFramePosition();
        }
        return 0;
    }

    public int getFrameSize() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getFormat().getFrameSize();
        }
        return 0;
    }

    public float getFrameRate() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getFormat().getFrameRate();
        }
        return 0f;
    }

    public float getSampleRate() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getFormat().getSampleRate();
        }
        return 0;
    }

    public int getSampleSizeInBits() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getFormat().getSampleSizeInBits();
        }
        return 0;
    }

    public int getChannels() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getFormat().getChannels();
        }
        return 0;
    }

    public long getLongFramePosition() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getLongFramePosition();
        }
        return 0;
    }

    public long getMicrosecondPosition() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getMicrosecondPosition();
        }
        return 0;
    }

    public int getBufferSize() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getBufferSize();
        }
        return 0;
    }

    public float getLevel() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.getLevel();
        }
        return 0;
    }

    public int available() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            return sourceDataLine.available();
        }
        return 0;
    }

    public int getPosition() {
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js) {
            return js.getPosition();
        }
        return 0;
    }

    public void setFramesPlayed() {
        SourceDataLine sourceDataLine;
        if (player != null && ((JavaZoomAccessor) player).getAudio() instanceof JavaSoundAudioDevice js && (sourceDataLine = ((JavaSoundAudioDeviceAccessor) js).getSource()) != null) {
            long secondsPosition = sourceDataLine.getMicrosecondPosition() / 1000000;
            int playedFrames = (int) (secondsPosition * 38.5f);

            if (playedFrames > 0) {
                this.playedFrames = skippedFrames + Math.min(playedFrames, getMaxFrames());
            }
        }
    }

    public int getFramesPlayed() {
        return playedFrames;
    }

    public int getSkippedFrames() {
        return skippedFrames;
    }

    public int getMaxFrames() {
        if (currentTrackFile == null) return 0;
        try {
            FileInputStream fis = new FileInputStream(currentTrackFile);
            Bitstream bitstream = new Bitstream(fis);
            Header header = null;
            int frameCount = 0;
            while ((header = bitstream.readFrame()) != null) {
                frameCount++;
                bitstream.closeFrame();
            }

            fis.close();
            return frameCount;
        } catch (Exception e) {
            System.err.println("Error getting max frames: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public void onEnable() {
        musicFiles.clear();
        loadMusicFiles();
        currentTrackIndex = 0;
        isPaused = false;
    }

    @Override
    public void onDisable() {
        stop();
    }

    @EventHandler
    public void left_event(GameEvent.Left e) {
        stop();
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
}