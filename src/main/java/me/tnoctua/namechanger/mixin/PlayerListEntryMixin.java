/*
 * This file is part of tnoctua's FabricMC Name Changer mod
 * Please read the full license terms at (https://github.com/tnoctua/name-changer)
 * Copyright (C) 2025	tnoctua
 */

package me.tnoctua.namechanger.mixin;

import com.mojang.authlib.GameProfile;
import me.tnoctua.namechanger.NameChanger;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.tnoctua.namechanger.NameChanger.client;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

    @Shadow @Final private GameProfile profile;

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        if (client.player != null && profile.getId().equals(client.player.getUuid())) {
            cir.setReturnValue(Text.literal(NameChanger.getName()));
        }
    }

}
