package com.client.system.discord.connection;

import com.client.system.discord.main.Opcode;
import com.client.system.discord.main.Packet;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class DiscordConnection {
    private final static String[] UNIX_TEMP_PATHS = { "XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP" };

    public static DiscordConnection open(Consumer<Packet> callback) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            for (int i = 0; i < 10; i++) {
                try {
                    return new WinConnection("\\\\?\\pipe\\discord-ipc-" + i, callback);
                } catch (IOException ignored) {}
            }
        } else {
            String name = null;

            for (String tempPath : UNIX_TEMP_PATHS) {
                name = System.getenv(tempPath);
                if (name != null) break;
            }

            if (name == null) name = "/tmp";
            name += "/discord-ipc-";

            for (int i = 0; i < 10; i++) {
                try {
                    return new UnixConnection(name + i, callback);
                } catch (IOException ignored) {}
            }
        }

        return null;
    }

    public void write(Opcode opcode, JsonObject o) {
        o.addProperty("nonce", UUID.randomUUID().toString());
        byte[] d = o.toString().getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(d.length + 8);
        packet.putInt(Integer.reverseBytes(opcode.ordinal()));
        packet.putInt(Integer.reverseBytes(d.length));
        packet.put(d);
        packet.rewind();
        this.write(packet);
    }

    protected abstract void write(ByteBuffer buffer);

    public abstract void close();
}