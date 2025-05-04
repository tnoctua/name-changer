/*
 * This file is part of tnoctua's FabricMC Name Changer mod
 * Please read the full license terms at (https://github.com/tnoctua/name-changer)
 * Copyright (C) 2025	tnoctua
 */

package me.tnoctua.namechanger.mixin;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameProfile.class)
public interface GameProfileAccessor {

    @Accessor("name")
    String getOriginalName();

}
