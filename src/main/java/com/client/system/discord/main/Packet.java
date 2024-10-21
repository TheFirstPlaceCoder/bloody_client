package com.client.system.discord.main;

import com.google.gson.JsonObject;

public record Packet(Opcode opcode, JsonObject data) {
}