/*
 * This file is part of tnoctua's FabricMC Name Changer mod
 * Please read the full license terms at (https://github.com/tnoctua/name-changer)
 * Copyright (C) 2025	tnoctua
 */

package me.tnoctua.namechanger.mixin;

import com.mojang.authlib.GameProfile;
import me.tnoctua.namechanger.NameChanger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static me.tnoctua.namechanger.NameChanger.client;

@Mixin(GameProfile.class)
public abstract class GameProfileMixin {

	@Shadow public abstract UUID getId();

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true, remap = false)
	private void getName(CallbackInfoReturnable<String> cir) {
		if (client.player != null && getId().equals(client.player.getUuid())) {
			cir.setReturnValue(NameChanger.getName());
		}
	}

}