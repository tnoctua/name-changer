/*
 * This file is part of tnoctua's FabricMC Name Changer mod
 * Please read the full license terms at (https://github.com/tnoctua/name-changer)
 * Copyright (C) 2025	tnoctua
 */

package me.tnoctua.namechanger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.tnoctua.namechanger.mixin.GameProfileAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.file.Paths;

public class NameChanger implements ClientModInitializer {
	public static final String MOD_ID = "namechanger";
	public static MinecraftClient client;

	// Configuration
	public static String originalName = "Player";
	public static String name = "NameChanger";
	public static boolean gameListProfile = true;
	public static boolean playerListEntry = true;
	public static boolean chatReplacement = true;

	// Config System
	public static File configFile;
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static BufferedReader br;
	private static BufferedWriter bw;

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();

		// Set up configuration
		configFile = Paths.get(client.runDirectory.getPath(), "config/%s.json".formatted(MOD_ID)).toFile();
		if (configFile.exists()) {
			fromJson();
		} else {
			toJson();
		}

		// Original name setter
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null) {
				originalName = ((GameProfileAccessor) client.player.getGameProfile()).getOriginalName();
			}
		});
		// Signed chat replacement
		ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (chatReplacement && client.player != null && message.getString().contains(originalName)) {
				String processed = message.getString().replace(originalName, getName());
				client.player.sendMessage(Text.literal(processed).setStyle(message.getStyle()), false);
				return false;
			}
			return true;
		});
		// Game chat replacement
		ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
			if (chatReplacement && client.player != null && message.getString().contains(originalName)) {
				String processed = message.getString().replace(originalName, getName());
				return Text.literal(processed).setStyle(message.getStyle());
			}
			return message;
		});
	}

	/**
	 * Returns the configured name.
	 * @return name to use in replacements
	 */
	public static String getName() {
		return name;
	}

	/**
	 * Builds current configuration values to JSON.
	 */
	private static void toJson() {
		// Build JSON
		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(name));
		json.add("game_profile", new JsonPrimitive(gameListProfile));
		json.add("player_list", new JsonPrimitive(playerListEntry));
		json.add("chat_replacement", new JsonPrimitive(chatReplacement));

		// Write File
		try {
			if (bw != null) {
				bw.close();
			}
			bw = new BufferedWriter(new FileWriter(configFile));
			gson.toJson(json, bw);
			bw.flush();
			bw.close();
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Reads configuration file values from JSON.
	 */
	private static void fromJson() {
		// Read File
		try {
			if (br != null) {
				br.close();
			}
			br = new BufferedReader(new FileReader(configFile));

			// Set values
			JsonObject json = gson.fromJson(br, JsonObject.class);
			name = json.get("name").getAsString();
			gameListProfile = json.get("game_profile").getAsBoolean();
			playerListEntry = json.get("player_list").getAsBoolean();
			chatReplacement = json.get("chat_replacement").getAsBoolean();

			br.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}